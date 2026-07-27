import { useState, useEffect } from 'react'
import { Layout as AntLayout, Menu, Avatar, Dropdown, message } from 'antd'
import { useNavigate, useLocation, Outlet } from 'react-router-dom'
import {
  DashboardOutlined, ShoppingOutlined, FileTextOutlined, UserOutlined,
  GiftOutlined, TagOutlined, ThunderboltOutlined, SettingOutlined,
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
  { key: '/coupon', icon: <TagOutlined />, label: '优惠券管理', children: [
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
