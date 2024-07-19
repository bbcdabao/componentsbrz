import { defineStore } from 'pinia';
import { Sshitem } from '@/types/sshitem';

export const useSidebarStore = defineStore('sidebar', {
	state: () => {
		return {
			collapse: false,
			sshitems: JSON.parse(localStorage.getItem('sidebar-sshitems') || '[]') as Sshitem[],
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
		},
		addSshitem(item: Sshitem) {
			this.sshitems.push(item);
			localStorage.setItem('sidebar-sshitems', JSON.stringify(this.sshitems));
		},
		delSshitem(addr: string) {
			this.sshitems = this.sshitems.filter(item => item.addr !== addr);
			localStorage.setItem('sidebar-sshitems', JSON.stringify(this.sshitems));
		},
		clsSshitem() {
			this.sshitems = [];
			localStorage.setItem('sidebar-sshitems', JSON.stringify(this.sshitems));
		},
	}
});
