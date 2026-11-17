import {
    Languages,
    PanelLeft,
    PanelRight,
    Palette,
    Plus,
    SquareTerminal,
} from "lucide-react";
import { useTranslation } from "react-i18next";
import type { AppLanguage } from "./i18n/i18n";
import {
    type AppTheme,
    useThemeStore,
} from "./theme.store";

const THEME_VALUES: readonly AppTheme[] = [
    "light",
    "dark",
    "midnight",
    "forest",
];

interface HeaderProps {
    currentConnection?: string;
    sidebarVisible: boolean;
    terminalVisible: boolean;
    assistantVisible: boolean;
    onToggleSidebar: () => void;
    onToggleTerminal: () => void;
    onToggleAssistant: () => void;
}

function panelToggleClass(active: boolean) {
    return [
        "flex size-8 shrink-0 items-center justify-center rounded-md transition-colors",
        active
            ? "bg-accent text-accent-foreground"
            : "text-muted-foreground hover:bg-accent hover:text-accent-foreground",
    ].join(" ");
}

export function Header({
                           currentConnection,
                           sidebarVisible,
                           terminalVisible,
                           assistantVisible,
                           onToggleSidebar,
                           onToggleTerminal,
                           onToggleAssistant,
                       }: HeaderProps) {
    const { t, i18n } = useTranslation();
    const theme = useThemeStore((state) => state.theme);
    const setTheme = useThemeStore((state) => state.setTheme);

    const language = (i18n.resolvedLanguage ?? "zh-CN") as AppLanguage;

    return (
        <header className="flex h-12 shrink-0 items-center gap-2 border-b border-border bg-background px-2 text-foreground">
            <button
                type="button"
                aria-label={t("header.toggleSidebar")}
                title={t("header.toggleSidebar")}
                aria-pressed={sidebarVisible}
                onClick={onToggleSidebar}
                className={panelToggleClass(sidebarVisible)}
            >
                <PanelLeft className="size-4" aria-hidden="true" />
            </button>

            <div className="flex min-w-0 items-center gap-2 px-1">
                <span className="flex size-7 shrink-0 items-center justify-center rounded-md bg-primary text-primary-foreground">
                    <SquareTerminal className="size-4" aria-hidden="true" />
                </span>

                <span className="text-sm font-bold">{t("app.name")}</span>
            </div>

            <span className="hidden max-w-64 truncate border-l border-border pl-3 text-xs text-muted-foreground md:block">
                {currentConnection
                    ? t("header.currentConnection", {
                        name: currentConnection,
                    })
                    : t("header.noConnection")}
            </span>

            <div className="ml-auto flex min-w-0 items-center gap-1">
                <button
                    disabled
                    type="button"
                    aria-label={t("header.addConnection")}
                    title={t("header.addConnectionUnavailable")}
                    className="hidden h-8 items-center gap-2 rounded-md border border-input bg-background px-3 text-xs font-semibold text-foreground hover:bg-accent disabled:cursor-not-allowed disabled:opacity-50 lg:flex"
                >
                    <Plus className="size-4" aria-hidden="true" />
                    {t("header.addConnection")}
                </button>

                <label className="flex h-8 items-center gap-1 rounded-md border border-input bg-background px-2">
                    <Palette
                        className="size-4 shrink-0 text-muted-foreground"
                        aria-hidden="true"
                    />
                    <span className="sr-only">{t("theme.label")}</span>
                    <select
                        value={theme}
                        aria-label={t("theme.label")}
                        onChange={(event) =>
                            setTheme(event.target.value as AppTheme)
                        }
                        className="max-w-24 bg-transparent text-xs font-semibold text-foreground outline-none"
                    >
                        {THEME_VALUES.map((themeValue) => (
                            <option key={themeValue} value={themeValue}>
                                {t(`theme.${themeValue}.name`)}
                            </option>
                        ))}
                    </select>
                </label>

                <label className="flex h-8 items-center gap-1 rounded-md border border-input bg-background px-2">
                    <Languages
                        className="size-4 shrink-0 text-muted-foreground"
                        aria-hidden="true"
                    />
                    <span className="sr-only">{t("language.label")}</span>
                    <select
                        value={language}
                        aria-label={t("language.label")}
                        onChange={(event) => {
                            void i18n.changeLanguage(
                                event.target.value as AppLanguage,
                            );
                        }}
                        className="max-w-24 bg-transparent text-xs font-semibold text-foreground outline-none"
                    >
                        <option value="zh-CN">
                            {t("language.simplifiedChinese")}
                        </option>
                        <option value="en-US">
                            {t("language.english")}
                        </option>
                    </select>
                </label>

                <button
                    type="button"
                    aria-label={t("header.toggleTerminal")}
                    title={t("header.toggleTerminal")}
                    aria-pressed={terminalVisible}
                    onClick={onToggleTerminal}
                    className={panelToggleClass(terminalVisible)}
                >
                    <SquareTerminal className="size-4" aria-hidden="true" />
                </button>

                <button
                    type="button"
                    aria-label={t("header.toggleAssistant")}
                    title={t("header.toggleAssistant")}
                    aria-pressed={assistantVisible}
                    onClick={onToggleAssistant}
                    className={panelToggleClass(assistantVisible)}
                >
                    <PanelRight className="size-4" aria-hidden="true" />
                </button>
            </div>
        </header>
    );
}
