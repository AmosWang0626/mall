import { useState, useEffect } from 'react'
import { Card, Table, Button, Input, Space, Tag, Modal, Form, InputNumber, DatePicker, Select, message } from 'antd'
import { PlusOutlined, ReloadOutlined } from '@ant-design/icons'
import { couponApi } from '../../api'

const typeMap = { 1: '满减券', 2: '折扣券', 3: '无门槛券' }

export default function CouponTemplate() {
  const [data, setData] = useState({ list: [], total: 0 })
  const [params, setParams] = useState({ pageNum: 1, pageSize: 10, name: '' })
  const [loading, setLoading] = useState(false)
  const [modal, setModal] = useState({ open: false, data: {} })
  const [form] = Form.useForm()

  const loadData = async () => {
    setLoading(true)
    try { const res = await couponApi.list(params); setData(res.data) } catch (e) {}
    setLoading(false)
  }
  useEffect(() => { loadData() }, [params])

  const handleSave = async () => {
    const values = await form.validateFields()
    if (values.validType === 1) { values.validStart = values.validRange?.[0]?.format('YYYY-MM-DD HH:mm:ss'); values.validEnd = values.validRange?.[1]?.format('YYYY-MM-DD HH:mm:ss') }
    delete values.validRange
    if (modal.data.id) values.id = modal.data.id
    await couponApi.save(values)
    message.success('保存成功')
    setModal({ open: false, data: {} })
    loadData()
  }

  return (
    <Card>
      <div className="search-bar">
        <Input.Search placeholder="优惠券名称" onSearch={v => setParams({ ...params, name: v, pageNum: 1 })} style={{ width: 200 }} />
        <Button type="primary" icon={<PlusOutlined />} onClick={() => { form.resetFields(); form.setFieldsValue({ type: 1, totalCount: -1, perLimit: 1, validType: 1, status: 1, minSpend: 0 }); setModal({ open: true, data: {} }) }}>新增优惠券</Button>
        <Button icon={<ReloadOutlined />} onClick={loadData}>刷新</Button>
      </div>
      <Table rowKey="id" loading={loading} dataSource={data.list}
        pagination={{ current: params.pageNum, pageSize: params.pageSize, total: data.total, onChange: (p, s) => setParams({ ...params, pageNum: p, pageSize: s }) }}
        columns={[
          { title: 'ID', dataIndex: 'id', width: 60 },
          { title: '名称', dataIndex: 'name' },
          { title: '类型', dataIndex: 'type', width: 90, render: v => typeMap[v] },
          { title: '面值/折扣', width: 100, render: (_, r) => r.type === 2 ? (r.discount * 10) + '折' : '¥' + r.faceValue },
          { title: '门槛', dataIndex: 'minSpend', width: 80, render: v => v > 0 ? '满¥' + v : '无门槛' },
          { title: '已发/总量', width: 100, render: (_, r) => `${r.issuedCount}/${r.totalCount === -1 ? '不限' : r.totalCount}` },
          { title: '状态', dataIndex: 'status', width: 80, render: v => <Tag color={v === 1 ? 'green' : 'red'}>{v === 1 ? '启用' : '禁用'}</Tag> },
          { title: '操作', width: 150, render: (_, r) => (
            <Space>
              <Button size="small" onClick={() => { form.setFieldsValue({ ...r, validRange: r.validStart ? [r.validStart, r.validEnd] : null }); setModal({ open: true, data: r }) }}>编辑</Button>
              <Button size="small" danger onClick={() => Modal.confirm({ title: '确认删除', onOk: async () => { await couponApi.delete(r.id); message.success('删除成功'); loadData() } })}>删除</Button>
            </Space>
          )}
        ]}
      />
      <Modal title={modal.data.id ? '编辑优惠券' : '新增优惠券'} open={modal.open} onOk={handleSave} onCancel={() => setModal({ open: false, data: {} })} width={600}>
        <Form form={form} layout="vertical">
          <Form.Item name="name" label="优惠券名称" rules={[{ required: true }]}><Input /></Form.Item>
          <Form.Item name="type" label="类型" rules={[{ required: true }]}>
            <Select options={[{ value: 1, label: '满减券' }, { value: 2, label: '折扣券' }, { value: 3, label: '无门槛券' }]} />
          </Form.Item>
          <Form.Item name="faceValue" label="面值(满减/无门槛)"><InputNumber min={0} precision={2} style={{ width: '100%' }} /></Form.Item>
          <Form.Item name="discount" label="折扣率(如0.85表示85折)"><InputNumber min={0} max={1} step={0.01} precision={2} style={{ width: '100%' }} /></Form.Item>
          <Form.Item name="minSpend" label="最低消费"><InputNumber min={0} precision={2} style={{ width: '100%' }} /></Form.Item>
          <Form.Item name="totalCount" label="发放总量(-1不限)"><InputNumber style={{ width: '100%' }} /></Form.Item>
          <Form.Item name="perLimit" label="每人限领"><InputNumber min={1} style={{ width: '100%' }} /></Form.Item>
          <Form.Item name="validType" label="有效期类型" rules={[{ required: true }]}>
            <Select options={[{ value: 1, label: '固定日期' }, { value: 2, label: '领取后N天' }]} />
          </Form.Item>
          <Form.Item name="validRange" label="有效期范围"><DatePicker.RangePicker showTime style={{ width: '100%' }} /></Form.Item>
          <Form.Item name="validDays" label="领取后有效天数"><InputNumber /></Form.Item>
          <Form.Item name="status" label="状态"><Select options={[{ value: 1, label: '启用' }, { value: 0, label: '禁用' }]} /></Form.Item>
        </Form>
      </Modal>
    </Card>
  )
}
