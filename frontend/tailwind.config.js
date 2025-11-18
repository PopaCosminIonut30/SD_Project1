/** @type {import('tailwindcss').Config} */
export default {
    content: [
        "./index.html",
        "./app.jsx", // Îi spunem să scaneze și app.jsx
        "./main.jsx",
    ],
    theme: {
        extend: {},
    },
    plugins: [],
}