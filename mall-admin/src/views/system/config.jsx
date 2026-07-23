import { useState, useEffect } from 'react'
import { Card, Table, Button, Input, Space, Modal, Form, Select, Tag, message } from 'antd'
import { PlusOutlined } from '@ant-design/icons'
import { sysConfigApi } from '../../api'

export default function SysConfig() {
  const [data, setData] = useState({ list: [], total: 0 })
  const [params, setParams] = useState({ pageNum: 1, pageSize: 10, keyword: '' })
  const [loading, setLoading] = useState(false)
  const [modal, setModal] = useState({ open: false, data: {} })
  const [form] = Form.useForm()

  const loadData = async () => {
    setLoading(true)
    try { const res = await sysConfigApi.list(params); setData(res.data) } catch (e) {}
    setLoading(false)
  }
  useEffect(() => { loadData() }, [params])

  const handleSave = async () => {
    const values = await form.validateFields()
    if (modal.data.id) values.id = modal.data.id
    await sysConfigApi.save(values)
    message.success('保存成功'); setModal({ open: false, data: {} }); loadData()
  }

  return (
    <Card>
      <div className="search-bar">
        <Input.Search placeholder="配置键/名称" onSearch={v => setParams({ ...params, keyword: v, pageNum: 1 })} style={{ width: 200 }} />
        <Button type="primary" icon={<PlusOutlined />} onClick={() => { form.resetFields(); form.setFieldsValue({ configType: 'string', isSystem: 0 }); setModal({ open: true, data: {} }) }}>新增配置</Button>
      </div>
      <Table rowKey="id" loading={loading} dataSource={data.list}
        pagination={{ current: params.pageNum, pageSize: params.pageSize, total: data.total, onChange: (p, s) => setParams({ ...params, pageNum: p, pageSize: s }) }}
        columns={[
          { title: 'ID', dataIndex: 'id', width: 60 },
          { title: '配置键', dataIndex: 'configKey', width: 180 },
          { title: '配置值', dataIndex: 'configValue', ellipsis: true },
          { title: '名称', dataIndex: 'name', width: 120 },
          { title: '类型', dataIndex: 'configType', width: 80 },
          { title: '系统内置', dataIndex: 'isSystem', width: 80, render: v => v === 1 ? <Tag color="blue">是</Tag> : <Tag>否</Tag> },
          { title: '操作', width: 150, render: (_, r) => (
            <Space>
              <Button size="small" onClick={() => { form.setFieldsValue(r); setModal({ open: true, data: r }) }}>编辑</Button>
              {r.isSystem !== 1 && <Button size="small" danger onClick={() => Modal.confirm({ title: '确认删除', onOk: async () => { await sysConfigApi.delete(r.id); message.success('删除成功'); loadData() } })}>删除</Button>}
            </Space>
          )}
        ]}
      />
      <Modal title={modal.data.id ? '编辑配置' : '新增配置'} open={modal.open} onOk={handleSave} onCancel={() => setModal({ open: false, data: {} })}>
        <Form form={form} layout="vertical">
          <Form.Item name="configKey" label="配置键" rules={[{ required: true }]}><Input disabled={!!modal.data.id} /></Form.Item>
          <Form.Item name="configValue" label="配置值" rules={[{ required: true }]}><Input.TextArea rows={3} /></Form.Item>
          <Form.Item name="configType" label="值类型"><Select options={[{ value: 'string', label: '字符串' }, { value: 'number', label: '数字' }, { value: 'boolean', label: '布尔' }, { value: 'json', label: 'JSON' }]} /></Form.Item>
          <Form.Item name="name" label="配置名称"><Input /></Form.Item>
          <Form.Item name="description" label="描述"><Input.TextArea rows={2} /></Form.Item>
        </Form>
      </Modal>
    </Card>
  )
}
