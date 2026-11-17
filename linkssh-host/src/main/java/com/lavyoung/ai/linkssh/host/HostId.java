package com.lavyoung.ai.linkssh.host;

import java.util.Objects;
import java.util.UUID;

/**
 * 标识 LinkSSH 中的一台主机。
 *
 * <p>该标识独立于主机地址。即使主机名、IP 地址或端口发生变化，
 * 已存在主机的身份仍然保持不变。</p>
 *
 * @param value 主机的全局唯一标识
 * @author <a href="mailto:lavyoung1325@outlook.com">lavyoung</a>
 * @since 0.1.0
 */
public record HostId(UUID value) {

    /**
     * 创建主机标识。
     *
     * @throws NullPointerException 当标识值为 {@code null} 时抛出
     */
    public HostId {
        Objects.requireNonNull(value, "Host id must not be null");
    }

    /**
     * 生成一个新的主机标识。
     *
     * @return 新生成的主机标识
     */
    public static HostId newId() {
        return new HostId(UUID.randomUUID());
    }
}
