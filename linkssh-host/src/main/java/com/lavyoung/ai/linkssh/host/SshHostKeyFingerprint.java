package com.lavyoung.ai.linkssh.host;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * 表示经过确认的 SSH 主机公钥 SHA-256 指纹。
 *
 * <p>指纹使用 OpenSSH 的规范格式，例如
 * {@code SHA256:AbCdEf0123456789012345678901234567890123456}。
 * 建立 SSH 连接时必须验证服务器实际指纹与该值一致。</p>
 *
 * @param value OpenSSH 格式的 SHA-256 主机公钥指纹
 * @author <a href="mailto:lavyoung1325@outlook.com">lavyoung</a>
 * @since 0.1.0
 */
public record SshHostKeyFingerprint(String value) {

    private static final Pattern SHA256_FINGERPRINT = Pattern.compile("^SHA256:[A-Za-z0-9+/]{43}$");

    /**
     * 创建 SSH 主机公钥指纹。
     *
     * @throws NullPointerException     当指纹为 {@code null} 时抛出
     * @throws IllegalArgumentException 当指纹不是规范的 SHA-256 格式时抛出
     */
    public SshHostKeyFingerprint {
        Objects.requireNonNull(value, "SSH host key fingerprint must not be null");

        if (!SHA256_FINGERPRINT.matcher(value).matches()) {
            throw new IllegalArgumentException(
                "SSH host key fingerprint must use the SHA256:<base64> format"
            );
        }
    }
}
