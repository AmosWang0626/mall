import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { NavBar, Form, Input, Button, Toast, Dialog } from 'antd-mobile'
import { userApi } from '../../api'
import { useAuthStore } from '../../store'

export default function ChangePassword() {
  const navigate = useNavigate()
  const { logout } = useAuthStore()
  const [loading, setLoading] = useState(false)

  const handleSubmit = async (values) => {
    if (values.newPassword !== values.confirmPassword) {
      Toast.show({ icon: 'fail', content: '两次输入的新密码不一致' })
      return
    }
    setLoading(true)
    try {
      await userApi.changePassword({
        oldPassword: values.oldPassword,
        newPassword: values.newPassword
      })
      Toast.show({ icon: 'success', content: '密码修改成功' })
      // 密码已改，需要重新登录
      const ok = await Dialog.confirm({ content: '密码已修改，请重新登录' })
      await logout()
      navigate('/login', { replace: true })
    } catch {}
    setLoading(false)
  }

  return (
    <div className="page" style={{ background: '#f5f5f5', minHeight: '100vh' }}>
      <NavBar onBack={() => navigate('/mine')}>修改密码</NavBar>
      <Form
        layout="horizontal"
        onFinish={handleSubmit}
        footer={
          <Button block type="submit" color="danger" size="large" loading={loading}>
            确认修改
          </Button>
        }
      >
        <Form.Item name="oldPassword" label="原密码" rules={[{ required: true, message: '请输入原密码' }]}>
          <Input type="password" placeholder="请输入原密码" clearable />
        </Form.Item>
        <Form.Item name="newPassword" label="新密码" rules={[
          { required: true, message: '请输入新密码' },
          { min: 6, message: '密码至少6位' }
        ]}>
          <Input type="password" placeholder="请输入新密码(至少6位)" clearable />
        </Form.Item>
        <Form.Item name="confirmPassword" label="确认密码" rules={[{ required: true, message: '请确认新密码' }]}>
          <Input type="password" placeholder="请再次输入新密码" clearable />
        </Form.Item>
      </Form>
    </div>
  )
}
