# LinkSSH Agent

## 模块职责

`linkssh-agent` 负责 AI Agent 上下文，包括：

- Agent 运行生命周期
- 用户对话与消息管理
- 工具调用编排
- 模型请求与响应抽象
- AI 模型适配器

## 模块边界

本模块：

- 不直接创建或持有 SSH Client
- 不直接访问主机凭据
- 不绕过治理模块执行命令
- 不负责 SSH 会话和命令执行细节
- 不在领域层依赖 Spring AI 或具体模型 SDK

Agent 只能通过 `linkssh-execution` 的公开 API 提交执行请求。

模型产生的可执行命令必须经过 `linkssh-governance` 判断和授权。

## 公开 API

当前 P0 阶段暂未定义公开 API。

后续公开契约放置在：

```text
com.lavyoung.ai.linkssh.agent
```

具体实现放置在：

```text
com.lavyoung.ai.linkssh.agent.internal
```

其他模块禁止访问 `internal` 包。

## 依赖关系

```text
linkssh-agent
├── linkssh-shared-kernel
├── linkssh-execution
└── linkssh-governance
```

`linkssh-agent` 不允许直接依赖 `linkssh-host`。

## 框架隔离

后续接入 Spring AI 时，具体模型客户端和框架适配代码应放在基础设施适配器中。领域模型和应用用例不得直接依赖 Spring AI 类型。