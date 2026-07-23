import { useState, useEffect } from 'react'
import { Card, Table, Button, Input, Select, Space, Tag, Modal, message, Form, InputNumber } from 'antd'
import { ReloadOutlined } from '@ant-design/icons'
import { useNavigate } from 'react-router-dom'
import { orderApi } from '../../api'

const statusMap = { 0: { text: '待付款', color: 'orange' }, 1: { text: '待发货', color: 'blue' }, 2: { text: '待收货', color: 'cyan' }, 3: { text: '已完成', color: 'green' }, 4: { text: '已取消', color: 'default' }, 5: { text: '已退款', color: 'red' } }

export default function OrderList() {
  const navigate = useNavigate()
  const [data, setData] = useState([])
  const [loading, setLoading] = useState(false)
  const [params, setParams] = useState({ pageNum: 1, pageSize: 10, orderNo: '', status: null })
  const [shipModal, setShipModal] = useState({ open: false, id: null })
  const [shipForm] = Form.useForm()

  const loadData = async () => {
    setLoading(true)
    try { const res = await orderApi.list(params); setData(res.data) } catch (e) {}
    setLoading(false)
  }
  useEffect(() => { loadData() }, [params])

  const handleShip = async () => {
    const values = await shipForm.validateFields()
    await orderApi.ship(shipModal.id, values)
    message.success('发货成功')
    setShipModal({ open: false, id: null })
    shipForm.resetFields()
    loadData()
  }

  const handleRefund = (id) => {
    Modal.confirm({ title: '确认退款', content: '确认要退款此订单吗？', onOk: async () => { await orderApi.refund(id); message.success('退款成功'); loadData() } })
  }

  return (
    <Card>
      <div className="search-bar">
        <Input.Search placeholder="订单号" allowClear onSearch={v => setParams({ ...params, orderNo: v, pageNum: 1 })} style={{ width: 200 }} />
        <Select placeholder="状态" allowClear style={{ width: 120 }} onChange={v => setParams({ ...params, status: v, pageNum: 1 })} options={Object.entries(statusMap).map(([k, v]) => ({ value: parseInt(k), label: v.text }))} />
        <Button icon={<ReloadOutlined />} onClick={loadData}>刷新</Button>
      </div>
      <Table
        rowKey="id" loading={loading}
        dataSource={data.list || []} pagination={{ current: params.pageNum, pageSize: params.pageSize, total: data.total || 0, onChange: (p, s) => setParams({ ...params, pageNum: p, pageSize: s }) }}
        columns={[
          { title: '订单号', dataIndex: 'orderNo', width: 180 },
          { title: '用户ID', dataIndex: 'userId', width: 80 },
          { title: '商品总额', dataIndex: 'totalAmount', width: 100, render: v => '¥' + v },
          { title: '实付金额', dataIndex: 'payAmount', width: 100, render: v => '¥' + v },
          { title: '状态', dataIndex: 'status', width: 90, render: v => { const s = statusMap[v]; return <Tag color={s.color}>{s.text}</Tag> } },
          { title: '收货人', dataIndex: 'receiver', width: 80 },
          { title: '下单时间', dataIndex: 'createTime', width: 170 },
          { title: '操作', width: 220, render: (_, r) => (
            <Space>
              <Button size="small" onClick={() => navigate(`/order/${r.id}`)}>详情</Button>
              {r.status === 1 && <Button size="small" type="primary" onClick={() => { shipForm.resetFields(); setShipModal({ open: true, id: r.id }) }}>发货</Button>}
              {r.status === 3 && <Button size="small" danger onClick={() => handleRefund(r.id)}>退款</Button>}
            </Space>
          )}
        ]}
      />
      <Modal title="订单发货" open={shipModal.open} onOk={handleShip} onCancel={() => setShipModal({ open: false, id: null })}>
        <Form form={shipForm} layout="vertical">
          <Form.Item name="shipCompany" label="物流公司" rules={[{ required: true }]}><Input placeholder="如: 顺丰速运" /></Form.Item>
          <Form.Item name="shipNo" label="物流单号" rules={[{ required: true }]}><Input /></Form.Item>
        </Form>
      </Modal>
    </Card>
  )
}
