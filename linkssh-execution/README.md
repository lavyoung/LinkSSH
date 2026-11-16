# LinkSSH Execution

## 模块职责

`linkssh-execution` 负责 SSH 执行上下文，包括：

- SSH 会话生命周期管理
- 命令执行编排
- 文件上传与下载
- 执行状态和结果管理
- 发布执行生命周期事件

## 模块边界

本模块：

- 不负责保存主机和凭据，通过 `linkssh-host` 的公开 API 获取主机连接信息
- 不自行决定命令风险，通过 `linkssh-governance` 的公开 API 请求风险判断和授权
- 不包含 AI 模型调用或提示词处理
- 不向 Agent 暴露底层 SSH Client
- 不在领域层依赖 Apache MINA SSHD、Spring 或数据库框架

## 公开 API

当前 P0 阶段暂未定义公开 API。

后续公开契约放置在：

```text
com.lavyoung.ai.linkssh.execution
```

具体实现放置在：

```text
com.lavyoung.ai.linkssh.execution.internal
```

其他模块禁止访问 `internal` 包。

## 依赖关系

```text
linkssh-execution
├── linkssh-shared-kernel
├── linkssh-host
└── linkssh-governance
```

只允许访问依赖模块的公开 API。

## 安全约束

任何由 AI 模型产生的命令都必须先经过治理模块，不允许 Agent 绕过治理流程直接操作 SSH Client。
