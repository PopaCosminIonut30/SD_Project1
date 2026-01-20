import React, { useState, useEffect, useRef } from 'react';
import Stomp from 'stompjs';
import { toast, ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

const ChatAndNotification = ({ user }) => {
    const [messages, setMessages] = useState([]);
    const [input, setInput] = useState('');
    const [isOpen, setIsOpen] = useState(false);
    const stompClient = useRef(null);

    useEffect(() => {
        // 1. Definim URL-ul (ws:// pentru WebSocket nativ prin Traefik)
        const socketUrl = 'ws://localhost/ws-energy';
        const socket = new WebSocket(socketUrl);

        // 2. Inițializăm clientul Stomp peste socket-ul nativ
        const client = Stomp.over(socket);

        // Dezactivăm log-urile de debug pentru a vedea doar mesajele noastre
        client.debug = (str) => {
            if (str.includes("Received") || str.includes("Connected")) console.log(str);
        };

        // 3. Încercăm conectarea
        client.connect({}, (frame) => {
            console.log('✅ CONNECTED TO WEBSOCKET');
            stompClient.current = client;

            // Subscriere la notificări de supraconsum
            client.subscribe('/topic/notifications', (msg) => {
                const alertData = JSON.parse(msg.body);
                toast.error(`⚠️ ALERTA: ${alertData.message}`, { position: "top-right" });
            });

            // Subscriere la chat
            client.subscribe('/topic/chat', (msg) => {
                const chatMsg = JSON.parse(msg.body);
                setMessages((prev) => [...prev, chatMsg]);
            });

        }, (error) => {
            console.error('❌ WebSocket error:', error);
        });

        // Cleanup la închiderea componentei
        return () => {
            if (stompClient.current) {
                stompClient.current.disconnect();
                console.log('Disconnected');
            }
        };
    }, [user]);

    const sendMessage = (e) => {
        e.preventDefault();
        if (input.trim() && stompClient.current && stompClient.current.connected) {
            const chatMsg = {
                sender: user?.username || "Guest",
                content: input,
                timestamp: Date.now()
            };
            stompClient.current.send("/app/chat", {}, JSON.stringify(chatMsg));
            setInput('');
        }
    };

    return (
        <>
            <ToastContainer />
            <div className="fixed bottom-4 right-4 z-50">
                <button
                    onClick={() => setIsOpen(!isOpen)}
                    className="bg-cyan-600 p-4 rounded-full shadow-lg hover:bg-cyan-500 transition-all text-white font-bold"
                >
                    {isOpen ? 'Închide' : 'Suport Chat'}
                </button>
                {isOpen && (
                    <div className="bg-gray-800 border border-gray-700 w-80 h-96 mt-2 rounded-lg flex flex-col shadow-2xl overflow-hidden">
                        <div className="p-3 border-b border-gray-700 font-bold text-cyan-400">Support Chat</div>
                        <div className="flex-1 overflow-y-auto p-3 space-y-2">
                            {messages.map((m, i) => (
                                <div key={i} className={`p-2 rounded-lg text-sm text-white ${m.sender === user?.username ? 'bg-cyan-900 ml-8' : 'bg-gray-700 mr-8'}`}>
                                    <span className="block text-xs text-gray-400 font-bold">{m.sender}</span>
                                    {m.content}
                                </div>
                            ))}
                        </div>
                        <form onSubmit={sendMessage} className="p-2 border-t border-gray-700 flex">
                            <input
                                value={input}
                                onChange={(e) => setInput(e.target.value)}
                                className="flex-1 bg-gray-700 text-white rounded-l p-2 outline-none text-sm"
                                placeholder="Scrie un mesaj..."
                            />
                            <button type="submit" className="bg-cyan-600 text-white px-3 rounded-r">Trimite</button>
                        </form>
                    </div>
                )}
            </div>
        </>
    );
};

export default ChatAndNotification;