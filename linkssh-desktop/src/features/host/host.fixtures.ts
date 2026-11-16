import type { HostGroup, HostItem } from "./host.types";

export const DEFAULT_HOST_ID = "production-web-01";

export const hostGroups: HostGroup[] = [
    {
        id: "production",
        name: "生产环境",
        hosts: [
            {
                id: "production-web-01",
                name: "production-web-01",
                address: "10.0.1.21",
                username: "deploy",
                status: "online",
            },
            {
                id: "production-db-01",
                name: "production-db-01",
                address: "10.0.1.31",
                username: "database",
                status: "offline",
            },
        ],
    },
    {
        id: "staging",
        name: "测试环境",
        hosts: [
            {
                id: "staging-api-01",
                name: "staging-api-01",
                address: "10.0.2.11",
                username: "staging",
                status: "online",
            },
        ],
    },
];

export function findHostById(hostId: string): HostItem | undefined {
    return hostGroups
        .flatMap((group) => group.hosts)
        .find((host) => host.id === hostId);
}
