export type HostStatus = "online" | "offline";

export interface HostItem {
    id: string;
    name: string;
    address: string;
    username: string;
    status: HostStatus;
}

export interface HostGroup {
    id: string;
    name: string;
    hosts: HostItem[];
}
