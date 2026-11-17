package com.lavyoung.ai.linkssh.execution.internal.application.port.outbound;

import com.lavyoung.ai.linkssh.execution.SshConnectionResult;
import com.lavyoung.ai.linkssh.execution.SshSessionId;
import com.lavyoung.ai.linkssh.execution.SshSessionStatus;
import com.lavyoung.ai.linkssh.host.SshConnectionTarget;

import java.time.Duration;
import java.util.Optional;

/**
 * 定义 Execution 上下文访问 SSH 客户端的出站端口。
 *
 * <p>应用层通过该端口管理 SSH 会话，基础设施适配器负责实现网络连接、
 * 身份认证、主机密钥校验和底层资源释放。</p>
 *
 * <p>该端口不得返回 Apache MINA SSHD 的客户端、会话或通道类型。</p>
 *
 * @author <a href="mailto:lavyoung1325@outlook.com">lavyoung</a>
 * @since 0.1.0
 */
public interface SshSessionPort {

    /**
     * 建立并认证 SSH 会话。
     *
     * <p>如果相同会话标识已经关联底层连接，实现必须安全关闭旧连接，
     * 再用新的连接替换它，不能泄漏旧资源。</p>
     *
     * @param sessionId             本次连接使用的会话标识
     * @param target                SSH 连接目标及安全校验信息
     * @param connectTimeout        建立网络连接的最大等待时间
     * @param authenticationTimeout 完成身份认证的最大等待时间
     * @return 成功或失败的稳定连接结果
     * @throws NullPointerException     当任一参数为 {@code null} 时抛出
     * @throws IllegalArgumentException 当超时时间为零或负数时抛出
     */
    SshConnectionResult connect(SshSessionId sessionId, SshConnectionTarget target, Duration connectTimeout,
                                Duration authenticationTimeout);

    /**
     * 断开指定 SSH 会话并释放其底层资源。
     *
     * <p>断开操作必须具备幂等性。会话不存在或已经断开时重复调用，
     * 不应产生失败。</p>
     *
     * @param sessionId    需要断开的会话标识
     * @param closeTimeout 关闭底层连接的最大等待时间
     * @throws NullPointerException     当任一参数为 {@code null} 时抛出
     * @throws IllegalArgumentException 当关闭超时时间为零或负数时抛出
     */
    void disconnect(SshSessionId sessionId, Duration closeTimeout);

    /**
     * 查询已经注册的 SSH 会话状态。
     *
     * <p>当会话标识从未建立连接或已经完成清理时返回空结果。</p>
     *
     * @param sessionId 需要查询的会话标识
     * @return 已注册会话的状态；不存在时返回空结果
     * @throws NullPointerException 当会话标识为 {@code null} 时抛出
     */
    Optional<SshSessionStatus> findStatus(SshSessionId sessionId);
}
