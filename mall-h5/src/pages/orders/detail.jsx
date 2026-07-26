import { useState, useEffect } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { NavBar, Button, Toast, Dialog } from 'antd-mobile'
import { orderApi } from '../../api'
import './list.css'
import './detail.css'

const formatPrice = (v) => Number(v || 0).toFixed(2)
const ORDER_STATUS = { 0: '待付款', 1: '待发货', 2: '待收货', 3: '已完成', 4: '已取消', 5: '退款中', 6: '已退款' }

export default function OrderDetail() {
  const { id } = useParams()
  const navigate = useNavigate()
  const [order, setOrder] = useState(null)

  useEffect(() => {
    orderApi.detail(id).then(res => setOrder(res.data)).catch(() => {})
  }, [id])

  if (!order) return <div style={{ padding: 40, textAlign: 'center', color: '#999' }}>加载中...</div>

  const handleAction = async (action) => {
    try {
      if (action === 'pay') {
        await orderApi.pay(order.id, 1)
        Toast.show({ content: '支付成功', icon: 'success' })
      } else if (action === 'cancel') {
        const ok = await Dialog.confirm({ content: '确认取消该订单？' })
        if (!ok) return
        await orderApi.cancel(order.id)
        Toast.show({ content: '已取消', icon: 'success' })
      } else if (action === 'receive') {
        const ok = await Dialog.confirm({ content: '确认收货？' })
        if (!ok) return
        await orderApi.receive(order.id)
        Toast.show({ content: '已收货', icon: 'success' })
      }
      orderApi.detail(id).then(res => setOrder(res.data))
    } catch (e) { /* handled */ }
  }

  return (
    <div className="page">
      <NavBar onBack={() => navigate(-1)}>订单详情</NavBar>

      {/* Status banner */}
      <div className={`detail-status-banner status-${order.status}`}>
        <div className="detail-status-text">{ORDER_STATUS[order.status]}</div>
        {order.status === 0 && <div className="muted" style={{ color: 'rgba(255,255,255,0.8)' }}>请尽快完成支付</div>}
      </div>

      {/* Address */}
      {order.receiver && (
        <div className="section" style={{ padding: 16 }}>
          <div style={{ fontWeight: 600 }}>{order.receiver} {order.receiverPhone}</div>
          <div className="muted" style={{ marginTop: 4 }}>{order.receiverAddress}</div>
        </div>
      )}

      {/* Items */}
      <div className="section" style={{ padding: 0 }}>
        {order.items?.map(item => (
          <div key={item.id} className="order-item">
            <img src={item.productImage || 'data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 60 60"><rect fill="%23f5f5f5" width="60" height="60"/></svg>'} alt="" />
            <div style={{ flex: 1 }}>
              <div className="order-item-name">{item.productName}</div>
              {item.skuName && <div className="muted">{item.skuName}</div>}
              <div className="muted">x{item.quantity}</div>
            </div>
            <div className="price-small">&#165;{formatPrice(item.price)}</div>
          </div>
        ))}
      </div>

      {/* Amount info */}
      <div className="section" style={{ padding: 16 }}>
        <div className="checkout-row" style={{ display: 'flex', justifyContent: 'space-between', padding: '6px 0', borderBottom: '1px solid #f0f0f0' }}>
          <span>商品总额</span>
          <span>&#165;{formatPrice(order.totalAmount)}</span>
        </div>
        {Number(order.discountAmount) > 0 && (
          <div style={{ display: 'flex', justifyContent: 'space-between', padding: '6px 0', borderBottom: '1px solid #f0f0f0', fontSize: 14 }}>
            <span>优惠券抵扣</span>
            <span className="price-small">-&#165;{formatPrice(order.discountAmount)}</span>
          </div>
        )}
        {Number(order.pointsAmount) > 0 && (
          <div style={{ display: 'flex', justifyContent: 'space-between', padding: '6px 0', borderBottom: '1px solid #f0f0f0', fontSize: 14 }}>
            <span>积分抵扣</span>
            <span className="price-small">-&#165;{formatPrice(order.pointsAmount)}</span>
          </div>
        )}
        <div style={{ display: 'flex', justifyContent: 'space-between', padding: '8px 0', fontWeight: 600 }}>
          <span>实付金额</span>
          <span className="price" style={{ fontSize: 18 }}>&#165;{formatPrice(order.payAmount)}</span>
        </div>
      </div>

      {/* Order info */}
      <div className="section" style={{ padding: 16, fontSize: 13, color: '#999', lineHeight: 2 }}>
        <div>订单编号: {order.orderNo}</div>
        <div>下单时间: {order.createTime}</div>
        {order.payTime && <div>支付时间: {order.payTime}</div>}
        {order.shipTime && <div>发货时间: {order.shipTime}</div>}
        {order.shipCompany && <div>物流公司: {order.shipCompany}</div>}
        {order.shipNo && <div>物流单号: {order.shipNo}</div>}
        {order.remark && <div>备注: {order.remark}</div>}
      </div>

      {/* Actions */}
      <div style={{ height: 60 }} />
      {(order.status === 0 || order.status === 2) && (
        <div className="checkout-bottom">
          {order.status === 0 && <>
            <Button onClick={() => handleAction('cancel')}>取消订单</Button>
            <Button color="danger" onClick={() => handleAction('pay')}>立即付款</Button>
          </>}
          {order.status === 2 && <Button color="danger" onClick={() => handleAction('receive')}>确认收货</Button>}
        </div>
      )}
    </div>
  )
}
