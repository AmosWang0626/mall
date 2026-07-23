import { useState, useEffect } from 'react'
import { Card, Table, Button, Input, Space, Tag, Modal, Form, InputNumber, DatePicker, Select, message } from 'antd'
import { PlusOutlined } from '@ant-design/icons'
import { marketingApi } from '../../api'

const typeMap = { FLASH_SALE: '限时秒杀', FULL_REDUCTION: '满减', DISCOUNT: '折扣' }
const statusMap = { 0: { text: '未开始', color: 'default' }, 1: { text: '进行中', color: 'green' }, 2: { text: '已结束', color: 'red' }, 3: { text: '已终止', color: 'orange' } }

export default function MarketingActivity() {
  const [data, setData] = useState({ list: [], total: 0 })
  const [params, setParams] = useState({ pageNum: 1, pageSize: 10, name: '' })
  const [loading, setLoading] = useState(false)
  const [modal, setModal] = useState({ open: false, data: {} })
  const [form] = Form.useForm()

  const loadData = async () => {
    setLoading(true)
    try { const res = await marketingApi.list(params); setData(res.data) } catch (e) {}
    setLoading(false)
  }
  useEffect(() => { loadData() }, [params])

  const handleSave = async () => {
    const values = await form.validateFields()
    values.startTime = values.timeRange?.[0]?.format('YYYY-MM-DD HH:mm:ss')
    values.endTime = values.timeRange?.[1]?.format('YYYY-MM-DD HH:mm:ss')
    delete values.timeRange
    if (modal.data.id) values.id = modal.data.id
    await marketingApi.save(values)
    message.success('保存成功')
    setModal({ open: false, data: {} })
    loadData()
  }

  return (
    <Card>
      <div className="search-bar">
        <Input.Search placeholder="活动名称" onSearch={v => setParams({ ...params, name: v, pageNum: 1 })} style={{ width: 200 }} />
        <Button type="primary" icon={<PlusOutlined />} onClick={() => { form.resetFields(); form.setFieldsValue({ type: 'FLASH_SALE', enabled: 1, rules: '{}' }); setModal({ open: true, data: {} }) }}>新增活动</Button>
      </div>
      <Table rowKey="id" loading={loading} dataSource={data.list}
        pagination={{ current: params.pageNum, pageSize: params.pageSize, total: data.total, onChange: (p, s) => setParams({ ...params, pageNum: p, pageSize: s }) }}
        columns={[
          { title: 'ID', dataIndex: 'id', width: 60 },
          { title: '活动名称', dataIndex: 'name' },
          { title: '类型', dataIndex: 'type', width: 100, render: v => typeMap[v] || v },
          { title: '开始时间', dataIndex: 'startTime', width: 170 },
          { title: '结束时间', dataIndex: 'endTime', width: 170 },
          { title: '状态', dataIndex: 'status', width: 80, render: v => { const s = statusMap[v]; return <Tag color={s.color}>{s.text}</Tag> } },
          { title: '操作', width: 150, render: (_, r) => (
            <Space>
              <Button size="small" onClick={() => { form.setFieldsValue({ ...r, timeRange: [r.startTime, r.endTime] }); setModal({ open: true, data: r }) }}>编辑</Button>
              <Button size="small" danger onClick={() => Modal.confirm({ title: '确认删除', onOk: async () => { await marketingApi.delete(r.id); message.success('删除成功'); loadData() } })}>删除</Button>
            </Space>
          )}
        ]}
      />
      <Modal title={modal.data.id ? '编辑活动' : '新增活动'} open={modal.open} onOk={handleSave} onCancel={() => setModal({ open: false, data: {} })} width={600}>
        <Form form={form} layout="vertical">
          <Form.Item name="name" label="活动名称" rules={[{ required: true }]}><Input /></Form.Item>
          <Form.Item name="type" label="活动类型" rules={[{ required: true }]}>
            <Select options={[{ value: 'FLASH_SALE', label: '限时秒杀' }, { value: 'FULL_REDUCTION', label: '满减' }, { value: 'DISCOUNT', label: '折扣' }]} />
          </Form.Item>
          <Form.Item name="description" label="活动描述"><Input.TextArea rows={2} /></Form.Item>
          <Form.Item name="timeRange" label="活动时间" rules={[{ required: true }]}><DatePicker.RangePicker showTime style={{ width: '100%' }} /></Form.Item>
          <Form.Item name="rules" label="活动规则(JSON)"><Input.TextArea rows={3} placeholder='{"limitPerUser":1}' /></Form.Item>
        </Form>
      </Modal>
    </Card>
  )
}
