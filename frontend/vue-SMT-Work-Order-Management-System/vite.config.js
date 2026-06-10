import {fileURLToPath, URL} from 'node:url'

import {defineConfig} from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    vueDevTools(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },
  server: {
    proxy: {
      // 配置API请求代理，所有以/api开头的请求，都会被代理到后端服务器
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        // 将代理路径中的/api去掉
        rewrite: (path) => path.replace(/^\/api/, ''),
      },
    },
  }
})
