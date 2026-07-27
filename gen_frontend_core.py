#!/usr/bin/env python3
"""Generate React frontend project for mall admin panel."""
import os

BASE = "/Users/dorian/WorkBuddy/2026-07-21-23-31-52/mall-admin"
SRC = BASE + "/src"

def w(path, content):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, 'w') as f:
        f.write(content)
    print(f"  + {path}")

print("=== Creating React Frontend ===")

# package.json
w(f"{BASE}/package.json", '''{
  "name": "mall-admin",
  "version": "1.0.0",
  "private": true,
  "scripts": {
    "dev": "vite",
    "build": "vite build",
    "preview": "vite preview"
  },
  "dependencies": {
    "react": "^18.2.0",
    "react-dom": "^18.2.0",
    "react-router-dom": "^6.20.0",
    "antd": "^5.12.0",
    "@ant-design/icons": "^5.2.6",
    "axios": "^1.6.2",
    "dayjs": "^1.11.10",
    "zustand": "^4.4.7"
  },
  "devDependencies": {
    "@vitejs/plugin-react": "^4.2.0",
    "vite": "^5.0.0"
  }
}
''')

# vite.config.js
w(f"{BASE}/vite.config.js", '''import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
''')

# index.html
w(f"{BASE}/index.html", '''<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Mall 商城管理后台</title>
</head>
<body>
  <div id="root"></div>
  <script type="module" src="/src/main.jsx"></script>
</body>
</html>
''')

# main.jsx
w(f"{SRC}/main.jsx", '''import React from 'react'
import ReactDOM from 'react-dom/client'
import { ConfigProvider } from 'antd'
import zhCN from 'antd/locale/zh_CN'
import App from './App'
import 'dayjs/locale/zh-cn'
import './index.css'

ReactDOM.createRoot(document.getElementById('root')).render(
  <ConfigProvider locale={zhCN} theme={{ token: { colorPrimary: '#4096ff' } }}>
    <App />
  </ConfigProvider>
)
''')

# index.css
w(f"{SRC}/index.css", '''* { margin: 0; padding: 0; box-sizing: border-box; }
body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; }
#root { height: 100vh; }
.page-container { padding: 24px; background: #f0f2f5; min-height: calc(100vh - 64px); }
.table-card { margin-bottom: 16px; }
.search-bar { margin-bottom: 16px; display: flex; gap: 12px; flex-wrap: wrap; align-items: center; }
''')

# App.jsx
w(f"{SRC}/App.jsx", '''import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import Layout from './layout'
import Login from './views/login'
import Dashboard from './views/dashboard'
import ProductList from './views/product/list'
import ProductCategory from './views/product/category'
import ProductEdit from './views/product/edit'
import OrderList from './views/order/list'
import OrderDetail from './views/order/detail'
import UserList from './views/user/list'
import PointsAccount from './views/points/account'
import PointsLog from './views/points/log'
import CouponTemplate from './views/coupon/template'
import CouponRecord from './views/coupon/record'
import MarketingActivity from './views/marketing/activity'
import SysAdmin from './views/system/admin'
import SysRole from './views/system/role'
import SysPermission from './views/system/permission'
import SysConfig from './views/system/config'
import SysLog from './views/system/log'
import { useAuthStore } from './store'

function PrivateRoute({ children }) {
  const token = useAuthStore(s => s.token)
  return token ? children : <Navigate to="/login" />
}

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/" element={<PrivateRoute><Layout /></PrivateRoute>}>
          <Route index element={<Dashboard />} />
          <Route path="product/list" element={<ProductList />} />
          <Route path="product/category" element={<ProductCategory />} />
          <Route path="product/edit" element={<ProductEdit />} />
          <Route path="product/edit/:id" element={<ProductEdit />} />
          <Route path="order/list" element={<OrderList />} />
          <Route path="order/:id" element={<OrderDetail />} />
          <Route path="user/list" element={<UserList />} />
          <Route path="points/account" element={<PointsAccount />} />
          <Route path="points/log" element={<PointsLog />} />
          <Route path="coupon/template" element={<CouponTemplate />} />
          <Route path="coupon/record" element={<CouponRecord />} />
          <Route path="marketing/activity" element={<MarketingActivity />} />
          <Route path="system/admin" element={<SysAdmin />} />
          <Route path="system/role" element={<SysRole />} />
          <Route path="system/permission" element={<SysPermission />} />
          <Route path="system/config" element={<SysConfig />} />
          <Route path="system/log" element={<SysLog />} />
        </Route>
      </Routes>
    </BrowserRouter>
  )
}
''')

# store/index.js
w(f"{SRC}/store/index.js", '''import { create } from 'zustand'

export const useAuthStore = create((set) => ({
  token: localStorage.getItem('token') || '',
  userInfo: JSON.parse(localStorage.getItem('userInfo') || '{}'),
  permissions: JSON.parse(localStorage.getItem('permissions') || '[]'),
  setAuth: (token, userInfo) => {
    localStorage.setItem('token', token)
    localStorage.setItem('userInfo', JSON.stringify(userInfo))
    set({ token, userInfo })
  },
  setPermissions: (permissions) => {
    localStorage.setItem('permissions', JSON.stringify(permissions))
    set({ permissions })
  },
  logout: () => {
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    localStorage.removeItem('permissions')
    set({ token: '', userInfo: {}, permissions: [] })
  }
}))
''')

# api/request.js
w(f"{SRC}/api/request.js", '''import axios from 'axios'
import { message } from 'antd'
import { useAuthStore } from '../store'

const request = axios.create({
  baseURL: '/api',
  timeout: 15000
})

request.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) config.headers.Authorization = 'Bearer ' + token
  return config
})

request.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code === 200) return res
    message.error(res.message || '请求失败')
    return Promise.reject(res)
  },
  error => {
    if (error.response?.status === 401) {
      message.error('登录已过期，请重新登录')
      useAuthStore.getState().logout()
      window.location.href = '/login'
    } else {
      message.error(error.response?.data?.message || '网络错误')
    }
    return Promise.reject(error)
  }
)

export default request
''')

# api/index.js
w(f"{SRC}/api/index.js", '''import request from './request'

// Auth
export const authApi = {
  login: (data) => request.post('/auth/login', data),
  register: (data) => request.post('/auth/register', data),
  info: () => request.get('/auth/info'),
  logout: () => request.post('/auth/logout')
}

// Product
export const productApi = {
  list: (params) => request.get('/product/list', { params }),
  publicList: (params) => request.get('/public/product/list', { params }),
  detail: (id) => request.get(`/product/${id}`),
  save: (data) => request.post('/product', data),
  update: (data) => request.put('/product', data),
  updateStatus: (id, status) => request.put(`/product/${id}/status`, null, { params: { status } }),
  delete: (id) => request.delete(`/product/${id}`)
}

export const categoryApi = {
  tree: () => request.get('/product/category/tree'),
  save: (data) => request.post('/product/category', data),
  update: (data) => request.put('/product/category', data),
  delete: (id) => request.delete(`/product/category/${id}`)
}

export const skuApi = {
  listByProduct: (productId) => request.get(`/product/sku/list/${productId}`),
  save: (data) => request.post('/product/sku', data),
  delete: (id) => request.delete(`/product/sku/${id}`)
}

// Order
export const orderApi = {
  list: (params) => request.get('/order/list', { params }),
  detail: (id) => request.get(`/order/${id}`),
  ship: (id, data) => request.put(`/order/${id}/ship`, null, { params: data }),
  refund: (id) => request.put(`/order/${id}/refund`)
}

// User
export const userApi = {
  list: (params) => request.get('/user/list', { params }),
  detail: (id) => request.get(`/user/${id}`),
  update: (data) => request.put('/user', data),
  updateStatus: (id, status) => request.put(`/${id}/status`, null, { params: { status } }),
  delete: (id) => request.delete(`/user/${id}`)
}

// Points
export const pointsApi = {
  account: (userId) => request.get(`/points/account/${userId}`),
  logs: (params) => request.get(`/points/logs/${params.userId}`, { params })
}

// Coupon
export const couponApi = {
  list: (params) => request.get('/coupon/list', { params }),
  detail: (id) => request.get(`/coupon/${id}`),
  save: (data) => request.post('/coupon', data),
  delete: (id) => request.delete(`/coupon/${id}`),
  updateStatus: (id, status) => request.put(`/coupon/${id}/status`, null, { params: { status } })
}

// Marketing
export const marketingApi = {
  list: (params) => request.get('/marketing/list', { params }),
  detail: (id) => request.get(`/marketing/${id}`),
  save: (data) => request.post('/marketing', data),
  delete: (id) => request.delete(`/marketing/${id}`),
  updateStatus: (id, status) => request.put(`/marketing/${id}/status`, null, { params: { status } })
}

// System
export const sysAdminApi = {
  list: (params) => request.get('/system/admin/list', { params }),
  save: (data) => request.post('/system/admin', data),
  update: (data) => request.put('/system/admin', data),
  updateStatus: (id, status) => request.put(`/system/admin/${id}/status`, null, { params: { status } }),
  delete: (id) => request.delete(`/system/admin/${id}`),
  assignRoles: (id, roleIds) => request.put(`/system/admin/${id}/roles`, { roleIds }),
  getRoleIds: (id) => request.get(`/system/admin/${id}/roles`),
  resetPassword: (id, password) => request.put(`/system/admin/${id}/password`, { password })
}

export const sysRoleApi = {
  list: (params) => request.get('/system/role/list', { params }),
  all: () => request.get('/system/role/all'),
  save: (data) => request.post('/system/role', data),
  update: (data) => request.put('/system/role', data),
  delete: (id) => request.delete(`/system/role/${id}`),
  assignPermissions: (id, permissionIds) => request.put(`/system/role/${id}/permissions`, { permissionIds }),
  getPermissionIds: (id) => request.get(`/system/role/${id}/permissions`)
}

export const sysPermissionApi = {
  tree: () => request.get('/system/permission/tree'),
  save: (data) => request.post('/system/permission', data),
  update: (data) => request.put('/system/permission', data),
  delete: (id) => request.delete(`/system/permission/${id}`)
}

export const sysConfigApi = {
  list: (params) => request.get('/system/config/list', { params }),
  all: () => request.get('/system/config/all'),
  save: (data) => request.post('/system/config', data),
  delete: (id) => request.delete(`/system/config/${id}`)
}

export const sysLogApi = {
  list: (params) => request.get('/system/log/list', { params }),
  delete: (id) => request.delete(`/system/log/${id}`)
}
''')

# Layout
w(f"{SRC}/layout/index.jsx", '''import { useState, useEffect } from 'react'
import { Layout as AntLayout, Menu, Avatar, Dropdown, message } from 'antd'
import { useNavigate, useLocation, Outlet } from 'react-router-dom'
import {
  DashboardOutlined, ShoppingOutlined, FileTextOutlined, UserOutlined,
  GiftOutlined, TicketOutlined, ThunderboltOutlined, SettingOutlined,
  LogoutOutlined, MenuFoldOutlined, MenuUnfoldOutlined
} from '@ant-design/icons'
import { useAuthStore } from '../store'
import { authApi } from '../api'

const { Header, Sider, Content } = AntLayout

const menuItems = [
  { key: '/', icon: <DashboardOutlined />, label: '仪表盘' },
  { key: '/product', icon: <ShoppingOutlined />, label: '商品管理', children: [
    { key: '/product/list', label: '商品列表' },
    { key: '/product/category', label: '分类管理' }
  ]},
  { key: '/order', icon: <FileTextOutlined />, label: '订单管理', children: [
    { key: '/order/list', label: '订单列表' }
  ]},
  { key: '/user', icon: <UserOutlined />, label: '用户管理', children: [
    { key: '/user/list', label: '用户列表' }
  ]},
  { key: '/points', icon: <GiftOutlined />, label: '积分管理', children: [
    { key: '/points/account', label: '积分账户' },
    { key: '/points/log', label: '积分流水' }
  ]},
  { key: '/coupon', icon: <TicketOutlined />, label: '优惠券管理', children: [
    { key: '/coupon/template', label: '优惠券模板' },
    { key: '/coupon/record', label: '领取记录' }
  ]},
  { key: '/marketing/activity', icon: <ThunderboltOutlined />, label: '营销活动' },
  { key: '/system', icon: <SettingOutlined />, label: '系统管理', children: [
    { key: '/system/admin', label: '管理员管理' },
    { key: '/system/role', label: '角色管理' },
    { key: '/system/permission', label: '权限管理' },
    { key: '/system/config', label: '系统配置' },
    { key: '/system/log', label: '操作日志' }
  ]}
]

export default function Layout() {
  const [collapsed, setCollapsed] = useState(false)
  const navigate = useNavigate()
  const location = useLocation()
  const { userInfo, logout } = useAuthStore()
  const [permissions, setPermissions] = useState([])

  useEffect(() => {
    authApi.info().then(res => {
      if (res.data?.permissions) {
        setPermissions(res.data.permissions)
        useAuthStore.getState().setPermissions(res.data.permissions)
      }
    })
  }, [])

  const path = '/' + location.pathname.split('/').slice(0, 3).join('/').replace('/', '')
  const openKeys = ['/' + location.pathname.split('/')[1]]

  const handleLogout = () => {
    authApi.logout().finally(() => {
      logout()
      navigate('/login')
    })
  }

  return (
    <AntLayout style={{ height: '100vh' }}>
      <Sider trigger={null} collapsible collapsed={collapsed} theme="dark">
        <div style={{ height: 64, display: 'flex', alignItems: 'center', justifyContent: 'center', color: '#fff', fontSize: 18, fontWeight: 'bold' }}>
          {collapsed ? 'M' : 'Mall'}
        </div>
        <Menu
          theme="dark"
          mode="inline"
          selectedKeys={[location.pathname]}
          defaultOpenKeys={openKeys}
          items={menuItems}
          onClick={({ key }) => navigate(key)}
        />
      </Sider>
      <AntLayout>
        <Header style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', background: '#fff', padding: '0 24px' }}>
          <div style={{ cursor: 'pointer', fontSize: 18 }} onClick={() => setCollapsed(!collapsed)}>
            {collapsed ? <MenuUnfoldOutlined /> : <MenuFoldOutlined />}
          </div>
          <Dropdown menu={{ items: [
            { key: 'logout', icon: <LogoutOutlined />, label: '退出登录', onClick: handleLogout }
          ]}}>
            <div style={{ cursor: 'pointer', display: 'flex', alignItems: 'center', gap: 8 }}>
              <Avatar size="small" style={{ backgroundColor: '#4096ff' }}>{userInfo.nickname?.[0] || 'A'}</Avatar>
              <span>{userInfo.nickname || userInfo.username || 'Admin'}</span>
            </div>
          </Dropdown>
        </Header>
        <Content className="page-container">
          <Outlet />
        </Content>
      </AntLayout>
    </AntLayout>
  )
}
''')

# Login
w(f"{SRC}/views/login/index.jsx", '''import { useState } from 'react'
import { Card, Form, Input, Button, message, Typography } from 'antd'
import { UserOutlined, LockOutlined } from '@ant-design/icons'
import { useNavigate } from 'react-router-dom'
import { authApi } from '../../api'
import { useAuthStore } from '../../store'

export default function Login() {
  const [loading, setLoading] = useState(false)
  const navigate = useNavigate()
  const { setAuth } = useAuthStore()

  const onFinish = async (values) => {
    setLoading(true)
    try {
      const res = await authApi.login({ ...values, type: 'admin' })
      setAuth(res.data.token, { username: res.data.username, nickname: res.data.nickname, userId: res.data.userId })
      message.success('登录成功')
      navigate('/')
    } catch (e) {
      // error handled by interceptor
    }
    setLoading(false)
  }

  return (
    <div style={{ height: '100vh', display: 'flex', alignItems: 'center', justifyContent: 'center', background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)' }}>
      <Card style={{ width: 400, boxShadow: '0 4px 12px rgba(0,0,0,0.15)' }}>
        <Typography.Title level={3} style={{ textAlign: 'center', marginBottom: 32 }}>Mall 管理后台</Typography.Title>
        <Form onFinish={onFinish} initialValues={{ username: 'admin', password: 'admin123' }}>
          <Form.Item name="username" rules={[{ required: true, message: '请输入用户名' }]}>
            <Input prefix={<UserOutlined />} placeholder="用户名" size="large" />
          </Form.Item>
          <Form.Item name="password" rules={[{ required: true, message: '请输入密码' }]}>
            <Input.Password prefix={<LockOutlined />} placeholder="密码" size="large" />
          </Form.Item>
          <Form.Item>
            <Button type="primary" htmlType="submit" size="large" block loading={loading}>登录</Button>
          </Form.Item>
        </Form>
      </Card>
    </div>
  )
}
''')

# Dashboard
w(f"{SRC}/views/dashboard/index.jsx", '''import { Card, Col, Row, Statistic, Table } from 'antd'
import { ShoppingOutlined, ShoppingCartOutlined, DollarOutlined, UserOutlined } from '@ant-design/icons'

export default function Dashboard() {
  const recentOrders = [
    { key: 1, orderNo: '20240701001', amount: '299.00', status: '已完成' },
    { key: 2, orderNo: '20240701002', amount: '1299.00', status: '待发货' },
    { key: 3, orderNo: '20240701003', amount: '59.00', status: '待付款' },
  ]

  return (
    <div>
      <Row gutter={16} style={{ marginBottom: 24 }}>
        <Col span={6}>
          <Card><Statistic title="商品总数" value={128} prefix={<ShoppingOutlined />} /></Card>
        </Col>
        <Col span={6}>
          <Card><Statistic title="今日订单" value={36} prefix={<ShoppingCartOutlined />} valueStyle={{ color: '#3f8600' }} /></Card>
        </Col>
        <Col span={6}>
          <Card><Statistic title="今日销售额" value={5289} prefix={<DollarOutlined />} precision={2} valueStyle={{ color: '#cf1322' }} /></Card>
        </Col>
        <Col span={6}>
          <Card><Statistic title="注册用户" value={1024} prefix={<UserOutlined />} /></Card>
        </Col>
      </Row>
      <Card title="最近订单">
        <Table
          dataSource={recentOrders}
          pagination={false}
          columns={[
            { title: '订单号', dataIndex: 'orderNo' },
            { title: '金额', dataIndex: 'amount' },
            { title: '状态', dataIndex: 'status' }
          ]}
        />
      </Card>
    </div>
  )
}
''')

print("=== Core frontend files generated! ===")
