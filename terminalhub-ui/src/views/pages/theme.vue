<template>
    <div class="this-page">
        <div style="padding: 10px;">
            <el-card class="mgb20 custom-shadow" shadow="hover">
                <template #header>
                    <div class="content-title">{{ $t('sysTheme') }}</div>
                </template>
                <div class="theme-list mgb20">
                    <div class="theme-item" @click="setSystemTheme(value)" v-for="[key, value] in Object.entries(systemmap)"
                        :style="{ background: getGradientBackground(value.headerBgColor, value.sidebarBgColor), color: getInverseColor(value.headerBgColor) }">{{ value.name }}
                    </div>
                </div>
                <div class="flex-center">
                    <el-button type="primary" @click="resetSystemTheme">{{ $t('resetTheme') }}</el-button>
                </div>
            </el-card>
            <el-card class="mgb20 custom-shadow" shadow="hover">
                <template #header>
                    <div class="content-title">Element-Plus</div>
                </template>
                <div class="theme-list mgb20">
                    <div class="theme-item" v-for="theme in themes">
                        <el-button :type="theme.name">{{ theme.name }}</el-button>
                        <div class="theme-color">{{ theme.color }}</div>
                        <el-color-picker v-model="color[theme.name]" @change="changeColor(theme.name)" />
                    </div>
                </div>
                <div class="flex-center">
                    <el-button type="primary" @click="resetTheme">{{ $t('resetTheme') }}</el-button>
                </div>
            </el-card>
        </div>
    </div>
</template>

<script setup lang="ts">
import { useThemeStore } from '@/store/theme'
import { reactive } from 'vue';
const themeStore = useThemeStore();

const color = reactive({
    primary: localStorage.getItem('theme-primary') || '#409eff',
    success: localStorage.getItem('theme-success') || '#67c23a',
    warning: localStorage.getItem('theme-warning') || '#e6a23c',
    danger: localStorage.getItem('theme-danger') || '#f56c6c',
    info: localStorage.getItem('theme-info') || '#909399',
    headerBgColor: themeStore.headerBgColor,
    headerTextColor: themeStore.headerTextColor,
});

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
];

const changeColor = (name: string) => {
    themeStore.setPropertyColor(color[name], name)
};

const resetTheme = () => {
    themeStore.resetTheme()
};

const getInverseColor = (color: string) => {
    color = color.substring(1)
    const r = parseInt(color.substring(0, 2), 16)
    const g = parseInt(color.substring(2, 4), 16)
    const b = parseInt(color.substring(4, 6), 16)
    const inverseR = (255 - r).toString(16).padStart(2, '0')
    const inverseG = (255 - g).toString(16).padStart(2, '0')
    const inverseB = (255 - b).toString(16).padStart(2, '0')
    return `#${inverseR}${inverseG}${inverseB}`
};

const getGradientBackground = (color1: string, color2: string) => {
    return `linear-gradient(to bottom, ${color1} 0%, ${color1} 30%, ${color2} 0%, ${color2} 70%)`;
};

const systemmap = {
    '1': {
        name: '默认',
        headerBgColor:'#000000',
        headerTextColor: '#ffffff',
        sidebarBgColor: '#000000',
        sidebarTextColor: '#ffffff',
        sidebarIndexBgColor: '#4B4B4B',
        sidebarIndexTextColor: '#ffffff',
        shadowColor: '#ff0000',
        nodeptBgColor: '#99CCCC',
        nodeptTextColor: '#663333',
    },
    '2': {
        name: '灰色',
        headerBgColor:'#4B4B4B',
        headerTextColor: '#ffffff',
        sidebarBgColor: '#4B4B4B',
        sidebarTextColor: '#ffffff',
        sidebarIndexBgColor: '#808080',
        sidebarIndexTextColor: '#ffffff',
        shadowColor: '#00ff00',
        nodeptBgColor: '#ffffff',
        nodeptTextColor: '#000000',
    },
    '3': {
        name: '双拼',
        headerBgColor:'#000000',
        headerTextColor: '#ffffff',
        sidebarBgColor: '#4B4B4B',
        sidebarTextColor: '#ffffff',
        sidebarIndexBgColor: '#808080',
        sidebarIndexTextColor: '#ffffff',
        shadowColor: '#ff0000',
        nodeptBgColor: '#ffffff',
        nodeptTextColor: '#000000',
    },
    '4': {
        name: '洁净',
        headerBgColor:'#003366',
        headerTextColor: '#ffffff',
        sidebarBgColor: '#003366',
        sidebarTextColor: '#ffffff',
        sidebarIndexBgColor: '#666699',
        sidebarIndexTextColor: '#ffffff',
        shadowColor: '#0099FF',
        nodeptBgColor: '#FFCC99',
        nodeptTextColor: '#003366',
    },
    '5': {
        name: '小粉红',
        headerBgColor:'#FF99CC',
        headerTextColor: '#000000',
        sidebarBgColor: '#FF99CC',
        sidebarTextColor: '#000000',
        sidebarIndexBgColor: '#CCCCFF',
        sidebarIndexTextColor: '#000000',
        shadowColor: '#663366',
        nodeptBgColor: '#006633',
        nodeptTextColor: '#FF99CC',
    },
    '6': {
        name: '明亮',
        headerBgColor:'#ffffff',
        headerTextColor: '#000000',
        sidebarBgColor: '#ffffff',
        sidebarTextColor: '#000000',
        sidebarIndexBgColor: '#CCCCCC',
        sidebarIndexTextColor: '#000000',
        shadowColor: '#CCCCCC',
        nodeptBgColor: '#000000',
        nodeptTextColor: '#ffffff',
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
    themeStore.setShadowColor(data.shadowColor);
    themeStore.setNodeptBgColor(data.nodeptBgColor);
    themeStore.setNodeptTextColor(data.nodeptTextColor);
};

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
};

</script>

<style scoped>
.theme-list {
    display: flex;
    flex-wrap: wrap;
    justify-content: center;
}

.theme-item {
    margin-right: 10px;
    margin-top: 10px;
    padding: 30px;
    width: 70px;
    border: 1px solid #dcdfe6;
    border-radius: 10px;
    text-align: center;
}

.theme-color {
    margin: 20px 0;
}
</style>
