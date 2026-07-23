import { useState, useEffect } from 'react'
import { Card, Table, Input, Tag, Button } from 'antd'
import { pointsApi } from '../../api'

const typeMap = { EARN: { text: '获得', color: 'green' }, USE: { text: '使用', color: 'orange' }, FREEZE: { text: '冻结', color: 'blue' }, UNFREEZE: { text: '解冻', color: 'cyan' }, REFUND: { text: '退回', color: 'purple' } }

export default function PointsLog() {
  const [data, setData] = useState({ list: [], total: 0 })
  const [userId, setUserId] = useState('')
  const [params, setParams] = useState({ pageNum: 1, pageSize: 10, userId: '' })
  const [loading, setLoading] = useState(false)

  const load = async () => {
    if (!params.userId) return
    setLoading(true)
    try { const res = await pointsApi.logs(params); setData(res.data) } catch (e) {}
    setLoading(false)
  }

  return (
    <Card title="积分流水">
      <div className="search-bar">
        <Input.Search placeholder="输入用户ID" onSearch={v => setParams({ ...params, userId: v, pageNum: 1 })} style={{ width: 200 }} />
        <Button type="primary" onClick={load}>查询</Button>
      </div>
      <Table rowKey="id" loading={loading} dataSource={data.list}
        pagination={{ current: params.pageNum, pageSize: params.pageSize, total: data.total, onChange: (p, s) => setParams({ ...params, pageNum: p, pageSize: s }) }}
        columns={[
          { title: 'ID', dataIndex: 'id', width: 60 },
          { title: '用户ID', dataIndex: 'userId', width: 80 },
          { title: '变动类型', dataIndex: 'changeType', width: 80, render: v => { const t = typeMap[v]; return <Tag color={t.color}>{t.text}</Tag> } },
          { title: '积分', dataIndex: 'points', width: 80 },
          { title: '变动后余额', dataIndex: 'balanceAfter', width: 100 },
          { title: '来源', dataIndex: 'source', width: 80 },
          { title: '备注', dataIndex: 'remark' },
          { title: '时间', dataIndex: 'createTime', width: 170 }
        ]}
      />
    </Card>
  )
}
