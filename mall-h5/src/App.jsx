import { useState, useEffect } from 'react'
import { BrowserRouter, Routes, Route, Navigate, useLocation } from 'react-router-dom'
import { TabBar } from 'antd-mobile'
import { AppOutline, ShopbagOutline, UserOutline } from 'antd-mobile-icons'
import { useAuthStore } from './store'
import { cartApi } from './api'
import Home from './pages/home'
import ProductDetail from './pages/product/detail'
import Cart from './pages/cart'
import Checkout from './pages/checkout'
import Mine from './pages/mine'
import Login from './pages/login'
import OrderList from './pages/orders/list'
import OrderDetail from './pages/orders/detail'
import Points from './pages/points'
import Coupons from './pages/coupons'
import Address from './pages/address'
import ChangePassword from './pages/password'

const tabs = [
  { key: '/', title: '首页', icon: <AppOutline /> },
  { key: '/cart', title: '购物车', icon: <ShopbagOutline /> },
  { key: '/mine', title: '我的', icon: <UserOutline /> }
]

function MainLayout() {
  const location = useLocation()
  const { token, cartCount, setCartCount } = useAuthStore()

  useEffect(() => {
    if (token) {
      cartApi.count().then(res => setCartCount(res.data || 0)).catch(() => {})
    }
  }, [token, location.pathname])

  const showTabBar = ['/', '/cart', '/mine'].includes(location.pathname)
  const activeKey = ['/', '/cart', '/mine'].includes(location.pathname) ? location.pathname : '/'

  return (
    <div style={{ paddingBottom: showTabBar ? '50px' : 0 }}>
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/product/:id" element={<ProductDetail />} />
        <Route path="/cart" element={<Cart />} />
        <Route path="/checkout" element={<Checkout />} />
        <Route path="/mine" element={<Mine />} />
        <Route path="/orders" element={<OrderList />} />
        <Route path="/order/:id" element={<OrderDetail />} />
        <Route path="/points" element={<Points />} />
        <Route path="/coupons" element={<Coupons />} />
        <Route path="/address" element={<Address />} />
        <Route path="/change-password" element={<ChangePassword />} />
        <Route path="*" element={<Navigate to="/" />} />
      </Routes>
      {showTabBar && (
        <div style={{ position: 'fixed', bottom: 0, left: 0, right: 0, background: '#fff', borderTop: '1px solid #f0f0f0', zIndex: 100 }}>
          <TabBar activeKey={activeKey} onChange={key => window.location.href = key} safeArea>
            {tabs.map(tab => (
              <TabBar.Item key={tab.key} icon={tab.icon} title={tab.title} badge={tab.key === '/cart' && cartCount > 0 ? cartCount : null} />
            ))}
          </TabBar>
        </div>
      )}
    </div>
  )
}

function ProtectedRoutes() {
  const token = useAuthStore(s => s.token)
  if (!token) return <Navigate to="/login" replace />
  return <MainLayout />
}

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/*" element={<ProtectedRoutes />} />
      </Routes>
    </BrowserRouter>
  )
}
