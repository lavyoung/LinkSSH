import { FileBrowserSidebar } from "../features/execution/FileBrowserSidebar";
import { HostSidebar } from "../features/host/HostSidebar";
import type { SidebarView } from "./sidebar.types";

interface LeftSidebarProps {
    activeView: SidebarView;
}

export function LeftSidebar({ activeView }: LeftSidebarProps) {
    switch (activeView) {
        case "hosts":
            return <HostSidebar />;

        case "files":
            return (
                <FileBrowserSidebar
                    key="files"
                    mode="files"
                />
            );

        case "sftp":
            return (
                <FileBrowserSidebar
                    key="sftp"
                    mode="sftp"
                />
            );
    }
}
