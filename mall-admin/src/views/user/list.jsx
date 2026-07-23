import { useState, useEffect } from 'react'
import { Card, Table, Button, Input, Select, Space, Tag, Switch, Modal, message } from 'antd'
import { userApi } from '../../api'

export default function UserList() {
  const [data, setData] = useState([])
  const [loading, setLoading] = useState(false)
  const [params, setParams] = useState({ pageNum: 1, pageSize: 10, keyword: '', status: null })

  const loadData = async () => {
    setLoading(true)
    try { const res = await userApi.list(params); setData(res.data) } catch (e) {}
    setLoading(false)
  }
  useEffect(() => { loadData() }, [params])

  const handleStatus = async (id, checked) => {
    await userApi.updateStatus(id, checked ? 1 : 0)
    message.success('操作成功')
    loadData()
  }

  const handleDelete = (id) => {
    Modal.confirm({ title: '确认删除', content: '删除后不可恢复', onOk: async () => { await userApi.delete(id); message.success('删除成功'); loadData() } })
  }

  return (
    <Card>
      <div className="search-bar">
        <Input.Search placeholder="用户名/昵称/手机号" allowClear onSearch={v => setParams({ ...params, keyword: v, pageNum: 1 })} style={{ width: 250 }} />
        <Select placeholder="状态" allowClear style={{ width: 120 }} onChange={v => setParams({ ...params, status: v, pageNum: 1 })} options={[{ value: 0, label: '禁用' }, { value: 1, label: '正常' }]} />
      </div>
      <Table rowKey="id" loading={loading} dataSource={data.list || []}
        pagination={{ current: params.pageNum, pageSize: params.pageSize, total: data.total || 0, onChange: (p, s) => setParams({ ...params, pageNum: p, pageSize: s }) }}
        columns={[
          { title: 'ID', dataIndex: 'id', width: 60 },
          { title: '用户名', dataIndex: 'username', width: 120 },
          { title: '昵称', dataIndex: 'nickname', width: 120 },
          { title: '手机号', dataIndex: 'phone', width: 130 },
          { title: '邮箱', dataIndex: 'email', width: 180 },
          { title: '注册时间', dataIndex: 'createTime', width: 170 },
          { title: '最后登录', dataIndex: 'lastLogin', width: 170 },
          { title: '状态', dataIndex: 'status', width: 80, render: (v, r) => <Switch checked={v === 1} onChange={(c) => handleStatus(r.id, c)} /> },
          { title: '操作', width: 80, render: (_, r) => <Button size="small" danger onClick={() => handleDelete(r.id)}>删除</Button> }
        ]}
      />
    </Card>
  )
}
