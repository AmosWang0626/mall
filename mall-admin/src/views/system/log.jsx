import { useState, useEffect } from 'react'
import { Card, Table, Input, Button } from 'antd'
import { sysLogApi } from '../../api'

export default function SysLog() {
  const [data, setData] = useState({ list: [], total: 0 })
  const [params, setParams] = useState({ pageNum: 1, pageSize: 10, module: '' })
  const [loading, setLoading] = useState(false)

  const loadData = async () => {
    setLoading(true)
    try { const res = await sysLogApi.list(params); setData(res.data) } catch (e) {}
    setLoading(false)
  }
  useEffect(() => { loadData() }, [params])

  return (
    <Card title="操作日志">
      <div className="search-bar">
        <Input.Search placeholder="模块" onSearch={v => setParams({ ...params, module: v, pageNum: 1 })} style={{ width: 200 }} />
        <Button onClick={loadData}>刷新</Button>
      </div>
      <Table rowKey="id" loading={loading} dataSource={data.list}
        pagination={{ current: params.pageNum, pageSize: params.pageSize, total: data.total, onChange: (p, s) => setParams({ ...params, pageNum: p, pageSize: s }) }}
        columns={[
          { title: 'ID', dataIndex: 'id', width: 60 },
          { title: '操作人', dataIndex: 'adminName', width: 100 },
          { title: '模块', dataIndex: 'module', width: 100 },
          { title: '操作', dataIndex: 'operation', width: 150 },
          { title: '请求URL', dataIndex: 'requestUrl', ellipsis: true },
          { title: 'IP', dataIndex: 'ip', width: 120 },
          { title: '耗时(ms)', dataIndex: 'costTime', width: 90 },
          { title: '时间', dataIndex: 'createTime', width: 170 }
        ]}
      />
    </Card>
  )
}
