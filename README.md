# LinkSSH

LinkSSH 是一个面向开发者和运维人员的 AI SSH 智能终端项目。项目使用 Java 21、
Spring Boot、DDD 和模块化单体架构，在保持本地开发与部署简单的同时，通过自动化
架构测试保护模块边界。

当前已经完成 P0 工程骨架，尚未实现具体 SSH、AI 或数据库业务。

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
| `linkssh-bootstrap` | Spring Boot 启动和模块装配 |

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

应用默认监听 `8080`。当前尚未创建 Controller，因此访问根路径返回 `404` 属于正常现象。

## 项目管控

- [项目规划](PROJECT_PLAN.md)
- [架构决策记录](docs/adr/README.md)
- [代码与构建规范](docs/development/code-and-build-standards.md)

新增模块、改变模块依赖方向或引入规划外能力前，必须先更新项目规划并记录 ADR。
