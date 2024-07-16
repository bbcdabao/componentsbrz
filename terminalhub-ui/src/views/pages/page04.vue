<template>
    <div class="this-page">
        <div class="this-box">
            <div class="this-info">页面04</div>
        </div>
    </div>
</template>

<script setup lang="ts">
</script>

<style scoped>
.this-page {
    display: flex;
    justify-content: center;
    align-items: center;
    flex-direction: column;
    width: 100%;
    height: 100vh;
    background: #eef0fc;
    box-sizing: border-box;
}

.this-box {
    width: 400px;
    background-color: #fff;
    padding: 80px 50px;
    border-radius: 5px;
}

.this-info {
    line-height: 1;
    font-size: 100px;
    font-weight: bold;
    color: var(--el-color-primary);
    margin-bottom: 20px;
    text-align: center;
}
</style>
