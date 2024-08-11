import { createI18n } from 'vue-i18n';
import zh from './locales/zh.json';
import en from './locales/en.json';

const messages = {
  en,
  zh,
};

const i18n = createI18n({
  locale: 'en',
  fallbackLocale: 'zh',
  messages,
});

export default i18n;
