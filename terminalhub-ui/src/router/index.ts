import { createRouter, createWebHashHistory, RouteRecordRaw } from 'vue-router';
import Home from '../views/home.vue';
import NProgress from 'nprogress';
import 'nprogress/nprogress.css';

const routes: RouteRecordRaw[] = [
    {
        path: '/',
        redirect: '/page01',
    },
    {
        path: '/',
        name: 'Home',
        component: Home,
        children: [
            {
                path: '/page01',
                name: 'page01',
                meta: {
                    title: '页面01',
                    noAuth: false,
                },
                component: () => import(/* webpackChunkName: "dashboard" */ '../views/pages/page01.vue'),
            },
            {
                path: '/page02',
                name: 'page02',
                meta: {
                    title: '页面02',
                    noAuth: false,
                },
                component: () => import(/* webpackChunkName: "system-role" */ '../views/pages/page02.vue'),
            },
            {
                path: '/page03',
                name: 'page03',
                meta: {
                    title: '页面03',
                    noAuth: false,
                },
                component: () => import(/* webpackChunkName: "system-role" */ '../views/pages/page03.vue'),
            },
            {
                path: '/page04',
                name: 'page04',
                meta: {
                    title: '页面04',
                    noAuth: false,
                },
                component: () => import(/* webpackChunkName: "system-role" */ '../views/pages/page04.vue'),
            },
            {
                path: '/page05',
                name: 'page05',
                meta: {
                    title: '页面05',
                    noAuth: false,
                },
                component: () => import(/* webpackChunkName: "system-role" */ '../views/pages/page05.vue'),
            },
            {
                path: '/page06',
                name: 'page06',
                meta: {
                    title: '页面06',
                    noAuth: false,
                },
                component: () => import(/* webpackChunkName: "system-role" */ '../views/pages/page06.vue'),
            },
            {
                path: '/theme',
                name: 'theme',
                meta: {
                    title: '主题设置',
                    noAuth: false,
                },
                component: () => import(/* webpackChunkName: "theme" */ '../views/pages/theme.vue'),
            },
            {
                path: '/icon',
                name: 'icon',
                meta: {
                    title: '图标使用',
                    noAuth: false,
                },
                component: () => import(/* webpackChunkName: "theme" */ '../views/pages/icon.vue'),
            },
        ],
    },
    {
        path: '/login',
        meta: {
            title: '登录',
            noAuth: true,
        },
        component: () => import(/* webpackChunkName: "login" */ '../views/pages/login.vue'),
    },
    {
        path: '/404',
        meta: {
            title: '找不到页面',
            noAuth: true,
        },
        component: () => import(/* webpackChunkName: "404" */ '../views/pages/404.vue'),
    },
    { path: '/:path(.*)', redirect: '/404' },
];

const router = createRouter({
    history: createWebHashHistory(),
    routes,
});

router.beforeEach((to, from, next) => {
    NProgress.start();
    const role = localStorage.getItem('vuems_name');
    if (!role && to.meta.noAuth !== true) {
        next('/login');
    } else {
        next();
    }
});

router.afterEach(() => {
    NProgress.done();
});

export default router;
