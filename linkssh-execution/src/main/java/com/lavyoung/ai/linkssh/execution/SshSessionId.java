package com.lavyoung.ai.linkssh.execution;

import java.util.Objects;
import java.util.UUID;

/**
 * 标识一次独立的 SSH 会话。
 *
 * <p>同一台主机可以建立多个 SSH 会话，因此会话标识不能直接使用主机标识。
 * 该标识也用于连接替换、断开和状态查询。</p>
 *
 * @param value 会话的全局唯一标识
 * @author <a href="mailto:lavyoung1325@outlook.com">lavyoung</a>
 * @since 0.1.0
 */
public record SshSessionId(UUID value) {

    /**
     * 创建 SSH 会话标识。
     *
     * @throws NullPointerException 当标识值为 {@code null} 时抛出
     */
    public SshSessionId {
        Objects.requireNonNull(value, "SSH session id must not be null");
    }

    /**
     * 生成一个新的 SSH 会话标识。
     *
     * @return 新生成的会话标识
     */
    public static SshSessionId newId() {
        return new SshSessionId(UUID.randomUUID());
    }
}
