interface BaseFileTreeNode {
    id: string;
    name: string;
}

export interface FileTreeFile extends BaseFileTreeNode {
    kind: "file";
}

export interface FileTreeDirectory extends BaseFileTreeNode {
    kind: "directory";
    children: readonly FileTreeNode[];
}

export type FileTreeNode = FileTreeFile | FileTreeDirectory;
