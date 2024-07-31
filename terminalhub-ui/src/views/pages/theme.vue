<template>
    <div>
        <el-card class="mgb20" shadow="hover">
            <template #header>
                <div class="content-title">系统主题</div>
            </template>
            <div class="theme-list mgb20">
                <div class="theme-item" @click="setSystemTheme(value)" v-for="[key, value] in Object.entries(systemmap)"
                    :style="{ backgroundColor: value.headerBgColor, color: getInverseColor(value.headerBgColor) }">{{ value.name }}
                </div>
            </div>
            <div class="flex-center">
                <el-button @click="resetSystemTheme">重置主题</el-button>
            </div>
        </el-card>
        <el-card class="mgb20" shadow="hover">
            <template #header>
                <div class="content-title">Element-Plus主题</div>
            </template>
            <div class="theme-list mgb20">
                <div class="theme-item" v-for="theme in themes">
                    <el-button :type="theme.name">{{ theme.name }}</el-button>
                    <div class="theme-color">{{ theme.color }}</div>
                    <el-color-picker v-model="color[theme.name]" @change="changeColor(theme.name)" />
                </div>
            </div>
            <div class="flex-center">
                <el-button @click="resetTheme">重置主题</el-button>
            </div>
        </el-card>
    </div>
</template>

<script setup lang="ts">
import { useSidebarStore } from '@/store/sidebar';
import { useThemeStore } from '@/store/theme'
import { reactive } from 'vue';
const themeStore = useThemeStore();
const sidebar = useSidebarStore();

const color = reactive({
    primary: localStorage.getItem('theme-primary') || '#409eff',
    success: localStorage.getItem('theme-success') || '#67c23a',
    warning: localStorage.getItem('theme-warning') || '#e6a23c',
    danger: localStorage.getItem('theme-danger') || '#f56c6c',
    info: localStorage.getItem('theme-info') || '#909399',
    headerBgColor: themeStore.headerBgColor,
    headerTextColor: themeStore.headerTextColor,
})

const themes = [
    {
        name: 'primary',
        color: themeStore.primary || color.primary
    },
    {
        name: 'success',
        color: themeStore.success || color.success
    },
    {
        name: 'warning',
        color: themeStore.warning || color.warning
    },
    {
        name: 'danger',
        color: themeStore.danger || color.danger
    },
    {
        name: 'info',
        color: themeStore.info || color.info
    }
]

const changeColor = (name: string) => {
    themeStore.setPropertyColor(color[name], name)
}

const resetTheme = () => {
    themeStore.resetTheme()
}
const getInverseColor = (color) => {
    color = color.substring(1)
    const r = parseInt(color.substring(0, 2), 16)
    const g = parseInt(color.substring(2, 4), 16)
    const b = parseInt(color.substring(4, 6), 16)
    const inverseR = (255 - r).toString(16).padStart(2, '0')
    const inverseG = (255 - g).toString(16).padStart(2, '0')
    const inverseB = (255 - b).toString(16).padStart(2, '0')
    return `#${inverseR}${inverseG}${inverseB}`
}
const systemmap = {
    '1': {
        name: '默认',
        headerBgColor:'#000000',
        headerTextColor: '#ffffff',
        sidebarBgColor: '#000000',
        sidebarTextColor: '#ffffff',
        sidebarIndexBgColor: '#4B4B4B',
        sidebarIndexTextColor: '#ffffff',
    },
    '2': {
        name: '灰色',
        headerBgColor:'#4B4B4B',
        headerTextColor: '#ffffff',
        sidebarBgColor: '#4B4B4B',
        sidebarTextColor: '#ffffff',
        sidebarIndexBgColor: '#808080',
        sidebarIndexTextColor: '#ffffff',
    },
    '3': {
        name: '黑灰双拼',
        headerBgColor:'#000000',
        headerTextColor: '#ffffff',
        sidebarBgColor: '#4B4B4B',
        sidebarTextColor: '#ffffff',
        sidebarIndexBgColor: '#808080',
        sidebarIndexTextColor: '#ffffff',
    }
};
const setSystemTheme = (data: any) => {
    console.info("theme:", data);
    themeStore.setHeaderBgColor(data.headerBgColor);
    themeStore.setHeaderTextColor(data.headerTextColor);
    themeStore.setSidebarBgColor(data.sidebarBgColor);
    themeStore.setSidebarTextColor(data.sidebarTextColor);
    themeStore.setSidebarIndexBgColor(data.sidebarIndexBgColor);
    themeStore.setSidebarIndexTextColor(data.sidebarIndexTextColor);
}
const resetSystemTheme = () => {
    resetTheme();
    localStorage.removeItem('header-bg-color');
    localStorage.removeItem('header-text-color');
    localStorage.removeItem('sidebar-bg-color');
    localStorage.removeItem('sidebar-text-color');
    localStorage.removeItem('sidebar-bg-color');
    localStorage.removeItem('sidebar-index-bg-color');
    localStorage.removeItem('sidebar-index-text-color');
    location.reload();
}
</script>

<style scoped>
.theme-list {
    display: flex;
    justify-content: center;
}

.theme-item {
    margin-right: 20px;
    padding: 30px;
    border: 1px solid #dcdfe6;
    border-radius: 4px;
    text-align: center;
}

.theme-color {
    color: #787878;
    margin: 20px 0;
}
</style>
