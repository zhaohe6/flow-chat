import React, { useState, useEffect, createContext } from 'react';
import { BrowserRouter, Route, Routes } from 'react-router-dom';
import { ConfigProvider } from 'antd';
import zhCN from 'antd/locale/zh_CN';
import LoginPage from './pages/loginPage/LoginPage';
import ChatPage from './pages/chatPage/ChatPage';
import './App.css';
import DiyPage01 from './pages/DiyPage/DiyPage01';
import { Provider } from 'react-redux';
import store from './store';
export const MyContext = createContext("这是默认值！~~")

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
    <Provider store={store}>
      <MyContext.Provider value={{ des: "组件通信方式context", message: "hhhh nihao ya " }}>
        <ConfigProvider locale={zhCN}>
          <BrowserRouter>
            <Routes>
              <Route
                path="/"
                element={
                  <div className="App">
                    {isLoggedIn ? (
                      <ChatPage onLogout={handleLogout} />
                    ) : (
                      <LoginPage onLoginSuccess={handleLoginSuccess} />
                    )}
                  </div>
                }
              />
              <Route path="/test" element={<DiyPage01 />} />
            </Routes>
          </BrowserRouter>
        </ConfigProvider>
      </MyContext.Provider>
    </Provider>
  );
}

export default App;
