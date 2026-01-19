import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vitejs.dev/config/
export default defineConfig({
    plugins: [react()],
    define: { global: 'globalThis' },
    server: {
        proxy: {
            '/api': { target: 'http://localhost', changeOrigin: true },
            // ADAUGĂ ACEASTA pentru WebSocket prin Traefik
            '/ws-energy': {
                target: 'http://localhost:8085',
                ws: true,
                changeOrigin: true
            },
        }
    }
})