<template>
    <div class="this-page-ssh">
        <div v-if="connectInfo != 'ok'" class="this-box">
            <div class="this-info">{{ connectInfo }}
                <el-button v-if="!isWsConnecting" type="primary" @click="connectWebSocket">{{ $t('update') }}</el-button>
            </div>
        </div>
        <div v-else id="xterm" class="terminal" />
    </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue';
import { useSidebarStore } from '@/store/sidebar';
import { useThemeStore } from '@/store/theme';
import { useHeaderStore } from '@/store/header';
import { Sshitem } from '@/types/sshitem';
import { useRoute } from 'vue-router';
import { Terminal } from 'xterm';
import { FitAddon } from 'xterm-addon-fit';
import { AttachAddon } from 'xterm-addon-attach';
import 'xterm/css/xterm.css';

const themeStore = useThemeStore();
const sidebar = useSidebarStore();
const route = useRoute();
const header = useHeaderStore();
const param = route.params.param;

let  nowItem : Sshitem = null;
if (!Array.isArray(param)) {
    nowItem = sidebar.sshitems[param];
}

const connectInfo = ref('begin...');

const protocol = window.location.protocol === 'https:' ? 'wss' : 'ws';
const host = window.location.hostname;
const port = window.location.port ? window.location.port : (window.location.protocol === 'https:' ? 443 : 80);
const wspt = `/bbadabao?sessionfactory=sshfactory&addr=${nowItem.addr}&user=${nowItem.user}&pass=${nowItem.pass}`;
let wbsurl = `${protocol}://${host}:${port}` + wspt;
if (import.meta.env.MODE === 'development') {
    wbsurl = `ws://localhost:9090` + wspt;
}
const ws = ref<WebSocket | null>(null);

const isWsConnecting = ref(false);
const connectWebSocket = () => {
    isWsConnecting.value = true;
    connectInfo.value = 'websocket begin connect ...';
    ws.value = new WebSocket(wbsurl);
    ws.value.onopen = () => {
        connectInfo.value = 'ok';
        nextTick(() => {
            initTerm();
        });
    };
    ws.value.onerror = () => {
        isWsConnecting.value = false;
        connectInfo.value = 'websocket error';
    };
    ws.value.onclose = () => {
        isWsConnecting.value = false;
        connectInfo.value = 'websocket connection closed';
    };
};

const terminalRef = ref(null);
const initTerm = () => {
    const term = new Terminal({
        fontSize: 16,
        cursorBlink: true,
        theme: {
            background: themeStore.sidebarBgColor,
            foreground: themeStore.sidebarTextColor
        }
    });
    const attachAddon = new AttachAddon(ws.value);
    const fitAddon = new FitAddon();
    term.loadAddon(attachAddon);
    term.loadAddon(fitAddon);
    const terminalElement = document.getElementById('xterm');
    if (terminalElement) {
        term.open(terminalElement);
        fitAddon.fit();
        console.log('Initial size - Cols:', term.cols, 'Rows:', term.rows);
        const handleResize = () => {
            fitAddon.fit();
            console.log('Resized - Cols:', term.cols, 'Rows:', term.rows);
        };
        window.addEventListener('resize', handleResize);
        onBeforeUnmount(() => {
            window.removeEventListener('resize', handleResize);
        });
        term.focus();
        terminalRef.value = term;
    } else {
        connectInfo.value = 'Terminal element not found';
    }
};

const closeWebSocket = () => {
    if (ws.value) {
        ws.value.close();
    }
};

onMounted(() => {
    console.info("begin:", nowItem);
    connectWebSocket();
    header.setTitlesp(nowItem.addr);
});

onBeforeUnmount(() => {
    console.info("end:", nowItem);
    closeWebSocket();
    header.setTitlesp('');
});

</script>

<style scoped>
.this-page-ssh {
    width: 100%;
    height: 100%;
    border: 0;
    padding: 0;
    margin: 0;
	background: var(--sidebar-bg-color);
}
.this-box {
    width: 100%;
    height: 100%;
    padding: 100px 50px;
    border-radius: 10px;
    background-color: var(--sidebar-bg-color);
}
.this-info {
    line-height: 1;
    font-size: 40px;
    font-weight: bold;
    margin-bottom: 20px;
    text-align: center;
    color: var(--sidebar-text-color);
}
.terminal {
    width: 100%;
    height: 100%;
    border: 0;
    padding: 0;
    margin: 0;
    background-color: var(--sidebar-bg-color);
}
</style>
