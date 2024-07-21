<template>
    <div class="this-page">
        <img class="logo" src="@/assets/img/logo.svg" alt="" />
        <p class="red-large-font">欢迎使用</p>
    </div>
</template>

<script setup lang="ts">
</script>

<style scoped>
.logo {
    width: 300px;
}
.this-page {
    display: flex;
    align-items: center;
    flex-direction: column;
    width: 100%;
    height: 100%;
    background: #eef0fc;
}
.red-large-font {
    color: rgb(161, 60, 6);
    font-size: 50px;
    font-weight: bold;
}
</style>
