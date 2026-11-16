package com.lavyoung.ai.linkssh;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

/**
 * LinkSSH 代码级架构约束。
 *
 * <p>Spring Modulith 负责验证应用模块模型，本测试补充验证 DDD 分层、
 * 内部包封装以及关键安全依赖方向。</p>
 *
 * @author <a href="mailto:lavyoung1325@outlook.com">lavyoung</a>
 * @since 0.1.0
 */
@AnalyzeClasses(
        packages = "com.lavyoung.ai.linkssh",
        importOptions = ImportOption.DoNotIncludeTests.class
)
class ArchitectureTest {

    private static final String BASE_PACKAGE = "com.lavyoung.ai.linkssh";

    /**
     * 限界上下文之间不能形成循环依赖。
     */
    @ArchTest
    static final ArchRule MODULE_PACKAGES_MUST_BE_FREE_OF_CYCLES = slices()
            .matching(BASE_PACKAGE + ".(*)..")
            .should().beFreeOfCycles()
            .because("限界上下文之间禁止形成循环依赖");

    /**
     * 领域层不能依赖框架、适配器或外层实现。
     */
    @ArchTest
    static final ArchRule DOMAIN_MUST_NOT_DEPEND_ON_FRAMEWORKS_OR_OUTER_LAYERS =
            noClasses()
                    .that().resideInAPackage("..internal.domain..")
                    .should().dependOnClassesThat().resideInAnyPackage(
                            "org.springframework..",
                            "jakarta.persistence..",
                            "org.apache.sshd..",
                            "org.springframework.ai..",
                            "..internal.application..",
                            "..internal.infrastructure..",
                            BASE_PACKAGE + ".interfaces.."
                    )
                    .because("领域层必须保持框架无关并且只能依赖内层")
                    .allowEmptyShould(true);

    /**
     * 应用层不能反向依赖基础设施实现。
     */
    @ArchTest
    static final ArchRule APPLICATION_MUST_NOT_DEPEND_ON_INFRASTRUCTURE =
            noClasses()
                    .that().resideInAPackage("..internal.application..")
                    .should().dependOnClassesThat()
                    .resideInAPackage("..internal.infrastructure..")
                    .because("应用层定义用例和端口，基础设施只能依赖并实现内层端口")
                    .allowEmptyShould(true);

    /**
     * Controller 只能调用应用用例，不能直接访问 Repository。
     */
    @ArchTest
    static final ArchRule CONTROLLERS_MUST_NOT_DEPEND_ON_REPOSITORIES =
            noClasses()
                    .that().haveSimpleNameEndingWith("Controller")
                    .should().dependOnClassesThat()
                    .haveSimpleNameEndingWith("Repository")
                    .because("Controller 必须通过应用用例访问领域能力")
                    .allowEmptyShould(true);

    /**
     * Agent 不能绕过 Execution 直接接触主机或 SSH 客户端。
     */
    @ArchTest
    static final ArchRule AGENT_MUST_NOT_ACCESS_HOST_OR_SSH_CLIENT =
            noClasses()
                    .that().resideInAPackage(BASE_PACKAGE + ".agent..")
                    .should().dependOnClassesThat().resideInAnyPackage(
                            BASE_PACKAGE + ".host..",
                            "org.apache.sshd.."
                    )
                    .because("Agent 只能通过 execution 的公开 API 请求执行");

    /**
     * 业务模块不能反向依赖入站接口模块。
     */
    @ArchTest
    static final ArchRule BUSINESS_MODULES_MUST_NOT_DEPEND_ON_INTERFACES =
            noClasses()
                    .that().resideInAnyPackage(
                            BASE_PACKAGE + ".host..",
                            BASE_PACKAGE + ".governance..",
                            BASE_PACKAGE + ".execution..",
                            BASE_PACKAGE + ".agent..",
                            BASE_PACKAGE + ".audit.."
                    )
                    .should().dependOnClassesThat()
                    .resideInAPackage(BASE_PACKAGE + ".interfaces..")
                    .because("入站适配器可以依赖业务模块，业务模块不能反向依赖入站适配器");

    @ArchTest
    static final ArchRule SHARED_INTERNALS_MUST_BE_ENCAPSULATED =
            internalPackagesAreOnlyAccessedBy("shared");

    @ArchTest
    static final ArchRule HOST_INTERNALS_MUST_BE_ENCAPSULATED =
            internalPackagesAreOnlyAccessedBy("host");

    @ArchTest
    static final ArchRule GOVERNANCE_INTERNALS_MUST_BE_ENCAPSULATED =
            internalPackagesAreOnlyAccessedBy("governance");

    @ArchTest
    static final ArchRule EXECUTION_INTERNALS_MUST_BE_ENCAPSULATED =
            internalPackagesAreOnlyAccessedBy("execution");

    @ArchTest
    static final ArchRule AGENT_INTERNALS_MUST_BE_ENCAPSULATED =
            internalPackagesAreOnlyAccessedBy("agent");

    @ArchTest
    static final ArchRule AUDIT_INTERNALS_MUST_BE_ENCAPSULATED =
            internalPackagesAreOnlyAccessedBy("audit");

    @ArchTest
    static final ArchRule INTERFACES_INTERNALS_MUST_BE_ENCAPSULATED =
            internalPackagesAreOnlyAccessedBy("interfaces");

    /**
     * 创建模块内部包封装规则。
     *
     * @param moduleName 模块根包名称
     * @return 模块内部包访问规则
     */
    private static ArchRule internalPackagesAreOnlyAccessedBy(String moduleName) {
        String modulePackage = BASE_PACKAGE + "." + moduleName;

        return noClasses()
                .that().resideOutsideOfPackage(modulePackage + "..")
                .should().dependOnClassesThat()
                .resideInAPackage(modulePackage + ".internal..")
                .because("模块的 internal 包只能由模块自身访问")
                .allowEmptyShould(true);
    }
}