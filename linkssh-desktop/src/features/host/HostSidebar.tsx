import {
    ChevronDown,
    CirclePlus,
    Moon,
    MoreHorizontal,
    Search,
    Server,
    Settings,
    SquareTerminal,
    Wifi,
} from "lucide-react";
import { hostGroups } from "./host.fixtures";
import { useHostSelectionStore } from "./host.store";
import type { HostStatus } from "./host.types";

function HostStatusDot({ status }: { status: HostStatus }) {
    const color =
        status === "online" ? "bg-emerald-500" : "bg-muted-foreground/40";

    return (
        <span
            className={`size-2 shrink-0 rounded-full ${color}`}
            aria-label={status === "online" ? "在线" : "离线"}
        />
    );
}

export function HostSidebar() {
    const selectedHostId = useHostSelectionStore(
        (state) => state.selectedHostId,
    );
    const selectHost = useHostSelectionStore((state) => state.selectHost);

    return (
        <aside
            aria-label="主机导航"
            className="flex min-h-0 flex-col overflow-hidden rounded-xl border border-sidebar-border bg-sidebar text-sidebar-foreground shadow-sm"
        >
            <header className="flex h-16 shrink-0 items-center gap-3 border-b border-sidebar-border px-4">
        <span className="flex size-9 items-center justify-center rounded-lg bg-sidebar-primary text-sidebar-primary-foreground">
          <SquareTerminal className="size-5" aria-hidden="true" />
        </span>

                <div className="min-w-0 flex-1">
                    <p className="truncate text-sm font-bold">LinkSSH</p>
                    <p className="truncate text-xs text-muted-foreground">
                        AI Shell Workspace
                    </p>
                </div>

                <button
                    type="button"
                    className="flex size-8 items-center justify-center rounded-md text-muted-foreground transition-colors hover:bg-sidebar-accent hover:text-sidebar-accent-foreground"
                    aria-label="添加主机"
                >
                    <CirclePlus className="size-4" />
                </button>
            </header>

            <div className="shrink-0 border-b border-sidebar-border p-3">
                <label className="relative block">
                    <span className="sr-only">搜索主机</span>
                    <Search
                        className="pointer-events-none absolute left-3 top-1/2 size-4 -translate-y-1/2 text-muted-foreground"
                        aria-hidden="true"
                    />
                    <input
                        type="search"
                        placeholder="搜索主机"
                        className="h-9 w-full rounded-md border border-input bg-background pl-9 pr-3 text-sm outline-none transition-shadow placeholder:text-muted-foreground focus-visible:ring-2 focus-visible:ring-ring/40"
                    />
                </label>
            </div>

            <div className="min-h-0 flex-1 overflow-y-auto px-3 py-4">
                <div className="mb-4 flex items-center justify-between px-1">
                    <p className="text-xs font-semibold uppercase tracking-wider text-muted-foreground">
                        主机
                    </p>

                    <button
                        type="button"
                        className="flex size-7 items-center justify-center rounded-md text-muted-foreground hover:bg-sidebar-accent hover:text-sidebar-accent-foreground"
                        aria-label="主机菜单"
                    >
                        <MoreHorizontal className="size-4" />
                    </button>
                </div>

                <div className="space-y-5">
                    {hostGroups.map((group) => (
                        <section key={group.id}>
                            <button
                                type="button"
                                className="mb-1 flex h-8 w-full items-center gap-2 rounded-md px-2 text-xs font-semibold text-muted-foreground hover:bg-sidebar-accent hover:text-sidebar-accent-foreground"
                            >
                                <ChevronDown className="size-3.5" aria-hidden="true" />
                                <span>{group.name}</span>
                                <span className="ml-auto text-[11px] font-normal">
                  {group.hosts.length}
                </span>
                            </button>

                            <div className="space-y-1">
                                {group.hosts.map((host) => {
                                    const selected = host.id === selectedHostId;

                                    return (
                                        <button
                                            key={host.id}
                                            type="button"
                                            aria-pressed={selected}
                                            onClick={() => selectHost(host.id)}
                                            className={[
                                                "flex h-[46px] w-full items-center gap-3 rounded-lg px-3 text-left transition-colors",
                                                selected
                                                    ? "bg-sidebar-accent font-bold text-sidebar-accent-foreground"
                                                    : "font-semibold text-muted-foreground hover:bg-sidebar-accent/70 hover:text-sidebar-accent-foreground",
                                            ].join(" ")}
                                        >
                                            <Server
                                                className="size-5 shrink-0"
                                                aria-hidden="true"
                                            />

                                            <span className="min-w-0 flex-1">
                        <span className="block truncate text-sm">
                          {host.name}
                        </span>
                        <span className="block truncate text-xs font-normal text-muted-foreground">
                          {host.address}
                        </span>
                      </span>

                                            <HostStatusDot status={host.status} />
                                        </button>
                                    );
                                })}
                            </div>
                        </section>
                    ))}
                </div>
            </div>

            <footer className="shrink-0 border-t border-sidebar-border p-3">
                <div className="mb-2 flex h-9 items-center gap-2 rounded-md bg-emerald-500/10 px-3 text-xs font-semibold text-emerald-700 dark:text-emerald-400">
                    <Wifi className="size-4" aria-hidden="true" />
                    <span>后端服务在线</span>
                </div>

                <div className="grid grid-cols-2 gap-2">
                    <button
                        type="button"
                        className="flex h-9 items-center justify-center gap-2 rounded-md text-xs font-semibold text-muted-foreground hover:bg-sidebar-accent hover:text-sidebar-accent-foreground"
                    >
                        <Moon className="size-4" aria-hidden="true" />
                        主题
                    </button>

                    <button
                        type="button"
                        className="flex h-9 items-center justify-center gap-2 rounded-md text-xs font-semibold text-muted-foreground hover:bg-sidebar-accent hover:text-sidebar-accent-foreground"
                    >
                        <Settings className="size-4" aria-hidden="true" />
                        设置
                    </button>
                </div>
            </footer>
        </aside>
    );
}
