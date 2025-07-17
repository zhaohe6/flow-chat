import React, { useEffect, useState,useRef } from 'react';
import './ChatPage.css';
// import Websocket from 'websocket';
const ChatPage = () => {
    let ws = new WebSocket('ws://localhost:8080/websocket');
    const [message, setMessage] = useState('');
    const [sender, setSender] = useState('');
    const [receiver, setReceiver] = useState('');
    const [messageHistory, setMessageHistory] = useState([]);
    useEffect(() => {
        // 创建websocket连接
        ws.onopen = () => {
            console.log('WebSocket连接已建立');
        };
        ws.onmessage = (event) => {
            console.log('收到消息:', event.data);
        };
        ws.onclose = () => {
            console.log('WebSocket连接已关闭');
        }
    }, [])
    const handleSendMessage = () => {
        if (message.trim() && sender.trim() && receiver.trim()) {
            const newMessage = {
                id: Date.now(),
                content: message,
                sender: sender,
                receiver: receiver,
                timestamp: new Date().toLocaleTimeString()
            };

            setMessageHistory(prev => [...prev, newMessage]);
            setMessage(''); // 清空输入框
            // 发送websocket请求
            ws.send(JSON.stringify(newMessage)); // 假设ws是WebSocket实例
        }
    };

    const handleKeyPress = (e) => {
        if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault();
            handleSendMessage();
        }
    };

    return (
        <div className="chat-page">
            <div className="chat-header">
                <h1>FlowChat - 聊天页面</h1>
            </div>

            <div className="chat-container">
                {/* 消息历史区域 */}
                <div className="message-history">
                    {messageHistory.length === 0 ? (
                        <div className="no-messages">
                            <p>还没有消息，开始聊天吧！</p>
                        </div>
                    ) : (
                        messageHistory.map((msg) => (
                            <div key={msg.id} className="message-item">
                                <div className="message-info">
                                    <span className="sender">发送人: {msg.sender}</span>
                                    <span className="receiver">接收人: {msg.receiver}</span>
                                    <span className="timestamp">{msg.timestamp}</span>
                                </div>
                                <div className="message-content">{msg.content}</div>
                            </div>
                        ))
                    )}
                </div>

                {/* 消息输入区域 */}
                <div className="message-input-section">
                    <div className="user-info">
                        <div className="input-group">
                            <label htmlFor="sender">消息发送人:</label>
                            <input
                                type="text"
                                id="sender"
                                value={sender}
                                onChange={(e) => setSender(e.target.value)}
                                placeholder="输入发送人姓名"
                                className="user-input"
                            />
                        </div>

                        <div className="input-group">
                            <label htmlFor="receiver">消息接收人:</label>
                            <input
                                type="text"
                                id="receiver"
                                value={receiver}
                                onChange={(e) => setReceiver(e.target.value)}
                                placeholder="输入接收人姓名"
                                className="user-input"
                            />
                        </div>
                    </div>

                    <div className="message-input-container">
                        <div className="input-group">
                            <label htmlFor="message">消息内容:</label>
                            <textarea
                                id="message"
                                value={message}
                                onChange={(e) => setMessage(e.target.value)}
                                onKeyPress={handleKeyPress}
                                placeholder="输入消息内容... (Enter发送)"
                                className="message-input"
                                rows="3"
                            />
                        </div>

                        <button
                            onClick={handleSendMessage}
                            className="send-button"
                            disabled={!message.trim() || !sender.trim() || !receiver.trim()}
                        >
                            发送消息
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default ChatPage;
