import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { Form, Input, Button, Toast, NavBar, Tabs } from 'antd-mobile'
import { authApi } from '../../api'
import { useAuthStore } from '../../store'
import './index.css'

// 默认头像选项（DiceBear avataaars 风格）
const AVATAR_OPTIONS = [
  'https://api.dicebear.com/9.x/avataaars/svg?seed=Felix',
  'https://api.dicebear.com/9.x/avataaars/svg?seed=Aneka',
  'https://api.dicebear.com/9.x/avataaars/svg?seed=Salem',
  'https://api.dicebear.com/9.x/avataaars/svg?seed=Sassy',
  'https://api.dicebear.com/9.x/avataaars/svg?seed=Bubba',
  'https://api.dicebear.com/9.x/avataaars/svg?seed=Loki',
  'https://api.dicebear.com/9.x/avataaars/svg?seed=Midnight',
  'https://api.dicebear.com/9.x/avataaars/svg?seed=Cleo',
]

export default function Login() {
  const navigate = useNavigate()
  const { setAuth } = useAuthStore()
  const [mode, setMode] = useState('login')
  const [loading, setLoading] = useState(false)
  const [avatar, setAvatar] = useState(AVATAR_OPTIONS[0])

  const handleSubmit = async (values) => {
    setLoading(true)
    try {
      const api = mode === 'login' ? authApi.login : authApi.register
      const payload = { ...values, type: 'user' }
      if (mode === 'register') {
        payload.avatar = avatar
      }
      const res = await api(payload)
      const { token, username, nickname, userId } = res.data
      setAuth(token, { username, nickname, userId, avatar: res.data.avatar, type: 'user' })
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

          {mode === 'register' && (
            <div className="avatar-section">
              <div className="avatar-section-title">选择头像</div>
              <div className="avatar-grid">
                {AVATAR_OPTIONS.map((url, index) => (
                  <div
                    key={index}
                    className={`avatar-option${url === avatar ? ' active' : ''}`}
                    onClick={() => setAvatar(url)}
                  >
                    <img src={url} alt={`头像 ${index + 1}`} />
                  </div>
                ))}
              </div>
            </div>
          )}
        </Form>
      </div>
    </div>
  )
}
