package com.lavyoung.ai.linkssh.execution.internal.application.port.outbound;

import java.util.Arrays;
import java.util.Objects;

/**
 * 表示短生命周期的 SSH 私钥凭据。
 *
 * <p>私钥和可选口令都使用可清除数组保存。构造方法会复制传入数组，
 * 调用方仍负责清理自己持有的原始数组。</p>
 *
 * @author <a href="mailto:lavyoung1325@outlook.com">lavyoung</a>
 * @since 0.1.0
 */
public final class PrivateKeyCredentialMaterial implements SshCredentialMaterial {

    private byte[] privateKey;
    private char[] passphrase;

    /**
     * 创建不带口令的私钥凭据材料。
     *
     * @param privateKey 私钥文件内容
     * @throws NullPointerException     当私钥数组为 {@code null} 时抛出
     * @throws IllegalArgumentException 当私钥数组为空时抛出
     */
    public PrivateKeyCredentialMaterial(byte[] privateKey) {
        this(privateKey, new char[0]);
    }


    /**
     * 创建带有可选口令的私钥凭据材料。
     *
     * <p>空口令数组表示私钥没有口令。</p>
     *
     * @param privateKey 私钥文件内容
     * @param passphrase 私钥口令；没有口令时传入空数组
     * @throws NullPointerException     当任一数组为 {@code null} 时抛出
     * @throws IllegalArgumentException 当私钥数组为空时抛出
     */
    public PrivateKeyCredentialMaterial(byte[] privateKey, char[] passphrase) {
        Objects.requireNonNull(privateKey, "SSH private key must not be null");
        Objects.requireNonNull(passphrase, "SSH private key passphrase must not be null");
        if (privateKey.length == 0) {
            throw new IllegalArgumentException("SSH private key must not be empty");
        }
        this.privateKey = privateKey.clone();
        this.passphrase = passphrase.clone();
    }

    /**
     * 返回私钥内容的临时副本。
     *
     * <p>调用方使用完成后必须通过 {@link Arrays#fill(byte[], byte)}
     * 清除返回的副本。</p>
     *
     * @return 私钥字节数组副本
     * @throws IllegalStateException 当凭据材料已经关闭时抛出
     */
    public synchronized byte[] copyPrivateKey() {
        ensureOpen();
        return privateKey.clone();
    }

    /**
     * 返回私钥内容的临时副本。
     *
     * <p>调用方使用完成后必须通过 {@link Arrays#fill(byte[], byte)}
     * 清除返回的副本。</p>
     *
     * @return 私钥字节数组副本
     * @throws IllegalStateException 当凭据材料已经关闭时抛出
     */
    public synchronized boolean hasPassphrase() {
        ensureOpen();
        return passphrase.length > 0;
    }

    /**
     * 返回私钥口令的临时副本。
     *
     * <p>没有口令时返回空数组。调用方使用完成后仍应清除该数组。</p>
     *
     * @return 私钥口令字符数组副本
     * @throws IllegalStateException 当凭据材料已经关闭时抛出
     */
    public synchronized char[] copyPassphrase() {
        ensureOpen();
        return passphrase.clone();
    }

    @Override
    public void close() {
        if (privateKey != null) {
            Arrays.fill(privateKey, (byte) 0);
            privateKey = null;
        }

        if (passphrase != null) {
            Arrays.fill(passphrase, '\0');
            passphrase = null;
        }
    }

    /**
     * 返回不包含私钥或口令正文的安全描述。
     *
     * @return 脱敏后的类型描述
     */
    @Override
    public String toString() {
        return "PrivateKeyCredentialMaterial[redacted]";
    }

    private void ensureOpen() {
        if (privateKey == null || passphrase == null) {
            throw new IllegalStateException("SSH private key credential material has already been closed");
        }
    }
}
