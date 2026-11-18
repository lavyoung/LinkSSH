package com.lavyoung.ai.linkssh.execution.internal.application.port.outbound;

/**
 * 表示建立 SSH 连接时临时使用的凭据材料。
 *
 * <p>凭据材料必须保持短生命周期。取得该对象的调用方必须使用
 * try-with-resources，并在完成身份认证后立即调用 {@link #close()} 清理内容。</p>
 *
 * <p>实现不得在 {@code toString()}、日志、异常消息或审计正文中暴露凭据。</p>
 *
 * @author <a href="mailto:lavyoung1325@outlook.com">lavyoung</a>
 * @since 0.1.0
 */
public sealed interface SshCredentialMaterial extends AutoCloseable
    permits PasswordCredentialMaterial, PrivateKeyCredentialMaterial {

    /**
     * 清除当前对象持有的全部凭据内容。
     *
     * <p>该操作必须具备幂等性，允许重复调用。</p>
     */
    @Override
    void close();
}
