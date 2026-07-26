import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { NavBar, Button, InfiniteScroll, Empty, Toast } from 'antd-mobile'
import { pointsApi } from '../../api'
import './index.css'

const SOURCE_MAP = {
  1: '签到', 2: '购物', 3: '退款', 4: '管理员调整',
  5: '优惠券兑换', 6: '订单取消返还'
}

export default function Points() {
  const navigate = useNavigate()
  const [account, setAccount] = useState(null)
  const [logs, setLogs] = useState([])
  const [page, setPage] = useState(1)
  const [hasMore, setHasMore] = useState(true)

  useEffect(() => {
    pointsApi.account().then(res => setAccount(res.data)).catch(() => {})
  }, [])

  const loadMore = async () => {
    const res = await pointsApi.logs({ pageNum: page, pageSize: 20 })
    const items = res.data?.list || []
    setLogs(prev => page === 1 ? items : [...prev, ...items])
    setHasMore(items.length >= 20)
    setPage(p => p + 1)
  }

  const handleSign = async () => {
    try {
      await pointsApi.sign()
      Toast.show({ content: '签到成功', icon: 'success' })
      const res = await pointsApi.account()
      setAccount(res.data)
      setLogs([])
      setPage(1)
      setHasMore(true)
    } catch (e) { /* handled */ }
  }

  return (
    <div className="page">
      <NavBar onBack={() => navigate(-1)}>我的积分</NavBar>
      <div className="points-header">
        <div className="points-balance">
          <span className="points-balance-num">{account?.balance || 0}</span>
          <span className="points-balance-label">可用积分</span>
        </div>
        <Button color="primary" size="small" onClick={handleSign} style={{ '--background-color': '#fff', '--text-color': '#ff4d4f', borderRadius: 16 }}>
          每日签到
        </Button>
      </div>
      <div className="section" style={{ minHeight: 200 }}>
        <div style={{ padding: '12px 16px', fontWeight: 600, borderBottom: '1px solid #f0f0f0' }}>积分明细</div>
        {logs.length === 0 ? (
          <Empty description="暂无积分记录" style={{ padding: 40 }} />
        ) : (
          <>
            {logs.map(log => (
              <div key={log.id} className="points-log-item">
                <div>
                  <div className="log-title">{SOURCE_MAP[log.source] || '其他'}</div>
                  <div className="muted">{log.createTime}</div>
                </div>
                <div className={log.changeAmount > 0 ? 'log-positive' : 'log-negative'}>
                  {log.changeAmount > 0 ? '+' : ''}{log.changeAmount}
                </div>
              </div>
            ))}
            <InfiniteScroll loadMore={loadMore} hasMore={hasMore} />
          </>
        )}
      </div>
    </div>
  )
}
