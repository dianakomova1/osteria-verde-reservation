import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig(({ mode }) => ({
  plugins: mode === 'test' ? [] : [react()],
  server: {
    port: 5173,
    proxy: {
      '/api': 'http://localhost:8081',
    },
  },
}))
