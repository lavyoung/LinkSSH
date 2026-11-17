import {
    ChevronDown,
    CirclePlus,
    MoreHorizontal,
    Search,
    Server,
    Wifi,
} from "lucide-react";
import { useTranslation } from "react-i18next";
import { hostGroups } from "./host.fixtures";
import { useHostSelectionStore } from "./host.store";
import type { HostStatus } from "./host.types";

interface HostStatusDotProps {
    status: HostStatus;
    label: string;
}

function HostStatusDot({ status, label }: HostStatusDotProps) {
    const color =
        status === "online"
            ? "status-pulse bg-emerald-500"
            : "bg-muted-foreground/40";

    return (
        <span
            className={`size-2 shrink-0 rounded-full ${color}`}
            aria-label={label}
            title={label}
        />
    );
}

export function HostSidebar() {
    const { t } = useTranslation();

    const selectedHostId = useHostSelectionStore(
        (state) => state.selectedHostId,
    );
    const selectHost = useHostSelectionStore(
        (state) => state.selectHost,
    );

    return (
        <section
            aria-label={t("activity.hosts")}
            className="flex h-full min-h-0 flex-col bg-sidebar text-sidebar-foreground"
        >
            <header className="flex h-12 shrink-0 items-center gap-2 border-b border-sidebar-border px-3">
                <Server
                    className="size-4 text-muted-foreground"
                    aria-hidden="true"
                />

                <h2 className="text-xs font-bold uppercase tracking-wider">
                    {t("activity.hosts")}
                </h2>

                <button
                    disabled
                    type="button"
                    aria-label={t("sidebar.hosts.add")}
                    title={t("header.addConnectionUnavailable")}
                    className="ml-auto flex size-8 items-center justify-center rounded-md text-muted-foreground hover:bg-sidebar-accent hover:text-sidebar-accent-foreground disabled:cursor-not-allowed disabled:opacity-50"
                >
                    <CirclePlus className="size-4" aria-hidden="true" />
                </button>
            </header>

            <div className="shrink-0 border-b border-sidebar-border p-3">
                <label className="relative block">
                    <span className="sr-only">
                        {t("sidebar.hosts.search")}
                    </span>

                    <Search
                        className="pointer-events-none absolute left-3 top-1/2 size-4 -translate-y-1/2 text-muted-foreground"
                        aria-hidden="true"
                    />

                    <input
                        type="search"
                        placeholder={t("sidebar.hosts.search")}
                        className="h-9 w-full rounded-md border border-input bg-background pl-9 pr-3 text-sm outline-none transition-shadow placeholder:text-muted-foreground focus-visible:ring-2 focus-visible:ring-ring/40"
                    />
                </label>
            </div>

            <div className="min-h-0 flex-1 overflow-y-auto px-3 py-4">
                <div className="mb-4 flex items-center justify-between px-1">
                    <p className="text-xs font-semibold uppercase tracking-wider text-muted-foreground">
                        {t("sidebar.hosts.groups")}
                    </p>

                    <button
                        disabled
                        type="button"
                        aria-label={t("sidebar.hosts.menu")}
                        title={t("sidebar.hosts.menu")}
                        className="flex size-7 items-center justify-center rounded-md text-muted-foreground hover:bg-sidebar-accent hover:text-sidebar-accent-foreground disabled:cursor-not-allowed disabled:opacity-50"
                    >
                        <MoreHorizontal
                            className="size-4"
                            aria-hidden="true"
                        />
                    </button>
                </div>

                <div className="space-y-5">
                    {hostGroups.map((group) => (
                        <section key={group.id}>
                            <button
                                type="button"
                                className="mb-1 flex h-8 w-full items-center gap-2 rounded-md px-2 text-xs font-semibold text-muted-foreground hover:bg-sidebar-accent hover:text-sidebar-accent-foreground"
                            >
                                <ChevronDown
                                    className="size-3.5"
                                    aria-hidden="true"
                                />
                                <span>{group.name}</span>
                                <span className="ml-auto text-[11px] font-normal">
                                    {group.hosts.length}
                                </span>
                            </button>

                            <div className="space-y-1">
                                {group.hosts.map((host) => {
                                    const selected =
                                        host.id === selectedHostId;

                                    const statusLabel =
                                        host.status === "online"
                                            ? t("sidebar.hosts.online")
                                            : t("sidebar.hosts.offline");

                                    return (
                                        <button
                                            key={host.id}
                                            type="button"
                                            aria-pressed={selected}
                                            onClick={() =>
                                                selectHost(host.id)
                                            }
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

                                            <HostStatusDot
                                                status={host.status}
                                                label={statusLabel}
                                            />
                                        </button>
                                    );
                                })}
                            </div>
                        </section>
                    ))}
                </div>
            </div>

            <footer className="shrink-0 border-t border-sidebar-border p-3">
                <div className="flex h-9 items-center gap-2 rounded-md bg-emerald-500/10 px-3 text-xs font-semibold text-emerald-700 dark:text-emerald-400">
                    <Wifi className="size-4" aria-hidden="true" />
                    <span>{t("sidebar.hosts.backendOnline")}</span>
                </div>
            </footer>
        </section>
    );
}
