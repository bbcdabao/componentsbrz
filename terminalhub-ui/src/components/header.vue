<template>
    <div class="header">
        <el-dialog v-model="isDialogVisible" title="添加终端配置">
            <el-form :model="form" :rules="rules" ref="formRef">
                <el-form-item label="地址" prop="addr">
            <el-input v-model="form.addr"></el-input>
            </el-form-item>
            <el-form-item label="用户" prop="user">
                <el-input v-model="form.user"></el-input>
            </el-form-item>
            <el-form-item label="密码" prop="pass">
                <el-input v-model="form.pass" type="password"></el-input>
            </el-form-item>
            <span>
                <el-button @click="closeDialog">取 消</el-button>
                <el-button type="primary" @click="submitForm">确 定</el-button>
            </span>
            </el-form>
        </el-dialog>
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
            <div class="web-title">添加终端</div>
                <el-icon class="addlogopt" @click="openDialog">
                    <soccer />
                </el-icon>
                <el-icon class="addlogo" @click="openDialog">
                    <circle-plus-filled />
                </el-icon>
            </div>
        </div>
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
import { onMounted } from 'vue';
import { useSidebarStore } from '../store/sidebar';
import { useRouter } from 'vue-router';
import imgurl from '../assets/img/img.jpg';
import { ref } from 'vue'
import { ElButton, ElDialog, ElForm, ElFormItem, ElInput, ElMessage, FormRules } from 'element-plus'


const isDialogVisible = ref(false)

const formRef = ref(null)
const form = ref({
  addr: '',
  user: '',
  pass: ''
})

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

const openDialog = () => {
  isDialogVisible.value = true
}

const closeDialog = () => {
  isDialogVisible.value = false
}

const submitForm = () => {
  formRef.value.validate((valid: boolean) => {
    if (valid) {
      sidebar.addSshitem({
        addr: form.value.addr,
        user: form.value.user,
        pass: form.value.pass
      }) 
      isDialogVisible.value = false
    } else {
      ElMessage.error('表单校验失败')
    }
  })
}

const username: string | null = localStorage.getItem('vuems_name');

const sidebar = useSidebarStore();
// 侧边栏折叠
const collapseChage = () => {
    sidebar.handleCollapse();
};

onMounted(() => {
    if (document.body.clientWidth < 600) {
        collapseChage();
    }
});

// 用户名下拉菜单选择事件
const router = useRouter();
const handleCommand = (command: string) => {
    if (command == 'loginout') {
        localStorage.removeItem('vuems_name');
        router.push('/login');
    }
};

const setFullScreen = () => {
    if (document.fullscreenElement) {
        document.exitFullscreen();
    } else {
        document.body.requestFullscreen.call(document.body);
    }
};
</script>
<style scoped>
.header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    box-sizing: border-box;
    width: 100%;
    height: 40px;
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

.addlogopt {
    font-size: 32px;
    fill: rgb(255, 255, 255);
    animation: swing 1s linear infinite;
    animation-iteration-count: 1;
}

.addlogo {
    font-size: 32px;
    fill: rgb(255, 255, 255);
    animation: rotate 1.2s linear infinite;
    animation-iteration-count: 1;
}

.logo {
    width: 32px;
    fill: rgb(255, 255, 255);
}

@keyframes swing {
    0% {
        transform: translateX(-250%) rotate(0deg);
    }
    20% {
        transform: translateX(-200%) rotate(72deg);
    }
    40% {
        transform: translateX(-150%) rotate(144deg);
    }
    60% {
        transform: translateX(-100%) rotate(216deg);
    }
    80% {
        transform: translateX(-50%) rotate(288deg);
    }
    100% {
        transform: translateX(-0%) rotate(360deg);
    }
}

@keyframes rotate {
    0% {
        transform: scale(1);
    }
    15% {
        transform: scale(1);
    }
    30% {
        transform: scale(1);
    }
    45% {
        transform: scale(1);
    }
    60% {
        transform: scale(1);
    }
    75% {
        transform: scale(1);
    }
    90% {
        transform: scale(1);
    }
    95% {
        transform: scale(1.5);
    }
    100% {
        transform: scale(1);
    }
}

.web-title {
    margin: 0 40px 0 10px;
    width: 100px;
    font-size: 16px;
    font-weight: bold;
    text-align: left;
    /*animation: bounce 1s ease-in-out forwards;*/
}

@keyframes bounce {
    0% {
        transform: translateY(-30px);
    }
    16.67% {
        transform: translateY(30px);
    }
    33.34% {
        transform: translateY(-20px);
    }
    50% {
        transform: translateY(20px);
    }
    66.67% {
        transform: translateY(-5px);
    }
    83.34% {
        transform: translateY(5px);
    }
    100% {
        transform: translateY(0);
    }
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
    height: 40px;
    align-items: center;
}

.btn-fullscreen {
    transform: rotate(45deg);
    margin-right: 5px;
    font-size: 24px;
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

.btn-bell-badge {
    position: absolute;
    right: 4px;
    top: 0px;
    width: 8px;
    height: 8px;
    border-radius: 4px;
    background: #f56c6c;
    color: var(--header-text-color);
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
