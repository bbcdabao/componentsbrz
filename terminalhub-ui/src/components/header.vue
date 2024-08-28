<template>
    <div class="header">
        <!-- Collapse button -->
        <div class="header-left">
            <div class="collapse-btn" @click="collapseChage">
                <el-icon v-if="sidebar.collapse">
                    <Expand />
                </el-icon>
                <el-icon v-else>
                    <Fold />
                </el-icon>
            </div>
            <div class="collapse-img" v-if="!sidebar.collapse">
                <img :src="cmdTerminal" alt="CMD.terminal" class="cmd-terminal">
            </div>
        </div>
        <div>{{ header.titlesp }}</div>
        <div class="header-right">
            <div class="header-user-con">
                <div class="btn-icon" @click="router.push('/theme')">
                    <el-tooltip effect="dark" :content="$t('setTheme')" placement="bottom">
                        <i class="el-icon-lx-skin"></i>
                    </el-tooltip>
                </div>
                <div class="btn-icon" @click="setFullScreen">
                    <el-tooltip effect="dark" :content="$t('fullScreen')" placement="bottom">
                        <i class="el-icon-lx-full"></i>
                    </el-tooltip>
                </div>
                <!-- Language selection -->
                <vLanguage style="margin-top: 6px;"/>
                <!-- User avatar -->
                <el-avatar class="user-avator" :size="32" :src="imgurl" />
                <!-- Username drop-down menu -->
                <el-dropdown trigger="click" @command="handleCommand">
                    <span class="el-dropdown-link">
                        {{ username }}
                        <el-icon class="el-icon--right">
                            <arrow-down />
                        </el-icon>
                    </span>
                    <template #dropdown>
                        <el-dropdown-menu>
                            <el-dropdown-item divided command="loginout">{{ $t('exitLogin') }}</el-dropdown-item>
                        </el-dropdown-menu>
                    </template>
                </el-dropdown>
            </div>
        </div>
    </div>
</template>
<script setup lang="ts">
import { onMounted } from 'vue';
import { useSidebarStore } from '@/store/sidebar';
import { useHeaderStore } from '@/store/header';
import { useRouter } from 'vue-router';
import vLanguage from '@/components/language.vue';
import imgurl from '@/assets/img/user-logo.jpg';
import cmdTerminal from '@/assets/img/cmd-termianl.png';

const sidebar = useSidebarStore();
const header = useHeaderStore();
const username: string | null = localStorage.getItem('vuems_name');

const collapseChage = () => {
    sidebar.handleCollapse();
}

onMounted(() => {
    if (document.body.clientWidth < 600) {
        collapseChage();
    }
})

const router = useRouter();
const handleCommand = (command: string) => {
    if (command == 'loginout') {
        localStorage.removeItem('vuems_name');
        router.push('/login');
    }
}

/**
 * Set full screen
 */
const setFullScreen = () => {
    if (document.fullscreenElement) {
        document.exitFullscreen();
    } else {
        document.body.requestFullscreen.call(document.body);
    }
}
</script>
<style scoped>
.header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    box-sizing: border-box;
    width: 100%;
    height: var(--header-height);
    color: var(--header-text-color);
    background-color: var(--header-bg-color);
    border-bottom: 1px solid #ddd;
}
.header-left {
    display: flex;
    align-items: center;
    padding-left: 10px;
    height: 100%;
}
.collapse-btn {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 100%;
    padding: 0 10px;
    cursor: pointer;
    opacity: 0.8;
    font-size: 22px;
}
.collapse-btn:hover {
    opacity: 1;
}
.collapse-img {
    margin-top: 5px;
    align-items: center;
}
@keyframes graduallyShow {
    0% {
        opacity: 0;
    }
    100% {
        opacity: 1;
    }
}
.cmd-terminal {
    height: 40px;
    animation: graduallyShow 1.5s ease-in-out; 
}
.header-right {
    float: right;
    padding-right: 50px;
}
.header-user-con {
    display: flex;
    height: var(--header-height);
    align-items: center;
}
.btn-icon {
    position: relative;
    width: 30px;
    height: 30px;
    text-align: center;
    cursor: pointer;
    display: flex;
    align-items: center;
    color: var(--header-text-color);
    margin: 0 5px;
    font-size: 20px;
}
.user-avator {
    margin: 0 10px 0 20px;
}
.el-dropdown-link {
    color: var(--header-text-color);
    cursor: pointer;
    display: flex;
    align-items: center;
}
.el-dropdown-menu__item {
    text-align: center;
}
</style>
