import { createRouter, createWebHashHistory, RouteRecordRaw } from 'vue-router';
import Home from '../views/home.vue';
import NProgress from 'nprogress';
import 'nprogress/nprogress.css';

const routes: RouteRecordRaw[] = [
    {
        path: '/',
        redirect: '/manager',
    },
    {
        path: '/',
        name: 'Home',
        component: Home,
        children: [
            {
                path: '/welcome',
                name: 'welcome',
                meta: {
                    title: '欢迎',
                    noAuth: false,
                },
                component: () => import(/* webpackChunkName: "dashboard" */ '../views/pages/welcome.vue'),
            },
            {
                path: '/manager',
                name: 'manager',
                meta: {
                    title: '终端管理',
                    noAuth: false,
                },
                component: () => import(/* webpackChunkName: "dashboard" */ '../views/pages/manager.vue'),
            },
            {
                path: '/sshtelnet/:param',
                name: 'sshtelnet',
                meta: {
                    title: 'param',
                    noAuth: false,
                },
                component: () => import(/* webpackChunkName: "system-role" */ '../views/pages/sshtelnet.vue'),
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
