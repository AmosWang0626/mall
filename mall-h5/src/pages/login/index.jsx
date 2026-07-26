import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { Form, Input, Button, Toast, NavBar, Tabs } from 'antd-mobile'
import { authApi } from '../../api'
import { useAuthStore } from '../../store'
import './index.css'

export default function Login() {
  const navigate = useNavigate()
  const { setAuth } = useAuthStore()
  const [mode, setMode] = useState('login')
  const [loading, setLoading] = useState(false)

  const handleSubmit = async (values) => {
    setLoading(true)
    try {
      const api = mode === 'login' ? authApi.login : authApi.register
      const res = await api({ ...values, type: 'user' })
      const { token, username, nickname, userId } = res.data
      setAuth(token, { username, nickname, userId, type: 'user' })
      Toast.show({ content: mode === 'login' ? '登录成功' : '注册成功', icon: 'success' })
      navigate('/', { replace: true })
    } catch (e) { /* toast handled in interceptor */ }
    setLoading(false)
  }

  return (
    <div className="login-page">
      <NavBar back={null}>
        <span style={{ fontWeight: 600 }}>Mini Mall</span>
      </NavBar>
      <div className="login-header">
        <div className="logo">Mini Mall</div>
        <div className="slogan">一站式购物体验</div>
      </div>
      <div className="login-form-wrap">
        <div className="mode-switch">
          <Tabs activeKey={mode} onChange={setMode}>
            <Tabs.Tab key="login" title="登录" />
            <Tabs.Tab key="register" title="注册" />
          </Tabs>
        </div>
        <Form
          layout="horizontal"
          onFinish={handleSubmit}
          footer={
            <Button block type="submit" color="danger" size="large" loading={loading}>
              {mode === 'login' ? '登 录' : '注 册'}
            </Button>
          }
        >
          <Form.Item name="username" label="用户名" rules={[{ required: true, message: '请输入用户名' }]}>
            <Input placeholder="请输入用户名" clearable />
          </Form.Item>
          <Form.Item name="password" label="密码" rules={[{ required: true, message: '请输入密码' }]}>
            <Input type="password" placeholder="请输入密码" clearable />
          </Form.Item>
          {mode === 'register' && (
            <Form.Item
              name="nickname"
              label="昵称"
              rules={[{ required: true, message: '请输入昵称' }]}
            >
              <Input placeholder="请输入昵称" clearable />
            </Form.Item>
          )}
        </Form>
      </div>
    </div>
  )
}
