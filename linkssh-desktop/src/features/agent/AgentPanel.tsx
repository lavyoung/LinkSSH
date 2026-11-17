import {
    Bot,
    Clock3,
    History,
    MessageSquarePlus,
    Paperclip,
    Send,
    Sparkles,
    User,
} from "lucide-react";
import { useState } from "react";
import ReactMarkdown from "react-markdown";
import { useTranslation } from "react-i18next";
import remarkGfm from "remark-gfm";
import {
    DEMO_AGENT_MESSAGES,
    DEMO_AGENT_SESSIONS,
} from "./agent.fixtures";
import type { AgentMessage } from "./agent.types";

export function AgentPanel() {
    const { t, i18n } = useTranslation();

    const [messages, setMessages] = useState<
        readonly AgentMessage[]
    >(DEMO_AGENT_MESSAGES);
    const [draft, setDraft] = useState("");
    const [historyVisible, setHistoryVisible] = useState(false);

    const suggestions = [
        t("agent.suggestions.serverStatus"),
        t("agent.suggestions.errorLogs"),
        t("agent.suggestions.diskCleanup"),
    ];

    function startNewConversation() {
        setMessages([]);
        setDraft("");
        setHistoryVisible(false);
    }

    function restoreDemoSession() {
        setMessages(DEMO_AGENT_MESSAGES);
        setDraft("");
        setHistoryVisible(false);
    }

    function formatSessionTime(updatedAt: string) {
        return new Intl.DateTimeFormat(
            i18n.resolvedLanguage ?? "zh-CN",
            {
                month: "short",
                day: "numeric",
                hour: "2-digit",
                minute: "2-digit",
            },
        ).format(new Date(updatedAt));
    }

    return (
        <aside
            aria-label={t("agent.title")}
            className="flex min-h-0 flex-col overflow-hidden rounded-xl border border-border bg-card text-card-foreground shadow-sm"
        >
            <header className="flex h-12 shrink-0 items-center gap-3 border-b border-border px-3">
                <span className="flex size-8 shrink-0 items-center justify-center rounded-lg bg-primary text-primary-foreground">
                    <Bot className="size-4" aria-hidden="true" />
                </span>

                <div className="min-w-0">
                    <p className="truncate text-sm font-bold">
                        {t("agent.title")}
                    </p>
                    <p className="truncate text-xs text-muted-foreground">
                        {t("agent.notConnected")}
                    </p>
                </div>

                <div className="ml-auto flex items-center gap-1">
                    <button
                        type="button"
                        aria-label={t("agent.history")}
                        title={t("agent.history")}
                        aria-pressed={historyVisible}
                        onClick={() =>
                            setHistoryVisible((visible) => !visible)
                        }
                        className={[
                            "flex size-8 items-center justify-center rounded-md transition-colors",
                            historyVisible
                                ? "bg-accent text-accent-foreground"
                                : "text-muted-foreground hover:bg-accent hover:text-accent-foreground",
                        ].join(" ")}
                    >
                        <History
                            className="size-4"
                            aria-hidden="true"
                        />
                    </button>

                    <button
                        type="button"
                        aria-label={t("agent.newConversation")}
                        title={t("agent.newConversation")}
                        onClick={startNewConversation}
                        className="flex size-8 items-center justify-center rounded-md text-muted-foreground transition-colors hover:bg-accent hover:text-accent-foreground"
                    >
                        <MessageSquarePlus
                            className="size-4"
                            aria-hidden="true"
                        />
                    </button>
                </div>
            </header>

            {historyVisible && (
                <section className="shrink-0 border-b border-border bg-muted/30 p-2">
                    <h2 className="px-2 py-1 text-[11px] font-bold uppercase tracking-wider text-muted-foreground">
                        {t("agent.recentSessions")}
                    </h2>

                    <div className="mt-1 space-y-1">
                        {DEMO_AGENT_SESSIONS.map((session) => (
                            <button
                                key={session.id}
                                type="button"
                                onClick={restoreDemoSession}
                                className="flex w-full items-center gap-2 rounded-md px-2 py-2 text-left transition-colors hover:bg-accent hover:text-accent-foreground"
                            >
                                <Clock3
                                    className="size-4 shrink-0 text-muted-foreground"
                                    aria-hidden="true"
                                />

                                <span className="min-w-0 flex-1">
                                    <span className="block truncate text-xs font-semibold">
                                        {session.title}
                                    </span>
                                    <span className="block truncate text-[11px] text-muted-foreground">
                                        {formatSessionTime(
                                            session.updatedAt,
                                        )}
                                    </span>
                                </span>
                            </button>
                        ))}
                    </div>
                </section>
            )}

            <div className="min-h-0 flex-1 overflow-y-auto p-4">
                {messages.length === 0 ? (
                    <div className="flex h-full items-center justify-center text-center">
                        <div>
                            <span className="mx-auto flex size-12 items-center justify-center rounded-xl bg-muted">
                                <Sparkles
                                    className="size-5 text-muted-foreground"
                                    aria-hidden="true"
                                />
                            </span>

                            <h2 className="mt-4 text-sm font-bold">
                                {t("agent.emptyTitle")}
                            </h2>

                            <p className="mx-auto mt-2 max-w-60 text-xs leading-5 text-muted-foreground">
                                {t("agent.emptyDescription")}
                            </p>
                        </div>
                    </div>
                ) : (
                    <div className="space-y-4">
                        {messages.map((message) => {
                            const assistant =
                                message.role === "assistant";

                            return (
                                <article
                                    key={message.id}
                                    className={[
                                        "flex gap-2",
                                        assistant
                                            ? "items-start"
                                            : "items-start justify-end",
                                    ].join(" ")}
                                >
                                    {assistant && (
                                        <span className="flex size-7 shrink-0 items-center justify-center rounded-md bg-primary text-primary-foreground">
                                            <Bot
                                                className="size-3.5"
                                                aria-hidden="true"
                                            />
                                        </span>
                                    )}

                                    <div
                                        className={[
                                            "min-w-0 max-w-[85%] rounded-lg px-3 py-2 text-xs leading-5",
                                            assistant
                                                ? "border border-border bg-background"
                                                : "bg-primary text-primary-foreground",
                                        ].join(" ")}
                                    >
                                        <span className="sr-only">
                                            {assistant
                                                ? t(
                                                    "agent.assistantMessage",
                                                )
                                                : t("agent.userMessage")}
                                        </span>

                                        <ReactMarkdown
                                            skipHtml
                                            remarkPlugins={[remarkGfm]}
                                            components={{
                                                p: ({ children }) => (
                                                    <p className="mb-2 last:mb-0">
                                                        {children}
                                                    </p>
                                                ),
                                                ul: ({ children }) => (
                                                    <ul className="mb-2 list-disc space-y-1 pl-4 last:mb-0">
                                                        {children}
                                                    </ul>
                                                ),
                                                li: ({ children }) => (
                                                    <li>{children}</li>
                                                ),
                                                strong: ({ children }) => (
                                                    <strong className="font-bold">
                                                        {children}
                                                    </strong>
                                                ),
                                                code: ({ children }) => (
                                                    <code className="rounded bg-muted px-1 py-0.5 font-mono text-[11px] text-foreground">
                                                        {children}
                                                    </code>
                                                ),
                                                blockquote: ({
                                                                 children,
                                                             }) => (
                                                    <blockquote className="border-l-2 border-border pl-2 text-muted-foreground">
                                                        {children}
                                                    </blockquote>
                                                ),
                                            }}
                                        >
                                            {message.content}
                                        </ReactMarkdown>
                                    </div>

                                    {!assistant && (
                                        <span className="flex size-7 shrink-0 items-center justify-center rounded-md bg-muted text-muted-foreground">
                                            <User
                                                className="size-3.5"
                                                aria-hidden="true"
                                            />
                                        </span>
                                    )}
                                </article>
                            );
                        })}
                    </div>
                )}
            </div>

            <footer className="shrink-0 border-t border-border p-3">
                <div className="mb-2 flex gap-1 overflow-x-auto pb-1">
                    {suggestions.map((suggestion) => (
                        <button
                            key={suggestion}
                            type="button"
                            onClick={() => setDraft(suggestion)}
                            className="shrink-0 rounded-full border border-border bg-background px-2.5 py-1 text-[11px] text-muted-foreground transition-colors hover:bg-accent hover:text-accent-foreground"
                        >
                            {suggestion}
                        </button>
                    ))}
                </div>

                <div className="rounded-lg border border-input bg-background p-2 focus-within:ring-2 focus-within:ring-ring/40">
                    <textarea
                        rows={3}
                        maxLength={4000}
                        value={draft}
                        onChange={(event) =>
                            setDraft(event.target.value)
                        }
                        placeholder={t("agent.inputPlaceholder")}
                        className="max-h-40 min-h-18 w-full resize-y bg-transparent px-1 text-sm outline-none placeholder:text-muted-foreground"
                    />

                    <div className="mt-2 flex items-center gap-1">
                        <button
                            disabled
                            type="button"
                            aria-label={t("agent.attach")}
                            title={t("agent.unavailable")}
                            className="flex size-8 items-center justify-center rounded-md text-muted-foreground disabled:cursor-not-allowed disabled:opacity-40"
                        >
                            <Paperclip
                                className="size-4"
                                aria-hidden="true"
                            />
                        </button>

                        <label className="min-w-0 flex-1">
                            <span className="sr-only">
                                {t("agent.model")}
                            </span>

                            <select
                                disabled
                                aria-label={t("agent.model")}
                                className="h-8 w-full truncate rounded-md border border-input bg-background px-2 text-[11px] text-muted-foreground disabled:cursor-not-allowed"
                                defaultValue="spring-ai"
                            >
                                <option value="spring-ai">
                                    {t("agent.modelPlaceholder")}
                                </option>
                            </select>
                        </label>

                        <button
                            disabled
                            type="button"
                            aria-label={t("agent.send")}
                            title={t("agent.unavailable")}
                            className="flex size-8 items-center justify-center rounded-md bg-primary text-primary-foreground disabled:cursor-not-allowed disabled:opacity-40"
                        >
                            <Send
                                className="size-4"
                                aria-hidden="true"
                            />
                        </button>
                    </div>
                </div>

                <p className="mt-2 text-center text-[10px] leading-4 text-muted-foreground">
                    {t("agent.governanceNotice")}
                </p>
            </footer>
        </aside>
    );
}
