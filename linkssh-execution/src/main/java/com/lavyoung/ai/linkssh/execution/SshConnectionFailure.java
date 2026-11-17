package com.lavyoung.ai.linkssh.execution;

/**
 * 表示 SSH 连接失败的稳定业务分类。
 *
 * <p>该分类用于隔离底层 SSH 客户端异常。外部调用方不应接触 Apache MINA
 * SSHD 的异常类型，也不能接收可能包含敏感信息的原始异常消息。</p>
 *
 * @author <a href="mailto:lavyoung1325@outlook.com">lavyoung</a>
 * @since 0.1.0
 */
public enum SshConnectionFailure {

    /**
     * 无法按凭据引用取得可用凭据。
     */
    CREDENTIAL_UNAVAILABLE,

    /**
     * 服务器拒绝了提供的登录凭据。
     */
    AUTHENTICATION_FAILED,

    /**
     * 服务器实际主机密钥指纹与预期指纹不一致。
     */
    HOST_KEY_MISMATCH,

    /**
     * 网络连接或身份认证在规定时间内没有完成。
     */
    CONNECTION_TIMEOUT,

    /**
     * 目标地址无法解析、网络不可达或服务器拒绝连接。
     */
    HOST_UNREACHABLE,

    /**
     * SSH 客户端尚未启动或当前不可用于建立连接。
     */
    CLIENT_UNAVAILABLE,

    /**
     * 无法归入其他稳定分类的非预期失败。
     */
    UNEXPECTED_FAILURE
}
