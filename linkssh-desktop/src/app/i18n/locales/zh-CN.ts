const zhCN = {
    app: {
        name: "LinkSSH",
        subtitle: "AI Shell 智能终端",
    },
    common: {
        close: "关闭",
        settings: "设置",
    },
    language: {
        label: "语言",
        simplifiedChinese: "简体中文",
        english: "English",
    },
    theme: {
        label: "主题",
        light: {
            name: "浅色",
            description: "适合明亮环境的高对比浅色主题",
        },
        dark: {
            name: "深色",
            description: "适合日常终端工作的中性深色主题",
        },
        midnight: {
            name: "午夜",
            description: "带有蓝色倾向的 GitHub 风格深色主题",
        },
        forest: {
            name: "森林",
            description: "带有绿色倾向的低饱和深色主题",
        },
    },
    header: {
        currentConnection: "当前连接：{{name}}",
        noConnection: "当前没有连接",
        addConnection: "添加 SSH 连接",
        addConnectionUnavailable: "主机管理功能将在后续章节实现",
        toggleSidebar: "显示或隐藏主机栏",
        toggleTerminal: "显示或隐藏终端",
        toggleAssistant: "显示或隐藏 AI 助手",
    },
    activity: {
        label: "侧栏功能",
        hosts: "主机",
        files: "文件",
        sftp: "SFTP",
        settings: "设置",
    },
    sidebar: {
        hosts: {
            add: "添加主机",
            search: "搜索主机",
            groups: "主机",
            menu: "主机菜单",
            online: "在线",
            offline: "离线",
            backendOnline: "后端服务在线",
        },
        files: {
            description: "文件浏览功能将在远程执行章节中实现。",
        },
        sftp: {
            description: "SFTP 文件传输功能将在远程执行章节中实现。",
        },
        settings: {
            description: "完整设置面板将在本章后续步骤中实现。",
        },
    },
    fileBrowser: {
        refresh: "刷新文件树",
        unavailable: "真实文件访问将在后续章节实现",
        treeLabel: "{{name}}文件树",
        filesDemo: "当前显示教程使用的本地文件演示数据",
        sftpDemo: "当前显示教程使用的远程文件演示数据",
    },
    agent: {
        title: "AI 助手",
        notConnected: "当前未连接模型",
        newConversation: "新建会话",
        history: "会话历史",
        recentSessions: "最近会话",
        emptyTitle: "让 AI 协助处理远程任务",
        emptyDescription:
            "AI 后续可分析服务器状态、解释日志，并在治理和审批后执行命令。",
        userMessage: "用户消息",
        assistantMessage: "AI 助手消息",
        inputPlaceholder: "描述你希望 AI 协助完成的任务",
        attach: "添加附件",
        model: "模型",
        modelPlaceholder: "Spring AI（尚未连接）",
        send: "发送消息",
        unavailable: "完成 Agent 联调后可用",
        governanceNotice: "所有工具调用和命令都必须经过治理校验",
        suggestions: {
            serverStatus: "检查服务器状态",
            errorLogs: "分析错误日志",
            diskCleanup: "生成磁盘清理方案",
        },
    },
    terminal: {
        workspace: "终端工作区",
        noHost: "没有可用主机",
        copy: "复制终端内容",
        maximize: "最大化终端",
        more: "更多终端操作",
        unavailable: "真实终端将在远程执行章节实现",
        connected: "已连接",
        offline: "离线",
        offlineTitle: "{{name}} 当前离线",
        offlineDescription: "后续将在主机管理用例中提供连接测试。",
    },
    settings: {
        title: "设置",
        sections: {
            general: "常规",
            appearance: "外观",
            terminal: "终端",
            about: "关于",
        },
        general: {
            title: "常规设置",
            description: "管理 LinkSSH 的通用桌面应用行为。",
            pending: "会话恢复等常规设置将在会话生命周期实现后开放。",
        },
        appearance: {
            title: "外观",
            description: "调整应用主题和界面语言。",
            theme: "颜色主题",
            language: "界面语言",
        },
        terminal: {
            title: "终端",
            description: "调整终端区域使用的字体和字号。",
            fontFamily: "字体",
            fontSize: "字号",
            fonts: {
                "system-mono": "系统等宽字体",
                "cascadia-code": "Cascadia Code",
                "jetbrains-mono": "JetBrains Mono",
            },
        },
        about: {
            title: "关于 LinkSSH",
            description: "面向开发者和运维人员的 AI SSH 智能终端。",
            version: "版本",
            stack: "技术栈",
        },
    },
    layout: {
        noPanelVisible: "当前没有显示任何工作面板",
        resizeSidebar: "调整左侧栏宽度",
        resizeAssistant: "调整 AI 助手面板宽度",
    },
};

export default zhCN;
