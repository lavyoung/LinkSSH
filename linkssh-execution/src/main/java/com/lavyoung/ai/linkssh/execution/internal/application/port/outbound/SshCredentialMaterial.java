package com.lavyoung.ai.linkssh.execution.internal.application.port.outbound;

/**
 *
 *
 * @author <a href="mailto:lavyoung1325@outlook.com">lavyoung</a>
 * @version 1.0.0
 * @date 2026/11/17
 */
public sealed interface SshCredentialMaterial extends AutoCloseable permits PasswordCredentialMaterial,
    PrivateKeyCredentialMaterial {

    /**
     * 清除当前对象持有的全部凭据内容。
     *
     * <p>该操作必须具备幂等性，允许重复调用。</p>
     */
    @Override
    void close();
}
