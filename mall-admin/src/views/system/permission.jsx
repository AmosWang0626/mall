import { useState, useEffect } from 'react'
import { Card, Tree, Button, Modal, Form, Input, InputNumber, Select, Space, message } from 'antd'
import { PlusOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons'
import { sysPermissionApi } from '../../api'

export default function SysPermission() {
  const [tree, setTree] = useState([])
  const [modal, setModal] = useState({ open: false, data: {} })
  const [form] = Form.useForm()

  const loadData = async () => { const res = await sysPermissionApi.tree(); setTree(buildTree(res.data || [])) }
  const buildTree = (list) => list.map(i => ({ key: i.id, title: i.name, data: i, children: i.children ? buildTree(i.children) : [] }))

  useEffect(() => { loadData() }, [])

  const handleSave = async () => {
    const values = await form.validateFields()
    if (modal.data.id) values.id = modal.data.id
    await sysPermissionApi.save(values)
    message.success('保存成功'); setModal({ open: false, data: {} }); loadData()
  }

  return (
    <Card title="权限管理">
      <div style={{ marginBottom: 16 }}>
        <Button type="primary" icon={<PlusOutlined />} onClick={() => { form.resetFields(); form.setFieldsValue({ parentId: 0, type: 1, sort: 0, status: 1, visible: 1 }); setModal({ open: true, data: {} }) }}>新增顶级权限</Button>
      </div>
      <Tree treeData={tree} defaultExpandAll titleRender={(node) => (
        <Space>
          <span>{node.title}</span>
          <span style={{ color: '#999', fontSize: 12 }}>{node.data.code}</span>
          <Button size="small" type="link" icon={<PlusOutlined />} onClick={(e) => { e.stopPropagation(); form.resetFields(); form.setFieldsValue({ parentId: node.data.id, type: 2, sort: 0, status: 1, visible: 1 }); setModal({ open: true, data: {} }) }}>子权限</Button>
          <Button size="small" type="link" icon={<EditOutlined />} onClick={(e) => { e.stopPropagation(); form.setFieldsValue(node.data); setModal({ open: true, data: node.data }) }}>编辑</Button>
          <Button size="small" type="link" danger icon={<DeleteOutlined />} onClick={(e) => { e.stopPropagation(); Modal.confirm({ title: '确认删除', onOk: async () => { await sysPermissionApi.delete(node.data.id); message.success('删除成功'); loadData() } }) }}>删除</Button>
        </Space>
      )} />
      <Modal title={modal.data.id ? '编辑权限' : '新增权限'} open={modal.open} onOk={handleSave} onCancel={() => setModal({ open: false, data: {} })}>
        <Form form={form} layout="vertical">
          <Form.Item name="name" label="权限名称" rules={[{ required: true }]}><Input /></Form.Item>
          <Form.Item name="code" label="权限编码" rules={[{ required: true }]}><Input placeholder="如 product:list" /></Form.Item>
          <Form.Item name="type" label="类型"><Select options={[{ value: 1, label: '菜单' }, { value: 2, label: '按钮' }, { value: 3, label: '接口' }]} /></Form.Item>
          <Form.Item name="parentId" label="父权限ID"><InputNumber disabled style={{ width: '100%' }} /></Form.Item>
          <Form.Item name="path" label="路由路径"><Input /></Form.Item>
          <Form.Item name="component" label="组件路径"><Input /></Form.Item>
          <Form.Item name="icon" label="图标"><Input /></Form.Item>
          <Form.Item name="sort" label="排序"><InputNumber /></Form.Item>
        </Form>
      </Modal>
    </Card>
  )
}
