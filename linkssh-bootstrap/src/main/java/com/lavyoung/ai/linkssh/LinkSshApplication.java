package com.lavyoung.ai.linkssh;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.modulith.Modulith;

/**
 * LinkSSH 应用程序启动入口。
 *
 * <p>启动类位于 {@code com.lavyoung.ai.linkssh} 根包，
 * 以便 Spring 扫描各个限界上下文中的组件。</p>
 *
 * @author <a href="mailto:lavyoung1325@outlook.com">lavyoung</a>
 * @since 0.1.0
 */
@Modulith(
        systemName = "LinkSSH",
        sharedModules = "shared"
)
public class LinkSshApplication {

    /**
     * 启动 LinkSSH 应用程序。
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(LinkSshApplication.class, args);
    }
}
