<template>
    <div class="error-page">
        <div class="error-box">
            <div class="error-code">404</div>
            <div class="error-desc">{{ $t('pageNotExist') }}</div>
            <div class="error-handle">
                <router-link to="/">
                    <el-button type="primary" size="large">{{ $t('backToMain') }}</el-button>
                </router-link>
                <el-button class="error-btn" size="large" @click="goBack">{{ $t('backToPrev') }}</el-button>
            </div>
        </div>
    </div>
</template>
<script setup lang="ts" name="404">
import { useRouter } from 'vue-router';
const router = useRouter();
const goBack = () => {
    router.go(-1);
};
</script>
<style scoped>
.error-page {
    display: flex;
    justify-content: center;
    align-items: center;
    flex-direction: column;
    width: 100%;
    height: 100vh;
    background: var(--sidebar-bg-color);
    box-sizing: border-box;
}
.error-box {
    width: 400px;
    background-color: var(--sidebar-index-bg-color);
    padding: 80px 50px;
    border-radius: 5px;
}
.error-code {
    line-height: 1;
    font-size: 100px;
    font-weight: bold;
    color: var(--sidebar-index-text-color);
    margin-bottom: 20px;
    text-align: center;
}
.error-desc {
    font-size: 20px;
    color: var(--sidebar-index-text-color);
    text-align: center;
}
.error-handle {
    margin-top: 50px;
    text-align: center;
}
.error-btn {
    margin-left: 100px;
}
</style>
