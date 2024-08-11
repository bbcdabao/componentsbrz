import { createApp } from 'vue';
import { createPinia } from 'pinia';
import * as ElementPlusIconsVue from '@element-plus/icons-vue';
import App from './App.vue';
import router from '@/router';
import 'element-plus/dist/index.css';
import '@/assets/css/icon.css';
import i18n from '@/i18n';

const app = createApp(App);
app.use(createPinia());
app.use(router);
app.use(i18n);

// 注册elementplus图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component);
}

app.mount('#app');
