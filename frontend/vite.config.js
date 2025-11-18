import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vitejs.dev/config/
export default defineConfig({
    plugins: [react()],
    server: {
        // Acesta este pasul CRUCIAL
        // Spunem serverului de dev (Vite) să redirecționeze toate cererile /api
        // către backend-ul nostru (Traefik, care rulează pe portul 80)
        proxy: {
            '/api': {
                target: 'http://localhost', // Portul 80 (standard HTTP) al Traefik
                changeOrigin: true,
            }
        }
    }
})