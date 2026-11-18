package com.lavyoung.ai.linkssh.execution.internal.infrastructure.ssh;

import com.lavyoung.ai.linkssh.host.SshHostKeyFingerprint;
import org.apache.sshd.client.keyverifier.ServerKeyVerifier;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.common.AttributeRepository;
import org.apache.sshd.common.config.keys.KeyUtils;
import org.apache.sshd.common.digest.BuiltinDigests;

import java.net.SocketAddress;
import java.security.PublicKey;
import java.util.Objects;

/**
 * 使用连接上下文中预先固定的 SHA-256 指纹验证 SSH 服务器主机密钥。
 *
 * <p>该校验器作为 {@code SshClient} 的全局校验器使用。每次连接的预期指纹
 * 通过 {@link #connectionContext(SshHostKeyFingerprint)} 放入独立的连接上下文，
 * 从而避免并发连接不同主机时修改或共享可变的全局指纹状态。</p>
 *
 * <p>连接上下文、预期指纹或服务器公钥缺失时一律拒绝连接，不提供接受所有
 * 主机密钥的降级路径。</p>
 *
 * @author <a href="mailto:lavyoung1325@outlook.com">lavyoung</a>
 * @since 0.1.0
 */
final class PinnedHostKeyVerifier implements ServerKeyVerifier {

    private static final AttributeRepository.AttributeKey<SshHostKeyFingerprint> EXPECTED_FINGERPRINT = new AttributeRepository.AttributeKey<>();

    /**
     * 创建只包含本次连接预期主机指纹的连接上下文。
     *
     * @param expectedFingerprint 连接时必须匹配的主机公钥指纹
     * @return 可传递给 Apache MINA SSHD 连接方法的只读属性仓库
     * @throws NullPointerException 当预期指纹为 {@code null} 时抛出
     */
    static AttributeRepository connectionContext(SshHostKeyFingerprint expectedFingerprint) {
        Objects.requireNonNull(expectedFingerprint, "Expected SSH host key fingerprint must not be null");
        return AttributeRepository.ofKeyValuePair(EXPECTED_FINGERPRINT, expectedFingerprint);
    }

    /**
     * 验证服务器提供的公钥是否与本次连接固定的指纹一致。
     *
     * @param clientSession 正在进行密钥交换的客户端会话
     * @param remoteAddress 服务器网络地址；服务器身份不以地址本身作为信任依据
     * @param serverKey     服务器在密钥交换过程中提供的公钥
     * @return 指纹完全一致时返回 {@code true}；缺少信息或不一致时返回 {@code false}
     */
    @Override
    public boolean verifyServerKey(ClientSession clientSession, SocketAddress remoteAddress, PublicKey serverKey) {
        if (clientSession == null || serverKey == null) {
            return false;
        }
        AttributeRepository connectionContext = clientSession.getConnectionContext();
        if (connectionContext == null) {
            return false;
        }
        SshHostKeyFingerprint expectedFingerprint = connectionContext.getAttribute(EXPECTED_FINGERPRINT);
        if (expectedFingerprint == null) {
            return false;
        }
        String fingerPrint = KeyUtils.getFingerPrint(BuiltinDigests.sha256, serverKey);
        return expectedFingerprint.value().equals(fingerPrint);
    }
}
