import React, { useState, useEffect } from 'react';
import { ConfigProvider } from 'antd';
import zhCN from 'antd/locale/zh_CN';
import LoginPage from './pages/loginPage/LoginPage';
import ChatPage from './pages/chatPage/ChatPage';
import './App.css';

function App() {
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // 检查用户是否已经登录
    const username = localStorage.getItem('username');
    const token = localStorage.getItem('token');
    
    if (username && token) {
      setIsLoggedIn(true);
    }
    
    setLoading(false);
  }, []);

  const handleLoginSuccess = () => {
    setIsLoggedIn(true);
  };

  const handleLogout = () => {
    setIsLoggedIn(false);
  };

  if (loading) {
    return (
      <div className="loading-container">
        <div className="loading-spinner">加载中...</div>
      </div>
    );
  }

  return (
    <ConfigProvider locale={zhCN}>
      <div className="App">
        {isLoggedIn ? (
          <ChatPage onLogout={handleLogout} />
        ) : (
          <LoginPage onLoginSuccess={handleLoginSuccess} />
        )}
      </div>
    </ConfigProvider>
  );
}

export default App;
