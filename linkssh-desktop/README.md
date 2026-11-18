# LinkSSH Desktop

`linkssh-desktop` 是 LinkSSH 的跨平台桌面客户端，使用 Tauri 2、React 19、
TypeScript、Vite、Tailwind CSS 4、Zustand 和 i18next 构建。

桌面端与 Java 后端位于同一个 Git 仓库，但使用独立的 npm、Cargo 和 Tauri
构建体系，不加入根 Maven Reactor。

## 目标部署模式

桌面客户端必须支持两种显式运行方式：

- 本地模式：Tauri 启动并监督安装包内置的 Java 后端，通过环回随机端口连接，
  非敏感数据由 Java 后端保存到本机 SQLite。
- 远程模式：客户端连接用户配置的 LinkSSH Server，不启动本地 Java 后端。

两种模式复用统一的 API Client、REST、SSE 和 WebSocket 契约，业务组件不得写死
`localhost:8080` 或远程域名。完整安装包始终包含本地运行能力；远程连接失败时
不得静默切换到本地模式，本地与远程数据首期也不自动同步。

当前工程尚未实现本地后端生命周期、API Endpoint 选择或 SQLite 联调，这些能力
必须按 ADR-0005 和 `PROJECT_PLAN.md` 的 P0.4、P1.2 逐步落地。

## 环境要求

- Node.js 20 或更高版本
- npm
- Rust stable MSVC 工具链
- Windows C++ Build Tools
- Microsoft Edge WebView2

## 当前工程结构

```text
linkssh-desktop/
├── src/                    # React 与 TypeScript 源码
│   ├── app/                # 应用外壳、活动栏、布局、主题与国际化
│   ├── features/           # 按业务能力纵向组织的桌面端功能
│   │   ├── agent/          # AI 助手静态会话与安全 Markdown 展示
│   │   ├── execution/      # 终端、文件树与终端外观偏好
│   │   ├── host/           # 主机侧栏、展示类型、演示数据与选择状态
│   │   └── settings/       # 桌面端设置弹窗
│   ├── styles/             # Tailwind 入口与主题设计令牌
│   ├── App.tsx             # React 根组件
│   └── main.tsx            # React 入口与全局样式导入
├── src-tauri/              # Tauri 原生端工程
│   ├── capabilities/       # Tauri 权限能力声明
│   ├── icons/              # 桌面应用图标
│   ├── src/lib.rs          # Tauri 应用配置与命令注册
│   ├── src/main.rs         # Windows 桌面进程入口
│   ├── Cargo.toml          # Rust 依赖配置
│   └── tauri.conf.json     # Tauri 应用配置
├── index.html              # Vite HTML 入口
├── package.json            # npm 脚本与前端依赖
├── tsconfig.json           # TypeScript 配置
└── vite.config.ts          # Vite 与 Tauri 开发配置
```

前端按业务能力纵向组织，避免形成全局 `components`、`stores`、`types` 大杂烩。
只有真正跨多个业务功能复用的组件或类型，才提升到共享目录。

当前主机数据来自 `host.fixtures.ts`，仅用于教程的静态界面演示；
`host.store.ts` 只管理跨组件共享的选中主机 ID。两者都不是后端 Host 聚合或
真实 SSH 连接状态。

不为尚不存在的职责提前创建空目录或空文件。

## 当前界面范围

- 顶部：当前连接、主题、语言和面板显隐控制
- 左侧：ActivityBar、主机列表及本地文件/SFTP 演示树
- 中间：跟随主题和终端偏好的终端视觉占位，不执行命令
- 右侧：AI 助手静态会话、安全 Markdown 和输入区，不调用模型
- 主机选择：使用 Zustand 驱动终端标题和在线/离线演示状态
- 布局：左右面板支持显隐和有边界的拖拽调整宽度
- 设置：支持主题、语言、终端字体和字号，本地仅保存非敏感偏好

当前不包含主机 CRUD、真实文件访问、SSH 连接、凭据管理、Agent 调用或
前后端联调；文件树、终端输出、会话和模型消息均为教程演示数据。

## 安装依赖

```powershell
npm install
```

## Web 构建

```powershell
npm run build
```

## Rust 检查

```powershell
Set-Location src-tauri
cargo check
```

## 启动桌面应用

```powershell
npm run tauri dev
```

## 安全边界

- Tauri Capability 必须遵循最小权限原则。
- 前端不得直接获得不受控的 Shell、文件系统或凭据访问能力。
- 远程执行、风险判断和凭据管理必须通过 LinkSSH 后端公开 API 完成。
- 密码、私钥、Token 和凭据正文不得进入前端状态、日志或本地存储。
- React 与 Tauri 不得直接访问 SQLite；本地数据库由 Java 后端独占管理。
- 本地后端只能监听环回随机端口，并使用不落盘的短生命周期会话认证。
- 本地后端进程必须由 Tauri 原生端启动、监测并在应用退出时优雅关闭。
