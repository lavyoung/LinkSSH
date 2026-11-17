import { create } from "zustand";
import { persist } from "zustand/middleware";

export const TERMINAL_FONT_FAMILIES = [
    "system-mono",
    "cascadia-code",
    "jetbrains-mono",
] as const;

export type TerminalFontFamily =
    (typeof TERMINAL_FONT_FAMILIES)[number];

export const TERMINAL_FONT_STACKS: Record<
    TerminalFontFamily,
    string
> = {
    "system-mono":
        "ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace",
    "cascadia-code":
        "'Cascadia Code', 'Cascadia Mono', Consolas, monospace",
    "jetbrains-mono":
        "'JetBrains Mono', 'Cascadia Code', Consolas, monospace",
};

interface TerminalPreferencesState {
    fontFamily: TerminalFontFamily;
    fontSize: number;
    setFontFamily: (fontFamily: TerminalFontFamily) => void;
    setFontSize: (fontSize: number) => void;
}

export const useTerminalPreferencesStore =
    create<TerminalPreferencesState>()(
        persist(
            (set) => ({
                fontFamily: "system-mono",
                fontSize: 13,

                setFontFamily: (fontFamily) => {
                    set({ fontFamily });
                },

                setFontSize: (fontSize) => {
                    const normalizedFontSize = Math.min(
                        24,
                        Math.max(10, Math.round(fontSize)),
                    );

                    set({
                        fontSize: normalizedFontSize,
                    });
                },
            }),
            {
                name: "linkssh-terminal-preferences",
            },
        ),
    );
