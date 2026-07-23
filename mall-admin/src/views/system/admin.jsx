import { useState, useEffect } from 'react'
import { Card, Table, Button, Input, Space, Modal, Form, Tag, Switch, message } from 'antd'
import { PlusOutlined } from '@ant-design/icons'
import { sysAdminApi, sysRoleApi } from '../../api'

export default function SysAdmin() {
  const [data, setData] = useState({ list: [], total: 0 })
  const [params, setParams] = useState({ pageNum: 1, pageSize: 10, keyword: '' })
  const [loading, setLoading] = useState(false)
  const [modal, setModal] = useState({ open: false, data: {} })
  const [form] = Form.useForm()
  const [roles, setRoles] = useState([])
  const [roleModal, setRoleModal] = useState({ open: false, id: null })
  const [selectedRoles, setSelectedRoles] = useState([])

  const loadData = async () => {
    setLoading(true)
    try { const res = await sysAdminApi.list(params); setData(res.data) } catch (e) {}
    setLoading(false)
  }
  useEffect(() => { loadData(); sysRoleApi.all().then(r => setRoles(r.data || [])) }, [params])

  const handleSave = async () => {
    const values = await form.validateFields()
    if (modal.data.id) { values.id = modal.data.id; await sysAdminApi.update(values) }
    else { await sysAdminApi.save(values) }
    message.success('保存成功'); setModal({ open: false, data: {} }); loadData()
  }

  const handleAssignRoles = async () => {
    await sysAdminApi.assignRoles(roleModal.id, selectedRoles)
    message.success('分配成功'); setRoleModal({ open: false, id: null })
  }

  return (
    <Card>
      <div className="search-bar">
        <Input.Search placeholder="用户名/昵称" onSearch={v => setParams({ ...params, keyword: v, pageNum: 1 })} style={{ width: 200 }} />
        <Button type="primary" icon={<PlusOutlined />} onClick={() => { form.resetFields(); form.setFieldsValue({ status: 1 }); setModal({ open: true, data: {} }) }}>新增管理员</Button>
      </div>
      <Table rowKey="id" loading={loading} dataSource={data.list}
        pagination={{ current: params.pageNum, pageSize: params.pageSize, total: data.total, onChange: (p, s) => setParams({ ...params, pageNum: p, pageSize: s }) }}
        columns={[
          { title: 'ID', dataIndex: 'id', width: 60 },
          { title: '用户名', dataIndex: 'username', width: 120 },
          { title: '昵称', dataIndex: 'nickname', width: 120 },
          { title: '手机号', dataIndex: 'phone', width: 130 },
          { title: '状态', dataIndex: 'status', width: 80, render: v => <Tag color={v === 1 ? 'green' : 'red'}>{v === 1 ? '正常' : '禁用'}</Tag> },
          { title: '最后登录', dataIndex: 'lastLogin', width: 170 },
          { title: '操作', width: 250, render: (_, r) => (
            <Space>
              <Button size="small" onClick={() => { form.setFieldsValue(r); setModal({ open: true, data: r }) }}>编辑</Button>
              <Button size="small" onClick={async () => { const res = await sysAdminApi.getRoleIds(r.id); setSelectedRoles(res.data || []); setRoleModal({ open: true, id: r.id }) }}>分配角色</Button>
              <Button size="small" danger onClick={() => Modal.confirm({ title: '确认删除', onOk: async () => { await sysAdminApi.delete(r.id); message.success('删除成功'); loadData() } })}>删除</Button>
            </Space>
          )}
        ]}
      />
      <Modal title={modal.data.id ? '编辑管理员' : '新增管理员'} open={modal.open} onOk={handleSave} onCancel={() => setModal({ open: false, data: {} })}>
        <Form form={form} layout="vertical">
          <Form.Item name="username" label="用户名" rules={[{ required: true }]}><Input disabled={!!modal.data.id} /></Form.Item>
          <Form.Item name="password" label={modal.data.id ? '密码(留空不修改)' : '密码'} rules={modal.data.id ? [] : [{ required: true }]}><Input.Password /></Form.Item>
          <Form.Item name="nickname" label="昵称"><Input /></Form.Item>
          <Form.Item name="phone" label="手机号"><Input /></Form.Item>
          <Form.Item name="email" label="邮箱"><Input /></Form.Item>
          <Form.Item name="status" label="状态"><Switch checkedChildren="正常" unCheckedChildren="禁用" checked={form.getFieldValue('status') === 1} onChange={v => form.setFieldsValue({ status: v ? 1 : 0 })} /></Form.Item>
        </Form>
      </Modal>
      <Modal title="分配角色" open={roleModal.open} onOk={handleAssignRoles} onCancel={() => setRoleModal({ open: false, id: null })}>
        <Form layout="vertical">
          <Form.Item label="选择角色">
            <Switch checkedChildren="正常" unCheckedChildren="禁用" checked={selectedRoles.includes(1)} onChange={v => setSelectedRoles(v ? [...selectedRoles, 1] : selectedRoles.filter(r => r !== 1))} />
            {roles.map(r => <div key={r.id} style={{ margin: '8px 0' }}><Switch checked={selectedRoles.includes(r.id)} onChange={v => setSelectedRoles(v ? [...selectedRoles, r.id] : selectedRoles.filter(id => id !== r.id))} /> <span style={{ marginLeft: 8 }}>{r.name} ({r.code})</span></div>)}
          </Form.Item>
        </Form>
      </Modal>
    </Card>
  )
}
