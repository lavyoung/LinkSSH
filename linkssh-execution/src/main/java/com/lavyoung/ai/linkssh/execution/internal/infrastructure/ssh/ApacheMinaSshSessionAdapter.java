package com.lavyoung.ai.linkssh.execution.internal.infrastructure.ssh;

import com.lavyoung.ai.linkssh.execution.SshConnectionFailure;
import com.lavyoung.ai.linkssh.execution.SshConnectionResult;
import com.lavyoung.ai.linkssh.execution.SshSessionId;
import com.lavyoung.ai.linkssh.execution.SshSessionStatus;
import com.lavyoung.ai.linkssh.execution.internal.application.port.outbound.SshCredentialMaterial;
import com.lavyoung.ai.linkssh.execution.internal.application.port.outbound.SshCredentialResolverPort;
import com.lavyoung.ai.linkssh.execution.internal.application.port.outbound.SshSessionPort;
import com.lavyoung.ai.linkssh.host.SshConnectionTarget;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.future.ConnectFuture;
import org.apache.sshd.client.session.ClientSession;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 使用 Apache MINA SSHD 实现 SSH 会话生命周期出站端口。
 *
 * <p>适配器为每个 {@link SshSessionId} 保存独立会话槽，并以槽对象作为
 * 并发同步边界。连接不同会话标识时可以并行；连接、重连和断开同一会话
 * 标识时会串行执行，避免底层会话泄漏。</p>
 *
 * <p>线程锁定会话槽后还会重新检查槽是否仍注册在当前会话标识下，
 * 防止断开线程移除旧槽后，等待中的重连线程把新连接写入失效槽。</p>
 *
 * <p>凭据只在身份认证期间解析并安装，认证完成后立即从客户端会话移除。
 * 底层异常会被转换为稳定的 LinkSSH 失败分类，不向外暴露 Apache MINA
 * 类型或可能包含敏感信息的异常消息。</p>
 *
 * @author <a href="mailto:lavyoung1325@outlook.com">lavyoung</a>
 * @since 0.1.0
 */
final class ApacheMinaSshSessionAdapter implements SshSessionPort, AutoCloseable {

    private static final Duration SHUTDOWN_TIMEOUT = Duration.ofSeconds(5);

    private final SshClient client;
    private final SshCredentialResolverPort credentialResolver;
    private final ConcurrentMap<SshSessionId, SessionSlot> sessions = new ConcurrentHashMap<>();

    private volatile boolean closed;

    /**
     * 使用安全默认配置创建会话适配器。
     *
     * @param credentialResolver 按凭据引用解析短生命周期凭据的端口
     * @throws NullPointerException 当凭据解析端口为 {@code null} 时抛出
     */
    ApacheMinaSshSessionAdapter(SshCredentialResolverPort credentialResolver) {
        this(ApacheMinaSshClientFactory.create(), credentialResolver);
    }

    /**
     * 使用指定客户端创建会话适配器。
     *
     * <p>该构造方法用于模块内部装配和自动化测试。调用方仍须保证客户端
     * 使用强制主机密钥校验。</p>
     *
     * @param client             Apache MINA SSH 客户端
     * @param credentialResolver 短生命周期凭据解析端口
     * @throws NullPointerException 当任一参数为 {@code null} 时抛出
     */
    ApacheMinaSshSessionAdapter(SshClient client, SshCredentialResolverPort credentialResolver) {
        this.client = Objects.requireNonNull(client, "SSH client must not be null");
        this.credentialResolver = Objects.requireNonNull(credentialResolver,
            "SSH credential resolver must not be null");
    }

    /**
     * 启动当前适配器持有的 SSH 客户端。
     *
     * <p>重复调用不会重复启动客户端。</p>
     */
    synchronized void start() {
        if (closed) {
            throw new IllegalStateException("SSH session adapter has already been closed");
        }
        if (!client.isStarted()) {
            client.start();
        }
    }


    /**
     * 建立网络连接并完成 SSH 身份认证。
     * <pre>{@code
     * connect()
     * │
     * ├─ 取得当前 SessionSlot
     * ├─ 锁定同一会话标识
     * ├─ 关闭旧连接
     * ├─ 状态设为 CONNECTING
     * │
     * ├─ openSession()
     * │  └─ 传入预期主机指纹
     * │
     * ├─ credentialResolver.resolve()
     * │  └─ 根据 CredentialReference 取得凭据
     * │
     * ├─ authenticateSession()
     * │  ├─ 临时安装密码或私钥
     * │  ├─ 执行 session.auth()
     * │  └─ 立即移除认证身份
     * │
     * ├─ 发布 CONNECTED 状态
     * └─ 返回 Connected
     * }</pre>
     *
     * @param sessionId             本次连接使用的会话标识
     * @param target                SSH 连接目标及安全校验信息
     * @param connectTimeout        建立网络连接的最大等待时间
     * @param authenticationTimeout 完成身份认证的最大等待时间
     * @return 成功或失败的稳定连接结果
     */
    @Override
    public SshConnectionResult connect(SshSessionId sessionId, SshConnectionTarget target, Duration connectTimeout, Duration authenticationTimeout) {
        Objects.requireNonNull(sessionId, "SSH session id must not be null");
        Objects.requireNonNull(target, "SSH connection target must not be null");
        requirePositive(connectTimeout, "SSH connect timeout");
        requirePositive(authenticationTimeout, "SSH authentication timeout");

        if (closed) {
            return new SshConnectionResult.Failed(sessionId, SshConnectionFailure.CLIENT_UNAVAILABLE);
        }

        while (true) {
            SessionSlot slot = sessions.computeIfAbsent(sessionId, ignored -> new SessionSlot());
            synchronized (slot) {
                if (sessions.get(sessionId) != slot) {
                    continue;
                }
                return connectWithinSlot(slot, sessionId, target, connectTimeout, authenticationTimeout);
            }
        }
    }

    private SshConnectionResult connectWithinSlot(SessionSlot slot, SshSessionId sessionId, SshConnectionTarget target,
                                                  Duration connectTimeout, Duration authenticationTimeout) {
        slot.status = SshSessionStatus.CONNECTING;
        this.closeSession(slot.session, connectTimeout);
        slot.session = null;

        if (closed || !client.isStarted()) {
            SshConnectionResult result =
                failed(slot, sessionId, SshConnectionFailure.CLIENT_UNAVAILABLE, null, connectTimeout);
            removeSlotWhenClosed(sessionId, slot);
            return result;
        }

        ClientSession candidateSession;
        try {
            candidateSession = this.openSession(target, connectTimeout);
        } catch (IOException | RuntimeException exception) {
            return failed(slot, sessionId,
                SshConnectionFailureClassifier.classifyConnection(exception), null, connectTimeout);
        }

        Optional<SshCredentialMaterial> resolvedCredential;
        try {
            resolvedCredential = credentialResolver.resolve(target.credentialReference());
        } catch (RuntimeException exception) {
            return failed(slot, sessionId, SshConnectionFailure.UNEXPECTED_FAILURE, candidateSession, connectTimeout);
        }

        if (resolvedCredential == null || resolvedCredential.isEmpty()) {
            return failed(slot, sessionId, SshConnectionFailure.CREDENTIAL_UNAVAILABLE, candidateSession, connectTimeout);
        }

        try (SshCredentialMaterial credential = resolvedCredential.orElseThrow()) {
            this.authenticateSession(candidateSession, credential, authenticationTimeout);
        } catch (IOException | GeneralSecurityException | RuntimeException exception) {
            return failed(slot, sessionId, SshConnectionFailureClassifier.classifyAuthentication(exception), candidateSession, connectTimeout);
        }

        /*
         * start()、close() 和连接结果发布共同使用当前适配器作为生命周期锁。
         *
         * 如果连接先发布，之后执行的 close() 一定能在会话注册表中看到它；
         * 如果 close() 先把 closed 设为 true，则本次连接不得再发布为成功。
         */
        synchronized (this) {
            if (!closed && client.isStarted()) {
                slot.session = candidateSession;
                slot.status = SshSessionStatus.CONNECTED;
                return new SshConnectionResult.Connected(sessionId);
            }
        }
        SshConnectionResult result =
            failed(slot, sessionId, SshConnectionFailure.CLIENT_UNAVAILABLE, candidateSession, connectTimeout);
        removeSlotWhenClosed(sessionId, slot);
        return result;
    }

    /**
     * 断开指定会话并从内部注册表移除。
     *
     * @param sessionId    需要断开的会话标识
     * @param closeTimeout 关闭底层连接的最大等待时间
     */
    @Override
    public void disconnect(SshSessionId sessionId, Duration closeTimeout) {
        Objects.requireNonNull(sessionId, "SSH session id must not be null");
        requirePositive(closeTimeout, "SSH close timeout");

        while (true) {
            SessionSlot slot = sessions.get(sessionId);
            if (slot == null) {
                return;
            }

            synchronized (slot) {
                if (sessions.get(sessionId) != slot) {
                    continue;
                }
                slot.status = SshSessionStatus.DISCONNECTING;
                closeSession(slot.session, closeTimeout);
                slot.session = null;
                slot.status = SshSessionStatus.DISCONNECTED;
                sessions.remove(sessionId, slot);
                return;
            }
        }
    }

    /**
     * 查询指定会话的当前状态。
     *
     * @param sessionId 需要查询的会话标识
     * @return 已注册会话的状态；不存在时返回空
     */
    @Override
    public Optional<SshSessionStatus> findStatus(SshSessionId sessionId) {
        Objects.requireNonNull(sessionId, "SSH session id must not be null");
        while (true) {
            SessionSlot slot = sessions.get(sessionId);
            if (slot == null) {
                return Optional.empty();
            }
            synchronized (slot) {
                if (sessions.get(sessionId) != slot) {
                    continue;
                }
                markDisconnectedWhenClosed(slot);
                return Optional.of(slot.status);
            }
        }
    }

    /**
     * 关闭全部 SSH 会话并停止底层客户端。
     * <pre>{@code
     * closed = true
     *       │
     *       ▼
     * 阻止新连接成功发布
     *       │
     *       ▼
     * 逐个断开已注册会话
     *       │
     *       ▼
     * 停止 SshClient
     * }</pre>
     *
     * <p>该操作具备幂等性。关闭后不允许重新启动适配器。</p>
     */
    @Override
    public void close() {
        synchronized (this) {
            if (closed) {
                return;
            }
            closed = true;
        }
        for (SshSessionId sessionId : List.copyOf(sessions.keySet())) {
            disconnect(sessionId, SHUTDOWN_TIMEOUT);
        }
        synchronized (this) {
            if (client.isStarted()) {
                client.stop();
            }
        }
    }

    private ClientSession openSession(SshConnectionTarget target, Duration connectTimeout) throws IOException {
        ConnectFuture connectFuture = client.connect(target.username(), target.hostname(), target.port(),
            PinnedHostKeyVerifier.connectionContext(target.expectedHostKeyFingerprint()), null);
        connectFuture.verify(connectTimeout);
        ClientSession session = connectFuture.getSession();
        if (session == null) {
            throw new IOException("SSH connection completed without a client session");
        }
        return session;
    }

    private void authenticateSession(ClientSession session, SshCredentialMaterial credential,
                                     Duration authenticationTimeout) throws IOException, GeneralSecurityException {
        try (InstalledSshCredential installedSshCredential = InstalledSshCredential.install(session, credential)) {
            session.auth().verify(authenticationTimeout);
        }
    }

    /**
     * 在持有会话槽锁的前提下刷新底层连接状态。
     */
    private static void markDisconnectedWhenClosed(SessionSlot slot) {
        ClientSession session = slot.session;
        if (slot.status != SshSessionStatus.CONNECTED) {
            return;
        }
        if (session != null && session.isOpen()) {
            return;
        }

        slot.session = null;
        slot.status = SshSessionStatus.DISCONNECTED;
    }

    private void closeSession(ClientSession session, Duration timeout) {
        if (session == null) {
            return;
        }
        try {
            boolean closedGracefully = session.close(false).await(timeout);
            if (!closedGracefully) {
                forceClose(session);
            }
        } catch (IOException | RuntimeException exception) {
            forceClose(session);
        }
    }

    private static void forceClose(ClientSession session) {
        try {
            session.close(true);
        } catch (RuntimeException ignored) {
            // 即使底层关闭失败，也必须释放注册表持有的会话引用。
        }
    }

    private static void requirePositive(Duration timeout, String parameterName) {
        Objects.requireNonNull(timeout, parameterName + " must not be null");
        if (timeout.isZero() || timeout.isNegative()) {
            throw new IllegalArgumentException(parameterName + " must be positive");
        }
    }

    private SshConnectionResult failed(SessionSlot slot, SshSessionId sessionId, SshConnectionFailure failure,
                                       ClientSession session, Duration closeTimeout) {
        slot.status = SshSessionStatus.FAILED;
        closeSession(session, closeTimeout);
        return new SshConnectionResult.Failed(sessionId, failure);
    }

    private void removeSlotWhenClosed(SshSessionId sessionId, SessionSlot slot) {
        if (closed) {
            sessions.remove(sessionId, slot);
        }
    }

    private static final class SessionSlot {
        private volatile SshSessionStatus status = SshSessionStatus.DISCONNECTED;
        private volatile ClientSession session;
    }
}
