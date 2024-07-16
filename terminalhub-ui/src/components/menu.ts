import { Menus } from '@/types/menu';

export const menuData: Menus[] = [
    {
        title: '页面01',
        index: '/page01',
        icon: 'eleme-filled',
    },
    {
        title: '菜单1',
        index: '1',
        icon: 'menu',
        children: [
            {
                index: '/page01',
                title: '页面01',
            },
            {
                index: '/page02',
                title: '页面02',
            },
            {
                index: '/page03',
                title: '页面03',
            },
            {
                index: '1-1',
                title: '三级菜单',
                children: [
                    {
                        index: '/page04',
                        title: '页面04',
                    },
                    {
                        index: '/page05',
                        title: '页面05',
                    },
                    {
                        index: '/page06',
                        title: '页面06',
                    },
                ],
            },
        ],
    },
    {
        icon: 'brush',
        index: '/theme',
        title: '主题',
    },
    {
        icon: 'guide',
        index: '/icon',
        title: '图标',
    },
];
