import React, { useEffect, useState, useRef } from 'react';
import { message, Button, Avatar, Input, List, Badge, Divider, Tooltip, Empty } from 'antd';
import { 
    UserOutlined, 
    LogoutOutlined, 
    SendOutlined,
    SmileOutlined,
    PictureOutlined,
    SearchOutlined
} from '@ant-design/icons';
import { v4 as uuidv4 } from 'uuid';
import './ChatPage.css';
import axios from 'axios';

const { TextArea } = Input;

const ChatPage = ({ onLogout }) => {
    const wsRef = useRef(null);
    const messagesEndRef = useRef(null);
    const [messageText, setMessageText] = useState('');
    const [currentUser, setCurrentUser] = useState('');
    const [selectedFriend, setSelectedFriend] = useState(null);
    const [messageHistory, setMessageHistory] = useState({});
    const [onlineUsers, setOnlineUsers] = useState([]);
    const [isConnected, setIsConnected] = useState(false);
    const [searchText, setSearchText] = useState('');
    // 模拟好友列表数据
    const [friendsList,setFriendsList] = useState([
        { id: '1', name: '张三', avatar: null, lastMessage: '你好', lastTime: '10:30', unreadCount: 2, isOnline: true },
        { id: '2', name: '李四', avatar: null, lastMessage: '晚上一起吃饭吗？', lastTime: '昨天', unreadCount: 0, isOnline: true },
        { id: '3', name: '王五', avatar: null, lastMessage: '项目进展怎么样了', lastTime: '前天', unreadCount: 1, isOnline: false },
        { id: '4', name: '赵六', avatar: null, lastMessage: '周末约球', lastTime: '3天前', unreadCount: 0, isOnline: false },
        { id: '5', name: '小明', avatar: null, lastMessage: '收到', lastTime: '1周前', unreadCount: 0, isOnline: true },
    ]);
    useEffect(()=>{
        async function fetchFriendsList() {
            try {
                const response = await axios.get('http://localhost:8080/friendListAndLastMsg',
                    { params: { username: localStorage.getItem('username') } }
                ); // 假设有一个API可以获取好友列表
                const resData = []
                response.data.forEach(element => {
                    resData.push({
                        id: element.id,
                        name: element.friendName,
                        avatar: element.avatar || null,
                        lastMessage: element.lastMessage || '',
                        lastTime: element.lastTime || '',
                        unreadCount: element.unreadCount || 0,
                        isOnline: element.online || false
                    });
                });
                setFriendsList(resData);
                console.log('好友列表加载成功:', resData);
            } catch (error) {
                console.error('获取好友列表失败:', error);
                message.error('加载好友列表失败，请稍后再试');
            }
        }
        fetchFriendsList();
    },[])
    // 滚动到消息底部
    const scrollToBottom = () => {
        messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
    };

    useEffect(() => {
        scrollToBottom();
    }, [messageHistory, selectedFriend]);

    useEffect(() => {
        // 从localStorage获取当前用户信息
        const username = localStorage.getItem('username');
        if (username) {
            setCurrentUser(username);
        } else {
            message.error('请先登录');
            onLogout();
            return;
        }

        // 创建websocket连接
        const token = localStorage.getItem('token') || 'default_token';
        const ws = new WebSocket(`ws://localhost:8080/websocket?token=${token}&username=${username}`);
        wsRef.current = ws;
        
        ws.onopen = () => {
            console.log('WebSocket连接已建立');
            setIsConnected(true);
            message.success('连接成功');
        };
        
        ws.onmessage = (event) => {
            console.log('收到消息:', event.data);
            try {
                const receivedMessage = JSON.parse(event.data);
                
                // 根据消息类型处理
                if (receivedMessage.type === 'USER_LIST') {
                    setOnlineUsers(receivedMessage.users || []);
                } else if (receivedMessage.type === 'CHAT_MESSAGE') {
                    // 添加消息到对应好友的聊天记录中
                    const friendId = receivedMessage.sender === username ? receivedMessage.receiver : receivedMessage.sender;
                    setMessageHistory(prev => ({
                        ...prev,
                        [friendId]: [...(prev[friendId] || []), receivedMessage]
                    }));
                }
            } catch (error) {
                console.error('解析消息失败:', error);
            }
        };
        
        ws.onclose = () => {
            console.log('WebSocket连接已关闭');
            setIsConnected(false);
            message.warning('连接已断开');
        };
        
        ws.onerror = (error) => {
            console.error('WebSocket错误:', error);
            setIsConnected(false);
            message.error('连接出现错误');
        };
        
        // 清理函数
        return () => {
            if (ws.readyState === WebSocket.OPEN) {
                ws.close();
            }
        };
    }, [onLogout]);

    const handleSendMessage = () => {
        if (!messageText.trim() || !selectedFriend) {
            message.warning('请选择好友并输入消息内容');
            return;
        }

        if (!isConnected) {
            message.error('连接已断开，请刷新页面重新连接');
            return;
        }

        const newMessage = {
            type: 'CHAT_MESSAGE',
            id: uuidv4(),
            content: messageText.trim(),
            sender: currentUser,
            receiver: selectedFriend.name,
            timestamp: new Date().toLocaleTimeString(),
            date: new Date().toLocaleDateString()
        };

        // 添加到本地消息历史
        setMessageHistory(prev => ({
            ...prev,
            [selectedFriend.name]: [...(prev[selectedFriend.name] || []), newMessage]
        }));

        // 发送到服务器
        if (wsRef.current && wsRef.current.readyState === WebSocket.OPEN) {
            wsRef.current.send(JSON.stringify(newMessage));
        }

        setMessageText('');
    };

    const handleKeyPress = (e) => {
        if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault();
            handleSendMessage();
        }
    };

    const handleFriendSelect = (friend) => {
        setSelectedFriend(friend);
        // 清除未读消息计数（实际项目中应该发送已读状态到服务器
        console.log(`已选择好友: ${friend.name}`);
        // 这里请求和选定好友的消息历史
    };

    const handleLogout = () => {
        if (wsRef.current && wsRef.current.readyState === WebSocket.OPEN) {
            wsRef.current.close();
        }
        
        localStorage.removeItem('username');
        localStorage.removeItem('token');
        localStorage.removeItem('loginTime');
        
        message.success('已退出登录');
        onLogout();
    };

    // 过滤好友列表
    const filteredFriends = friendsList.filter(friend =>
        friend.name.toLowerCase().includes(searchText.toLowerCase())
    );

    // 获取当前选中好友的消息历史
    const currentMessages = selectedFriend ? (messageHistory[selectedFriend.name] || []) : [];

    return (
        <div className="wechat-container">
            {/* 左侧边栏 */}
            <div className="sidebar">
                {/* 用户信息头部 */}
                <div className="sidebar-header">
                    <div className="user-info">
                        <Avatar size={40} icon={<UserOutlined />} className="user-avatar" />
                        <div className="user-details">
                            <div className="username">{currentUser}</div>
                            <div className="connection-status">
                                <Badge 
                                    status={isConnected ? 'success' : 'error'} 
                                    text={isConnected ? '在线' : '离线'}
                                />
                            </div>
                        </div>
                    </div>
                    <Tooltip title="退出登录">
                        <Button 
                            type="text" 
                            icon={<LogoutOutlined />} 
                            onClick={handleLogout}
                            className="logout-btn"
                        />
                    </Tooltip>
                </div>

                <Divider style={{ margin: '12px 0' }} />

                {/* 搜索框 */}
                <div className="search-section">
                    <Input
                        placeholder="搜索好友"
                        prefix={<SearchOutlined />}
                        value={searchText}
                        onChange={(e) => setSearchText(e.target.value)}
                        className="search-input"
                    />
                </div>

                {/* 好友列表 */}
                <div className="friends-list">
                    <List
                        dataSource={filteredFriends}
                        renderItem={(friend) => (
                            <List.Item
                                className={`friend-item ${selectedFriend?.id === friend.id ? 'selected' : ''}`}
                                onClick={() => handleFriendSelect(friend)}
                            >
                                <div className="friend-info">
                                    <Badge dot={friend.isOnline} offset={[-8, 8]}>
                                        <Avatar 
                                            size={44} 
                                            icon={<UserOutlined />}
                                            style={{ backgroundColor: friend.isOnline ? '#3b82f6' : '#94a3b8' }}
                                        />
                                    </Badge>
                                    <div className="friend-details">
                                        <div className="friend-name">{friend.name}</div>
                                        <div className="last-message">{friend.lastMessage}</div>
                                    </div>
                                    <div className="friend-meta">
                                        <div className="last-time">{friend.lastTime}</div>
                                        {friend.unreadCount > 0 && (
                                            <Badge count={friend.unreadCount} size="small" />
                                        )}
                                    </div>
                                </div>
                            </List.Item>
                        )}
                    />
                </div>
            </div>

            {/* 右侧聊天区域 */}
            <div className="chat-area">
                {selectedFriend ? (
                    <>
                        {/* 聊天头部 */}
                        <div className="chat-header">
                            <div className="chat-title">
                                <Avatar 
                                    size={36} 
                                    icon={<UserOutlined />}
                                    style={{ backgroundColor: selectedFriend.isOnline ? '#3b82f6' : '#94a3b8' }}
                                />
                                <div className="chat-info">
                                    <div className="friend-name">{selectedFriend.name}</div>
                                    <div className="online-status">
                                        {selectedFriend.isOnline ? '在线' : '离线'}
                                    </div>
                                </div>
                            </div>
                        </div>

                        {/* 消息列表 */}
                        <div className="messages-container">
                            <div className="messages-list">
                                {currentMessages.length === 0 ? (
                                    <div className="no-messages">
                                        <Empty 
                                            description="还没有消息，开始聊天吧！"
                                            image={Empty.PRESENTED_IMAGE_SIMPLE}
                                        />
                                    </div>
                                ) : (
                                    currentMessages.map((msg, index) => (
                                        <div key={msg.id || index} className={`message-bubble ${msg.sender === currentUser ? 'sent' : 'received'}`}>
                                            <div className="message-content">
                                                <div className="message-text">{msg.content}</div>
                                                <div className="message-time">{msg.timestamp}</div>
                                            </div>
                                            <Avatar 
                                                size={32} 
                                                icon={<UserOutlined />}
                                                className="message-avatar"
                                            />
                                        </div>
                                    ))
                                )}
                                <div ref={messagesEndRef} />
                            </div>
                        </div>

                        {/* 消息输入区 */}
                        <div className="message-input-area">
                            <div className="input-toolbar">
                                <Button type="text" icon={<SmileOutlined />} />
                                <Button type="text" icon={<PictureOutlined />} />
                            </div>
                            <div className="input-section">
                                <TextArea
                                    value={messageText}
                                    onChange={(e) => setMessageText(e.target.value)}
                                    onPressEnter={handleKeyPress}
                                    placeholder="输入消息内容..."
                                    autoSize={{ minRows: 1, maxRows: 4 }}
                                    className="message-input"
                                    disabled={!isConnected}
                                />
                                <Button 
                                    type="primary"
                                    icon={<SendOutlined />}
                                    onClick={handleSendMessage}
                                    disabled={!messageText.trim() || !isConnected}
                                    className="send-button"
                                >
                                    发送
                                </Button>
                            </div>
                        </div>
                    </>
                ) : (
                    <div className="no-friend-selected">
                        <Empty 
                            description="请选择一个好友开始聊天"
                            image={Empty.PRESENTED_IMAGE_SIMPLE}
                        />
                    </div>
                )}
            </div>
        </div>
    );
};

export default ChatPage;
