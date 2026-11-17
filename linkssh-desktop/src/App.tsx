import { useLayoutEffect } from "react";
import { AppShell } from "./app/AppShell";
import { useThemeStore } from "./app/theme.store";

function App() {
    const theme = useThemeStore((state) => state.theme);

    useLayoutEffect(() => {
        const root = document.documentElement;

        root.dataset.theme = theme;
        root.style.colorScheme = theme === "light" ? "light" : "dark";
    }, [theme]);

    return <AppShell />;
}

export default App;
