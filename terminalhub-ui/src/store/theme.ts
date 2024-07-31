import { mix, setProperty } from '@/utils';
import { defineStore } from 'pinia';

export const useThemeStore = defineStore('theme', {
    state: () => {
        return {
            primary: '',
            success: '',
            warning: '',
            danger: '',
            info: '',
            headerBgColor: '#000000',
            headerTextColor: '#ffffff',
            sidebarBgColor: '#000000',
            sidebarTextColor: '#ffffff',
            sidebarIndexBgColor: '#4B4B4B',
            sidebarIndexTextColor: '#ffffff',
        };
    },
    getters: {},
    actions: {
        initTheme() {
            ['primary', 'success', 'warning', 'danger', 'info'].forEach((type) => {
                const color = localStorage.getItem(`theme-${type}`) || '';
                if (color) {
                    this.setPropertyColor(color, type);
                }
            });
            const headerBgColor = localStorage.getItem('header-bg-color');
            headerBgColor && this.setHeaderBgColor(headerBgColor);
            const headerTextColor = localStorage.getItem('header-text-color');
            headerTextColor && this.setHeaderTextColor(headerTextColor);
            const sidebarBgColor = localStorage.getItem('sidebar-bg-color');
            sidebarBgColor && this.setSidebarBgColor(sidebarBgColor);
            const sidebarTextColor = localStorage.getItem('sidebar-text-color');
            sidebarTextColor && this.setSidebarTextColor(sidebarTextColor);
            const sidebarIndexBgColor = localStorage.getItem('sidebar-index-bg-color');
            sidebarIndexBgColor && this.setSidebarIndexBgColor(sidebarIndexBgColor);
            const sidebarIndexTextColor = localStorage.getItem('sidebar-index-text-color');
            sidebarIndexTextColor && this.setSidebarIndexTextColor(sidebarIndexTextColor);
        },
        resetTheme() {
            ['primary', 'success', 'warning', 'danger', 'info'].forEach((type) => {
                this.setPropertyColor('', type);
            });
        },
        setPropertyColor(color: string, type: string = 'primary') {
            this[type] = color;
            setProperty(`--el-color-${type}`, color);
            localStorage.setItem(`theme-${type}`, color);
            this.setThemeLight(type);
        },
        setThemeLight(type: string = 'primary') {
            [3, 5, 7, 8, 9].forEach((v) => {
                setProperty(`--el-color-${type}-light-${v}`, mix('#ffffff', this[type], v / 10));
            });
            setProperty(`--el-color-${type}-dark-2`, mix('#ffffff', this[type], 0.2));
        },
        setHeaderBgColor(color: string) {
            this.headerBgColor = color;
            setProperty('--header-bg-color', color);
            localStorage.setItem(`header-bg-color`, color);
        },
        setHeaderTextColor(color: string) {
            this.headerTextColor = color;
            setProperty('--header-text-color', color);
            localStorage.setItem(`header-text-color`, color);
        },
        setSidebarBgColor(color: string) {
            this.sidebarBgColor = color;
            setProperty('--sidebar-bg-color', color);
            localStorage.setItem(`sidebar-bg-color`, color);
        },
        setSidebarTextColor(color: string) {
            this.sidebarTextColor = color;
            setProperty('--sidebar-text-color', color);
            localStorage.setItem(`sidebar-text-color`, color);
        },
        setSidebarIndexBgColor(color: string) {
            this.sidebarIndexBgColor = color;
            setProperty('--sidebar-index-bg-color', color);
            localStorage.setItem(`sidebar-index-bg-color`, color);
        },
        setSidebarIndexTextColor(color: string) {
            this.sidebarIndexTextColor = color;
            setProperty('--sidebar-index-text-color', color);
            localStorage.setItem(`sidebar-index-text-color`, color);
        }
    }
});