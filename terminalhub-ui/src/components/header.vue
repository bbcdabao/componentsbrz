<template>
    <div class="header">
        <!-- 折叠按钮 -->
        <div class="header-left">
            <div class="collapse-btn" @click="collapseChage">
                <el-icon v-if="sidebar.collapse">
                    <Expand />
                </el-icon>
                <el-icon v-else>
                    <Fold />
                </el-icon>
            </div>
            <div class="header-left" v-if="!sidebar.collapse">
            </div>
        </div>
        <div>{{ header.titlesp }}</div>
        <div class="header-right">
            <div class="header-user-con">
                <div class="btn-icon" @click="router.push('/theme')">
                    <el-tooltip effect="dark" content="设置主题" placement="bottom">
                        <i class="el-icon-lx-skin"></i>
                    </el-tooltip>
                </div>
                <div class="btn-icon" @click="setFullScreen">
                    <el-tooltip effect="dark" content="全屏" placement="bottom">
                        <i class="el-icon-lx-full"></i>
                    </el-tooltip>
                </div>
                <!-- 用户头像 -->
                <el-avatar class="user-avator" :size="32" :src="imgurl" />
                <!-- 用户名下拉菜单 -->
                <el-dropdown class="user-name" trigger="click" @command="handleCommand">
                    <span class="el-dropdown-link">
                        {{ username }}
                        <el-icon class="el-icon--right">
                            <arrow-down />
                        </el-icon>
                    </span>
                    <template #dropdown>
                        <el-dropdown-menu>
                            <el-dropdown-item divided command="loginout">退出登录</el-dropdown-item>
                        </el-dropdown-menu>
                    </template>
                </el-dropdown>
            </div>
        </div>
    </div>
</template>
<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useSidebarStore } from '@/store/sidebar';
import { useHeaderStore } from '@/store/header';
import { useRouter } from 'vue-router';
import { ElButton, ElDialog, ElForm, ElFormItem, ElInput, ElMessage, FormRules } from 'element-plus';
import imgurl from '@/assets/img/img.jpg';

const isDialogVisible = ref(false);

const formRef = ref(null);
const form = ref({
  addr: '',
  user: '',
  pass: ''
});

const rules = ref<FormRules>({
  addr: [
    { required: true, message: '请输入地址', trigger: 'blur' },
    { 
      validator: (rule, value, callback) => {
        const ipPortPattern = /^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?):([0-9]{1,5})$/;
        if (!ipPortPattern.test(value)) {
          callback(new Error('地址格式应为 IP:PORT'));
        } else {
          callback();
        }
      }, 
      trigger: 'blur' 
    }
  ],
  user: [{ required: true, message: '请输入用户', trigger: 'blur' }],
  pass: [{ required: true, message: '请输入密码', trigger: 'blur' }]
});

const closeDialog = () => {
  isDialogVisible.value = false
};

const submitForm = () => {
  formRef.value.validate((valid: boolean) => {
    if (valid) {
      sidebar.addSshitem({
        addr: form.value.addr,
        user: encodeURIComponent(form.value.user),
        pass: encodeURIComponent(form.value.pass)
      }) 
      isDialogVisible.value = false
    } else {
      ElMessage.error('表单校验失败')
    }
  })
};

const username: string | null = localStorage.getItem('vuems_name');

const sidebar = useSidebarStore();
const header = useHeaderStore();
// 侧边栏折叠
const collapseChage = () => {
    sidebar.handleCollapse();
}

onMounted(() => {
    if (document.body.clientWidth < 600) {
        collapseChage();
    }
})

// 用户名下拉菜单选择事件
const router = useRouter();
const handleCommand = (command: string) => {
    if (command == 'loginout') {
        localStorage.removeItem('vuems_name');
        router.push('/login');
    }
}

const setFullScreen = () => {
    if (document.fullscreenElement) {
        document.exitFullscreen();
    } else {
        document.body.requestFullscreen.call(document.body);
    }
}

</script>
<style scoped>
.header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    box-sizing: border-box;
    width: 100%;
    height: 48px;
    color: var(--header-text-color);
    background-color: var(--header-bg-color);
    border-bottom: 1px solid #ddd;
}

.header-left {
    display: flex;
    align-items: center;
    padding-left: 10px;
    height: 100%;
}

.collapse-btn {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 100%;
    padding: 0 10px;
    cursor: pointer;
    opacity: 0.8;
    font-size: 22px;
}

.collapse-btn:hover {
    opacity: 1;
}

.header-right {
    float: right;
    padding-right: 50px;
}

.header-user-con {
    display: flex;
    height: 48px;
    align-items: center;
}

.btn-icon {
    position: relative;
    width: 30px;
    height: 30px;
    text-align: center;
    cursor: pointer;
    display: flex;
    align-items: center;
    color: var(--header-text-color);
    margin: 0 5px;
    font-size: 20px;
}

.user-avator {
    margin: 0 10px 0 20px;
}

.el-dropdown-link {
    color: var(--header-text-color);
    cursor: pointer;
    display: flex;
    align-items: center;
}

.el-dropdown-menu__item {
    text-align: center;
}

</style>
