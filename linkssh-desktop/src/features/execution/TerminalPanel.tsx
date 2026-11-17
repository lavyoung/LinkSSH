import {
    Copy,
    Maximize2,
    MoreHorizontal,
    SquareTerminal,
} from "lucide-react";
import { useTranslation } from "react-i18next";
import {
    DEFAULT_HOST_ID,
    findHostById,
} from "../host/host.fixtures";
import { useHostSelectionStore } from "../host/host.store";
import {
    TERMINAL_FONT_STACKS,
    useTerminalPreferencesStore,
} from "./terminal-preferences.store";

export function TerminalPanel() {
    const { t } = useTranslation();

    const fontFamily = useTerminalPreferencesStore(
        (state) => state.fontFamily,
    );
    const fontSize = useTerminalPreferencesStore(
        (state) => state.fontSize,
    );
    const selectedHostId = useHostSelectionStore(
        (state) => state.selectedHostId,
    );

    const terminalFontFamily =
        TERMINAL_FONT_STACKS[fontFamily];

    const selectedHost =
        findHostById(selectedHostId) ??
        findHostById(DEFAULT_HOST_ID);

    if (!selectedHost) {
        return (
            <section className="flex items-center justify-center rounded-xl border border-border bg-card text-sm text-muted-foreground">
                {t("terminal.noHost")}
            </section>
        );
    }

    const connected = selectedHost.status === "online";
    const prompt = `${selectedHost.username}@${selectedHost.name}`;

    return (
        <section
            aria-label={t("terminal.workspace")}
            className="flex min-w-0 flex-col overflow-hidden rounded-xl border border-border bg-card text-card-foreground shadow-sm"
        >
            <header className="flex h-12 shrink-0 items-center border-b border-border px-3">
                <div className="flex h-full min-w-0 items-center">
                    <div className="flex h-full min-w-0 items-center gap-2 border-b-2 border-foreground px-3 text-sm font-semibold">
                        <SquareTerminal
                            className="size-4 shrink-0"
                            aria-hidden="true"
                        />

                        <span className="truncate">
                            {selectedHost.name}
                        </span>

                        <span
                            aria-label={
                                connected
                                    ? t("terminal.connected")
                                    : t("terminal.offline")
                            }
                            className={[
                                "size-2 shrink-0 rounded-full",
                                connected
                                    ? "status-pulse bg-terminal-success"
                                    : "bg-terminal-muted/50",
                            ].join(" ")}
                        />
                    </div>
                </div>

                <div className="ml-auto flex items-center gap-1">
                    <button
                        disabled
                        type="button"
                        aria-label={t("terminal.copy")}
                        title={t("terminal.unavailable")}
                        className="flex size-8 items-center justify-center rounded-md text-muted-foreground disabled:cursor-not-allowed disabled:opacity-40"
                    >
                        <Copy className="size-4" />
                    </button>

                    <button
                        disabled
                        type="button"
                        aria-label={t("terminal.maximize")}
                        title={t("terminal.unavailable")}
                        className="flex size-8 items-center justify-center rounded-md text-muted-foreground disabled:cursor-not-allowed disabled:opacity-40"
                    >
                        <Maximize2 className="size-4" />
                    </button>

                    <button
                        disabled
                        type="button"
                        aria-label={t("terminal.more")}
                        title={t("terminal.unavailable")}
                        className="flex size-8 items-center justify-center rounded-md text-muted-foreground disabled:cursor-not-allowed disabled:opacity-40"
                    >
                        <MoreHorizontal className="size-4" />
                    </button>
                </div>
            </header>

            <div className="flex min-h-0 flex-1 flex-col bg-terminal-background text-terminal-foreground">
                <div
                    className="min-h-0 flex-1 overflow-auto p-5 leading-6"
                    style={{
                        fontFamily: terminalFontFamily,
                        fontSize: `${fontSize}px`,
                    }}
                >
                    {connected ? (
                        <>
                            <p className="text-terminal-muted">
                                Last login: Sun Nov 16 16:42:18 2026
                            </p>

                            <p>
                                <span className="text-terminal-prompt">
                                    {prompt}
                                </span>
                                <span className="text-terminal-muted">
                                    :
                                </span>
                                <span className="text-terminal-path">
                                    ~
                                </span>
                                <span className="text-terminal-muted">
                                    $
                                </span>
                                <span className="ml-2">
                                    systemctl status linkssh-agent
                                </span>
                            </p>

                            <p className="text-terminal-success">
                                ● linkssh-agent.service - LinkSSH Agent
                                Runtime
                            </p>

                            <p className="pl-4 text-terminal-muted">
                                Active: active (running) since Sun
                                2026-11-16 15:28:03 HKT
                            </p>

                            <p className="pl-4 text-terminal-muted">
                                Memory: 184.2M · Tasks: 24 · Uptime:
                                1h 18min
                            </p>

                            <p className="mt-3">
                                <span className="text-terminal-prompt">
                                    {prompt}
                                </span>
                                <span className="text-terminal-muted">
                                    :
                                </span>
                                <span className="text-terminal-path">
                                    ~
                                </span>
                                <span className="text-terminal-muted">
                                    $
                                </span>
                                <span className="ml-2 inline-block h-4 w-2 animate-pulse bg-terminal-cursor align-middle" />
                            </p>
                        </>
                    ) : (
                        <div className="flex h-full items-center justify-center">
                            <div className="text-center">
                                <p className="font-sans text-sm font-semibold text-terminal-foreground">
                                    {t("terminal.offlineTitle", {
                                        name: selectedHost.name,
                                    })}
                                </p>

                                <p className="mt-2 font-sans text-xs text-terminal-muted">
                                    {t(
                                        "terminal.offlineDescription",
                                    )}
                                </p>
                            </div>
                        </div>
                    )}
                </div>

                <footer
                    className="flex h-8 shrink-0 items-center gap-4 border-t border-terminal-border px-4 text-terminal-muted"
                    style={{
                        fontFamily: terminalFontFamily,
                        fontSize: `${Math.max(
                            10,
                            fontSize - 2,
                        )}px`,
                    }}
                >
                    <span>SSH</span>
                    <span>UTF-8</span>
                    <span>bash</span>

                    <span
                        className={[
                            "ml-auto",
                            connected
                                ? "text-terminal-success"
                                : "text-terminal-muted",
                        ].join(" ")}
                    >
                        {connected
                            ? t("terminal.connected")
                            : t("terminal.offline")}
                    </span>
                </footer>
            </div>
        </section>
    );
}
