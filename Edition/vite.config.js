import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src')
    }
  },
  server: {
    port: 5174,
    proxy: {
      // 后端 application.yml 已配置 context-path: /api，直接透传即可
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
