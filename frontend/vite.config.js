import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vitejs.dev/config/
export default defineConfig({
    plugins: [react()],
    define: { global: 'globalThis' },
    server: {
        host: true,       // <--- IMPORTANT: Permite accesul din afara containerului
        port: 5173,       // Portul pe care va rula Vite
        strictPort: true,
        proxy: {
            // În Docker, proxy-ul ar trebui să pointeze către numele serviciilor,
            // dar dacă folosești Traefik ca punct central, poți lăsa 'http://localhost'
            // DOAR dacă accesezi aplicația de pe aceeași mașină.
            '/api': {
                target: 'http://traefik:80', // Mai sigur: folosește numele serviciului traefik
                changeOrigin: true,
                secure: false
            },
            '/ws-energy': {
                target: 'http://traefik:80',
                ws: true,
                changeOrigin: true,
                secure: false
            },
        }
    }
})