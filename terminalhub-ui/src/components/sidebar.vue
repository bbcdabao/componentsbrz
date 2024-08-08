<template>
    <div class="sidebar">
        <el-menu
            class="sidebar-el-menu"
            :default-active="onRoutes"
            :collapse="sidebar.collapse"
            :background-color="themeStore.sidebarBgColor"
            :text-color="themeStore.sidebarTextColor"
            :default-openeds="['1', '2']"
            router
        >
            <el-menu-item :index="'/manager'" :key="'/manager'">
                <el-icon>
                    <eleme-filled />
                </el-icon>
                <template #title>终端管理</template>
            </el-menu-item>
            <el-sub-menu :index="'1'" :key="'1'">
                <template #title>
                    <el-icon>
                        <switch-filled />
                    </el-icon>
                    <span>当前终端列表</span>
                </template>
                <template v-for="subItem in sidebar.sshitems" :key="subItem.addr">
                    <el-menu-item :index="`/sshtelnet/${subItem.addr}`">
                        <el-icon>
                            <tickets />
                        </el-icon>
                        {{ subItem.addr }}
                    </el-menu-item>
                </template>
            </el-sub-menu>
        </el-menu>
    </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { useSidebarStore } from '@/store/sidebar';
import { useThemeStore } from '@/store/theme';
import { useRoute } from 'vue-router';
import { ElMenu } from 'element-plus';

const route = useRoute();
const onRoutes = computed(() => {
    return route.path;
});

const sidebar = useSidebarStore();
const themeStore = useThemeStore();
</script>

<style scoped>
.sidebar {
    display: block;
    position: absolute;
    left: 0;
    top: 48px;
    bottom: 0;
    overflow-y: scroll;
}

.sidebar::-webkit-scrollbar {
    width: 0;
}

.sidebar-el-menu:not(.el-menu--collapse) {
    width: 250px;
}

.sidebar-el-menu {
    min-height: 100%;
}

.sidebar-el-menu .el-menu-item.is-active {
  background-color: var(--sidebar-index-bg-color) !important;
  color: var(--sidebar-index-text-color) !important;
  font-weight: bold !important;
}

</style>
