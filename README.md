# LinkSSH

LinkSSH 是一个面向开发者和运维人员的 AI SSH 智能终端项目。项目使用 Java 21、
Spring Boot、DDD 和模块化单体架构，通过自动化架构测试保护模块边界，并允许同一
业务核心以自包含桌面或独立远程服务两种方式运行。

本仓库专注于 SSH、远程执行、运维 Agent、风险治理和操作审计。Workspace、代码
理解与 Coding Agent 将由未来独立的 LinkCode 仓库承载，不作为 LinkSSH 模块加入。

当前已经完成工程骨架、桌面静态界面和安全 SSH 会话基础，正在设计教程第 1-4 节
的主机配置持久化。

## 部署模式

LinkSSH 规划支持两种并列模式：

- 自包含桌面模式：一个安装包包含 Tauri 客户端、Java 后端、精简 Java 21 Runtime
  和 SQLite，不依赖远程 LinkSSH 服务或外部数据库。
- 远程服务模式：桌面客户端通过相同 REST、SSE 和 WebSocket 契约连接独立部署的
  LinkSSH Server，不启动内置后端。

两种模式共享相同业务模块。SQLite 只保存非敏感数据并由 Java 后端独占访问；
密码、私钥和模型 Token 只通过凭据引用进入受控的本地密钥链或服务端 Vault。
本地与远程数据首期相互独立，不自动同步或静默切换。

详细约束见
[ADR-0005：同时支持自包含桌面与远程服务部署](docs/adr/0005-dual-deployment-topology.md)。

## 后端模块

| 模块 | 职责 |
|---|---|
| `linkssh-shared-kernel` | 少量稳定的共享领域类型和事件契约 |
| `linkssh-host` | 主机、连接配置、凭据引用和主机指纹 |
| `linkssh-governance` | 风险策略、授权和人工审批 |
| `linkssh-execution` | SSH 会话、命令执行和文件传输 |
| `linkssh-agent` | Agent 运行、对话、工具调用和模型适配 |
| `linkssh-audit` | 操作审计和执行追踪 |
| `linkssh-interfaces` | REST、SSE 和 WebSocket 入站适配器 |
| `linkssh-bootstrap` | Spring Boot 启动及本地或远程运行模式装配 |

## 环境要求

- JDK 21
- Maven 3.9 或更高版本

Maven Enforcer 会在构建开始时验证以上版本要求。

## 构建验证

```powershell
mvn clean verify
```

该命令会执行领域测试、Spring 上下文测试、Spring Modulith 模块验证、ArchUnit
架构测试和 Maven 构建基线检查。

局部模块开发可以执行：

```powershell
mvn -pl <module> -am test
```

## 运行后端

```powershell
java -jar .\linkssh-bootstrap\target\linkssh-bootstrap-0.1.0-SNAPSHOT.jar
```

当前开发启动默认监听 `8080`。正式桌面本地模式尚未实现，后续必须改为 Tauri
管理的环回随机端口和短生命周期会话认证；当前尚未创建 Controller，因此访问
根路径返回 `404` 属于正常现象。

## 项目管控

- [项目规划](PROJECT_PLAN.md)
- [架构决策记录](docs/adr/README.md)
- [代码与构建规范](docs/development/code-and-build-standards.md)

新增模块、改变模块依赖方向或引入规划外能力前，必须先更新项目规划并记录 ADR。
