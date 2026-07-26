import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { NavBar, Tabs, Button, Empty, Toast } from 'antd-mobile'
import { couponApi } from '../../api'
import './index.css'

const COUPON_TYPE = { 1: '满减券', 2: '折扣券', 3: '无门槛券' }
const COUPON_STATUS = { 0: '未使用', 1: '已使用', 2: '已过期' }

const formatPrice = (v) => Number(v || 0).toFixed(2)

export default function Coupons() {
  const navigate = useNavigate()
  const [activeTab, setActiveTab] = useState('0')
  const [list, setList] = useState([])
  const [available, setAvailable] = useState([])

  const loadMine = async (status) => {
    try {
      const res = await couponApi.mine(status)
      setList(res.data || [])
    } catch (e) { /* handled */ }
  }

  const loadAvailable = async () => {
    try {
      const res = await couponApi.available()
      setAvailable(res.data || [])
    } catch (e) { /* handled */ }
  }

  useEffect(() => {
    if (activeTab === 'available') {
      loadAvailable()
    } else {
      loadMine(parseInt(activeTab))
    }
  }, [activeTab])

  const handleReceive = async (couponId) => {
    try {
      await couponApi.receive(couponId)
      Toast.show({ content: '领取成功', icon: 'success' })
      loadAvailable()
    } catch (e) { /* handled */ }
  }

  const renderCoupon = (item, isAvailable = false) => {
    const cp = item.coupon || item
    const isUsed = !isAvailable && item.status !== 0
    return (
      <div key={item.id} className={`coupon-item ${isUsed ? 'used' : ''}`}>
        <div className="coupon-item-left">
          <div className="coupon-item-value">
            {cp.type === 2 ? (Number(cp.discount) * 10).toFixed(1) + '折' : '\u00A5' + formatPrice(cp.faceValue)}
          </div>
          <div className="coupon-item-type">{COUPON_TYPE[cp.type]}</div>
        </div>
        <div className="coupon-item-right">
          <div className="coupon-item-name">{cp.name}</div>
          <div className="muted" style={{ fontSize: 12, marginTop: 4 }}>
            {cp.type === 1 ? `满${formatPrice(cp.minSpend)}可用` : cp.type === 2 ? `满${formatPrice(cp.minSpend)}可用` : '无门槛'}
          </div>
          <div className="muted" style={{ fontSize: 11, marginTop: 2 }}>
            {isAvailable ? '有效期: ' + (cp.validEnd || '领取后生效') : '状态: ' + (COUPON_STATUS[item.status] || '')}
          </div>
          {isAvailable && (
            <Button size="mini" color="danger" onClick={() => handleReceive(cp.id)} style={{ marginTop: 6, borderRadius: 12 }}>
              立即领取
            </Button>
          )}
        </div>
      </div>
    )
  }

  return (
    <div className="page">
      <NavBar onBack={() => navigate(-1)}>我的优惠券</NavBar>
      <Tabs activeKey={activeTab} onChange={setActiveTab}>
        <Tabs.Tab key="0" title="未使用" />
        <Tabs.Tab key="1" title="已使用" />
        <Tabs.Tab key="2" title="已过期" />
        <Tabs.Tab key="available" title="可领取" />
      </Tabs>
      <div style={{ padding: 12 }}>
        {activeTab === 'available' ? (
          available.length === 0 ? <Empty description="暂无可领取的优惠券" style={{ padding: 40 }} /> :
            available.map(c => renderCoupon(c, true))
        ) : (
          list.length === 0 ? <Empty description="暂无优惠券" style={{ padding: 40 }} /> :
            list.map(c => renderCoupon(c, false))
        )}
      </div>
    </div>
  )
}
