import SockJS from 'sockjs-client';
import Stomp from 'stompjs';

let stompClient = null;

export const connectWebSocket = (onNotificationReceived, onChatMessageReceived) => {
    [cite_start]// Te conectezi la endpoint-ul definit în backend [cite: 171]
    const socket = new SockJS('http://localhost/ws-energy');
    stompClient = Stomp.over(socket);

    stompClient.connect({}, (frame) => {
        console.log('Connected: ' + frame);

        [cite_start]// 1. Te abonezi la notificările de overconsumption
        stompClient.subscribe('/topic/notifications', (notification) => {
            onNotificationReceived(JSON.parse(notification.body));
        });

        [cite_start]// 2. Te abonezi la canalul de chat [cite: 157]
        stompClient.subscribe('/topic/chat', (message) => {
            onChatMessageReceived(JSON.parse(message.body));
        });
    });
};

export const sendChatMessage = (message) => {
    if (stompClient && stompClient.connected) {
        stompClient.send("/app/chat", {}, JSON.stringify(message));
    }
};