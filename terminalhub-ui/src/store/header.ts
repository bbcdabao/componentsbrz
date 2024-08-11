import { defineStore } from 'pinia';
import i18n from '@/i18n';

export const shwoLangs: any = {};
shwoLangs['en'] = 'EN';
shwoLangs['zh'] = '中文';

export const useHeaderStore = defineStore('header', {
    state: () => {
        const storedLocale = localStorage.getItem('header-lang') as 'en' | 'zh' || 'en';
        i18n.global.locale = storedLocale;
        return {
            titlesp: '',
            locale: storedLocale,
        };
    },
    getters: {
        getNowShowLang() {
            return shwoLangs[this.locale];
        },
    },
    actions: {
        setTitlesp(titlespSet: string) {
            this.titlesp = titlespSet;
        },
        setLocale(locale: string) {
			this.locale = locale;
            i18n.global.locale = this.locale;
			localStorage.setItem('header-lang', this.locale);
		},
    }
});
