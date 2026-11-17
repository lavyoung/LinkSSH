import {
    ChevronRight,
    File,
    Folder,
    FolderOpen,
} from "lucide-react";
import { useState } from "react";
import type { FileTreeNode } from "./file-tree.types";

interface FileTreeProps {
    nodes: readonly FileTreeNode[];
    ariaLabel: string;
    defaultExpandedNodeIds?: readonly string[];
}

interface FileTreeItemProps {
    node: FileTreeNode;
    depth: number;
    selectedNodeId?: string;
    expandedNodeIds: ReadonlySet<string>;
    onSelectNode: (nodeId: string) => void;
    onToggleDirectory: (nodeId: string) => void;
}

function FileTreeItem({
                          node,
                          depth,
                          selectedNodeId,
                          expandedNodeIds,
                          onSelectNode,
                          onToggleDirectory,
                      }: FileTreeItemProps) {
    const directory = node.kind === "directory";
    const expanded = directory && expandedNodeIds.has(node.id);
    const selected = selectedNodeId === node.id;

    function handleClick() {
        onSelectNode(node.id);

        if (directory) {
            onToggleDirectory(node.id);
        }
    }

    return (
        <div role="treeitem" aria-selected={selected}>
            <button
                type="button"
                aria-expanded={directory ? expanded : undefined}
                onClick={handleClick}
                className={[
                    "flex h-8 w-full min-w-0 items-center gap-1 rounded-md pr-2 text-left text-xs transition-colors",
                    selected
                        ? "bg-sidebar-accent font-semibold text-sidebar-accent-foreground"
                        : "text-muted-foreground hover:bg-sidebar-accent/70 hover:text-sidebar-accent-foreground",
                ].join(" ")}
                style={{
                    paddingLeft: `${8 + depth * 16}px`,
                }}
            >
                {directory ? (
                    <ChevronRight
                        className={[
                            "size-3.5 shrink-0 transition-transform",
                            expanded ? "rotate-90" : "",
                        ].join(" ")}
                        aria-hidden="true"
                    />
                ) : (
                    <span className="size-3.5 shrink-0" />
                )}

                {directory ? (
                    expanded ? (
                        <FolderOpen
                            className="size-4 shrink-0"
                            aria-hidden="true"
                        />
                    ) : (
                        <Folder
                            className="size-4 shrink-0"
                            aria-hidden="true"
                        />
                    )
                ) : (
                    <File
                        className="size-4 shrink-0"
                        aria-hidden="true"
                    />
                )}

                <span className="truncate">{node.name}</span>
            </button>

            {directory && expanded && node.children.length > 0 && (
                <div role="group">
                    {node.children.map((child) => (
                        <FileTreeItem
                            key={child.id}
                            node={child}
                            depth={depth + 1}
                            selectedNodeId={selectedNodeId}
                            expandedNodeIds={expandedNodeIds}
                            onSelectNode={onSelectNode}
                            onToggleDirectory={onToggleDirectory}
                        />
                    ))}
                </div>
            )}
        </div>
    );
}

export function FileTree({
                             nodes,
                             ariaLabel,
                             defaultExpandedNodeIds = [],
                         }: FileTreeProps) {
    const [selectedNodeId, setSelectedNodeId] = useState<string>();
    const [expandedNodeIds, setExpandedNodeIds] = useState<
        ReadonlySet<string>
    >(() => new Set(defaultExpandedNodeIds));

    function toggleDirectory(nodeId: string) {
        setExpandedNodeIds((currentNodeIds) => {
            const nextNodeIds = new Set(currentNodeIds);

            if (nextNodeIds.has(nodeId)) {
                nextNodeIds.delete(nodeId);
            } else {
                nextNodeIds.add(nodeId);
            }

            return nextNodeIds;
        });
    }

    return (
        <div
            role="tree"
            aria-label={ariaLabel}
            className="space-y-0.5"
        >
            {nodes.map((node) => (
                <FileTreeItem
                    key={node.id}
                    node={node}
                    depth={0}
                    selectedNodeId={selectedNodeId}
                    expandedNodeIds={expandedNodeIds}
                    onSelectNode={setSelectedNodeId}
                    onToggleDirectory={toggleDirectory}
                />
            ))}
        </div>
    );
}
