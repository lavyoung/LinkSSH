import { AgentPanel } from "../features/agent/AgentPanel";
import { TerminalPanel } from "../features/execution/TerminalPanel";
import { HostSidebar } from "../features/host/HostSidebar";

export function AppShell() {
    return (
        <main className="grid h-screen min-h-[640px] grid-cols-[252px_minmax(0,1fr)] gap-2 overflow-hidden bg-muted/50 p-2 xl:grid-cols-[252px_minmax(0,1fr)_320px]">
            <HostSidebar />
            <TerminalPanel />
            <AgentPanel />
        </main>
    );
}
