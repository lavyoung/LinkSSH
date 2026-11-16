package com.lavyoung.ai.linkssh.governance;

import java.util.Objects;

/**
 * 表示远程操作或 Agent 工具调用可能产生的风险等级。
 *
 * <p>风险等级只描述潜在影响强度，不直接决定操作是否允许执行或是否需要人工审批。
 * 具体处置方式由执行策略结合用户、环境、主机和授权范围决定。</p>
 *
 * @author <a href="mailto:lavyoung1325@outlook.com">lavyoung</a>
 * @since 0.1.0
 */
public enum RiskLevel {

    /**
     * 低风险。
     *
     * <p>通常用于只读、影响范围有限且不会改变系统状态的操作，
     * 例如查看磁盘空间或读取非敏感运行状态。</p>
     */
    LOW(10),

    /**
     * 中等风险。
     *
     * <p>通常用于影响有限、可恢复或需要额外关注的操作，
     * 例如重启非关键开发服务。</p>
     */
    MEDIUM(20),

    /**
     * 高风险。
     *
     * <p>通常用于修改关键状态、访问敏感信息或可能影响服务可用性的操作，
     * 例如修改生产配置或重启生产服务。</p>
     */
    HIGH(30),

    /**
     * 严重风险。
     *
     * <p>通常用于不可逆、破坏性或影响范围广泛的操作，
     * 例如删除生产数据、格式化磁盘或修改核心访问控制。</p>
     */
    CRITICAL(40);

    private final int severity;

    RiskLevel(int severity) {
        this.severity = severity;
    }

    /**
     * 返回用于稳定比较风险等级的严重程度数值。
     *
     * <p>调用方不应依赖枚举声明顺序或 {@link #ordinal()} 比较风险等级。</p>
     *
     * @return 严重程度数值，数值越大表示潜在风险越高
     */
    public int severity() {
        return severity;
    }

    /**
     * 判断当前风险等级是否达到或超过指定阈值。
     *
     * @param threshold 需要比较的风险阈值
     * @return 当前等级大于或等于阈值时返回 {@code true}
     * @throws NullPointerException 当阈值为 {@code null} 时抛出
     */
    public boolean isAtLeast(RiskLevel threshold) {
        Objects.requireNonNull(threshold, "Risk threshold must not be null");
        return severity >= threshold.severity;
    }
}
