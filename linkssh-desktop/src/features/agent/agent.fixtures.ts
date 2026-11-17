import type {
    AgentMessage,
    AgentSessionSummary,
} from "./agent.types";

export const DEMO_AGENT_MESSAGES: readonly AgentMessage[] = [
    {
        id: "message-user-service-status",
        role: "user",
        content: "Check the status of `linkssh-agent.service`.",
    },
    {
        id: "message-assistant-service-status",
        role: "assistant",
        content: [
            "The service is currently **active**.",
            "",
            "- Status: `active (running)`",
            "- Memory: `184.2 MB`",
            "- Tasks: `24`",
            "",
            "> This is demonstration data. No model or remote command was invoked.",
        ].join("\n"),
    },
];

export const DEMO_AGENT_SESSIONS: readonly AgentSessionSummary[] = [
    {
        id: "session-service-status",
        title: "linkssh-agent.service",
        updatedAt: "2026-11-17T09:30:00+08:00",
    },
    {
        id: "session-error-log",
        title: "error.log analysis",
        updatedAt: "2026-11-16T17:45:00+08:00",
    },
];
