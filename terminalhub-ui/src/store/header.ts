import { defineStore } from 'pinia';

export const useHeaderStore = defineStore('header', {
    state: () => {
        return {
            titlesp: ''
        };
    },
    getters: {},
    actions: {
        setTitlesp(titlespSet: string) {
            this.titlesp = titlespSet;
        }
    }
});
