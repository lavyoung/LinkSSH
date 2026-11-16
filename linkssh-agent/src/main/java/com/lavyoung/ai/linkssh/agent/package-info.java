/**
 * Agent 运行、对话、工具调用和模型适配上下文。
 *
 * @author <a href="mailto:lavyoung1325@outlook.com">lavyoung</a>
 * @since 0.1.0
 */
@ApplicationModule(
        displayName = "Agent",
        allowedDependencies = {
                "execution",
                "governance"
        }
)
package com.lavyoung.ai.linkssh.agent;

import org.springframework.modulith.ApplicationModule;