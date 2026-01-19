import React, { useState, useEffect, useCallback, useRef } from 'react';
import { LineChart, Line, XAxis, YAxis, Tooltip, CartesianGrid, ResponsiveContainer } from 'recharts';
import SockJS from 'sockjs-client';
import Stomp from 'stompjs';
import { toast, ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

// --- Componenta pentru Graficul de Consum (Cerința 3.2 PDF A2) ---
const ConsumptionChart = ({ deviceId, api }) => {
    const [data, setData] = useState([]);
    const [selectedDate, setSelectedDate] = useState(new Date().toISOString().split('T')[0]);

    const fetchHourlyData = useCallback(async () => {
        try {
            // Endpoint-ul trebuie să returneze un array de forma: [{hour: 0, value: 1.2}, {hour: 1, value: 0.8}...]
            const response = await api.get(`/monitoring/history/${deviceId}?date=${selectedDate}`);
            if (response.ok) {
                const result = await response.json();
                setData(result);
            }
        } catch (error) {
            console.error("Error fetching chart data:", error);
        }
    }, [deviceId, selectedDate, api]);

    useEffect(() => {
        fetchHourlyData();
    }, [fetchHourlyData]);

    return (
        <div className="mt-6 p-4 bg-gray-900 rounded-lg border border-gray-700">
            <div className="flex justify-between items-center mb-4">
                <h4 className="text-sm font-semibold text-cyan-400">Consum Orar (kWh)</h4>
                <input
                    type="date"
                    value={selectedDate}
                    onChange={(e) => setSelectedDate(e.target.value)}
                    className="bg-gray-700 text-xs p-1 rounded border border-gray-600 text-white"
                />
            </div>
            <div className="h-64 w-full">
                <ResponsiveContainer width="100%" height="100%">
                    <LineChart data={data}>
                        <CartesianGrid strokeDasharray="3 3" stroke="#444" />
                        <XAxis
                            dataKey="hour"
                            stroke="#888"
                            fontSize={12}
                            label={{ value: 'Ora (h)', position: 'insideBottom', offset: -5, fill: '#888' }}
                        />
                        <YAxis
                            stroke="#888"
                            fontSize={12}
                            label={{ value: 'kWh', angle: -90, position: 'insideLeft', fill: '#888' }}
                        />
                        <Tooltip contentStyle={{ backgroundColor: '#1f2937', border: 'none', color: '#fff' }} />
                        <Line type="monotone" dataKey="value" stroke="#22d3ee" strokeWidth={2} dot={{ fill: '#22d3ee' }} />
                    </LineChart>
                </ResponsiveContainer>
            </div>
        </div>
    );
};

// --- Componenta Principală Dashboard ---
const ClientDashboard = ({ user, api }) => {
    const [devices, setDevices] = useState([]);
    const [chatMessages, setChatMessages] = useState([]);
    const [chatInput, setChatInput] = useState('');
    const [isChatOpen, setIsChatOpen] = useState(false);
    const stompClient = useRef(null);

    // 1. Fetch Dispozitive (A2)
    const fetchDevices = useCallback(async () => {
        try {
            const response = await api.get(`/devices/user/${user.userId}`);
            if (response.ok) setDevices(await response.json());
        } catch (e) { console.error(e); }
    }, [user.userId, api]);

    // 2. Conectare WebSockets (A3)
    useEffect(() => {
        const socket = new SockJS('http://localhost/ws-energy');
        stompClient.current = Stomp.over(socket);
        stompClient.current.debug = null; // Dezactivează log-urile excesive în consolă

        stompClient.current.connect({}, () => {
            [cite_start]// Subscriere Notificări Supraconsum
            stompClient.current.subscribe('/topic/notifications', (msg) => {
                const alert = JSON.parse(msg.body);
                toast.error(`⚠️ ALERTĂ: ${alert.message}`, {
                    position: "top-right",
                    autoClose: 5000,
                    theme: "dark"
                });
            });

            [cite_start]// Subscriere Chat [cite: 157]
            stompClient.current.subscribe('/topic/chat', (msg) => {
                setChatMessages(prev => [...prev, JSON.parse(msg.body)]);
            });
        });

        fetchDevices();

        return () => {
            if (stompClient.current) stompClient.current.disconnect();
        };
    }, [fetchDevices]);

    const handleSendMessage = (e) => {
        e.preventDefault();
        if (chatInput.trim() && stompClient.current) {
            const msg = {
                sender: user.username,
                content: chatInput,
                timestamp: Date.now()
            };
            stompClient.current.send("/app/chat", {}, JSON.stringify(msg));
            setChatInput('');
        }
    };

    return (
        <div className="p-8 bg-gray-900 min-h-screen text-white">
            <ToastContainer />
            <h1 className="text-3xl font-bold mb-8">Panou Control Client: <span className="text-cyan-400">{user.username}</span></h1>

            <div className="grid grid-cols-1 xl:grid-cols-2 gap-8">
                {devices.map(device => (
                    <div key={device.id} className="bg-gray-800 p-6 rounded-xl shadow-lg border border-gray-700">
                        <div className="flex justify-between items-start mb-4">
                            <div>
                                <h3 className="text-xl font-bold text-white">{device.name}</h3>
                                <p className="text-gray-400 text-sm">{device.address}</p>
                            </div>
                            <span className="bg-yellow-900 text-yellow-200 text-xs px-2 py-1 rounded">
                                Limită: {device.maxConsumption} kWh
                            </span>
                        </div>

                        [cite_start]{/* Graficul integrat pentru fiecare device  */}
                        <ConsumptionChart deviceId={device.id} api={api} />
                    </div>
                ))}
            </div>

            [cite_start]{/* Fereastra de Chat (Cerința 1.1.1 PDF A3)  */}
            <div className="fixed bottom-6 right-6 z-50">
                <button
                    onClick={() => setIsChatOpen(!isChatOpen)}
                    className="bg-cyan-600 hover:bg-cyan-500 text-white p-4 rounded-full shadow-2xl transition-transform transform hover:scale-110"
                >
                    💬 Chat Suport
                </button>

                {isChatOpen && (
                    <div className="absolute bottom-16 right-0 w-80 h-96 bg-gray-800 border border-gray-700 rounded-lg shadow-2xl flex flex-col">
                        <div className="p-4 border-b border-gray-700 font-bold text-cyan-400 flex justify-between">
                            <span>Asistență Clienți</span>
                            <button onClick={() => setIsChatOpen(false)} className="text-gray-500">✕</button>
                        </div>
                        <div className="flex-1 overflow-y-auto p-4 space-y-3 bg-gray-850">
                            {chatMessages.map((m, i) => (
                                <div key={i} className={`flex flex-col ${m.sender === user.username ? 'items-end' : 'items-start'}`}>
                                    <span className="text-[10px] text-gray-500 mb-1">{m.sender}</span>
                                    <div className={`px-3 py-2 rounded-2xl max-w-[80%] text-sm ${
                                        m.sender === user.username ? 'bg-cyan-700 text-white rounded-tr-none' : 'bg-gray-700 text-gray-200 rounded-tl-none'
                                    }`}>
                                        {m.content}
                                    </div>
                                </div>
                            ))}
                        </div>
                        <form onSubmit={handleSendMessage} className="p-3 border-t border-gray-700 flex gap-2">
                            <input
                                value={chatInput}
                                onChange={(e) => setChatInput(e.target.value)}
                                placeholder="Scrie un mesaj..."
                                className="flex-1 bg-gray-700 border-none rounded-lg px-3 py-2 text-sm focus:ring-1 focus:ring-cyan-500 outline-none"
                            />
                            <button type="submit" className="bg-cyan-600 px-3 py-2 rounded-lg hover:bg-cyan-500">➤</button>
                        </form>
                    </div>
                )}
            </div>
        </div>
    );
};

export default ClientDashboard;