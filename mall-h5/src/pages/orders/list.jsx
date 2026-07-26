import { useState, useEffect } from 'react'
import { useNavigate, useSearchParams } from 'react-router-dom'
import { NavBar, Tabs, InfiniteScroll, Empty, Button, Toast, Dialog } from 'antd-mobile'
import { orderApi } from '../../api'
import './index.css'

const formatPrice = (v) => Number(v || 0).toFixed(2)
const ORDER_STATUS = { 0: '待付款', 1: '待发货', 2: '待收货', 3: '已完成', 4: '已取消', 5: '退款中', 6: '已退款' }

const TABS = [
  { key: '', label: '全部' },
  { key: '0', label: '待付款' },
  { key: '1', label: '待发货' },
  { key: '2', label: '待收货' },
  { key: '3', label: '已完成' },
]

export default function OrderList() {
  const navigate = useNavigate()
  const [searchParams] = useSearchParams()
  const initialStatus = searchParams.get('status') || ''
  const [activeTab, setActiveTab] = useState(initialStatus)
  const [list, setList] = useState([])
  const [page, setPage] = useState(1)
  const [hasMore, setHasMore] = useState(true)
  const [loading, setLoading] = useState(false)

  const loadMore = async () => {
    if (loading) return
    setLoading(true)
    try {
      const params = { pageNum: page, pageSize: 10 }
      if (activeTab !== '') params.status = parseInt(activeTab)
      const res = await orderApi.myList(params)
      const items = res.data?.list || []
      setList(prev => page === 1 ? items : [...prev, ...items])
      setHasMore(items.length >= 10)
      setPage(p => p + 1)
    } catch (e) { /* handled */ }
    setLoading(false)
  }

  const handleTabChange = (key) => {
    setActiveTab(key)
    setList([])
    setPage(1)
    setHasMore(true)
  }

  useEffect(() => {
    setList([])
    setPage(1)
    setHasMore(true)
  }, [activeTab])

  const handleAction = async (order, action) => {
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
      setList([])
      setPage(1)
      setHasMore(true)
    } catch (e) { /* handled or cancelled */ }
  }

  return (
    <div className="page">
      <NavBar onBack={() => navigate(-1)}>我的订单</NavBar>
      <Tabs activeKey={activeTab} onChange={handleTabChange}>
        {TABS.map(t => <Tabs.Tab key={t.key} title={t.label} />)}
      </Tabs>
      {list.length === 0 && !loading ? (
        <Empty description="暂无订单" style={{ padding: 60 }} />
      ) : (
        <>
          {list.map(order => (
            <div key={order.id} className="order-card" onClick={() => navigate(`/order/${order.id}`)}>
              <div className="order-card-header">
                <span className="muted">订单号: {order.orderNo}</span>
                <span className={`order-status status-${order.status}`}>{ORDER_STATUS[order.status]}</span>
              </div>
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
              <div className="order-card-footer">
                <span>共{order.items?.length || 0}件 实付: </span>
                <span className="price">&#165;{formatPrice(order.payAmount)}</span>
                <div className="order-actions" onClick={(e) => e.stopPropagation()}>
                  {order.status === 0 && <>
                    <Button size="mini" onClick={() => handleAction(order, 'cancel')}>取消</Button>
                    <Button size="mini" color="danger" onClick={() => handleAction(order, 'pay')}>付款</Button>
                  </>}
                  {order.status === 2 && <Button size="mini" color="danger" onClick={() => handleAction(order, 'receive')}>确认收货</Button>}
                </div>
              </div>
            </div>
          ))}
          <InfiniteScroll loadMore={loadMore} hasMore={hasMore} />
        </>
      )}
    </div>
  )
}
