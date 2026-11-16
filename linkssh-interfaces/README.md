# LinkSSH Interfaces

## 模块职责

`linkssh-interfaces` 是系统的入站适配器模块，负责：

- 接收 REST 请求
- 提供 SSE 流式响应
- 提供 WebSocket 双向通信
- 校验请求的基本格式
- 将传输对象转换为应用用例输入
- 将应用结果转换为对外响应

## 模块边界

本模块只负责协议适配，不承载领域规则。

本模块：

- 不直接访问 Repository
- 不直接操作数据库
- 不直接创建 SSH Client
- 不直接调用具体 AI 模型客户端
- 不自行判断命令风险
- 不访问其他模块的 `internal` 包
- 不把 HTTP、SSE 或 WebSocket 类型传入领域层

Controller 只能调用业务模块公开的应用 API。

## 包结构

当前 P0 阶段不创建具体 Java 包。

后续适配器实现放置在：

```text
com.lavyoung.ai.linkssh.interfaces.internal
├── rest
├── sse
└── websocket
```

对外请求和响应对象应放置在对应协议适配器包中，不复用领域实体作为网络传输对象。

## 依赖关系

```text
linkssh-interfaces
├── linkssh-host
├── linkssh-governance
├── linkssh-execution
├── linkssh-agent
└── linkssh-audit
```

本模块只允许调用上述模块的公开 API。

业务模块禁止反向依赖 `linkssh-interfaces`。

## API 设计约束

后续实现接口时遵循：

- 请求 DTO 与领域对象分离
- Controller 不包含业务决策
- 输入校验分为格式校验和领域规则校验
- 错误响应采用统一结构
- SSE 和 WebSocket 明确定义断线与取消语义
- 敏感数据不得出现在响应、异常或日志中
- API 版本策略在实际出现兼容需求时再引入

## 框架隔离

Spring MVC、WebFlux、SSE 和 WebSocket 都属于接口适配技术。

这些框架类型只能存在于本模块的适配器实现中，不得泄漏到业务模块的公开 API、应用层或领域层。