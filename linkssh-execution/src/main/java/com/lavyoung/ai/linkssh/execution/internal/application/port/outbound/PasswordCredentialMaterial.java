package com.lavyoung.ai.linkssh.execution.internal.application.port.outbound;

import java.util.Arrays;
import java.util.Objects;

/**
 * 表示短生命周期的 SSH 密码凭据。
 *
 * <p>构造时会复制调用方提供的字符数组，因此调用方仍然负责清理原始数组。
 * {@link #copyPassword()} 返回的副本也必须由调用方在使用后立即清理。</p>
 *
 * <p>该类型不会把密码转换为 {@link String}，并且不会在文本表示中输出密码。</p>
 *
 * @author <a href="mailto:lavyoung1325@outlook.com">lavyoung</a>
 * @since 0.1.0
 */
public final class PasswordCredentialMaterial implements SshCredentialMaterial {

    private char[] password;

    /**
     * 创建密码凭据材料。
     *
     * @param password SSH 登录密码；构造器会保存其副本
     * @throws NullPointerException 当密码数组为 {@code null} 时抛出
     */
    public PasswordCredentialMaterial(char[] password) {
        this.password = Objects.requireNonNull(
            password,
            "SSH password must not be null"
        ).clone();
    }

    /**
     * 返回密码的临时副本。
     *
     * <p>调用方必须在使用完成后通过 {@link Arrays#fill(char[], char)}
     * 清理返回的数组。</p>
     *
     * @return 密码字符数组的副本
     * @throws IllegalStateException 当凭据材料已经关闭时抛出
     */
    public synchronized char[] copyPassword() {
        ensureOpen();
        return password.clone();
    }

    /**
     * 清除当前对象保存的密码。
     */
    @Override
    public synchronized void close() {
        if (password == null) {
            return;
        }

        Arrays.fill(password, '\0');
        password = null;
    }

    /**
     * 返回不包含密码正文的安全文本表示。
     *
     * @return 脱敏后的类型说明
     */
    @Override
    public String toString() {
        return "PasswordCredentialMaterial[redacted]";
    }

    private void ensureOpen() {
        if (password == null) {
            throw new IllegalStateException("SSH password credential material has already been closed");
        }
    }
}
