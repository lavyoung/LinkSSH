/**
 * SSH 会话、命令执行和文件传输上下文。
 *
 * @author <a href="mailto:lavyoung1325@outlook.com">lavyoung</a>
 * @since 0.1.0
 */
@ApplicationModule(
        displayName = "Execution",
        allowedDependencies = {
                "host",
                "governance"
        }
)
package com.lavyoung.ai.linkssh.execution;

import org.springframework.modulith.ApplicationModule;