import type zhCN from "./zh-CN";

const enUS = {
    app: {
        name: "LinkSSH",
        subtitle: "AI Shell Terminal",
    },
    common: {
        close: "Close",
        settings: "Settings",
    },
    language: {
        label: "Language",
        simplifiedChinese: "简体中文",
        english: "English",
    },
    theme: {
        label: "Theme",
        light: {
            name: "Light",
            description: "A high-contrast light theme for bright environments",
        },
        dark: {
            name: "Dark",
            description: "A neutral dark theme for everyday terminal work",
        },
        midnight: {
            name: "Midnight",
            description: "A GitHub-inspired dark theme with a blue tint",
        },
        forest: {
            name: "Forest",
            description: "A low-saturation dark theme with a green tint",
        },
    },
    header: {
        currentConnection: "Current connection: {{name}}",
        noConnection: "No active connection",
        addConnection: "Add SSH connection",
        addConnectionUnavailable:
            "Host management will be implemented in a later chapter",
        toggleSidebar: "Show or hide host sidebar",
        toggleTerminal: "Show or hide terminal",
        toggleAssistant: "Show or hide AI assistant",
    },
    activity: {
        label: "Sidebar features",
        hosts: "Hosts",
        files: "Files",
        sftp: "SFTP",
        settings: "Settings",
    },
    sidebar: {
        hosts: {
            add: "Add host",
            search: "Search hosts",
            groups: "Hosts",
            menu: "Host menu",
            online: "Online",
            offline: "Offline",
            backendOnline: "Backend service online",
        },
        files: {
            description:
                "File browsing will be implemented in the remote execution chapter.",
        },
        sftp: {
            description:
                "SFTP file transfer will be implemented in the remote execution chapter.",
        },
        settings: {
            description:
                "The complete settings panel will be implemented later in this chapter.",
        },
    },
    fileBrowser: {
        refresh: "Refresh file tree",
        unavailable:
            "Real file access will be implemented in a later chapter",
        treeLabel: "{{name}} file tree",
        filesDemo:
            "Showing local file demonstration data for this tutorial",
        sftpDemo:
            "Showing remote file demonstration data for this tutorial",
    },
    agent: {
        title: "AI Assistant",
        notConnected: "No model connected",
        newConversation: "New conversation",
        history: "Conversation history",
        recentSessions: "Recent conversations",
        emptyTitle: "Let AI assist with remote tasks",
        emptyDescription:
            "AI will analyze server status, explain logs, and execute commands after governance and approval.",
        userMessage: "User message",
        assistantMessage: "AI assistant message",
        inputPlaceholder: "Describe the task you want AI to assist with",
        attach: "Add attachment",
        model: "Model",
        modelPlaceholder: "Spring AI (not connected)",
        send: "Send message",
        unavailable: "Available after Agent integration",
        governanceNotice:
            "All tool calls and commands must pass governance checks",
        suggestions: {
            serverStatus: "Check server status",
            errorLogs: "Analyze error logs",
            diskCleanup: "Generate a disk cleanup plan",
        },
    },
    terminal: {
        workspace: "Terminal workspace",
        noHost: "No host available",
        copy: "Copy terminal content",
        maximize: "Maximize terminal",
        more: "More terminal actions",
        unavailable: "The real terminal will be implemented in the remote execution chapter",
        connected: "Connected",
        offline: "Offline",
        offlineTitle: "{{name}} is currently offline",
        offlineDescription: "Connection testing will be provided by the host management use case.",
    },
    settings: {
        title: "Settings",
        sections: {
            general: "General",
            appearance: "Appearance",
            terminal: "Terminal",
            about: "About",
        },
        general: {
            title: "General settings",
            description: "Manage general LinkSSH desktop behavior.",
            pending:
                "Session restoration settings will become available after the session lifecycle is implemented.",
        },
        appearance: {
            title: "Appearance",
            description: "Change the application theme and language.",
            theme: "Color theme",
            language: "Interface language",
        },
        terminal: {
            title: "Terminal",
            description:
                "Change the font and font size used by the terminal area.",
            fontFamily: "Font",
            fontSize: "Font size",
            fonts: {
                "system-mono": "System monospace",
                "cascadia-code": "Cascadia Code",
                "jetbrains-mono": "JetBrains Mono",
            },
        },
        about: {
            title: "About LinkSSH",
            description:
                "An AI SSH terminal for developers and operations engineers.",
            version: "Version",
            stack: "Technology stack",
        },
    },
    layout: {
        noPanelVisible: "No workspace panel is currently visible",
        resizeSidebar: "Resize left sidebar",
        resizeAssistant: "Resize AI assistant panel",
    },
} satisfies typeof zhCN;

export default enUS;
