<template>
    <div class="login-bg">
        <div class="login-container">
            <div class="login-header">
                <img class="logo mr10" src="@/assets/img/logo.svg" alt="" />
                <div class="login-title">{{ $t('starkIndustries') }}</div>
            </div>
            <el-form :model="param" :rules="rules" ref="login" size="large">
                <el-form-item prop="language">
                    <vLanguage />
                </el-form-item>
                <el-form-item prop="username">
                    <el-input v-model="param.username" :placeholder="$t('userName')">
                        <template #prepend>
                            <el-icon>
                                <User />
                            </el-icon>
                        </template>
                    </el-input>
                </el-form-item>
                <el-form-item prop="password">
                    <el-input
                        type="password"
                        :placeholder="$t('password')"
                        v-model="param.password"
                        @keyup.enter="submitForm(login)"
                    >
                        <template #prepend>
                            <el-icon>
                                <Lock />
                            </el-icon>
                        </template>
                    </el-input>
                </el-form-item>
                <div class="pwd-tips">
                    <el-checkbox class="pwd-checkbox" v-model="checked" :label="$t('rememberPassword')" />
                </div>
                <el-button class="login-btn" type="primary" size="large" @click="submitForm(login)">{{ $t('login')}}</el-button>
                <p class="login-tips">{{ $t('loginTip') }}</p>
            </el-form>
        </div>
    </div>
</template>
<script setup lang="ts">
import { ref, reactive } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import vLanguage from '@/components/language.vue';
import type { FormInstance, FormRules } from 'element-plus';
import { useI18n } from 'vue-i18n';

const { t } = useI18n();

interface LoginInfo {
    username: string;
    password: string;
}

const lgStr = localStorage.getItem('login-param');
const defParam = lgStr ? JSON.parse(lgStr) : null;
const checked = ref(lgStr ? true : false);

const router = useRouter();
const param = reactive<LoginInfo>({
    username: defParam ? defParam.username : '',
    password: defParam ? defParam.password : '',
});

const rules: FormRules = {
    username: [
        {
            required: true,
            message: t('enterUserName'),
            trigger: 'blur',
        },
    ],
    password: [
        {
            required: true,
            message: t('enterUserPassword'),
            trigger: 'blur'
        }
    ],
};

const login = ref<FormInstance>();
const submitForm = (formEl: FormInstance | undefined) => {
    if (!formEl) return;
    formEl.validate((valid: boolean) => {
        if (valid) {
            ElMessage.success(t('loginSuccess'));
            localStorage.setItem('vuems_name', param.username);
            router.push('/');
            if (checked.value) {
                localStorage.setItem('login-param', JSON.stringify(param));
            } else {
                localStorage.removeItem('login-param');
            }
        } else {
            ElMessage.error(t('loginFail'));
            return false;
        }
    });
};
</script>
<style scoped>
.login-bg {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 100%;
    height: 100vh;
    background: url(@/assets/img/login-bg.jpg) center/cover no-repeat;
}
.login-header {
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 30px;
}
.logo {
    width: 42px;
}
.login-title {
    font-size: 22px;
    color: var(--sidebar-text-color);
    font-weight: bold;
}
.login-container {
    width: 340px;
    margin-left: 8px;
    border-radius: 20px;
    background: var(--sidebar-bg-color);
    padding: 8px 50px 50px;
    box-sizing: border-box;
    border: 1px solid white;
}
.pwd-tips {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-size: 14px;
    margin: -10px 0 10px;
    color: var(--sidebar-text-color);
}
.pwd-checkbox {
    height: auto;
    color: var(--sidebar-text-color);
}
.login-btn {
    display: block;
    width: 100%;
}
.login-tips {
    font-size: 12px;
    color: var(--sidebar-text-color);
}
</style>
