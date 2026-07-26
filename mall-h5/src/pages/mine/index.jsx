import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { Avatar, Toast, Dialog } from 'antd-mobile'
import { useAuthStore } from '../../store'
import { userApi, pointsApi } from '../../api'
import './index.css'

const ORDER_STATUS = { 0: '待付款', 1: '待发货', 2: '待收货', 3: '已完成', 4: '已取消', 5: '退款中', 6: '已退款' }

export default function Mine() {
  const navigate = useNavigate()
  const { userInfo, logout, fetchUserInfo } = useAuthStore()
  const [points, setPoints] = useState(null)

  useEffect(() => {
    fetchUserInfo()
    pointsApi.account().then(res => setPoints(res.data)).catch(() => {})
  }, [])

  const handleLogout = async () => {
    const ok = await Dialog.confirm({ content: '确认退出登录？' })
    if (!ok) return
    await logout()
    navigate('/login', { replace: true })
  }

  const menuGroups = [
    [
      { icon: '\u{1F381}', label: '我的优惠券', path: '/coupons' },
      { icon: '\u{1F4AF}', label: '我的积分', path: '/points' },
    ],
    [
      { icon: '\u{1F4CD}', label: '收货地址', path: '/address' },
      { icon: '\u{1F510}', label: '修改密码', path: '/change-password' },
    ]
  ]

  return (
    <div className="page">
      {/* Profile header */}
      <div className="mine-header">
        <div className="mine-profile">
          <Avatar style={{ '--size': '60px', '--border-radius': '50%', background: '#fff', color: '#ff4d4f', fontSize: 24, fontWeight: 600 }}>
            {userInfo.nickname?.[0] || userInfo.username?.[0] || 'U'}
          </Avatar>
          <div className="mine-info">
            <div className="mine-name">{userInfo.nickname || userInfo.username || '用户'}</div>
            <div className="mine-id">ID: {userInfo.userId || '-'}</div>
          </div>
        </div>
        {points && (
          <div className="mine-points">
            <span className="points-num">{points.balance || 0}</span>
            <span className="points-label">积分</span>
          </div>
        )}
      </div>

      {/* Order shortcuts */}
      <div className="section" style={{ padding: '16px 0' }}>
        <div className="mine-section-title" style={{ padding: '0 16px 12px' }}>我的订单</div>
        <div className="order-shortcuts">
          {[
            { status: 0, label: '待付款', icon: '\u{1F4B0}' },
            { status: 1, label: '待发货', icon: '\u{1F4E6}' },
            { status: 2, label: '待收货', icon: '\u{1F69A}' },
            { status: 3, label: '已完成', icon: '\u{2705}' },
          ].map(item => (
            <div key={item.status} className="order-shortcut" onClick={() => navigate(`/orders?status=${item.status}`)}>
              <span className="shortcut-icon">{item.icon}</span>
              <span className="shortcut-label">{item.label}</span>
            </div>
          ))}
        </div>
      </div>

      {/* Menu groups */}
      {menuGroups.map((group, gi) => (
        <div key={gi} className="section" style={{ padding: '4px 0' }}>
          {group.map((item, i) => (
            <div key={i} className="mine-menu-item" onClick={() => navigate(item.path)}>
              <span className="menu-icon">{item.icon}</span>
              <span className="menu-label">{item.label}</span>
              <span className="menu-arrow">{'>'}</span>
            </div>
          ))}
        </div>
      ))}

      {/* Logout */}
      <div style={{ padding: 20 }}>
        <button className="logout-btn" onClick={handleLogout}>退出登录</button>
      </div>
    </div>
  )
}
