# LinkSSH Bootstrap

## 模块职责

`linkssh-bootstrap` 是 LinkSSH 后端应用的组合根，负责：

- 提供 Spring Boot 启动入口
- 装配各个业务模块
- 加载运行时配置
- 生成可执行应用程序 JAR
- 执行完整应用上下文测试

## 模块边界

本模块只负责应用装配，不承载业务逻辑。

本模块：

- 不定义领域实体
- 不实现应用用例
- 不直接访问 Repository
- 不实现 SSH、AI 或审计规则
- 不保存密码、私钥或模型密钥
- 不被业务模块反向依赖

## 启动入口

```text
com.lavyoung.ai.linkssh.LinkSshApplication
```

启动类位于基础包根目录，使 Spring 能够扫描：

```text
com.lavyoung.ai.linkssh.host
com.lavyoung.ai.linkssh.governance
com.lavyoung.ai.linkssh.execution
com.lavyoung.ai.linkssh.agent
com.lavyoung.ai.linkssh.audit
com.lavyoung.ai.linkssh.interfaces
```

## 依赖关系

```text
linkssh-bootstrap
├── linkssh-host
├── linkssh-governance
├── linkssh-execution
├── linkssh-agent
├── linkssh-audit
└── linkssh-interfaces
```

只有 `bootstrap` 可以承担最终应用装配职责。

业务模块不得依赖 `linkssh-bootstrap`。

## 配置原则

- 通用非敏感配置可以写入 `application.yml`
- 环境差异通过 Profile 或环境变量处理
- 密码、私钥、令牌和模型密钥不得提交到版本库
- 本地专用配置文件必须加入 `.gitignore`
- 默认配置应能在没有外部基础设施的情况下完成上下文启动

## 构建

```powershell
mvn -pl linkssh-bootstrap -am clean verify
```

## 运行

完成构建后执行：

```powershell
java -jar linkssh-bootstrap\target\linkssh-bootstrap-0.1.0-SNAPSHOT.jar
```

看到 Spring Boot 启动完成日志后，说明后端骨架已经可以独立运行。

## 模块化验证

项目使用 Spring Modulith 验证应用模块边界。

每次执行：

```powershell
mvn clean verify
```

都会检查：

- 应用模块是否可以被识别
- 模块之间是否存在循环依赖
- 是否访问其他模块的内部包
- 是否出现未声明的跨模块依赖
- `shared` 是否被识别为共享模块

模块允许的依赖声明在各模块根包的 `package-info.java` 中。

## 代码级架构验证

项目使用 ArchUnit 补充验证以下约束：

- 限界上下文之间不存在循环依赖
- Domain 不依赖 Spring、数据库、SSH、AI SDK 或外层实现
- Application 不依赖 Infrastructure
- Controller 不直接访问 Repository
- Agent 不直接访问 Host 或 SSH Client
- 业务模块不反向依赖 Interfaces
- 其他模块不能访问某个模块的 `internal` 包

ArchUnit 与 Spring Modulith 分工如下：

- Spring Modulith 验证逻辑应用模块及其公开边界
- ArchUnit 验证代码分层、安全依赖和命名约束
