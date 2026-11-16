import {Bot, Send, Sparkles} from "lucide-react";

const suggestions = [
    "检查当前服务器运行状态",
    "分析最近的错误日志",
    "生成安全的磁盘清理方案",
];

export function AgentPanel() {
    return (
        <aside
            aria-label="AI 助手"
            className="hidden min-h-0 flex-col overflow-hidden rounded-xl border border-border bg-card text-card-foreground shadow-sm xl:flex"
        >
            <header className="flex h-12 shrink-0 items-center gap-3 border-b border-border px-4">
        <span className="flex size-8 items-center justify-center rounded-lg bg-primary text-primary-foreground">
          <Bot className="size-4" aria-hidden="true"/>
        </span>

                <div className="min-w-0">
                    <p className="truncate text-sm font-bold">AI 助手</p>
                    <p className="truncate text-xs text-muted-foreground">
                        当前未连接模型
                    </p>
                </div>
            </header>

            <div className="flex min-h-0 flex-1 flex-col overflow-y-auto p-4">
                <div className="my-auto text-center">
          <span className="mx-auto mb-4 flex size-12 items-center justify-center rounded-xl bg-muted">
            <Sparkles
                className="size-5 text-muted-foreground"
                aria-hidden="true"
            />
          </span>

                    <h2 className="text-sm font-bold">让 AI 协助处理远程任务</h2>
                    <p className="mx-auto mt-2 max-w-[240px] text-xs leading-5 text-muted-foreground">
                        后续可分析服务器状态、解释日志，并在治理与审批后执行命令。
                    </p>

                    <div className="mt-6 space-y-2 text-left">
                        {suggestions.map((suggestion) => (
                            <button
                                key={suggestion}
                                type="button"
                                className="w-full rounded-lg border border-border bg-background px-3 py-2.5 text-left text-xs font-medium transition-colors hover:bg-accent hover:text-accent-foreground"
                            >
                                {suggestion}
                            </button>
                        ))}
                    </div>
                </div>
            </div>

            <footer className="shrink-0 border-t border-border p-3">
                <div
                    className="rounded-lg border border-input bg-background p-2 focus-within:ring-2 focus-within:ring-ring/40">
          <textarea
              disabled
              rows={3}
              placeholder="完成 Agent 联调后可用"
              className="w-full resize-none bg-transparent px-1 text-sm outline-none placeholder:text-muted-foreground disabled:cursor-not-allowed"
          />

                    <div className="mt-2 flex items-center justify-between">
            <span className="text-[11px] text-muted-foreground">
              所有命令都需要治理校验
            </span>

                        <button
                            disabled
                            type="button"
                            className="flex size-8 items-center justify-center rounded-md bg-primary text-primary-foreground disabled:cursor-not-allowed disabled:opacity-40"
                            aria-label="发送消息"
                        >
                            <Send className="size-4"/>
                        </button>
                    </div>
                </div>
            </footer>
        </aside>
    );
}
