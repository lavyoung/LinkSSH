package com.lavyoung.ai.linkssh.execution.internal.application.port.outbound;

import com.lavyoung.ai.linkssh.host.CredentialReference;

import java.util.Optional;

/**
 * 定义按凭据引用临时解析 SSH 凭据材料的出站端口。
 *
 * <p>具体适配器以后可以从操作系统密钥链、受控凭据存储或测试存储中读取凭据。
 * 该端口不得返回密码字符串、私钥路径或未受生命周期控制的原始对象。</p>
 *
 * @author <a href="mailto:lavyoung1325@outlook.com">lavyoung</a>
 * @since 0.1.0
 */
public interface SshCredentialResolverPort {

    /**
     * 按引用解析一份新的短生命周期凭据材料。
     *
     * <p>每次成功调用都必须返回由调用方独占的新对象。调用方必须在使用完成后
     * 调用 {@link SshCredentialMaterial#close()}，解析器不得缓存凭据正文。</p>
     *
     * @param reference 需要解析的凭据引用
     * @return 可用的凭据材料；引用不存在或当前无法取得凭据时返回空结果
     * @throws NullPointerException 当凭据引用为 {@code null} 时抛出
     */
    Optional<SshCredentialMaterial> resolve(CredentialReference reference);
}
