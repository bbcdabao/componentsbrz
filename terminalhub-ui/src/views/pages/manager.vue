<template>
    <div class="this-page">
        <div style="padding: 10px;">
            <el-card class="mgb20 custom-shadow" shadow="hover">
                <template #header>
                    <div class="content-title">{{ $t('managerWelcome') }}</div>
                </template>
                <div class="manager-items">
                    <div class="manager-item">
                        <el-form :model="form" :rules="rules" ref="formRef" autocomplete="off">
                            <el-form-item label="地址" prop="addr">
                                <el-input v-model="form.addr"></el-input>
                            </el-form-item>
                            <el-form-item label="用户" prop="user">
                                <el-input v-model="form.user" autocomplete="new-username"></el-input>
                            </el-form-item>
                            <el-form-item label="密码" prop="pass">
                                <el-input v-model="form.pass" type="password" autocomplete="new-password"></el-input>
                            </el-form-item>
                            <el-input
                                type="textarea"
                                :autosize="{ minRows: 2, maxRows: 4}"
                                :placeholder="$t('pleaseEnterCmd')"
                                v-model="runcmds">
                            </el-input>
                            <el-button style="margin-top: 10px;" type="primary" @click="submitForm">{{ $t('addTerminal') }}</el-button>
                        </el-form>
                    </div>
                </div>
            </el-card>
        </div>
        <div style="padding: 10px;">
            <el-card class="mgb20 custom-shadow" shadow="hover">
                <template #header>
                    <div class="content-title">{{ $t('terminalManagement') }}</div>
                </template>
                <div class="manager-nodeitems">
                    <div class="manager-nodeitem" v-for="(item, index) in sidebar.sshitems" :key="index">
                        <el-button style="font-size: 16px;" size="small" type="danger" @click="deleteItem(index)" icon="delete-filled" circle />
                        {{ item.addr }}
                    </div>
                </div>
            </el-card>
        </div>
    </div>
</template>
<script setup lang="ts">
import { ref } from 'vue';
import { useSidebarStore } from '@/store/sidebar';
import { ElButton, ElForm, ElFormItem, ElInput, ElMessage, FormRules } from 'element-plus';

const sidebar = useSidebarStore();

const runcmds = ref("");

const formRef = ref(null);
const form = ref({
  addr: '',
  user: '',
  pass: ''
});

const rules = ref<FormRules>({
  addr: [
    { required: true, message: '', trigger: 'blur' },
    { 
      validator: (rule, value, callback) => {
        const ipPortPattern = /^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?):([0-9]{1,5})$/;
        if (!ipPortPattern.test(value)) {
          callback(new Error('IP:PORT'));
        } else {
          callback();
        }
      }, 
      trigger: 'blur' 
    }
  ],
  user: [{ required: true, message: '', trigger: 'blur' }],
  pass: [{ required: true, message: '', trigger: 'blur' }]
});

const submitForm = () => {
  formRef.value.validate((valid: boolean) => {
    if (valid) {
      sidebar.addSshitem({
        addr: form.value.addr,
        user: encodeURIComponent(form.value.user),
        pass: encodeURIComponent(form.value.pass),
        cmds: runcmds.value
      }) 
    } else {
      ElMessage.error('fail')
    }
  })
};

const deleteItem = (index: string | number) => {
    if (typeof index === 'string') {
        sidebar.delSshitem(index);
    }
};

const setItem = (index: string | number) => {
    if (typeof index === 'string') {
        let sshitem = sidebar.sshitems[index];
        form.value = sshitem;
        runcmds.value = sshitem.cmds;
    }
};

</script>
<style scoped>
.this-page {
    display: flex;
    flex-direction: column;
}
.logo {
    width: 200px;
    padding: 10px;
    flex-shrink: 0;
}
.manager-items {
    display: flex;
    align-items: flex-start;
}
.manager-item {
    flex-grow: 1;
    width: 400px;
    padding: 10px;
    border-radius: 5px;
    background-color: var(--sidebar-index-bg-color);
}
.manager-nodeitems {
    display: flex;
    align-items: flex-start;
    flex-wrap: wrap;
}
.manager-nodeitem {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 4px;
    color: var(--nodept-text-color);
    width: 215x;
    margin: 4px;
    border-radius: 5px;
    font-weight: bold;
    flex-shrink: 0;
    background-color: var(--nodept-bg-color);
}
.el-image {
  margin-left: 20px;
  width: 200px;
  border-radius: 20px;
  flex-shrink: 0;
}
</style>
