<template>
    <div class="this-page">
        <div style="padding: 10px;">
            <el-card class="mgb20 custom-shadow" shadow="hover">
                <template #header>
                    <div class="content-title">{{ $t('managerWelcome') }}</div>
                </template>
                <div class="manager-form-items">
                    <div class="manager-form-item">
                        <el-form :model="form" :rules="rules" ref="formRef" autocomplete="off">
                            <el-form-item :label="$t('addr')" prop="addr">
                                <el-input v-model="form.addr"></el-input>
                            </el-form-item>
                            <el-form-item :label="$t('user')" prop="user">
                                <el-input v-model="form.user" autocomplete="new-username"></el-input>
                            </el-form-item>
                            <el-form-item :label="$t('pass')" prop="pass">
                                <el-input v-model="form.pass" autocomplete="new-password"></el-input>
                            </el-form-item>
                            <el-button style="margin-top: 0px;" type="primary" @click="submitForm">{{ $t('addTerminal') }}</el-button>
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
                <div class="manager-node-items">
                    <div class="manager-node-item" v-for="(item, index) in sidebar.sshitems" :key="index">
                        <el-button style="font-size: 16px;" size="small" type="danger" @click="deleteItem(index)" icon="delete-filled" circle />
                        {{ item.addr }}
                    </div>
                </div>
            </el-card>
        </div>
    </div>
</template>
<script setup lang="ts">
import { ref, computed } from 'vue';
import { useSidebarStore } from '@/store/sidebar';
import { ElButton, ElForm, ElFormItem, ElInput, ElMessage, FormRules } from 'element-plus';
import { Sshitem } from '@/types/sshitem';
import { useI18n } from 'vue-i18n';

const { t } = useI18n();

const sidebar = useSidebarStore();

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
        pass: encodeURIComponent(form.value.pass)
      })
        ElMessage.success(t('addSuccess'))
    } else {
        ElMessage.error(t('addFail'))
    }
  })
};

const deleteItem = (index: string | number) => {
    if (typeof index === 'string') {
        let sshitem = sidebar.sshitems[index];
        sidebar.delSshitem(index);
        ElMessage.success(t('deleteTerminal') + sshitem.addr);
    }
};

</script>
<style scoped>
.this-page {
    display: flex;
    flex-direction: column;
}
.manager-form-items {
    display: flex;
    align-items: flex-start;
}
.manager-form-item {
    flex-grow: 1;
    border-radius: 5px;
    background-color: transparent;
}
.manager-node-items {
    display: flex;
    flex-wrap: wrap;
    align-items: flex-start;
}
@keyframes expandWidth {
    0% {
        width: 0px;
    }
    100% {
        width: 250px;
    }
}
.manager-node-item {
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
    padding: 4px;
    width: 250px;
    margin: 4px;
    border-radius: 5px;
    font-weight: bold;
    flex-shrink: 0;
    color: var(--nodept-text-color);
    background-color: var(--nodept-bg-color);
    animation: expandWidth 0.5s ease-in-out;
}
</style>
