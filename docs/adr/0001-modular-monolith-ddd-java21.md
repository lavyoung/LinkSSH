# ADR-0001：采用模块化单体、DDD 与 Java 21

- 状态：Accepted
- 日期：2026-11-16
- 决策者：LinkSSH Maintainers

## 背景

LinkSSH 将同时涉及主机配置、SSH 执行、AI Agent、风险治理和操作审计。
这些能力具有不同的业务规则和变化频率，但在项目初期由同一个团队维护，尚无独立部署、
独立扩缩容或跨团队自治的实际需求。

项目需要在以下目标之间取得平衡：

- 保持领域边界清晰，避免 AI、SSH 和数据库框架污染核心业务
- 支持未来增加 MCP、Skills 和 WaLiCode 相关能力
- 保持本地开发、调试、测试和部署简单
- 让新参与者能够通过目录结构理解系统
- 用自动化测试阻止模块依赖逐渐失控

参考模板 `ddd-forge-template` 采用全局横向分层 Maven 模块。该方式适合单一限界上下文，
但 LinkSSH 后续包含多个上下文，如果所有领域类型都进入一个全局 `domain` 模块，容易形成
巨型模块并产生跨上下文耦合。

## 决策

### 1. 架构风格

LinkSSH 采用模块化单体，不在初期拆分微服务。

项目按限界上下文纵向划分 Maven 模块，每个业务模块内部使用 DDD 分层和六边形架构：

```text
public API
    ↓
application
    ↓
domain
    ↑
infrastructure adapters
```

### 2. 规划模块

```text
linkssh-shared-kernel
linkssh-host
linkssh-execution
linkssh-agent
linkssh-governance
linkssh-audit
linkssh-interfaces
linkssh-bootstrap
linkssh-desktop
```

当前不创建 `workspace`、`code-agent`、`rag` 或独立 `mcp` 模块。

### 3. 模块封装

业务模块的根包用于放置明确公开的 API、事件和必要契约；实现放在 `internal` 下：

```text
com.lavyoung.ai.linkssh.<context>
├─ public API and events
└─ internal
   ├─ application
   ├─ domain
   └─ infrastructure
```

其他模块禁止访问 `internal` 包。

### 4. 依赖方向

允许的主要依赖为：

```text
interfaces  → business module public APIs
agent       → execution, governance
execution   → host, governance
audit       → published event contracts
all modules → shared-kernel when necessary
bootstrap   → modules required for assembly
```

禁止循环依赖。跨模块副作用优先通过领域事件或应用事件解耦。

### 5. 框架隔离

Spring AI、Google ADK、Apache MINA SSHD、MyBatis、Tauri 和具体模型 SDK
不得出现在 Domain 中。

外部能力通过应用层或领域层定义的端口接入，由基础设施适配器实现。首期 Agent
运行时只实现 Spring AI 适配器，不同时维护两套 Agent 框架。

### 6. 架构守卫

使用 Spring Modulith 和 ArchUnit 自动验证：

- 模块之间不存在循环依赖
- 只能通过公开 API 访问其他模块
- 禁止跨模块访问 `internal`
- Domain 不依赖框架和基础设施
- Controller 不直接依赖 Repository

构建命令 `mvn clean verify` 必须执行架构测试。

### 7. 技术基线

- Java 21
- Maven 3.9+
- Maven Group ID：`com.lavyoung.ai`
- Java 基础包：`com.lavyoung.ai.linkssh`
- 使用 `${revision}` 作为唯一项目版本来源
- 使用 Flatten Maven Plugin 生成可消费的 POM
- Spring Boot 4.1.x
- Spring AI 2.0.x
- Spring Modulith 2.1.x
- Tauri 2 + React + TypeScript

依赖使用 BOM 或父 POM 统一管理，子模块不得随意覆盖公共依赖版本。
根项目和子模块父坐标使用 `${revision}`，内部模块依赖使用
`${project.version}`；安装或发布时由 Flatten Maven Plugin 解析 CI-Friendly 版本占位符。

## 后果

### 正面影响

- 业务边界能够从目录和 Maven 模块直接识别
- 单进程开发和部署比微服务简单
- 编译期和测试期可以阻止非法依赖
- AI、SSH 和持久化实现可以独立替换
- 限界上下文未来具备独立拆分条件
- 架构更适合作为长期学习和简历项目展示

### 代价与风险

- 初期 Maven 模块数量多于普通单体项目
- 团队必须维护公开 API，不能通过直接引用内部类快速绕过边界
- 跨模块事务和事件需要明确设计
- 过度使用聚合、接口或领域事件仍可能造成不必要复杂度
- Spring Modulith 与 ArchUnit 规则需要随架构演进维护

## 备选方案

### 全局横向分层 Maven 模块

不采用。它能保证技术层依赖方向，但不能有效阻止不同限界上下文在同一 `domain`
或 `infrastructure` 模块中互相耦合。

### 单 Maven 模块，仅依靠包约定

不采用。初期较简单，但缺少编译级边界，长期更容易绕过模块 API。

### 初期直接拆分微服务

不采用。目前没有独立部署、团队自治和容量隔离需求，会过早引入网络通信、分布式事务、
服务发现和运维成本。

### 同时采用 Spring AI 和 Google ADK

不采用。首期会增加重复抽象和调试成本。通过 Agent 运行时端口保留后续替换或新增适配器的能力。

## 验证方式

- `mvn clean verify` 通过
- Spring Modulith 模块验证通过
- ArchUnit 架构规则通过
- 模块关系图不存在循环
- 代码评审确认跨模块调用只使用公开 API
- 新模块或依赖方向变化具有新的 ADR

## 重新评估条件

出现以下情况之一时，应新建 ADR 重新评估：

- 某个限界上下文需要独立部署或扩缩容
- 不同团队需要独立发布某个上下文
- 单体构建或启动时间严重影响交付效率
- 模块之间无法通过当前事件和 API 边界合理协作
- Spring Boot、Spring AI 或 Spring Modulith 的兼容性发生重大变化
- 正式启动 WaLiCode 或 Workspace 领域建设

