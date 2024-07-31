<template>
    <div class="this-page">
        <div v-if="connectInfo != 'ok'" class="this-box">
            <div class="this-info">{{ connectInfo }}</div>
        </div>
        <div v-else id="xterm" class="terminal" />
    </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue';
import { useSidebarStore } from '@/store/sidebar';
import { useThemeStore } from '@/store/theme';
import { useHeaderStore } from '@/store/header';
import { useRoute } from 'vue-router';
import { Sshitem } from '@/types/sshitem';
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
//const url = `${protocol}://${host}:${port}/bbadabao?sessionfactory=sshfactory`;
const url = `ws://localhost:9090/bbadabao?sessionfactory=sshfactory&addr=${nowItem.addr}&user=${nowItem.user}&pass=${nowItem.pass}`;

const ws = ref<WebSocket | null>(null);
const connectWebSocket = () => {
    connectInfo.value = 'websocket begin connect ...';
    ws.value = new WebSocket(url);
    ws.value.onopen = () => {
        connectInfo.value = 'ok';
        nextTick(() => {
            initTerm();
        });
    };
    ws.value.onerror = () => {
        connectInfo.value = 'websocket error';
    };
    ws.value.onclose = () => {
        connectInfo.value = 'websocket connection closed';
    };
};
const terminalRef = ref(null);
const initTerm = () => {
    const term = new Terminal({
        fontSize: 16,
        cursorBlink: true,
        theme: {
            background: themeStore.headerBgColor,
            foreground: themeStore.headerTextColor
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
        window.addEventListener('resize', () => fitAddon.fit());
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
.this-page {
    display: flex;
    align-items: center;
    flex-direction: column;
    width: 100%;
    height: 100%;
    background: #eef0fc;
    box-sizing: border-box;
}

.this-box {
    width: 100%;
    height: 100%;
    background-color: var(--header-bg-color);
    padding: 100px 50px;
    border-radius: 10px;
}

.this-info {
    line-height: 1;
    font-size: 40px;
    font-weight: bold;
    color: var(--header-text-color);
    margin-bottom: 20px;
    text-align: center;
}

.terminal {
  width: 100%;
  height: 100%;
  background-color: var(--header-bg-color);
}
</style>
