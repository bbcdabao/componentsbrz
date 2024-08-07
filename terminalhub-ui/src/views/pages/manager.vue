<template>
    <div class="this-page">
        <div style="padding: 10px;">
            <el-card class="mgb20 custom-shadow" shadow="hover">
                <template #header>
                    <div class="content-title">欢迎使用斯塔克工业产品</div>
                </template>
                <div class="manageradd">
                <div class="additerm">
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
                    <el-button type="primary" @click="submitForm">添 加</el-button>
                </el-form>
                </div>
                <el-image style="margin-left: 20px; width: 130px; border-radius:20px" :src="imgurl01" alt="" />
                <el-image style="margin-left: 20px; width: 130px; border-radius:20px" :src="imgurl02" alt="" />
                <el-image style="margin-left: 20px; width: 130px; border-radius:20px" :src="imgurl03" alt="" />
                </div>
            </el-card>
        </div>
        <div style="padding: 10px;">
            <el-card class="mgb20 custom-shadow" shadow="hover">
                <template #header>
                    <div class="content-title">终端管理</div>
                </template>
            </el-card>
        </div>
    </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useSidebarStore } from '@/store/sidebar';
import { ElButton, ElForm, ElFormItem, ElInput, ElMessage, FormRules } from 'element-plus';
import imgurl01 from '@/assets/img/1.jpg';
import imgurl02 from '@/assets/img/2.jpg';
import imgurl03 from '@/assets/img/3.jpg';

const sidebar = useSidebarStore();

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

const submitForm = () => {
  formRef.value.validate((valid: boolean) => {
    if (valid) {
      sidebar.addSshitem({
        addr: form.value.addr,
        user: encodeURIComponent(form.value.user),
        pass: encodeURIComponent(form.value.pass)
      }) 
    } else {
      ElMessage.error('表单校验失败')
    }
  })
};
</script>
<style scoped>
.this-page {
    display: flex;
    flex-direction: column;
}
.manageradd {
    display: flex;
    flex-direction: row;
    flex-wrap: nowrap;
    justify-content: space-between;
}
.additerm {
    width: 400px;
    padding: 20px;
    border-radius: 20px;
    background-color: var(--sidebar-index-bg-color);
}
</style>
