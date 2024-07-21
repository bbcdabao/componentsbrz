<template>
    <div class="sidebar">
        <el-menu
            class="sidebar-el-menu"
            :default-active="onRoutes"
            :collapse="sidebar.collapse"
            :background-color="sidebar.bgColor"
            :text-color="sidebar.textColor"
            :default-openeds="['1', '2']"
            router
        >
            <el-menu-item :index="'/welcome'" :key="'/welcome'">
                <el-icon>
                    <eleme-filled />
                </el-icon>
                <template #title>欢迎</template>
            </el-menu-item>
            <el-sub-menu :index="'1'" :key="'1'">
                <template #title>
                    <el-icon>
                        <switch-filled />
                    </el-icon>
                    <span>当前终端列表</span>
                </template>
                <template v-for="subItem in sidebar.sshitems" :key="subItem.addr">
                    <el-menu-item :index="`/telnet/${subItem.addr}`">
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
import { useSidebarStore } from '../store/sidebar';
import { useRoute } from 'vue-router';
import { ElMenu } from 'element-plus';

const route = useRoute();
const onRoutes = computed(() => {
    return route.path;
});

const sidebar = useSidebarStore();

</script>

<style scoped>
.sidebar {
    display: block;
    position: absolute;
    left: 0;
    top: 40px;
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

</style>
