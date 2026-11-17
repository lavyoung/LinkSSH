import i18n from "i18next";
import LanguageDetector from "i18next-browser-languagedetector";
import { initReactI18next } from "react-i18next";
import enUS from "./locales/en-US";
import zhCN from "./locales/zh-CN";

export const SUPPORTED_LANGUAGES = ["zh-CN", "en-US"] as const;

export type AppLanguage = (typeof SUPPORTED_LANGUAGES)[number];

const resources = {
    "zh-CN": {
        translation: zhCN,
    },
    "en-US": {
        translation: enUS,
    },
};

i18n.on("languageChanged", (language) => {
    document.documentElement.lang = language;
});

void i18n
    .use(LanguageDetector)
    .use(initReactI18next)
    .init({
        resources,
        fallbackLng: "zh-CN",
        supportedLngs: [...SUPPORTED_LANGUAGES],
        load: "currentOnly",
        interpolation: {
            escapeValue: false,
        },
        detection: {
            order: ["localStorage", "navigator"],
            lookupLocalStorage: "linkssh-language",
            caches: ["localStorage"],
            convertDetectedLanguage: (language) =>
                language.toLowerCase().startsWith("en")
                    ? "en-US"
                    : "zh-CN",
        },
        react: {
            useSuspense: false,
        },
    });

export default i18n;
