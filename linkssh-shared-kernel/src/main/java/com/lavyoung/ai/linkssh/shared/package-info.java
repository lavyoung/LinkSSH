/**
 * LinkSSH 各限界上下文之间共享的稳定领域原语。
 *
 * <p>该包必须保持最小化，只允许放置具有明确跨模块语义且生命周期稳定的类型。
 * HTTP 响应、数据库对象、框架工具类和具体业务模型不得放入该包。</p>
 *
 * @author <a href="mailto:lavyoung1325@outlook.com">lavyoung</a>
 * @since 0.1.0
 */
@ApplicationModule(
        displayName = "Shared Kernel",
        allowedDependencies = {}
)
package com.lavyoung.ai.linkssh.shared;

import org.springframework.modulith.ApplicationModule;