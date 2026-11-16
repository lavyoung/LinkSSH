# LinkSSH 项目规划

> 本文件是 LinkSSH 项目范围、架构和迭代计划的唯一事实来源。
> 所有功能开发和架构调整必须符合本文件；发生方向变化时，应先修改规划或新增 ADR，再修改代码。

## 1. 文档状态

- 状态：Active
- 规划版本：0.3
- 最后更新：2026-11-16
- 当前阶段：P1 主机管理（待启动）
- 技术基线：Java 21、Maven 3.9+、Spring Boot 4.1.x
- Maven 坐标：`com.lavyoung.ai:linkssh`
- 基础包名：`com.lavyoung.ai.linkssh`
- 版本策略：`${revision}` + Flatten Maven Plugin

## 2. 项目愿景

LinkSSH 是一个面向开发者和运维人员的 AI SSH 智能终端。

项目通过 AI Agent、SSH、命令风险控制和执行审计，帮助用户完成服务器巡检、日志分析、故障诊断和受控命令执行。

长期可扩展为覆盖研发、交付和运维流程的 AI 工程助手，但当前阶段只建设 LinkSSH，不提前实现 WaLiCode。

## 3. 当前范围

第一阶段包含：

- 主机与 SSH 连接配置
- 凭据引用与主机指纹
- SSH 终端和命令执行
- SFTP 基础能力
- Agent 对话与工具调用
- 命令风险分级
- 高风险操作人工审批
- 操作审计
- REST、SSE 和 WebSocket 接口
- Tauri 桌面客户端

## 4. 明确不在当前范围

以下能力不得在当前阶段提前建设：

- Workspace 领域
- WaLiCode 或代码智能体
- 多 Agent 协作
- RAG 知识库
- Kubernetes 运维
- 微服务拆分
- 消息队列
- 复杂工作流引擎
- 多租户商业化系统

如需加入以上能力，必须新增 ADR，说明业务需求、替代方案、依赖影响和迁移成本。

## 5. 架构原则

采用：

- 领域驱动设计
- 模块化单体
- 按限界上下文纵向划分模块
- 模块内部使用分层和六边形架构
- 端口与适配器隔离外部框架
- 领域事件解耦跨模块副作用

不采用全局巨型 `domain`、`application`、`infrastructure` 模块。

## 6. 规划模块

| 模块 | 职责 |
|---|---|
| `linkssh-shared-kernel` | 极少量稳定的共享领域类型 |
| `linkssh-host` | 主机、连接配置、凭据引用和主机指纹 |
| `linkssh-execution` | SSH 会话、命令执行和文件传输 |
| `linkssh-agent` | AgentRun、对话、工具调用和模型适配 |
| `linkssh-governance` | 风险策略、授权和人工审批 |
| `linkssh-audit` | 操作审计和执行追踪 |
| `linkssh-interfaces` | REST、SSE 和 WebSocket 入口 |
| `linkssh-bootstrap` | Spring Boot 启动和模块装配 |
| `linkssh-desktop` | Tauri、React 和 TypeScript 客户端 |

## 7. 模块依赖规则

允许的主要依赖方向：

```text
interfaces → 各业务模块公开 API
agent → execution、governance
execution → host、governance
audit → 其他模块发布的事件契约
业务模块 → shared-kernel
bootstrap → 所有需要装配的模块
```

强制规则：

1. 模块之间只能访问公开 API。
2. 禁止访问其他模块的 `internal` 包。
3. 禁止循环依赖。
4. Domain 不依赖 Spring、数据库、SSH、AI SDK 或 Web 框架。
5. Application 负责用例编排，不承载核心业务规则。
6. Infrastructure 实现应用层或领域层定义的端口。
7. Controller 不直接访问 Repository。
8. Agent 不直接获得 SSH Client。
9. 模型生成的命令必须经过治理模块。
10. Audit 不参与主业务决策。

## 8. 安全基线

必须满足：

- SSH 主机密钥校验
- 凭据不进入 Prompt
- 凭据不写入普通日志
- 命令风险分级
- 高风险操作人工确认
- 审批绑定主机、命令、参数、用户和有效期
- 命令超时、取消和输出限制
- 工具最小权限
- 完整操作审计
- 敏感字段脱敏
- 禁止模型绕过治理模块直接执行命令

## 9. 可读性规则

- 包名和类名使用统一业务语言。
- 避免 `common`、`manager`、`helper`、`utils` 大杂烩。
- 一个应用服务对应一个明确用例。
- 领域事件使用过去式命名。
- 端口只建立在真实外部边界上。
- 不为简单 CRUD 强行创建复杂聚合。
- 每个模块必须说明职责、公开 API 和依赖。
- 重要架构决策必须记录在 `docs/adr`。
- 新模块必须先更新本文件。

## 10. 迭代计划

### P0：项目骨架

状态：已完成

- [x] 建立 Maven 父工程
- [x] 配置 CI-Friendly Version 与 Flatten Maven Plugin
- [x] 创建后端业务模块
- [x] 创建 Spring Boot 启动模块
- [x] 接入 Spring Modulith
- [x] 建立 ArchUnit 架构测试
- [x] 建立 ADR 目录
- [x] 建立统一代码和构建规范
- [x] 完成空项目构建验证

验收命令：

```powershell
mvn clean verify
```

### P1：主机管理

状态：未开始

- [ ] 创建主机
- [ ] 修改主机
- [ ] 主机分组和标签
- [ ] 凭据引用
- [ ] SSH 主机指纹
- [ ] 主机连接测试

### P2：远程执行

状态：未开始

- [ ] SSH 会话生命周期
- [ ] 交互式终端
- [ ] 命令执行
- [ ] 命令取消和超时
- [ ] 输出流
- [ ] SFTP 基础能力

### P3：治理与审计

状态：未开始

- [ ] 风险分级
- [ ] 审批请求
- [ ] 审批有效期
- [ ] 命令参数绑定
- [ ] 审计事件
- [ ] 敏感信息脱敏

### P4：AI Agent

状态：未开始

- [ ] Spring AI 模型适配器
- [ ] AgentRun 生命周期
- [ ] 对话上下文
- [ ] Tool Calling
- [ ] SSH 工具适配
- [ ] Agent 执行过程流式输出
- [ ] Token、耗时和失败指标

### P5：桌面客户端

状态：未开始

- [ ] Tauri 项目
- [ ] React 页面结构
- [ ] 主机管理界面
- [ ] 终端界面
- [ ] Agent 对话界面
- [ ] 审批界面
- [ ] Tauri 最小权限配置

## 11. 当前迭代

P0 项目骨架已经完成。父工程、业务模块、Spring Boot 启动模块、CI-Friendly
Version、Spring Modulith、ArchUnit、统一构建规范和完整构建验证均已落地。

下一迭代为 P1 主机管理，当前尚未开始。在进入 P1 业务实现前，需要先明确主机聚合、
凭据引用和 SSH 主机指纹的领域边界。本阶段仍未实现具体 SSH、AI 或数据库业务。

## 12. 完成定义

一个任务只有同时满足以下条件才算完成：

- 代码能够编译
- 自动化测试通过
- 架构测试通过
- 没有引入非法模块依赖
- 没有提交密钥或本地配置
- 相关规划或 ADR 已更新
- README 或模块说明已同步
- 有可重复执行的验证命令

## 13. 规划变更流程

任何超出当前范围的改动必须按以下顺序进行：

1. 提出具体业务需求。
2. 检查是否属于现有限界上下文。
3. 评估是否真的需要新模块。
4. 编写 ADR。
5. 更新本文件的范围、模块或里程碑。
6. 再进行代码修改。
7. 在变更记录中登记。

禁止先写代码、后补架构理由。

## 14. 变更记录

| 日期 | 版本 | 变更 |
|---|---|---|
| 2026-11-16 | 0.3 | 完成 P0 工程骨架、模块验证、ArchUnit 架构守卫及统一代码和构建规范 |
| 2026-11-16 | 0.2 | 统一 AI 项目命名空间，并确立 `${revision}` 与 Flatten Maven Plugin 版本策略 |
| 2026-11-16 | 0.1 | 建立项目规划、范围、模块边界和迭代路线 |
