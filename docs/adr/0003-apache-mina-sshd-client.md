# ADR-0003：采用 Apache MINA SSHD 实现安全 SSH 连接

- 状态：Accepted
- 日期：2026-11-17
- 决策者：LinkSSH Maintainers

## 背景

教程第 1-3 节使用 JSch 建立 SSH Session，并通过 Domain Port 隔离具体实现。
LinkSSH 需要保留 Port/Adapter 的依赖倒置思想，但不能照搬以下做法：

- 使用 `StrictHostKeyChecking=no` 跳过服务器身份校验
- 让公开契约直接接收密码和私钥正文
- 向外层暴露 JSch `Session`
- 在连接测试中绕过治理边界直接执行任意命令
- 依赖个人云服务器完成自动化测试

项目需要选择一套支持 Java 21、主机密钥校验、密码和密钥认证、后续命令执行
与 SFTP，并且适合自动化测试的 SSH 客户端实现。

## 决策

### 1. SSH 客户端

LinkSSH 采用 Apache MINA SSHD 2.20.x 稳定版本作为首期 SSH 客户端实现。
不采用仍处于里程碑阶段的 3.0.0 版本。首期只引入 `sshd-core`；需要真实 SFTP
能力时再按规划引入 `sshd-sftp`，不提前增加未使用依赖。

Apache MINA SSHD 只允许出现在 `linkssh-execution` 的内部基础设施适配器中，
不得出现在 Domain、公开 API、Agent、Controller 或桌面端代码中。

### 2. 契约边界

Host 上下文公开主机连接目标、`CredentialReference` 和预期主机指纹等稳定业务
契约。Execution 上下文负责 SSH 会话生命周期，并在内层定义出站端口。

公开或内层契约不得返回 Apache MINA SSHD 的 `SshClient`、`ClientSession`、
`ClientChannel` 或其他技术类型。连接结果使用 LinkSSH 自有类型表达会话标识、
状态和失败原因。

### 3. 主机身份校验

每次连接必须使用以下机制之一验证服务器身份：

1. `KnownHostsServerKeyVerifier` 验证受控的 known_hosts；或
2. LinkSSH 实现的固定指纹校验器，验证 Host 上下文保存的预期指纹。

禁止使用接受所有服务器密钥的验证器，禁止通过关闭校验解决测试或连接问题。
首次信任和指纹变更必须成为显式业务流程，不能在基础设施层静默接受。

### 4. 凭据处理

领域模型和公开 API 只传递凭据引用，不传递凭据正文。基础设施边界在建立连接
前按引用解析短生命周期的凭据材料，并遵守以下规则：

- 密码、私钥和口令不得写入日志、异常消息、审计正文或 Prompt
- 优先使用可清除的字符或字节载体，不长期保存为 `String`
- 使用结束后尽快清理临时缓冲区
- 会话缓存不得附带保存凭据正文
- 测试和示例配置不得包含真实密码、私钥或 Token

### 5. 生命周期与超时

`SshClient` 由基础设施组件统一启动和停止。会话由 Execution 上下文内部管理，
支持连接、断开、状态查询和重复连接的资源替换语义。

连接、认证和关闭必须有明确超时。并发连接同一会话标识时必须避免旧会话或
新会话泄漏。失败结果保留可诊断的稳定错误分类，但不得向客户端暴露敏感的
底层异常细节。

### 6. 测试策略

自动化集成测试优先启动本地可控的 Apache MINA `SshServer`，覆盖：

- 正确凭据和正确指纹成功连接
- 错误密码或密钥认证失败
- 指纹不匹配拒绝连接
- 网络或认证超时
- 重复连接释放旧会话
- 断开操作幂等且释放资源

真实云服务器验证仅作为可选手动冒烟测试。连接参数和凭据必须从控制台或本地
忽略配置中取得，不能硬编码或提交。

### 7. 本节范围

教程第 1-3 节在 LinkSSH 中只实现 SSH Session 生命周期。命令执行、交互式
终端和 SFTP 属于后续步骤。模型产生的命令在治理能力完成前不得接入 SSH。

## 后果

### 正面影响

- 与 ADR-0001 的框架隔离和现有 Execution 模块方向一致
- 主机密钥校验从第一条真实连接开始即为强制约束
- 内嵌 SSH Server 让自动化测试不依赖个人云服务器
- 后续可以在同一基础设施实现上扩展命令通道和 SFTP
- 更换 SSH 库时不影响 Host、Domain、Agent 和桌面端契约

### 代价与风险

- Apache MINA SSHD API 和生命周期比 JSch 教程示例更复杂
- 需要自行设计凭据解析、会话注册和稳定错误映射
- 内嵌服务器测试仍需注意与真实 OpenSSH 的算法和行为差异
- 后续升级到 3.x 可能存在 API 迁移成本

## 备选方案

### mwiede JSch 2.28.x

不采用。该社区分支持续维护、体积较小且与教程最接近，但官方文档较少，API
仍带有原始 JSch 的可变全局身份和 Session 使用方式。它能够通过适配器隔离，
但在 LinkSSH 当前阶段没有超过 Apache MINA SSHD 的明显收益。

### Apache MINA SSHD 3.0.0 里程碑版本

不采用。当前仍为预发布里程碑，不适合作为项目首条 SSH 生产基线。

### 调用系统 OpenSSH 客户端

不采用。它会增加跨平台进程管理、可执行文件发现、输出解析和 Tauri 权限边界
复杂度，也不利于在 Java 模块内统一管理 Session 生命周期。

## 验证方式

- `mvn -pl linkssh-execution -am test` 通过
- `mvn clean verify` 通过
- Spring Modulith 与 ArchUnit 验证没有技术类型泄漏到 Domain 或公开 API
- 集成测试证明错误主机指纹无法连接
- 仓库扫描不包含真实凭据或关闭主机密钥校验的配置

## 重新评估条件

- Apache MINA SSHD 2.x 停止安全维护
- 3.x 发布稳定版本且提供必须的新能力
- 真实负载证明当前客户端存在无法规避的性能或兼容性问题
- 需要使用系统 SSH Agent、硬件密钥或平台原生凭据能力，而当前库无法满足

## 参考资料

- Apache MINA SSHD 项目：https://mina.apache.org/sshd-project/
- Apache MINA SSHD 客户端设置：https://github.com/apache/mina-sshd/blob/master/docs/client-setup.md
- Apache MINA SSHD 发布记录：https://github.com/apache/mina-sshd/releases
- mwiede JSch：https://github.com/mwiede/jsch
