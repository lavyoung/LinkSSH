export type AgentMessageRole = "user" | "assistant";

export interface AgentMessage {
    id: string;
    role: AgentMessageRole;
    content: string;
}

export interface AgentSessionSummary {
    id: string;
    title: string;
    updatedAt: string;
}
