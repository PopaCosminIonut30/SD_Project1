import React, { useState, useEffect, useRef } from 'react';
import SockJS from 'sockjs-client';
import Stomp from 'stompjs';
import { toast, ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

const ChatAndNotification = ({ user }) => {
    const [messages, setMessages] = useState([]);
    const [input, setInput] = useState('');
    const [isOpen, setIsOpen] = useState(false);
    const stompClient = useRef(null);

    // În interiorul useEffect
    useEffect(() => {
        // UTILIZĂM CALE RELATIVĂ - Vite va face proxy la localhost:8085
        const socket = new SockJS('/ws-energy');
        const client = Stomp.over(socket);

        client.connect({}, (frame) => {
            console.log('Connected to WebSocket: ' + frame);
            stompClient.current = client;

            // Abonările se fac DOAR în interiorul acestui callback de succes
            client.subscribe('/topic/notifications', (msg) => {
                const alertData = JSON.parse(msg.body);
                toast.error(`⚠️ ALERTA: ${alertData.message}`, { position: "top-right" });
            });

            client.subscribe('/topic/chat', (msg) => {
                const chatMsg = JSON.parse(msg.body);
                setMessages((prev) => [...prev, chatMsg]);
            });
        }, (error) => {
            console.error('WebSocket connection error:', error);
        });

        return () => {
            if (stompClient.current) stompClient.current.disconnect();
        };
    }, [user]);

// Corecție în sendMessage
    const sendMessage = (e) => {
        if (e) e.preventDefault(); // Prevenim refresh-ul paginii

        if (input.trim() && stompClient.current && stompClient.current.connected) {
            const chatMsg = {
                sender: user.username,
                content: input,
                timestamp: Date.now()
            };

            // Pentru librăria 'stompjs' veche, folosim .send
            stompClient.current.send("/app/chat", {}, JSON.stringify(chatMsg));

            setMessages(prev => [...prev, chatMsg]);
            setInput('');
        } else {
            toast.warn("Conexiunea nu este gata. Mai încearcă o dată.");
        }
    };

    return (
        <>
            <ToastContainer />
            <div className="fixed bottom-4 right-4 z-50">
                <button
                    onClick={() => setIsOpen(!isOpen)}
                    className="bg-cyan-600 p-4 rounded-full shadow-lg hover:bg-cyan-500 transition-all"
                >
                    Chat Support
                </button>

                {isOpen && (
                    <div className="bg-gray-800 border border-gray-700 w-80 h-96 mt-2 rounded-lg flex flex-col shadow-2xl">
                        <div className="p-3 border-b border-gray-700 font-bold text-cyan-400">Support Chat</div>
                        <div className="flex-1 overflow-y-auto p-3 space-y-2">
                            {messages.map((m, i) => (
                                <div key={i} className={`p-2 rounded-lg text-sm ${m.sender === user.username ? 'bg-cyan-900 ml-8' : 'bg-gray-700 mr-8'}`}>
                                    <span className="block text-xs text-gray-400 font-bold">{m.sender}</span>
                                    {m.content}
                                </div>
                            ))}
                        </div>
                        <form onSubmit={sendMessage} className="p-2 border-t border-gray-700 flex">
                            <input
                                value={input}
                                onChange={(e) => setInput(e.target.value)}
                                className="flex-1 bg-gray-700 rounded-l p-2 outline-none text-sm"
                                placeholder="Type a message..."
                            />
                            <button className="bg-cyan-600 px-3 rounded-r">Send</button>
                        </form>
                    </div>
                )}
            </div>
        </>
    );
};

export default ChatAndNotification;