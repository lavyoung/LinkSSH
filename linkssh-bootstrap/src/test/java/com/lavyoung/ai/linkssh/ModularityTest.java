package com.lavyoung.ai.linkssh;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * LinkSSH 模块结构验证测试。
 *
 * @author <a href="mailto:lavyoung1325@outlook.com">lavyoung</a>
 * @since 0.1.0
 */
class ModularityTest {

    private static final ApplicationModules MODULES =
            ApplicationModules.of(LinkSshApplication.class);

    /**
     * 验证模块之间不存在循环依赖、非法内部访问或未授权依赖。
     */
    @Test
    void verifiesModuleStructure() {
        MODULES.verify();
    }

    /**
     * 验证规划中的应用模块都能够被 Spring Modulith 识别。
     */
    @Test
    void detectsPlannedModules() {
        assertThat(MODULES.getModuleByName("shared")).isPresent();
        assertThat(MODULES.getModuleByName("host")).isPresent();
        assertThat(MODULES.getModuleByName("governance")).isPresent();
        assertThat(MODULES.getModuleByName("execution")).isPresent();
        assertThat(MODULES.getModuleByName("agent")).isPresent();
        assertThat(MODULES.getModuleByName("audit")).isPresent();
        assertThat(MODULES.getModuleByName("interfaces")).isPresent();

        var sharedKernel = MODULES.getModuleByName("shared").orElseThrow();

        assertThat(MODULES.getSharedModules()).contains(sharedKernel);
    }
}