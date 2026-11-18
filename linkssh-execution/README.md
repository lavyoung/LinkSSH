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

当前公开契约放置在：

```text
com.lavyoung.ai.linkssh.execution
```

已定义：

- `SshSessionId`：一次独立 SSH 会话的稳定标识
- `SshSessionStatus`：连接、断开和失败等会话生命周期状态
- `SshConnectionResult`：互斥表达连接成功或失败的密封结果
- `SshConnectionFailure`：隔离底层客户端异常的稳定失败分类

SSH 客户端出站端口 `SshSessionPort` 位于内部应用层，只负责连接、断开和
状态查询，不属于其他模块可以直接调用的公开 API。

凭据解析同样位于内部应用层：`SshCredentialResolverPort` 只接受 Host 上下文
提供的 `CredentialReference`，并返回短生命周期的 `SshCredentialMaterial`。
密码、私钥和口令使用可清除数组承载，调用方必须通过 try-with-resources 在
认证结束后立即清理；凭据正文不得进入公开 API、日志、异常或会话缓存。

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

## 验证

自动化验证使用进程内 Apache MINA SSH Server，不依赖开发者个人服务器：

```powershell
mvn -pl linkssh-execution -am test
```

如需使用受控真实服务器做可选验证，必须先阅读
[真实 SSH 服务器冒烟测试指南](../docs/development/ssh-real-server-smoke-test.md)。
不得为完成冒烟测试而关闭主机密钥校验，也不得把服务器地址、账号、密码或
私钥提交到仓库。
