import { useState, useEffect } from 'react'
import { Card, Table, Input, Button } from 'antd'
import { pointsApi } from '../../api'

export default function PointsAccount() {
  const [data, setData] = useState({ list: [], total: 0 })
  const [userId, setUserId] = useState('')
  const [loading, setLoading] = useState(false)

  const load = async () => {
    if (!userId) return
    setLoading(true)
    try { const res = await pointsApi.account(userId); setData({ list: [res.data], total: 1 }) } catch (e) {}
    setLoading(false)
  }

  return (
    <Card title="积分账户">
      <div className="search-bar">
        <Input.Search placeholder="输入用户ID" onSearch={load} value={userId} onChange={e => setUserId(e.target.value)} style={{ width: 200 }} />
        <Button type="primary" onClick={load}>查询</Button>
      </div>
      <Table rowKey="userId" loading={loading} dataSource={data.list} pagination={false} columns={[
        { title: '用户ID', dataIndex: 'userId' },
        { title: '可用积分', dataIndex: 'balance' },
        { title: '冻结积分', dataIndex: 'frozen' },
        { title: '累计获得', dataIndex: 'totalEarned' },
        { title: '累计使用', dataIndex: 'totalUsed' }
      ]} />
    </Card>
  )
}
