import React, { useState, useEffect } from 'react';
import { Form, Input, Button, Card, Typography, message, Space } from 'antd';
import { UserOutlined, LockOutlined, LoginOutlined } from '@ant-design/icons';
import axios from 'axios';
import './LoginPage.css';

const { Title, Text } = Typography;

const LoginPage = ({ onLoginSuccess }) => {
    const [form] = Form.useForm();
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        // 检查是否已经登录
        const username = localStorage.getItem('username');
        if (username) {
            message.success(`欢迎回来，${username}！`);
            onLoginSuccess();
        }
    }, [onLoginSuccess]);

    const handleLogin = async (values) => {
        setLoading(true);
        try {
            // 模拟登录请求
            // 在实际项目中，这里应该是真实的API调用
            // const response = await simulateLogin(values);
            const response = await realApiLogin(values);
            if (response.code === 200) {
                // 登录成功，存储用户信息到localStorage
                localStorage.setItem('username', values.username);
                localStorage.setItem('token', response.token);
                localStorage.setItem('loginTime', new Date().toISOString());
                
                message.success('登录成功！');
                onLoginSuccess();
            } else {
                message.error(response.message || '登录失败，请检查用户名和密码');
            }
        } catch (error) {
            console.error('登录错误:', error);
            message.error('网络错误，请稍后重试');
        } finally {
            setLoading(false);
        }
    };

    // 模拟登录API调用
    const simulateLogin = async (credentials) => {
        // 模拟网络延迟
        await new Promise(resolve => setTimeout(resolve, 1000));
        
        // 简单的模拟验证逻辑
        const validUsers = [
            { username: 'zhao', password: 'zhao123' }
        ];
        
        const user = validUsers.find(
            u => u.username === credentials.username && u.password === credentials.password
        );
        
        if (user) {
            return {
                success: true,
                token: 'mock_token_' + Date.now(),
                user: {
                    username: user.username,
                    id: Date.now()
                }
            };
        } else {
            return {
                success: false,
                message: '用户名或密码错误'
            };
        }
    };

    // 真实的API调用示例（注释掉的代码）
    const realApiLogin = async (credentials) => {
        try {
            const params = new URLSearchParams();
            params.append('username', credentials.username);
            params.append('password', credentials.password);
            const response = await axios.post(
                'http://localhost:8080/login',
                params,
                {
                    withCredentials: true,
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded'
                    }
                }
            );
            console.log('登录响应:', response);
            return response.data;
        } catch (error) {
            throw error;
        }
    };

    const handleLogout = () => {
        localStorage.removeItem('username');
        localStorage.removeItem('token');
        localStorage.removeItem('loginTime');
        message.success('已退出登录');
        window.location.reload();
    };

    // 如果已经登录，显示欢迎页面
    const username = localStorage.getItem('username');
    if (username) {
        return (
            <div className="login-page">
                <div className="login-container">
                    <Card className="welcome-card">
                        <div className="welcome-content">
                            <Title level={2}>欢迎回来！</Title>
                            <Text className="welcome-text">
                                你好，<strong>{username}</strong>！你已经成功登录。
                            </Text>
                            <div className="welcome-actions">
                                <Button 
                                    type="primary" 
                                    size="large"
                                    onClick={onLoginSuccess}
                                    style={{ marginRight: 16 }}
                                >
                                    进入聊天室
                                </Button>
                                <Button 
                                    size="large"
                                    onClick={handleLogout}
                                >
                                    退出登录
                                </Button>
                            </div>
                        </div>
                    </Card>
                </div>
            </div>
        );
    }

    return (
        <div className="login-page">
            <div className="login-container">
                <Card className="login-card" bordered={false}>
                    <div className="login-header">
                        <Title level={2} className="login-title">
                            <LoginOutlined className="login-icon" />
                            FlowChat 登录
                        </Title>
                        <Text type="secondary" className="login-subtitle">
                            请输入您的用户名和密码登录
                        </Text>
                    </div>

                    <Form
                        form={form}
                        name="login"
                        onFinish={handleLogin}
                        autoComplete="off"
                        size="large"
                        className="login-form"
                    >
                        <Form.Item
                            name="username"
                            rules={[
                                {
                                    required: true,
                                    message: '请输入用户名！',
                                },
                                {
                                    min: 2,
                                    message: '用户名至少2个字符！',
                                }
                            ]}
                        >
                            <Input
                                prefix={<UserOutlined />}
                                placeholder="用户名"
                                allowClear
                            />
                        </Form.Item>

                        <Form.Item
                            name="password"
                            rules={[
                                {
                                    required: true,
                                    message: '请输入密码！',
                                },
                                {
                                    min: 6,
                                    message: '密码至少6个字符！',
                                }
                            ]}
                        >
                            <Input.Password
                                prefix={<LockOutlined />}
                                placeholder="密码"
                                allowClear
                            />
                        </Form.Item>

                        <Form.Item>
                            <Button
                                type="primary"
                                htmlType="submit"
                                className="login-button"
                                loading={loading}
                                block
                            >
                                {loading ? '登录中...' : '登录'}
                            </Button>

                        </Form.Item>
                    </Form>

                    <div className="login-demo">
                        <Text type="secondary" style={{ fontSize: '12px' }}>
                            演示账号：admin/123456 | user1/password | test/test123 | zhao/123456
                        </Text>
                    </div>
                </Card>
            </div>
        </div>
    );
};

export default LoginPage;
