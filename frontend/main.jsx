import React from 'react'
import ReactDOM from 'react-dom/client'
import App from './app.jsx' // Importăm componenta ta principală
import './index.css'     // Importăm stilurile Tailwind

ReactDOM.createRoot(document.getElementById('root')).render(
    <React.StrictMode>
        <App />
    </React.StrictMode>,
)