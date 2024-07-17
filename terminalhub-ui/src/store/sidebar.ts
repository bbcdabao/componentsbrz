import { defineStore } from 'pinia';
import { Menus } from '@/types/menu';

export const useSidebarStore = defineStore('sidebar', {
	state: () => {
		return {
			collapse: false,
			menuData: JSON.parse(localStorage.getItem('sidebar-items') || '[]') as Menu[],
			bgColor: localStorage.getItem('sidebar-bg-color') || '#000000',
			textColor: localStorage.getItem('sidebar-text-color') || '#bfcbd9'
		};
	},
	getters: {},
	actions: {
		handleCollapse() {
			this.collapse = !this.collapse;
		},
		setBgColor(color: string) {
			this.bgColor = color;
			localStorage.setItem('sidebar-bg-color', color);
		},
		setTextColor(color: string) {
			this.textColor = color;
			localStorage.setItem('sidebar-text-color', color);
		}
	}
});
