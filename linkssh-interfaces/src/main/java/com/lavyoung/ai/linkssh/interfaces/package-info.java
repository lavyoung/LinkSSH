/**
 * REST、SSE 和 WebSocket 入站适配器。
 *
 * @author <a href="mailto:lavyoung1325@outlook.com">lavyoung</a>
 * @since 0.1.0
 */
@ApplicationModule(
        displayName = "Interfaces",
        allowedDependencies = {
                "host",
                "governance",
                "execution",
                "agent",
                "audit"
        }
)
package com.lavyoung.ai.linkssh.interfaces;

import org.springframework.modulith.ApplicationModule;