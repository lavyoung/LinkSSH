package com.lavyoung.ai.linkssh.host;

import java.util.Objects;

/**
 * 描述建立 SSH 连接所需的安全目标信息。
 *
 * <p>该契约只包含目标地址、用户名、凭据引用和期望的主机指纹，
 * 不包含密码、私钥正文，也不暴露任何具体 SSH 客户端类型。</p>
 *
 * @param hostId                     主机标识
 * @param hostname                   主机名、IPv4 地址或 IPv6 地址
 * @param port                       SSH 服务端口
 * @param username                   SSH 登录用户名
 * @param credentialReference        登录所需的凭据引用
 * @param expectedHostKeyFingerprint 连接时必须匹配的主机公钥指纹
 * @author <a href="mailto:lavyoung1325@outlook.com">lavyoung</a>
 * @since 0.1.0
 */
public record SshConnectionTarget(
    HostId hostId,
    String hostname,
    int port,
    String username,
    CredentialReference credentialReference,
    SshHostKeyFingerprint expectedHostKeyFingerprint
) {

    /**
     * 创建 SSH 连接目标。
     *
     * @throws NullPointerException     当必需的对象或文本参数为 {@code null} 时抛出
     * @throws IllegalArgumentException 当主机名或用户名为空，或者端口超出有效范围时抛出
     */
    public SshConnectionTarget {
        Objects.requireNonNull(hostId, "Host id must not be null");
        Objects.requireNonNull(hostname, "Hostname must not be null");
        Objects.requireNonNull(username, "Username must not be null");
        Objects.requireNonNull(credentialReference, "Credential reference must not be null");
        Objects.requireNonNull(expectedHostKeyFingerprint, "Expected SSH host key fingerprint must not be null");

        hostname = hostname.strip();
        username = username.strip();

        if (hostname.isEmpty()) {
            throw new IllegalArgumentException("Hostname must not be blank");
        }

        if (port < 1 || port > 65_535) {
            throw new IllegalArgumentException("SSH port must be between 1 and 65535");
        }

        if (username.isEmpty()) {
            throw new IllegalArgumentException("Username must not be blank");
        }
    }
}
