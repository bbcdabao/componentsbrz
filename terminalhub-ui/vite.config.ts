import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';
import VueSetupExtend from 'vite-plugin-vue-setup-extend';
import AutoImport from 'unplugin-auto-import/vite';
import Components from 'unplugin-vue-components/vite';
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers';
export default defineConfig({
	base: './',
	plugins: [
		vue(),
		VueSetupExtend(),
		AutoImport({
			resolvers: [ElementPlusResolver()]
		}),
		Components({
			resolvers: [ElementPlusResolver()]
		})
	],
	optimizeDeps: {
		include: ['schart.js']
	},
	resolve: {
		alias: {
			'@': '/src',
			'~': '/src/assets'
		}
	},
	define: {
		__VUE_PROD_HYDRATION_MISMATCH_DETAILS__: "true",
	},
	server: {
		proxy: {
			'/api': {
				target: 'http://127.0.0.1:9090/api',
				changeOrigin: true,
				rewrite: (path) => path.replace(/^\/api/, '')
			},
			'/bbcdabao': {
				target: 'ws://127.0.0.1:9090/bbcdabao',
				changeOrigin: true,
				ws: true,
				rewrite: (path) => path.replace(/^\/bbcdabao/, ''),
				configure: (proxy, options) => {
					proxy.on('proxyReq', (proxyReq, req, res) => {
						console.log('>>>>>> request to:', req.url);
					});
				}
			}
		}
	}
});


