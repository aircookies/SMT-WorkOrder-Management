import { fileURLToPath, URL } from 'node:url'
import { visualizer } from 'rollup-plugin-visualizer'
import viteCompression from 'vite-plugin-compression'

import { defineConfig } from 'vite'
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import {ElementPlusResolver} from 'unplugin-vue-components/resolvers'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [
    // vueDevTools(),
    AutoImport({
      resolvers: [
        ElementPlusResolver({ importStyle: 'sass' }),
      ],
    }),
    Components({
      resolvers: [
        ElementPlusResolver({ importStyle: 'sass' }),
      ],
      dirs: ['src/components'],
    }),
    visualizer({
      filename: 'stats.html',
      gzipSize: true,
    }),
    viteCompression(),
    vue(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },
  build: {
    rollupOptions: {
      output: {
        manualChunks: {
          'element-plus-icons': ['@element-plus/icons-vue'],
          'echarts': ['echarts'],
          'vue-vendor': ['vue', 'vue-router'],
          'crypto': ['crypto-js', 'jsencrypt'],
        },
      },
    },
    minify: 'terser',
  },
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, ''),
      },
    },
  }
})