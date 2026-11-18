# 真实 SSH 服务器冒烟测试指南

## 目的与适用范围

本指南用于在自动化集成测试通过后，可选验证 LinkSSH SSH 会话适配器与受控
OpenSSH 服务器的基础兼容性。验证范围仅包括连接、身份认证、状态查询和断开，
不得执行远程命令、SFTP、Agent Tool 或其他超出教程第 1-3 节的能力。

日常开发和 CI 仍以进程内 Apache MINA SSH Server 测试为准。真实服务器不是
构建、合并或发布的前置条件。

## 安全前提

开始前必须同时满足以下条件：

1. 服务器属于本人或已获得明确测试授权。
2. 使用权限最小、可随时撤销的测试账号，不使用生产管理员账号。
3. 预期主机公钥指纹通过云控制台、服务器管理员或其他可信带外渠道取得。
4. 不把首次网络连接看到的指纹直接视为可信指纹。
5. 不修改代码以使用接受所有主机密钥的校验器。
6. 不把密码、私钥、口令、Token 或服务器清单写入仓库文件、测试代码、日志、
   Prompt 或命令行参数。

## 本地参数准备

主机地址、端口、用户名和预期指纹可以放入仅当前终端会话有效的环境变量。
这些值不是凭据正文，但仍不应提交到仓库：

```powershell
$env:LINKSSH_SMOKE_HOST = "由测试人员填写"
$env:LINKSSH_SMOKE_PORT = "22"
$env:LINKSSH_SMOKE_USERNAME = "由测试人员填写"
$env:LINKSSH_SMOKE_FINGERPRINT = "SHA256:由可信渠道取得"
```

密码或私钥口令不得作为命令行参数传递。需要密码时，应由本地临时验证入口
通过控制台掩码读取，并立即转换为 `char[]` 交给 `PasswordCredentialMaterial`；
验证结束后必须关闭凭据材料。需要私钥时，只能读取仓库外的测试密钥，并使用
`PrivateKeyCredentialMaterial` 的短生命周期副本；不得复制密钥到项目目录。

当前仓库没有提交真实服务器测试入口，避免 CI 或其他开发者意外连接外部主机。
确需验证时，应在以下包中创建本地临时测试或 IDE Scratch 文件：

```text
com.lavyoung.ai.linkssh.execution.internal.infrastructure.ssh
```

本地入口应完成以下流程：

1. 从环境读取主机、端口、用户名和可信指纹。
2. 通过控制台掩码读取凭据正文，不打印读取结果。
3. 创建只识别本次 `CredentialReference` 的临时 `SshCredentialResolverPort`。
4. 启动 `ApacheMinaSshSessionAdapter`。
5. 调用 `connect`，确认返回 `SshConnectionResult.Connected`。
6. 调用 `findStatus`，确认状态为 `SshSessionStatus.CONNECTED`。
7. 在 `finally` 中调用 `disconnect` 和 `close`，并关闭凭据材料。
8. 删除本地临时入口，不将其加入 Git 暂存区。

## 失败判定

以下情况必须视为验证失败，不得通过降低安全配置绕过：

- 指纹不匹配或无法从可信渠道确认指纹。
- 凭据不可用或认证失败。
- 连接、认证或关闭超过指定超时。
- 断开后仍有活动会话或后台线程未释放。
- 错误输出、日志或调试信息中出现凭据正文。

内嵌服务器测试通过而真实服务器失败时，应记录服务端实现、认证方式、主机密钥
算法和客户端稳定失败分类。不得记录密码、私钥内容或完整底层异常中的敏感数据。

## 清理

验证完成后关闭测试账号或撤销临时授权，删除本地临时测试入口，并清理当前
PowerShell 会话中的非凭据参数：

```powershell
Remove-Item Env:LINKSSH_SMOKE_HOST -ErrorAction SilentlyContinue
Remove-Item Env:LINKSSH_SMOKE_PORT -ErrorAction SilentlyContinue
Remove-Item Env:LINKSSH_SMOKE_USERNAME -ErrorAction SilentlyContinue
Remove-Item Env:LINKSSH_SMOKE_FINGERPRINT -ErrorAction SilentlyContinue
```

最后运行 `git status --short`，确认没有服务器配置、凭据文件或本地测试入口进入
工作区或暂存区。
