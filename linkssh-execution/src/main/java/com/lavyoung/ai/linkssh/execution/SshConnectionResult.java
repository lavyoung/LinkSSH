package com.lavyoung.ai.linkssh.execution;


import java.util.Objects;

/**
 * 表示一次 SSH 连接尝试的结果。
 *
 * <p>连接结果使用密封类型区分成功与失败，避免通过布尔值和可空失败原因
 * 表达互斥状态。</p>
 *
 * @author <a href="mailto:lavyoung1325@outlook.com">lavyoung</a>
 * @since 0.1.0
 */
public sealed interface SshConnectionResult permits SshConnectionResult.Connected,
    SshConnectionResult.Failed {

    /**
     * 返回本次连接对应的会话标识。
     *
     * @return SSH 会话标识
     */
    SshSessionId sessionId();

    /**
     * 表示 SSH 会话已经成功连接并完成身份认证。
     *
     * @param sessionId 已建立连接的会话标识
     */
    record Connected(SshSessionId sessionId) implements SshConnectionResult {
        /**
         * 创建成功连接结果。
         *
         * @throws NullPointerException 当会话标识为 {@code null} 时抛出
         */
        public Connected {
            Objects.requireNonNull(sessionId, "SSH session id must not be null");
        }
    }

    /**
     * 表示 SSH 会话连接失败。
     *
     * @param sessionId 连接尝试对应的会话标识
     * @param failure   稳定的失败分类
     */
    record Failed(SshSessionId sessionId, SshConnectionFailure failure) implements SshConnectionResult {
        /**
         * 创建连接失败结果。
         *
         * @throws NullPointerException 当会话标识或失败分类为 {@code null} 时抛出
         */
        public Failed {
            Objects.requireNonNull(sessionId, "SSH session id must not be null");
            Objects.requireNonNull(failure, "SSH connection failure must not be null");
        }
    }
}
