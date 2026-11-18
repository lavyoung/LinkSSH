package com.lavyoung.ai.linkssh.execution.internal.infrastructure.ssh;

import com.lavyoung.ai.linkssh.execution.internal.application.port.outbound.PasswordCredentialMaterial;
import com.lavyoung.ai.linkssh.execution.internal.application.port.outbound.PrivateKeyCredentialMaterial;
import com.lavyoung.ai.linkssh.execution.internal.application.port.outbound.SshCredentialMaterial;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.common.NamedResource;
import org.apache.sshd.common.config.keys.FilePasswordProvider;
import org.apache.sshd.common.util.security.SecurityUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 表示临时安装到 Apache MINA 客户端会话中的认证身份。
 *
 * <p>密码或私钥身份只在身份认证期间安装。调用方必须使用
 * try-with-resources，并在认证完成或失败后立即关闭该对象，从会话中移除
 * 已安装的身份。</p>
 *
 * <p>该类型不负责关闭原始 {@link SshCredentialMaterial}。调用方仍须单独
 * 关闭凭据材料，清除其中保存的字符和字节数组。</p>
 *
 * @author <a href="mailto:lavyoung1325@outlook.com">lavyoung</a>
 * @since 0.1.0
 */
final class InstalledSshCredential implements AutoCloseable {

    private static final NamedResource IN_MEMORY_PRIVATE_KEY = NamedResource.ofName("in-memory-private-key");

    private ClientSession session;
    private String passwordIdentity;
    private List<KeyPair> publicKeyIdentities;

    private InstalledSshCredential(ClientSession session, String passwordIdentity, List<KeyPair> publicKeyIdentities) {
        this.session = session;
        this.passwordIdentity = passwordIdentity;
        this.publicKeyIdentities = publicKeyIdentities;
    }

    /**
     * 将凭据材料临时安装到指定客户端会话。
     *
     * @param session            需要完成身份认证的客户端会话
     * @param credentialMaterial 短生命周期凭据材料
     * @return 可在认证结束后移除身份的关闭句柄
     * @throws NullPointerException     当任一参数为 {@code null} 时抛出
     * @throws IOException              当私钥内容无法读取时抛出
     * @throws GeneralSecurityException 当私钥无法解析或解密时抛出
     */
    static InstalledSshCredential install(
            ClientSession session,
            SshCredentialMaterial credentialMaterial
    ) throws IOException, GeneralSecurityException {
        Objects.requireNonNull(session, "SSH client session must not be null");
        Objects.requireNonNull(credentialMaterial, "SSH credential material must not be null");
        return switch (credentialMaterial) {
            case PasswordCredentialMaterial password -> installPassword(session, password);
            case PrivateKeyCredentialMaterial privateKey -> installPrivateKey(session, privateKey);
        };
    }

    private static InstalledSshCredential installPassword(ClientSession session,
                                                          PasswordCredentialMaterial credentialMaterial) {
        char[] password = credentialMaterial.copyPassword();
        try {
            /*
             * Apache MINA 的密码认证 API 只接受 String。这里将不可清除 String
             * 的生命周期限制在认证阶段，并在 close() 时从会话中移除引用。
             */
            String passwordIdentity = new String(password);
            session.addPasswordIdentity(passwordIdentity);
            return new InstalledSshCredential(session, passwordIdentity, List.of());
        } finally {
            Arrays.fill(password, '\0');
        }
    }

    private static InstalledSshCredential installPrivateKey(ClientSession session,
                                                            PrivateKeyCredentialMaterial credentialMaterial)
        throws IOException, GeneralSecurityException {
        byte[] privateKey = credentialMaterial.copyPrivateKey();
        char[] passphrase = credentialMaterial.copyPassphrase();

        try (ByteArrayInputStream input = new ByteArrayInputStream(privateKey)) {
            FilePasswordProvider passwordProvider = passphrase.length == 0 ?
                FilePasswordProvider.EMPTY : FilePasswordProvider.of(new String(passphrase));

            Iterable<KeyPair> decodedIdentities = SecurityUtils.loadKeyPairIdentities(session,
                IN_MEMORY_PRIVATE_KEY, input, passwordProvider);
            if (decodedIdentities == null) {
                throw new InvalidKeyException("No SSH private key identity could be decoded");
            }

            List<KeyPair> installedIdentities = new ArrayList<>();
            try {
                for (KeyPair identity : decodedIdentities) {
                    session.addPublicKeyIdentity(identity);
                    installedIdentities.add(identity);
                }
                if (installedIdentities.isEmpty()) {
                    throw new InvalidKeyException("No SSH private key identity could be decoded");
                }
                return new InstalledSshCredential(session, null, List.copyOf(installedIdentities));
            } catch (GeneralSecurityException | RuntimeException exception) {
                installedIdentities.forEach(session::removePublicKeyIdentity);
                throw exception;
            }
        } finally {
            Arrays.fill(privateKey, (byte) 0);
            Arrays.fill(passphrase, '\0');
        }
    }


    /**
     * 从客户端会话中移除临时安装的密码或私钥身份。
     *
     * <p>该操作具备幂等性，允许重复调用。</p>
     */
    @Override
    public synchronized void close() {
        if (session == null) {
            return;
        }
        if (passwordIdentity != null) {
            session.removePasswordIdentity(passwordIdentity);
        }
        publicKeyIdentities.forEach(session::removePublicKeyIdentity);
        passwordIdentity = null;
        publicKeyIdentities = List.of();
        session = null;
    }

    /**
     * 返回不包含认证身份内容的安全描述。
     *
     * @return 脱敏后的认证身份描述
     */
    @Override
    public String toString() {
        return "InstalledSshCredential[redacted]";
    }

}
