import { useState, useEffect } from 'react'
import { Card, Table, Button, Input, InputNumber, Select, Space, Modal, Form, Tag, Tree, message } from 'antd'
import { PlusOutlined } from '@ant-design/icons'
import { sysRoleApi, sysPermissionApi } from '../../api'

export default function SysRole() {
  const [data, setData] = useState({ list: [], total: 0 })
  const [params, setParams] = useState({ pageNum: 1, pageSize: 10, keyword: '' })
  const [loading, setLoading] = useState(false)
  const [modal, setModal] = useState({ open: false, data: {} })
  const [form] = Form.useForm()
  const [permModal, setPermModal] = useState({ open: false, id: null })
  const [permTree, setPermTree] = useState([])
  const [checkedKeys, setCheckedKeys] = useState([])

  const loadData = async () => {
    setLoading(true)
    try { const res = await sysRoleApi.list(params); setData(res.data) } catch (e) {}
    setLoading(false)
  }
  useEffect(() => { loadData() }, [params])

  const handleSave = async () => {
    const values = await form.validateFields()
    if (modal.data.id) values.id = modal.data.id
    await sysRoleApi.save(values)
    message.success('保存成功'); setModal({ open: false, data: {} }); loadData()
  }

  const handleAssignPerms = async () => {
    await sysRoleApi.assignPermissions(permModal.id, checkedKeys.checked || checkedKeys)
    message.success('分配成功'); setPermModal({ open: false, id: null })
  }

  const openPermModal = async (roleId) => {
    const [treeRes, permRes] = await Promise.all([sysPermissionApi.tree(), sysRoleApi.getPermissionIds(roleId)])
    setPermTree(buildTreeData(treeRes.data || []))
    setCheckedKeys(permRes.data || [])
    setPermModal({ open: true, id: roleId })
  }

  const buildTreeData = (list) => list.map(i => ({ key: i.id, title: i.name, children: i.children ? buildTreeData(i.children) : [] }))

  return (
    <Card>
      <div className="search-bar">
        <Input.Search placeholder="角色名称/编码" onSearch={v => setParams({ ...params, keyword: v, pageNum: 1 })} style={{ width: 200 }} />
        <Button type="primary" icon={<PlusOutlined />} onClick={() => { form.resetFields(); form.setFieldsValue({ status: 1, dataScope: 1, sort: 0 }); setModal({ open: true, data: {} }) }}>新增角色</Button>
      </div>
      <Table rowKey="id" loading={loading} dataSource={data.list}
        pagination={{ current: params.pageNum, pageSize: params.pageSize, total: data.total, onChange: (p, s) => setParams({ ...params, pageNum: p, pageSize: s }) }}
        columns={[
          { title: 'ID', dataIndex: 'id', width: 60 },
          { title: '角色名称', dataIndex: 'name', width: 120 },
          { title: '编码', dataIndex: 'code', width: 120 },
          { title: '描述', dataIndex: 'description' },
          { title: '状态', dataIndex: 'status', width: 80, render: v => <Tag color={v === 1 ? 'green' : 'red'}>{v === 1 ? '正常' : '禁用'}</Tag> },
          { title: '操作', width: 250, render: (_, r) => (
            <Space>
              <Button size="small" onClick={() => { form.setFieldsValue(r); setModal({ open: true, data: r }) }}>编辑</Button>
              <Button size="small" type="primary" onClick={() => openPermModal(r.id)}>分配权限</Button>
              <Button size="small" danger onClick={() => Modal.confirm({ title: '确认删除', onOk: async () => { await sysRoleApi.delete(r.id); message.success('删除成功'); loadData() } })}>删除</Button>
            </Space>
          )}
        ]}
      />
      <Modal title={modal.data.id ? '编辑角色' : '新增角色'} open={modal.open} onOk={handleSave} onCancel={() => setModal({ open: false, data: {} })}>
        <Form form={form} layout="vertical">
          <Form.Item name="name" label="角色名称" rules={[{ required: true }]}><Input /></Form.Item>
          <Form.Item name="code" label="角色编码" rules={[{ required: true }]}><Input /></Form.Item>
          <Form.Item name="description" label="描述"><Input.TextArea rows={2} /></Form.Item>
          <Form.Item name="sort" label="排序"><InputNumber style={{ width: '100%' }} /></Form.Item>
          <Form.Item name="dataScope" label="数据权限范围">
            <Select options={[{ label: '全部数据', value: 1 }, { label: '自定义数据', value: 2 }, { label: '本部门数据', value: 3 }, { label: '本部门及以下', value: 4 }]} />
          </Form.Item>
          <Form.Item name="status" label="状态">
            <Select options={[{ label: '正常', value: 1 }, { label: '禁用', value: 0 }]} />
          </Form.Item>
        </Form>
      </Modal>
      <Modal title="分配权限" open={permModal.open} onOk={handleAssignPerms} onCancel={() => setPermModal({ open: false, id: null })} width={500}>
        <Tree check checkStrictly treeData={permTree} checkedKeys={checkedKeys} onCheck={setCheckedKeys} defaultExpandAll />
      </Modal>
    </Card>
  )
}
