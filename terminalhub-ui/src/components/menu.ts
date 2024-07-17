import { Menus } from '@/types/menu';

export const menuData: Menus[] = [
    {
        title: '添加终端配置',
        index: '/page01dd',
        icon: 'circle-plus-filled',
    },
    {
        title: '当前添加终端',
        index: '1',
        icon: 'eleme',
        children: [
        ],
    },
    {
        title: '后台管理终端',
        index: '2',
        icon: 'eleme-filled',
        children: [
        ],
    },
];
