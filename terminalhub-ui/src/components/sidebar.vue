<template>
    <div class="sidebar">
        <el-menu
            class="sidebar-el-menu"
            :default-active="onRoutes"
            :collapse="sidebar.collapse"
            :background-color="sidebar.bgColor"
            :text-color="sidebar.textColor"
            router
        >
                    <el-sub-menu :index="1" :key="1">
                        <template #title>
                            <el-icon>
                                <credit-card />
                            </el-icon>
                            <span>当前终端列表</span>
                        </template>
                    </el-sub-menu>
                    <template v-for="subItem in sidebar.sshitems" :key="subItem.addr">
                        <el-menu-item :index="subItem.addr">
                            {{ subItem.addr }}
                        </el-menu-item>
                    </template>

                    
        </el-menu>
    </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { useSidebarStore } from '../store/sidebar';
import { useRoute } from 'vue-router';
import { menuData } from '@/components/menu';

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
