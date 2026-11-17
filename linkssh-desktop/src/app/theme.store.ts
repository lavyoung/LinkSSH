import { create } from "zustand";
import { persist } from "zustand/middleware";

export type AppTheme = "light" | "dark" | "midnight" | "forest";

interface ThemeState {
    theme: AppTheme;
    setTheme: (theme: AppTheme) => void;
}

export const useThemeStore = create<ThemeState>()(
    persist(
        (set) => ({
            theme: "dark",

            setTheme: (theme) => {
                set({ theme });
            },
        }),
        {
            name: "linkssh-theme",
        },
    ),
);
