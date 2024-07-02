<template>
  <div id="k8sConsoleView">
    <el-dialog
      :title="consoleTitleShow"
      :visible.sync="dialogVisible"
      :before-close="onBeforeClose"
      :show-close="true"
      v-loading="isLoading"
      @open="onOpen"
    >
    <div id="xterm" class="xterm" />
    </el-dialog>
  </div>
</template>

<script>

import 'xterm/css/xterm.css'
import { Terminal } from 'xterm'
import { FitAddon } from 'xterm-addon-fit'
import { AttachAddon } from 'xterm-addon-attach'

export default {
  name: 'K8sConsoleView',
  componentName: 'K8sConsoleView',
  props: ['paramIn', 'dialogVisible'],
  data () {
    return {
      isLoading: false,

      consoleTitle: '',
      consoleTitleShow: '',
      socketURI: '',

      socket: null,
      term: null
    }
  },
  methods: {
    onOpen () {
      this.consoleTitle = this.paramIn.consoleTitle
      this.consoleTitleShow = this.consoleTitle
      this.socketURI = this.paramIn.socketURI
      this.$nextTick(() => {
        this.onOpenAct()
      })
    },
    onOpenAct () {
      this.initSocket()
    },
    onBeforeClose (done) {
      this.$emit('dialogClose')
      this.socket.close()
      this.term.dispose()
    },
    initTerm() {
      const term = new Terminal({
        fontSize: 16,
        cols: 128,
        rows: 39,
        cursorBlink: true
      });
      const attachAddon = new AttachAddon(this.socket)
      const fitAddon = new FitAddon()
      term.loadAddon(attachAddon)
      term.loadAddon(fitAddon)
      term.open(document.getElementById('xterm'));
      fitAddon.fit()
      term.focus()
      this.term = term
    },
    initSocket() {
      this.socket = new WebSocket(this.socketURI);
      this.socket.onopen = () => {
        // 链接成功后
        this.socketOnOpen();
      }
      this.socket.onclose = () => {
        this.socketOnClose()
      }
      this.socket.onerror = () => {
        this.socketOnError()
      }
    },
    socketOnOpen() {
      this.consoleTitleShow = this.consoleTitle + '---[connect ok]'
      this.initTerm()
    },
    socketOnClose() {
      this.consoleTitleShow = this.consoleTitle + '---[connect close]'
    },
    socketOnError() {
      this.consoleTitleShow = this.consoleTitle + '---[connect error]'
    }
  }
}
</script>

<style>
@import "";
#k8sConsoleView .el-dialog {
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
  margin: 0px !important;
  width: 80% !important;
  min-width: 600px !important;
  top: 10% !important;
  min-height: 50% !important;
  height: 80%;
  margin-bottom: 0 !important;
  border-radius: 6px !important;
}

</style>

    "xlsx": "^0.12.10",
    "xterm": "^4.9.0",
    "xterm-addon-attach": "^0.6.0",
    "xterm-addon-fit": "^0.4.0"
