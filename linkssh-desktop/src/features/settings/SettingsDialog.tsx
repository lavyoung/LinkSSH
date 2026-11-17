import {
    Info,
    Palette,
    Settings,
    SquareTerminal,
    X,
} from "lucide-react";
import {
    useEffect,
    useRef,
    useState,
    type ComponentType,
} from "react";
import { useTranslation } from "react-i18next";
import type { AppLanguage } from "../../app/i18n/i18n";
import {
    type AppTheme,
    useThemeStore,
} from "../../app/theme.store";
import {
    TERMINAL_FONT_FAMILIES,
    type TerminalFontFamily,
    useTerminalPreferencesStore,
} from "../execution/terminal-preferences.store";

const THEME_VALUES: readonly AppTheme[] = [
    "light",
    "dark",
    "midnight",
    "forest",
];

type SettingsSection =
    | "general"
    | "appearance"
    | "terminal"
    | "about";

interface SettingsDialogProps {
    open: boolean;
    onClose: () => void;
}

interface SettingsNavigationItem {
    id: SettingsSection;
    label: string;
    icon: ComponentType<{ className?: string }>;
}

export function SettingsDialog({
                                   open,
                                   onClose,
                               }: SettingsDialogProps) {
    const { t, i18n } = useTranslation();
    const dialogRef = useRef<HTMLDialogElement>(null);
    const [activeSection, setActiveSection] =
        useState<SettingsSection>("general");

    const theme = useThemeStore((state) => state.theme);
    const setTheme = useThemeStore((state) => state.setTheme);

    const fontFamily = useTerminalPreferencesStore(
        (state) => state.fontFamily,
    );
    const fontSize = useTerminalPreferencesStore(
        (state) => state.fontSize,
    );
    const setFontFamily = useTerminalPreferencesStore(
        (state) => state.setFontFamily,
    );
    const setFontSize = useTerminalPreferencesStore(
        (state) => state.setFontSize,
    );

    const language = (i18n.resolvedLanguage ??
        "zh-CN") as AppLanguage;

    const navigationItems: SettingsNavigationItem[] = [
        {
            id: "general",
            label: t("settings.sections.general"),
            icon: Settings,
        },
        {
            id: "appearance",
            label: t("settings.sections.appearance"),
            icon: Palette,
        },
        {
            id: "terminal",
            label: t("settings.sections.terminal"),
            icon: SquareTerminal,
        },
        {
            id: "about",
            label: t("settings.sections.about"),
            icon: Info,
        },
    ];

    useEffect(() => {
        const dialog = dialogRef.current;

        if (!dialog) {
            return;
        }

        if (open && !dialog.open) {
            dialog.showModal();
        }

        if (!open && dialog.open) {
            dialog.close();
        }
    }, [open]);

    function renderContent() {
        switch (activeSection) {
            case "general":
                return (
                    <section>
                        <h2 className="text-base font-bold">
                            {t("settings.general.title")}
                        </h2>
                        <p className="mt-2 text-sm leading-6 text-muted-foreground">
                            {t("settings.general.description")}
                        </p>

                        <div className="mt-6 rounded-lg border border-dashed border-border bg-muted/30 p-4 text-sm text-muted-foreground">
                            {t("settings.general.pending")}
                        </div>
                    </section>
                );

            case "appearance":
                return (
                    <section>
                        <h2 className="text-base font-bold">
                            {t("settings.appearance.title")}
                        </h2>
                        <p className="mt-2 text-sm leading-6 text-muted-foreground">
                            {t("settings.appearance.description")}
                        </p>

                        <div className="mt-6 space-y-5">
                            <label className="block">
                                <span className="text-sm font-semibold">
                                    {t("settings.appearance.theme")}
                                </span>

                                <select
                                    value={theme}
                                    onChange={(event) =>
                                        setTheme(
                                            event.target
                                                .value as AppTheme,
                                        )
                                    }
                                    className="mt-2 h-10 w-full rounded-md border border-input bg-background px-3 text-sm outline-none focus-visible:ring-2 focus-visible:ring-ring/40"
                                >
                                    {THEME_VALUES.map(
                                        (themeValue) => (
                                            <option
                                                key={themeValue}
                                                value={themeValue}
                                            >
                                                {t(
                                                    `theme.${themeValue}.name`,
                                                )}
                                            </option>
                                        ),
                                    )}
                                </select>
                            </label>

                            <label className="block">
                                <span className="text-sm font-semibold">
                                    {t(
                                        "settings.appearance.language",
                                    )}
                                </span>

                                <select
                                    value={language}
                                    onChange={(event) => {
                                        void i18n.changeLanguage(
                                            event.target
                                                .value as AppLanguage,
                                        );
                                    }}
                                    className="mt-2 h-10 w-full rounded-md border border-input bg-background px-3 text-sm outline-none focus-visible:ring-2 focus-visible:ring-ring/40"
                                >
                                    <option value="zh-CN">
                                        {t(
                                            "language.simplifiedChinese",
                                        )}
                                    </option>
                                    <option value="en-US">
                                        {t("language.english")}
                                    </option>
                                </select>
                            </label>
                        </div>
                    </section>
                );

            case "terminal":
                return (
                    <section>
                        <h2 className="text-base font-bold">
                            {t("settings.terminal.title")}
                        </h2>
                        <p className="mt-2 text-sm leading-6 text-muted-foreground">
                            {t("settings.terminal.description")}
                        </p>

                        <div className="mt-6 space-y-5">
                            <label className="block">
                                <span className="text-sm font-semibold">
                                    {t(
                                        "settings.terminal.fontFamily",
                                    )}
                                </span>

                                <select
                                    value={fontFamily}
                                    onChange={(event) =>
                                        setFontFamily(
                                            event.target
                                                .value as TerminalFontFamily,
                                        )
                                    }
                                    className="mt-2 h-10 w-full rounded-md border border-input bg-background px-3 text-sm outline-none focus-visible:ring-2 focus-visible:ring-ring/40"
                                >
                                    {TERMINAL_FONT_FAMILIES.map(
                                        (family) => (
                                            <option
                                                key={family}
                                                value={family}
                                            >
                                                {t(
                                                    `settings.terminal.fonts.${family}`,
                                                )}
                                            </option>
                                        ),
                                    )}
                                </select>
                            </label>

                            <label className="block">
                                <span className="flex items-center justify-between text-sm font-semibold">
                                    <span>
                                        {t(
                                            "settings.terminal.fontSize",
                                        )}
                                    </span>
                                    <span>{fontSize}px</span>
                                </span>

                                <input
                                    type="range"
                                    min={10}
                                    max={24}
                                    step={1}
                                    value={fontSize}
                                    onChange={(event) =>
                                        setFontSize(
                                            Number(
                                                event.target.value,
                                            ),
                                        )
                                    }
                                    className="mt-3 w-full accent-primary"
                                />
                            </label>

                            <div
                                className="rounded-lg border border-terminal-border bg-terminal-background p-4 text-terminal-foreground"
                                style={{
                                    fontFamily:
                                        fontFamily ===
                                        "system-mono"
                                            ? "ui-monospace, monospace"
                                            : fontFamily ===
                                            "cascadia-code"
                                                ? '"Cascadia Code", Consolas, monospace'
                                                : '"JetBrains Mono", Consolas, monospace',
                                    fontSize: `${fontSize}px`,
                                }}
                            >
                                deploy@linkssh:~$ echo LinkSSH
                            </div>
                        </div>
                    </section>
                );

            case "about":
                return (
                    <section>
                        <h2 className="text-base font-bold">
                            {t("settings.about.title")}
                        </h2>
                        <p className="mt-2 text-sm leading-6 text-muted-foreground">
                            {t("settings.about.description")}
                        </p>

                        <dl className="mt-6 grid grid-cols-[120px_1fr] gap-x-4 gap-y-3 text-sm">
                            <dt className="text-muted-foreground">
                                {t("settings.about.version")}
                            </dt>
                            <dd className="font-semibold">0.1.0</dd>

                            <dt className="text-muted-foreground">
                                {t("settings.about.stack")}
                            </dt>
                            <dd className="font-semibold">
                                Tauri 2 · React 19 · TypeScript
                            </dd>
                        </dl>
                    </section>
                );
        }
    }

    return (
        <dialog
            ref={dialogRef}
            aria-labelledby="settings-dialog-title"
            onCancel={(event) => {
                event.preventDefault();
                onClose();
            }}
            onClick={(event) => {
                if (event.target === event.currentTarget) {
                    onClose();
                }
            }}
            className="m-auto h-[min(620px,calc(100vh-48px))] w-[min(820px,calc(100vw-48px))] overflow-hidden rounded-xl border border-border bg-background p-0 text-foreground shadow-2xl backdrop:bg-black/60"
        >
            <div className="flex h-full flex-col">
                <header className="flex h-12 shrink-0 items-center border-b border-border px-4">
                    <h1
                        id="settings-dialog-title"
                        className="text-sm font-bold"
                    >
                        {t("settings.title")}
                    </h1>

                    <button
                        type="button"
                        aria-label={t("common.close")}
                        title={t("common.close")}
                        onClick={onClose}
                        className="ml-auto flex size-8 items-center justify-center rounded-md text-muted-foreground hover:bg-accent hover:text-accent-foreground"
                    >
                        <X className="size-4" aria-hidden="true" />
                    </button>
                </header>

                <div className="flex min-h-0 flex-1">
                    <nav className="w-48 shrink-0 border-r border-border bg-muted/30 p-2">
                        {navigationItems.map((item) => {
                            const active =
                                activeSection === item.id;
                            const Icon = item.icon;

                            return (
                                <button
                                    key={item.id}
                                    type="button"
                                    onClick={() =>
                                        setActiveSection(item.id)
                                    }
                                    className={[
                                        "flex h-10 w-full items-center gap-3 rounded-md px-3 text-left text-sm transition-colors",
                                        active
                                            ? "bg-accent font-semibold text-accent-foreground"
                                            : "text-muted-foreground hover:bg-accent/70 hover:text-accent-foreground",
                                    ].join(" ")}
                                >
                                    <Icon className="size-4" />
                                    {item.label}
                                </button>
                            );
                        })}
                    </nav>

                    <div className="min-w-0 flex-1 overflow-y-auto p-6">
                        {renderContent()}
                    </div>
                </div>
            </div>
        </dialog>
    );
}
