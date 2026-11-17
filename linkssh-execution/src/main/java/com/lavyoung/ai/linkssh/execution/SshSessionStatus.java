package com.lavyoung.ai.linkssh.execution;

/**
 * 表示 SSH 会话当前所处的生命周期状态。
 *
 * @author <a href="mailto:lavyoung1325@outlook.com">lavyoung</a>
 * @since 0.1.0
 */
public enum SshSessionStatus {

    /**
     * 正在建立网络连接或进行身份认证。
     */
    CONNECTING,

    /**
     * 已完成连接和认证，可以承载后续受治理的远程操作。
     */
    CONNECTED,

    /**
     * 正在关闭底层连接并释放资源。
     */
    DISCONNECTING,

    /**
     * 会话已经关闭或底层连接已经失效。
     */
    DISCONNECTED,

    /**
     * 最近一次连接或会话维护操作失败。
     */
    FAILED
}
