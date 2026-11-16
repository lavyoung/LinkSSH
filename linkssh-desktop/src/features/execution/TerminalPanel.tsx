import {
    Copy,
    Maximize2,
    MoreHorizontal,
    SquareTerminal,
} from "lucide-react";
import {
    DEFAULT_HOST_ID,
    findHostById,
} from "../host/host.fixtures";
import { useHostSelectionStore } from "../host/host.store";

export function TerminalPanel() {
    const selectedHostId = useHostSelectionStore(
        (state) => state.selectedHostId,
    );

    const selectedHost =
        findHostById(selectedHostId) ?? findHostById(DEFAULT_HOST_ID);

    if (!selectedHost) {
        return (
            <section className="flex items-center justify-center rounded-xl border border-border bg-card text-sm text-muted-foreground">
                没有可用主机
            </section>
        );
    }

    const connected = selectedHost.status === "online";
    const prompt = `${selectedHost.username}@${selectedHost.name}`;

    return (
        <section
            aria-label="终端工作区"
            className="flex min-w-0 flex-col overflow-hidden rounded-xl border border-border bg-card text-card-foreground shadow-sm"
        >
            <header className="flex h-12 shrink-0 items-center border-b border-border px-3">
                <div className="flex h-full min-w-0 items-center">
                    <div className="flex h-full min-w-0 items-center gap-2 border-b-2 border-foreground px-3 text-sm font-semibold">
                        <SquareTerminal
                            className="size-4 shrink-0"
                            aria-hidden="true"
                        />
                        <span className="truncate">{selectedHost.name}</span>
                        <span
                            className={[
                                "size-2 shrink-0 rounded-full",
                                connected
                                    ? "bg-emerald-500"
                                    : "bg-muted-foreground/40",
                            ].join(" ")}
                        />
                    </div>
                </div>

                <div className="ml-auto flex items-center gap-1">
                    <button
                        type="button"
                        className="flex size-8 items-center justify-center rounded-md text-muted-foreground hover:bg-accent hover:text-accent-foreground"
                        aria-label="复制终端内容"
                    >
                        <Copy className="size-4" />
                    </button>

                    <button
                        type="button"
                        className="flex size-8 items-center justify-center rounded-md text-muted-foreground hover:bg-accent hover:text-accent-foreground"
                        aria-label="最大化终端"
                    >
                        <Maximize2 className="size-4" />
                    </button>

                    <button
                        type="button"
                        className="flex size-8 items-center justify-center rounded-md text-muted-foreground hover:bg-accent hover:text-accent-foreground"
                        aria-label="更多终端操作"
                    >
                        <MoreHorizontal className="size-4" />
                    </button>
                </div>
            </header>

            <div className="flex min-h-0 flex-1 flex-col bg-[#0f1115] text-[#d7dae0]">
                <div className="min-h-0 flex-1 overflow-auto p-5 font-mono text-[13px] leading-6">
                    {connected ? (
                        <>
                            <p className="text-[#7f8793]">
                                Last login: Sun Nov 16 16:42:18 2026
                            </p>
                            <p>
                                <span className="text-emerald-400">{prompt}</span>
                                <span className="text-[#7f8793]">:</span>
                                <span className="text-sky-400">~</span>
                                <span className="text-[#7f8793]">$</span>
                                <span className="ml-2">
                  systemctl status linkssh-agent
                </span>
                            </p>
                            <p className="text-emerald-400">
                                ● linkssh-agent.service - LinkSSH Agent Runtime
                            </p>
                            <p className="pl-4 text-[#9da4af]">
                                Active: active (running) since Sun 2026-11-16 15:28:03 HKT
                            </p>
                            <p className="pl-4 text-[#9da4af]">
                                Memory: 184.2M · Tasks: 24 · Uptime: 1h 18min
                            </p>
                            <p className="mt-3">
                                <span className="text-emerald-400">{prompt}</span>
                                <span className="text-[#7f8793]">:</span>
                                <span className="text-sky-400">~</span>
                                <span className="text-[#7f8793]">$</span>
                                <span className="ml-2 inline-block h-4 w-2 animate-pulse bg-[#d7dae0] align-middle" />
                            </p>
                        </>
                    ) : (
                        <div className="flex h-full items-center justify-center">
                            <div className="text-center">
                                <p className="font-sans text-sm font-semibold text-[#d7dae0]">
                                    {selectedHost.name} 当前离线
                                </p>
                                <p className="mt-2 font-sans text-xs text-[#7f8793]">
                                    后续将在主机管理用例中提供连接测试。
                                </p>
                            </div>
                        </div>
                    )}
                </div>

                <footer className="flex h-8 shrink-0 items-center gap-4 border-t border-white/10 px-4 font-mono text-[11px] text-[#8d95a1]">
                    <span>SSH</span>
                    <span>UTF-8</span>
                    <span>bash</span>
                    <span
                        className={[
                            "ml-auto",
                            connected ? "text-emerald-400" : "text-[#7f8793]",
                        ].join(" ")}
                    >
            {connected ? "CONNECTED" : "OFFLINE"}
          </span>
                </footer>
            </div>
        </section>
    );
}
