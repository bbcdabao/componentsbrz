import { defineStore } from 'pinia';
import { Sshitem } from '@/types/sshitem';

export const useSidebarStore = defineStore('sidebar', {
	state: () => {
		return {
			collapse: false,
			sshitems: JSON.parse(localStorage.getItem('sidebar-sshitems') || '{}') as { [key: string]: Sshitem }
		};
	},
	getters: {},
	actions: {
		handleCollapse() {
			this.collapse = !this.collapse;
		},
		addSshitem(item: Sshitem) {
			this.sshitems[item.addr] = item;
			localStorage.setItem('sidebar-sshitems', JSON.stringify(this.sshitems));
		},
		delSshitem(addr: string) {
			delete this.sshitems[addr];
			localStorage.setItem('sidebar-sshitems', JSON.stringify(this.sshitems));
		},
		clsSshitem() {
			this.sshitems = {};
			localStorage.setItem('sidebar-sshitems', JSON.stringify(this.sshitems));
		},
	}
});
