package com.lavyoung.ai.linkssh.execution.internal.infrastructure.ssh;

import com.lavyoung.ai.linkssh.execution.SshConnectionFailure;
import com.lavyoung.ai.linkssh.execution.SshConnectionResult;
import com.lavyoung.ai.linkssh.execution.SshSessionId;
import com.lavyoung.ai.linkssh.execution.SshSessionStatus;
import com.lavyoung.ai.linkssh.execution.internal.application.port.outbound.PasswordCredentialMaterial;
import com.lavyoung.ai.linkssh.execution.internal.application.port.outbound.SshCredentialResolverPort;
import com.lavyoung.ai.linkssh.host.CredentialReference;
import com.lavyoung.ai.linkssh.host.HostId;
import com.lavyoung.ai.linkssh.host.SshConnectionTarget;
import com.lavyoung.ai.linkssh.host.SshHostKeyFingerprint;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.common.config.keys.KeyUtils;
import org.apache.sshd.common.digest.BuiltinDigests;
import org.apache.sshd.common.keyprovider.KeyPairProvider;
import org.apache.sshd.common.session.Session;
import org.apache.sshd.common.session.SessionListener;
import org.apache.sshd.server.SshServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.time.Duration;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 使用进程内 Apache MINA SSH Server 验证 SSH 会话适配器的真实协议链路。
 *
 * <p>测试只建立和认证会话，不执行远程命令。主机密钥与凭据均在测试运行时
 * 生成或创建，不读取个人 SSH 配置，也不依赖真实服务器。</p>
 *
 * @author <a href="mailto:lavyoung1325@outlook.com">lavyoung</a>
 * @since 0.1.0
 */
class ApacheMinaSshSessionAdapterIntegrationTest {

    private static final String TEST_USERNAME = "linkssh-test-user";
    private static final String TEST_PASSWORD = "linkssh-test-password";
    private static final Duration TEST_TIMEOUT = Duration.ofSeconds(5);

    private static ScheduledExecutorService serverExecutor;

    private SshServer server;
    private KeyPair serverHostKey;
    private ApacheMinaSshSessionAdapter adapter;

    @BeforeAll
    static void createServerExecutor() {
        serverExecutor = Executors.newScheduledThreadPool(2);
    }

    @AfterAll
    static void stopServerExecutor() throws InterruptedException {
        serverExecutor.shutdown();
        assertTrue(serverExecutor.awaitTermination(2, TimeUnit.SECONDS));
    }

    @BeforeEach
    void startServer() throws Exception {
        serverHostKey = generateRsaKeyPair();
        server = SshServer.setUpDefaultServer();
        server.setHost("127.0.0.1");
        server.setPort(0);
        server.setScheduledExecutorService(serverExecutor, false);
        server.setKeyPairProvider(KeyPairProvider.wrap(serverHostKey));
        server.setPasswordAuthenticator(
                (username, password, session) -> TEST_USERNAME.equals(username)
                        && TEST_PASSWORD.equals(password)
        );
        server.start();
    }

    @AfterEach
    void stopResources() throws Exception {
        if (adapter != null) {
            adapter.close();
        }
        if (server != null && server.isStarted()) {
            server.stop(false);
        }
    }

    @Test
    void shouldConnectQueryStatusAndDisconnect() {
        CredentialReference credentialReference = credentialReference();
        adapter = startedAdapter(passwordResolver(
                credentialReference,
                TEST_PASSWORD
        ));
        SshSessionId sessionId = SshSessionId.newId();

        SshConnectionResult result = adapter.connect(
                sessionId,
                target(credentialReference, fingerprintOf(serverHostKey)),
                TEST_TIMEOUT,
                TEST_TIMEOUT
        );

        assertInstanceOf(SshConnectionResult.Connected.class, result);
        assertEquals(
                Optional.of(SshSessionStatus.CONNECTED),
                adapter.findStatus(sessionId)
        );

        adapter.disconnect(sessionId, TEST_TIMEOUT);

        assertEquals(Optional.empty(), adapter.findStatus(sessionId));
    }

    @Test
    void shouldRejectIncorrectPassword() {
        CredentialReference credentialReference = credentialReference();
        adapter = startedAdapter(passwordResolver(
                credentialReference,
                "incorrect-test-password"
        ));
        SshSessionId sessionId = SshSessionId.newId();

        SshConnectionResult.Failed result = assertInstanceOf(
                SshConnectionResult.Failed.class,
                adapter.connect(
                        sessionId,
                        target(credentialReference, fingerprintOf(serverHostKey)),
                        TEST_TIMEOUT,
                        TEST_TIMEOUT
                )
        );

        assertEquals(SshConnectionFailure.AUTHENTICATION_FAILED, result.failure());
        assertEquals(
                Optional.of(SshSessionStatus.FAILED),
                adapter.findStatus(sessionId)
        );
    }

    @Test
    void shouldRejectMismatchedHostKeyFingerprint() throws GeneralSecurityException {
        CredentialReference credentialReference = credentialReference();
        adapter = startedAdapter(passwordResolver(
                credentialReference,
                TEST_PASSWORD
        ));
        SshSessionId sessionId = SshSessionId.newId();
        SshHostKeyFingerprint unexpectedFingerprint =
                fingerprintOf(generateRsaKeyPair());

        SshConnectionResult.Failed result = assertInstanceOf(
                SshConnectionResult.Failed.class,
                adapter.connect(
                        sessionId,
                        target(credentialReference, unexpectedFingerprint),
                        TEST_TIMEOUT,
                        TEST_TIMEOUT
                )
        );

        assertEquals(SshConnectionFailure.HOST_KEY_MISMATCH, result.failure());
    }

    @Test
    void shouldRejectUnavailableCredentialAndCloseCandidateSession() {
        CredentialReference credentialReference = credentialReference();
        adapter = startedAdapter(reference -> Optional.empty());
        SshSessionId sessionId = SshSessionId.newId();

        SshConnectionResult.Failed result = assertInstanceOf(
                SshConnectionResult.Failed.class,
                adapter.connect(
                        sessionId,
                        target(credentialReference, fingerprintOf(serverHostKey)),
                        TEST_TIMEOUT,
                        TEST_TIMEOUT
                )
        );

        assertEquals(SshConnectionFailure.CREDENTIAL_UNAVAILABLE, result.failure());
        assertEquals(
                Optional.of(SshSessionStatus.FAILED),
                adapter.findStatus(sessionId)
        );
    }

    @Test
    void shouldClassifyAuthenticationTimeoutAndCloseCandidateSession() throws Exception {
        CountDownLatch authenticationStarted = new CountDownLatch(1);
        CountDownLatch releaseAuthentication = new CountDownLatch(1);
        CountDownLatch authenticationCompleted = new CountDownLatch(1);
        server.setPasswordAuthenticator((username, password, session) -> {
            try {
                authenticationStarted.countDown();
                releaseAuthentication.await(2, TimeUnit.SECONDS);
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
                return false;
            } finally {
                authenticationCompleted.countDown();
            }
            return true;
        });
        CredentialReference credentialReference = credentialReference();
        adapter = startedAdapter(passwordResolver(
                credentialReference,
                TEST_PASSWORD
        ));
        SshSessionId sessionId = SshSessionId.newId();

        SshConnectionResult.Failed result = assertInstanceOf(
                SshConnectionResult.Failed.class,
                adapter.connect(
                        sessionId,
                        target(credentialReference, fingerprintOf(serverHostKey)),
                        TEST_TIMEOUT,
                        Duration.ofMillis(100)
                )
        );

        assertEquals(SshConnectionFailure.CONNECTION_TIMEOUT, result.failure());
        assertEquals(
                Optional.of(SshSessionStatus.FAILED),
                adapter.findStatus(sessionId)
        );
        assertTrue(authenticationStarted.await(1, TimeUnit.SECONDS));

        releaseAuthentication.countDown();

        assertTrue(authenticationCompleted.await(1, TimeUnit.SECONDS));
    }

    @Test
    void shouldReleasePreviousConnectionWhenReconnectingSameSessionId() throws Exception {
        CountDownLatch closedSession = new CountDownLatch(1);
        server.addSessionListener(new SessionListener() {
            @Override
            public void sessionClosed(Session session) {
                closedSession.countDown();
            }
        });
        CredentialReference credentialReference = credentialReference();
        adapter = startedAdapter(passwordResolver(
                credentialReference,
                TEST_PASSWORD
        ));
        SshSessionId sessionId = SshSessionId.newId();
        SshConnectionTarget target = target(
                credentialReference,
                fingerprintOf(serverHostKey)
        );

        assertInstanceOf(
                SshConnectionResult.Connected.class,
                adapter.connect(sessionId, target, TEST_TIMEOUT, TEST_TIMEOUT)
        );
        assertInstanceOf(
                SshConnectionResult.Connected.class,
                adapter.connect(sessionId, target, TEST_TIMEOUT, TEST_TIMEOUT)
        );

        assertTrue(closedSession.await(2, TimeUnit.SECONDS));
        assertEquals(
                Optional.of(SshSessionStatus.CONNECTED),
                adapter.findStatus(sessionId)
        );
    }

    @Test
    void shouldCloseRegisteredSessionsAndStopClient() {
        CredentialReference credentialReference = credentialReference();
        SshClient client = ApacheMinaSshClientFactory.create();
        adapter = new ApacheMinaSshSessionAdapter(
                client,
                passwordResolver(credentialReference, TEST_PASSWORD)
        );
        adapter.start();
        SshSessionId sessionId = SshSessionId.newId();

        assertInstanceOf(
                SshConnectionResult.Connected.class,
                adapter.connect(
                        sessionId,
                        target(credentialReference, fingerprintOf(serverHostKey)),
                        TEST_TIMEOUT,
                        TEST_TIMEOUT
                )
        );

        adapter.close();

        assertFalse(client.isStarted());
        assertEquals(Optional.empty(), adapter.findStatus(sessionId));
    }

    @Test
    void shouldAllowRepeatedDisconnect() {
        CredentialReference credentialReference = credentialReference();
        adapter = startedAdapter(passwordResolver(
                credentialReference,
                TEST_PASSWORD
        ));
        SshSessionId sessionId = SshSessionId.newId();

        assertInstanceOf(
                SshConnectionResult.Connected.class,
                adapter.connect(
                        sessionId,
                        target(credentialReference, fingerprintOf(serverHostKey)),
                        TEST_TIMEOUT,
                        TEST_TIMEOUT
                )
        );

        adapter.disconnect(sessionId, TEST_TIMEOUT);

        assertDoesNotThrow(() -> adapter.disconnect(sessionId, TEST_TIMEOUT));
        assertEquals(Optional.empty(), adapter.findStatus(sessionId));
    }

    @Test
    void shouldRetryWithCurrentSlotWhenPreviousSlotIsRemovedWhileReconnectWaits()
            throws Exception {
        CredentialReference credentialReference = credentialReference();
        AtomicBoolean credentialAvailable = new AtomicBoolean(false);
        SshCredentialResolverPort resolver = reference -> {
            if (!credentialReference.equals(reference)
                    || !credentialAvailable.get()) {
                return Optional.empty();
            }
            return Optional.of(
                    new PasswordCredentialMaterial(
                            TEST_PASSWORD.toCharArray()
                    )
            );
        };
        adapter = startedAdapter(resolver);
        SshSessionId sessionId = SshSessionId.newId();
        SshConnectionTarget target = target(
                credentialReference,
                fingerprintOf(serverHostKey)
        );

        assertInstanceOf(
                SshConnectionResult.Failed.class,
                adapter.connect(
                        sessionId,
                        target,
                        TEST_TIMEOUT,
                        TEST_TIMEOUT
                )
        );

        ConcurrentMap<SshSessionId, Object> sessionSlots =
                sessionSlots(adapter);
        Object previousSlot = sessionSlots.get(sessionId);
        assertTrue(previousSlot != null);
        credentialAvailable.set(true);

        AtomicReference<Thread> reconnectThread = new AtomicReference<>();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        try {
            Future<SshConnectionResult> reconnectResult;
            synchronized (previousSlot) {
                reconnectResult = executor.submit(() -> {
                    reconnectThread.set(Thread.currentThread());
                    return adapter.connect(
                            sessionId,
                            target,
                            TEST_TIMEOUT,
                            TEST_TIMEOUT
                    );
                });

                assertTrue(awaitThreadState(
                        reconnectThread,
                        Thread.State.BLOCKED,
                        Duration.ofSeconds(2)
                ));
                assertTrue(sessionSlots.remove(sessionId, previousSlot));
            }

            assertInstanceOf(
                    SshConnectionResult.Connected.class,
                    reconnectResult.get(5, TimeUnit.SECONDS)
            );
            assertEquals(
                    Optional.of(SshSessionStatus.CONNECTED),
                    adapter.findStatus(sessionId)
            );
        } finally {
            executor.shutdownNow();
            assertTrue(executor.awaitTermination(2, TimeUnit.SECONDS));
        }
    }

    private ApacheMinaSshSessionAdapter startedAdapter(
            SshCredentialResolverPort resolver
    ) {
        ApacheMinaSshSessionAdapter sessionAdapter =
                new ApacheMinaSshSessionAdapter(resolver);
        sessionAdapter.start();
        return sessionAdapter;
    }

    private SshConnectionTarget target(
            CredentialReference credentialReference,
            SshHostKeyFingerprint fingerprint
    ) {
        return new SshConnectionTarget(
                new HostId(UUID.randomUUID()),
                "127.0.0.1",
                server.getPort(),
                TEST_USERNAME,
                credentialReference,
                fingerprint
        );
    }

    private static SshCredentialResolverPort passwordResolver(
            CredentialReference expectedReference,
            String password
    ) {
        return reference -> expectedReference.equals(reference)
                ? Optional.of(
                        new PasswordCredentialMaterial(password.toCharArray())
                )
                : Optional.empty();
    }

    private static CredentialReference credentialReference() {
        return new CredentialReference(UUID.randomUUID());
    }

    private static SshHostKeyFingerprint fingerprintOf(KeyPair keyPair) {
        return new SshHostKeyFingerprint(
                KeyUtils.getFingerPrint(
                        BuiltinDigests.sha256,
                        keyPair.getPublic()
                )
        );
    }

    @SuppressWarnings("unchecked")
    private static ConcurrentMap<SshSessionId, Object> sessionSlots(
            ApacheMinaSshSessionAdapter sessionAdapter
    ) throws ReflectiveOperationException {
        Field sessionsField = ApacheMinaSshSessionAdapter.class
                .getDeclaredField("sessions");
        sessionsField.setAccessible(true);
        return (ConcurrentMap<SshSessionId, Object>) sessionsField.get(
                sessionAdapter
        );
    }

    private static boolean awaitThreadState(
            AtomicReference<Thread> threadReference,
            Thread.State expectedState,
            Duration timeout
    ) throws InterruptedException {
        long deadline = System.nanoTime() + timeout.toNanos();
        while (System.nanoTime() < deadline) {
            Thread thread = threadReference.get();
            if (thread != null && thread.getState() == expectedState) {
                return true;
            }
            Thread.sleep(10);
        }
        return false;
    }

    private static KeyPair generateRsaKeyPair()
            throws GeneralSecurityException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        return generator.generateKeyPair();
    }
}
