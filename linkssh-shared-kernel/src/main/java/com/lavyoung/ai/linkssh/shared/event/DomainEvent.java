package com.lavyoung.ai.linkssh.shared.event;

import java.time.Instant;
import java.util.UUID;

/**
 * 所有领域事件共同遵循的最小契约。
 *
 * <p>领域事件描述已经发生且具有业务意义的事实。具体事件由所属限界上下文定义，
 * Shared Kernel 只规定事件标识和发生时间。</p>
 *
 * @author <a href="mailto:lavyoung1325@outlook.com">lavyoung</a>
 * @since 0.1.0
 */
public interface DomainEvent {

    /**
     * 返回事件的全局唯一标识。
     *
     * @return 事件标识
     */
    UUID eventId();

    /**
     * 返回事件实际发生的时间。
     *
     * @return UTC 时间点
     */
    Instant occurredAt();
}
