import {
    Files,
    FolderSync,
    RefreshCw,
} from "lucide-react";
import { useTranslation } from "react-i18next";
import { FileTree } from "./FileTree";
import {
    localFileTree,
    sftpFileTree,
} from "./file-tree.fixtures";

export type FileBrowserMode = "files" | "sftp";

interface FileBrowserSidebarProps {
    mode: FileBrowserMode;
}

export function FileBrowserSidebar({
                                       mode,
                                   }: FileBrowserSidebarProps) {
    const { t } = useTranslation();

    const sftpMode = mode === "sftp";
    const title = sftpMode
        ? t("activity.sftp")
        : t("activity.files");
    const Icon = sftpMode ? FolderSync : Files;
    const nodes = sftpMode ? sftpFileTree : localFileTree;
    const defaultExpandedNodeIds = sftpMode
        ? ["remote-home-deploy"]
        : ["local-linkssh"];

    return (
        <section
            aria-label={title}
            className="flex h-full min-h-0 flex-col bg-sidebar text-sidebar-foreground"
        >
            <header className="flex h-12 shrink-0 items-center gap-2 border-b border-sidebar-border px-3">
                <Icon
                    className="size-4 text-muted-foreground"
                    aria-hidden="true"
                />

                <h2 className="text-xs font-bold uppercase tracking-wider">
                    {title}
                </h2>

                <button
                    disabled
                    type="button"
                    aria-label={t("fileBrowser.refresh")}
                    title={t("fileBrowser.unavailable")}
                    className="ml-auto flex size-8 items-center justify-center rounded-md text-muted-foreground hover:bg-sidebar-accent hover:text-sidebar-accent-foreground disabled:cursor-not-allowed disabled:opacity-50"
                >
                    <RefreshCw
                        className="size-4"
                        aria-hidden="true"
                    />
                </button>
            </header>

            <div className="min-h-0 flex-1 overflow-y-auto p-2">
                <FileTree
                    nodes={nodes}
                    ariaLabel={t("fileBrowser.treeLabel", {
                        name: title,
                    })}
                    defaultExpandedNodeIds={defaultExpandedNodeIds}
                />
            </div>

            <footer className="shrink-0 border-t border-sidebar-border px-3 py-2 text-[11px] leading-4 text-muted-foreground">
                {sftpMode
                    ? t("fileBrowser.sftpDemo")
                    : t("fileBrowser.filesDemo")}
            </footer>
        </section>
    );
}
