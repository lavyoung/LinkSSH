import type { FileTreeNode } from "./file-tree.types";

export const localFileTree = [
    {
        id: "local-linkssh",
        name: "linkssh",
        kind: "directory",
        children: [
            {
                id: "local-docs",
                name: "docs",
                kind: "directory",
                children: [
                    {
                        id: "local-adr",
                        name: "adr",
                        kind: "directory",
                        children: [
                            {
                                id: "local-adr-0001",
                                name: "0001-modular-monolith-ddd-java21.md",
                                kind: "file",
                            },
                            {
                                id: "local-adr-0002",
                                name: "0002-desktop-internationalization.md",
                                kind: "file",
                            },
                        ],
                    },
                ],
            },
            {
                id: "local-desktop",
                name: "linkssh-desktop",
                kind: "directory",
                children: [
                    {
                        id: "local-desktop-src",
                        name: "src",
                        kind: "directory",
                        children: [
                            {
                                id: "local-desktop-app",
                                name: "app",
                                kind: "directory",
                                children: [],
                            },
                            {
                                id: "local-desktop-features",
                                name: "features",
                                kind: "directory",
                                children: [],
                            },
                            {
                                id: "local-desktop-main",
                                name: "main.tsx",
                                kind: "file",
                            },
                        ],
                    },
                    {
                        id: "local-package",
                        name: "package.json",
                        kind: "file",
                    },
                ],
            },
            {
                id: "local-project-plan",
                name: "PROJECT_PLAN.md",
                kind: "file",
            },
            {
                id: "local-pom",
                name: "pom.xml",
                kind: "file",
            },
        ],
    },
] satisfies readonly FileTreeNode[];

export const sftpFileTree = [
    {
        id: "remote-home-deploy",
        name: "/home/deploy",
        kind: "directory",
        children: [
            {
                id: "remote-apps",
                name: "apps",
                kind: "directory",
                children: [
                    {
                        id: "remote-linkssh",
                        name: "linkssh-agent",
                        kind: "directory",
                        children: [
                            {
                                id: "remote-linkssh-config",
                                name: "application.yml",
                                kind: "file",
                            },
                            {
                                id: "remote-linkssh-jar",
                                name: "linkssh-agent.jar",
                                kind: "file",
                            },
                        ],
                    },
                ],
            },
            {
                id: "remote-logs",
                name: "logs",
                kind: "directory",
                children: [
                    {
                        id: "remote-application-log",
                        name: "application.log",
                        kind: "file",
                    },
                    {
                        id: "remote-error-log",
                        name: "error.log",
                        kind: "file",
                    },
                ],
            },
            {
                id: "remote-readme",
                name: "README.txt",
                kind: "file",
            },
        ],
    },
] satisfies readonly FileTreeNode[];
