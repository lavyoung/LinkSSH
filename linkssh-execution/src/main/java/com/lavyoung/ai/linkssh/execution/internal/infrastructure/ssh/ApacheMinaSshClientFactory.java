package com.lavyoung.ai.linkssh.execution.internal.infrastructure.ssh;

import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.auth.keyboard.UserInteraction;
import org.apache.sshd.client.auth.password.PasswordIdentityProvider;
import org.apache.sshd.client.config.hosts.HostConfigEntryResolver;
import org.apache.sshd.common.keyprovider.KeyIdentityProvider;

/**
 * 创建满足 LinkSSH 安全基线的 Apache MINA SSH 客户端。
 *
 * <p>客户端不读取用户目录中的 SSH 配置或默认私钥，不允许交互式认证，
 * 并且强制使用固定主机指纹校验器。每次连接所需的凭据必须由 Execution
 * 上下文按凭据引用显式提供。</p>
 *
 * <p>该工厂只负责客户端配置，不负责调用 {@link SshClient#start()} 或
 * {@link SshClient#stop()}。客户端生命周期由后续的会话适配器统一管理。</p>
 *
 * @author <a href="mailto:lavyoung1325@outlook.com">lavyoung</a>
 * @since 0.1.0
 */
final class ApacheMinaSshClientFactory {

    private ApacheMinaSshClientFactory() {}

    /**
     * 创建尚未启动且已经应用安全配置的 SSH 客户端。
     *
     * @return 配置完成但尚未启动的 SSH 客户端
     */
    static SshClient create() {
        SshClient client = SshClient.setUpDefaultClient();
        // 拒绝指纹不匹配的服务器
        client.setServerKeyVerifier(new PinnedHostKeyVerifier());
        // 不隐式读取个人 ~/.ssh/config
        client.setHostConfigEntryResolver(HostConfigEntryResolver.EMPTY);
        // 不自动尝试个人默认密码或私钥
        client.setKeyIdentityProvider(KeyIdentityProvider.EMPTY_KEYS_PROVIDER);
        client.setPasswordIdentityProvider(PasswordIdentityProvider.EMPTY_PASSWORDS_PROVIDER);
        // 禁止底层库弹出或等待交互式认证
        client.setUserInteraction(UserInteraction.NONE);
        return client;
    }
}
