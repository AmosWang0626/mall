import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
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
import PrizePoolPage from './views/prize/pool'
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
          <Route path="prize/pool" element={<PrizePoolPage />} />
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
