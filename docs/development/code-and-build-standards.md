# LinkSSH 代码与构建规范

## 1. 适用范围

本文档约束 LinkSSH 后端工程的代码风格、依赖管理、模块边界、测试和构建流程。

- `PROJECT_PLAN.md` 说明项目范围和迭代目标。
- `docs/adr` 记录重要架构决策及其原因。
- 本文档说明日常开发必须遵守的工程规则。

## 2. 技术和文件基线

- 使用 JDK 21 编译和运行。
- 使用 Maven 3.9 或更高版本。
- 源码、配置和文档统一使用 UTF-8。
- Java、XML、YAML、JSON 和 Markdown 使用 LF 行尾。
- PowerShell、Batch 和 CMD 脚本使用 CRLF 行尾。
- Java 和 XML 使用 4 个空格缩进。
- YAML 和 JSON 使用 2 个空格缩进。
- 所有文本文件保留文件末尾换行并清理无意义的行尾空格。

根目录 `.editorconfig` 和 `.gitattributes` 是格式与行尾规则的机器可读来源。

## 3. Maven 规范

- 所有后端模块继承根项目 `com.lavyoung.ai:linkssh`。
- 项目版本只通过根 POM 的 `${revision}` 管理。
- 内部模块依赖使用 `${project.version}`，由 Flatten Maven Plugin 解析发布 POM。
- 框架依赖优先使用 BOM 统一管理版本。
- 公共依赖和插件版本只在根 POM 管理，子模块不得随意覆盖。
- 依赖只添加到真实使用它的模块，不得为了方便加入所有模块。
- 同一 POM 中不得重复声明相同坐标的依赖。

Maven Enforcer 自动检查：

- JDK 必须为 21。
- Maven 必须为 3.9 或更高版本。
- POM 不得重复声明相同版本坐标的依赖。

## 4. 模块和包结构

Maven 模块按限界上下文纵向划分。业务模块的推荐结构为：

```text
com.lavyoung.ai.linkssh.<context>
├─ public API and events
└─ internal
   ├─ application
   ├─ domain
   └─ infrastructure
```

- 模块根包只放公开 API、公开事件和模块元数据。
- 实现代码放在模块自己的 `internal` 包下。
- 其他模块禁止引用该模块的 `internal` 包。
- 模块允许依赖通过根包 `package-info.java` 的 `@ApplicationModule` 声明。
- 禁止创建全局巨型 `domain`、`application` 或 `infrastructure` Maven 模块。
- 不为尚不存在的职责提前创建空层、空端口或空接口。

新增、拆分、合并模块或改变依赖方向前，必须先更新规划并记录 ADR。

## 5. DDD 与依赖方向

- Domain 不依赖 Spring、数据库、SSH、AI SDK、HTTP 或桌面框架。
- Application 负责用例和事务编排，不承载核心领域规则。
- Infrastructure 实现内层定义的端口，不能把技术类型泄漏到内层。
- Controller 只做协议适配并调用应用用例，不能直接访问 Repository。
- Agent 不直接访问 Host、凭据或 SSH Client。
- 模型生成的命令必须经过 Governance，再交给 Execution。
- Audit 记录已经发生的事实，不参与主业务决策。
- Shared Kernel 只容纳少量稳定类型，禁止演变为 `common` 或 `utils` 大杂烩。

Spring Modulith 和 ArchUnit 在 `mvn verify` 中自动检查可以机械验证的规则。

## 6. Java 编码规范

- 包名全部小写，类型使用 UpperCamelCase，成员使用 lowerCamelCase。
- 常量和枚举项使用 UPPER_SNAKE_CASE。
- 使用业务语言命名，避免含义模糊的 `Manager`、`Helper`、`Common` 和 `Utils`。
- Command、Query、Result 等不可变数据载体优先使用 `record`。
- 有生命周期和业务行为的对象使用实体或聚合，不强行使用 `record`。
- 正常语义下可能没有结果时返回 `Optional<T>`，但不将 Optional 用作字段或参数。
- 集合返回空集合而不是 `null`，公开集合优先不可变。
- 时间类型使用 `java.time`，不新增 `Date` 或 `SimpleDateFormat`。
- 领域事件使用过去式，表达已经发生的事实。
- 公开类型和核心公共方法编写 Javadoc，项目版本沿革使用 `@since`。
- 不在日志、异常、Trace 或审计正文中输出密码、私钥、Token 或凭据正文。

## 7. 测试规范

- 领域规则使用快速、确定性的纯单元测试。
- Application 测试验证用例编排和端口协作。
- Infrastructure 和协议适配器使用集成测试。
- 修复缺陷时应先增加能够复现问题的测试。
- 测试不得依赖执行顺序、真实外部服务或开发者本机私有配置。
- Spring 上下文由 `LinkSshApplicationTest` 验证。
- 模块模型由 `ModularityTest` 验证。
- 分层和安全依赖由 `ArchitectureTest` 验证。

最小相关验证：

```powershell
mvn -pl <module> -am test
```

完整验收：

```powershell
mvn clean verify
```

提交或交付前必须执行完整验收命令。

## 8. 配置与安全

- 通用且非敏感的默认配置可以提交到 `application.yml`。
- 环境差异通过 Profile、环境变量或不入库的本地配置提供。
- 禁止提交 `.env`、密码、私钥、Token、模型密钥或真实生产凭据。
- SSH 必须校验主机密钥或指纹，不能默认忽略校验。
- 日志、错误响应和审计记录必须对敏感字段脱敏。

## 9. 完成定义

一项变更只有同时满足以下条件才算完成：

1. 代码能够在 JDK 21 下编译。
2. `mvn clean verify` 通过。
3. 没有违反模块、分层或安全依赖规则。
4. 新增行为具有与风险匹配的测试。
5. 没有提交密钥、构建产物或本地配置。
6. README、项目规划或 ADR 已根据影响同步更新。
