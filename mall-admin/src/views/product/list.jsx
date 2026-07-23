import { useState, useEffect } from 'react'
import { Card, Table, Button, Input, Select, Space, Tag, Modal, message, Image } from 'antd'
import { PlusOutlined, ReloadOutlined } from '@ant-design/icons'
import { useNavigate } from 'react-router-dom'
import { productApi } from '../../api'

const statusMap = { 0: { text: '下架', color: 'red' }, 1: { text: '上架', color: 'green' }, 2: { text: '草稿', color: 'default' } }

export default function ProductList() {
  const navigate = useNavigate()
  const [data, setData] = useState([])
  const [loading, setLoading] = useState(false)
  const [params, setParams] = useState({ pageNum: 1, pageSize: 10, keyword: '', status: null })

  const loadData = async () => {
    setLoading(true)
    try {
      const res = await productApi.list(params)
      setData(res.data)
    } catch (e) {}
    setLoading(false)
  }

  useEffect(() => { loadData() }, [params])

  const handleStatusChange = async (id, status) => {
    await productApi.updateStatus(id, status)
    message.success(status === 1 ? '已上架' : '已下架')
    loadData()
  }

  const handleDelete = (id) => {
    Modal.confirm({
      title: '确认删除',
      content: '删除后不可恢复，是否继续？',
      onOk: async () => { await productApi.delete(id); message.success('删除成功'); loadData() }
    })
  }

  return (
    <Card>
      <div className="search-bar">
        <Input.Search placeholder="商品名称" allowClear onSearch={v => setParams({ ...params, keyword: v, pageNum: 1 })} style={{ width: 200 }} />
        <Select placeholder="状态" allowClear style={{ width: 120 }} onChange={v => setParams({ ...params, status: v, pageNum: 1 })} options={[{ value: 0, label: '下架' }, { value: 1, label: '上架' }, { value: 2, label: '草稿' }]} />
        <Space>
          <Button type="primary" icon={<PlusOutlined />} onClick={() => navigate('/product/edit')}>新增商品</Button>
          <Button icon={<ReloadOutlined />} onClick={loadData}>刷新</Button>
        </Space>
      </div>
      <Table
        rowKey="id" loading={loading}
        dataSource={data.list || []} pagination={{ current: params.pageNum, pageSize: params.pageSize, total: data.total || 0, onChange: (p, s) => setParams({ ...params, pageNum: p, pageSize: s }) }}
        columns={[
          { title: 'ID', dataIndex: 'id', width: 60 },
          { title: '图片', dataIndex: 'mainImage', width: 80, render: v => v ? <Image src={v} width={50} height={50} style={{ objectFit: 'cover' }} /> : '-' },
          { title: '商品名称', dataIndex: 'name', ellipsis: true },
          { title: '价格', dataIndex: 'price', width: 100, render: v => '¥' + v },
          { title: '库存', dataIndex: 'stock', width: 80 },
          { title: '销量', dataIndex: 'sales', width: 80 },
          { title: '状态', dataIndex: 'status', width: 80, render: v => { const s = statusMap[v]; return <Tag color={s.color}>{s.text}</Tag> } },
          { title: '操作', width: 200, render: (_, r) => (
            <Space>
              <Button size="small" onClick={() => navigate(`/product/edit/${r.id}`)}>编辑</Button>
              {r.status === 1 ? <Button size="small" danger onClick={() => handleStatusChange(r.id, 0)}>下架</Button> : <Button size="small" type="primary" onClick={() => handleStatusChange(r.id, 1)}>上架</Button>}
              <Button size="small" danger onClick={() => handleDelete(r.id)}>删除</Button>
            </Space>
          )}
        ]}
      />
    </Card>
  )
}
