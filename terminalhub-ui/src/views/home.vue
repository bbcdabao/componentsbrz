<template>
    <div class="wrapper">
        <v-header />
        <v-sidebar />
        <div class="content-box" :class="{ 'content-collapse': sidebar.collapse }">
            <div class="content">
                <router-view v-slot="{ Component }" :key="$route.fullPath">
                    <transition name="move" mode="out-in">
                        <component :is="Component"></component>
                    </transition>
                </router-view>
            </div>
        </div>
    </div>
</template>
<script setup lang="ts">
import { useSidebarStore } from '@/store/sidebar';
import vHeader from '@/components/header.vue';
import vSidebar from '@/components/sidebar.vue';

const sidebar = useSidebarStore();
</script>

<style>
.wrapper {
    height: 100vh;
    overflow: hidden;
}
.content-box {
    position: absolute;
    left: 250px;
    right: 0;
    top: 40px;
    bottom: 0;
    padding-bottom: 0px;
    -webkit-transition: left 0.3s ease-in-out;
    transition: left 0.3s ease-in-out;
    background: #eef0fc;
    overflow: hidden;
}

.content {
    width: auto;
    height: 100%;
    padding: 0px;
    box-sizing: border-box;
}

.content::-webkit-scrollbar {
    width: 0;
}

.content-collapse {
    left: 64px;
}
</style>
