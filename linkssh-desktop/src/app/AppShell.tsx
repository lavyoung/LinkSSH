import {useState} from "react";
import {useTranslation} from "react-i18next";
import {AgentPanel} from "../features/agent/AgentPanel";
import {TerminalPanel} from "../features/execution/TerminalPanel";
import {findHostById} from "../features/host/host.fixtures";
import {useHostSelectionStore} from "../features/host/host.store";
import {SettingsDialog} from "../features/settings/SettingsDialog";
import {ActivityBar} from "./ActivityBar";
import {Header} from "./Header";
import {LeftSidebar} from "./LeftSidebar";
import {ResizeHandle} from "./ResizeHandle";
import type {SidebarView} from "./sidebar.types";

const SIDEBAR_MIN_WIDTH = 240;
const SIDEBAR_MAX_WIDTH = 420;
const ASSISTANT_MIN_WIDTH = 280;
const ASSISTANT_MAX_WIDTH = 480;

function clamp(
    value: number,
    minimum: number,
    maximum: number,
) {
    return Math.min(maximum, Math.max(minimum, value));
}

export function AppShell() {
    const {t} = useTranslation();

    const selectedHostId = useHostSelectionStore(
        (state) => state.selectedHostId,
    );

    const [activeSidebarView, setActiveSidebarView] =
        useState<SidebarView>("hosts");
    const [sidebarVisible, setSidebarVisible] = useState(true);
    const [terminalVisible, setTerminalVisible] = useState(true);
    const [assistantVisible, setAssistantVisible] = useState(true);
    const [settingsOpen, setSettingsOpen] = useState(false);

    const [sidebarWidth, setSidebarWidth] = useState(300);
    const [assistantWidth, setAssistantWidth] = useState(320);

    const selectedHost = findHostById(selectedHostId);

    const noPanelVisible =
        !sidebarVisible &&
        !terminalVisible &&
        !assistantVisible;

    const showSidebarResizeHandle =
        sidebarVisible &&
        (terminalVisible || assistantVisible);

    const showAssistantResizeHandle =
        terminalVisible && assistantVisible;

    const gridColumns: string[] = [];

    if (sidebarVisible) {
        gridColumns.push(`${sidebarWidth}px`);
    }

    if (showSidebarResizeHandle) {
        gridColumns.push("4px");
    }

    if (terminalVisible) {
        gridColumns.push("minmax(0, 1fr)");
    }

    if (showAssistantResizeHandle) {
        gridColumns.push("4px");
    }

    if (assistantVisible) {
        gridColumns.push(`${assistantWidth}px`);
    }

    function changeSidebarView(view: SidebarView) {
        setActiveSidebarView(view);

        if (!sidebarVisible) {
            setSidebarVisible(true);
        }
    }

    function resizeSidebar(deltaX: number) {
        setSidebarWidth((currentWidth) =>
            clamp(
                currentWidth + deltaX,
                SIDEBAR_MIN_WIDTH,
                SIDEBAR_MAX_WIDTH,
            ),
        );
    }

    function resizeAssistant(deltaX: number) {
        setAssistantWidth((currentWidth) =>
            clamp(
                currentWidth - deltaX,
                ASSISTANT_MIN_WIDTH,
                ASSISTANT_MAX_WIDTH,
            ),
        );
    }

    return (
        <div className="flex h-screen min-h-[640px] flex-col overflow-hidden bg-muted/50">
            <Header
                currentConnection={selectedHost?.name}
                sidebarVisible={sidebarVisible}
                terminalVisible={terminalVisible}
                assistantVisible={assistantVisible}
                onToggleSidebar={() =>
                    setSidebarVisible((visible) => !visible)
                }
                onToggleTerminal={() =>
                    setTerminalVisible((visible) => !visible)
                }
                onToggleAssistant={() =>
                    setAssistantVisible((visible) => !visible)
                }
            />

            <main
                className="grid min-h-0 flex-1 overflow-hidden p-2"
                style={{
                    gridTemplateColumns: noPanelVisible
                        ? "minmax(0, 1fr)"
                        : gridColumns.join(" "),
                }}
            >
                {sidebarVisible && (
                    <aside
                        className="flex min-h-0 overflow-hidden rounded-xl border border-sidebar-border bg-sidebar shadow-sm">
                        <ActivityBar
                            activeView={activeSidebarView}
                            settingsOpen={settingsOpen}
                            onChangeView={changeSidebarView}
                            onOpenSettings={() =>
                                setSettingsOpen(true)
                            }
                        />

                        <div
                            role="tabpanel"
                            className="min-w-0 flex-1 overflow-hidden"
                        >
                            <LeftSidebar
                                activeView={activeSidebarView}
                            />
                        </div>
                    </aside>
                )}

                {showSidebarResizeHandle && (
                    <ResizeHandle
                        ariaLabel={t(
                            "layout.resizeSidebar",
                        )}
                        value={sidebarWidth}
                        min={SIDEBAR_MIN_WIDTH}
                        max={SIDEBAR_MAX_WIDTH}
                        onResize={resizeSidebar}
                    />
                )}

                {terminalVisible && <TerminalPanel/>}

                {showAssistantResizeHandle && (
                    <ResizeHandle
                        ariaLabel={t(
                            "layout.resizeAssistant",
                        )}
                        value={assistantWidth}
                        min={ASSISTANT_MIN_WIDTH}
                        max={ASSISTANT_MAX_WIDTH}
                        onResize={resizeAssistant}
                    />
                )}

                {assistantVisible && <AgentPanel/>}

                {noPanelVisible && (
                    <section
                        className="flex items-center justify-center rounded-xl border border-dashed border-border bg-card text-sm text-muted-foreground">
                        {t("layout.noPanelVisible")}
                    </section>
                )}
            </main>

            <SettingsDialog
                open={settingsOpen}
                onClose={() => setSettingsOpen(false)}
            />
        </div>
    );
}
