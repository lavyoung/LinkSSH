# LinkSSH Governance

该模块负责 LinkSSH 中远程操作的风险治理、授权判断和人工审批。

## 核心职责

- 对待执行操作进行风险分级
- 判断操作是否允许自动执行
- 为敏感操作创建人工审批请求
- 校验审批是否与用户、主机、命令参数和有效期匹配
- 阻止模型或调用方绕过治理流程

## 规划中的领域概念

- `RiskLevel`：操作风险等级
- `ExecutionIntent`：等待评估的执行意图
- `ExecutionPolicy`：风险判断规则
- `ApprovalRequest`：人工审批请求
- `ApprovalDecision`：审批结果
- `AuthorizationScope`：授权范围

以上类型将在实际业务用例出现时逐步创建，本阶段不提前生成空模型。

## 公开边界

其他模块只能通过 Governance 公开 API 提交执行意图并获取治理决策，不得访问 `internal` 包。

## 依赖规则

该模块：

- 可以依赖 `linkssh-shared-kernel`
- 不依赖 `linkssh-host`
- 不依赖 `linkssh-execution`
- 不依赖 `linkssh-agent`
- 不依赖 SSH、AI、数据库或 Web 框架