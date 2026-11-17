import {
    Files,
    FolderSync,
    Server,
    Settings,
    type LucideIcon,
} from "lucide-react";
import { useTranslation } from "react-i18next";
import type { SidebarView } from "./sidebar.types";

interface ActivityBarProps {
    activeView: SidebarView;
    settingsOpen: boolean;
    onChangeView: (view: SidebarView) => void;
    onOpenSettings: () => void;
}

interface ActivityItem {
    view: SidebarView;
    label: string;
    icon: LucideIcon;
}

function activityButtonClass(active: boolean) {
    return [
        "flex h-12 w-full items-center justify-center border-l-2 transition-colors",
        active
            ? "border-sidebar-primary bg-sidebar-accent text-sidebar-accent-foreground"
            : "border-transparent text-muted-foreground hover:bg-sidebar-accent/70 hover:text-sidebar-accent-foreground",
    ].join(" ");
}

export function ActivityBar({
                                activeView,
                                settingsOpen,
                                onChangeView,
                                onOpenSettings,
                            }: ActivityBarProps) {
    const { t } = useTranslation();

    const primaryItems: ActivityItem[] = [
        {
            view: "hosts",
            label: t("activity.hosts"),
            icon: Server,
        },
        {
            view: "files",
            label: t("activity.files"),
            icon: Files,
        },
        {
            view: "sftp",
            label: t("activity.sftp"),
            icon: FolderSync,
        },
    ];

    return (
        <nav
            aria-label={t("activity.label")}
            className="flex w-12 shrink-0 flex-col border-r border-sidebar-border bg-sidebar"
        >
            <div role="tablist" className="flex flex-col">
                {primaryItems.map((item) => {
                    const active = activeView === item.view;
                    const Icon = item.icon;

                    return (
                        <button
                            key={item.view}
                            type="button"
                            role="tab"
                            aria-selected={active}
                            aria-label={item.label}
                            title={item.label}
                            onClick={() =>
                                onChangeView(item.view)
                            }
                            className={activityButtonClass(active)}
                        >
                            <Icon
                                className="size-5"
                                aria-hidden="true"
                            />
                        </button>
                    );
                })}
            </div>

            <button
                type="button"
                aria-pressed={settingsOpen}
                aria-label={t("activity.settings")}
                title={t("activity.settings")}
                onClick={onOpenSettings}
                className={[
                    "mt-auto",
                    activityButtonClass(settingsOpen),
                ].join(" ")}
            >
                <Settings className="size-5" aria-hidden="true" />
            </button>
        </nav>
    );
}
