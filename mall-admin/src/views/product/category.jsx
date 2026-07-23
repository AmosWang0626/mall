import { useState, useEffect } from 'react'
import { Card, Tree, Button, Modal, Form, Input, InputNumber, Space, message } from 'antd'
import { PlusOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons'
import { categoryApi } from '../../api'

export default function ProductCategory() {
  const [tree, setTree] = useState([])
  const [modal, setModal] = useState({ open: false, data: {} })
  const [form] = Form.useForm()

  const load = async () => { const res = await categoryApi.tree(); setTree(buildTree(res.data || [])) }

  const buildTree = (list) => list.map(i => ({ key: i.id, title: i.name, data: i, children: i.children ? buildTree(i.children) : [] }))

  useEffect(() => { load() }, [])

  const handleSave = async () => {
    const values = await form.validateFields()
    if (modal.data.id) values.id = modal.data.id
    if (modal.data.id) { values.id = modal.data.id; await categoryApi.update(values) }
    else { await categoryApi.save(values) }
    message.success('保存成功')
    setModal({ open: false, data: {} })
    load()
  }

  const handleDelete = (node) => {
    Modal.confirm({ title: '确认删除', content: `删除分类「${node.name}」？`, onOk: async () => { await categoryApi.delete(node.id); message.success('删除成功'); load() } })
  }

  return (
    <Card title="商品分类管理">
      <div style={{ marginBottom: 16 }}>
        <Button type="primary" icon={<PlusOutlined />} onClick={() => { form.resetFields(); form.setFieldsValue({ parentId: 0, sort: 0, status: 1, level: 1 }); setModal({ open: true, data: {} }) }}>新增顶级分类</Button>
      </div>
      <Tree treeData={tree} defaultExpandAll titleRender={(node) => (
        <Space>
          <span>{node.title}</span>
          <Button size="small" type="link" icon={<PlusOutlined />} onClick={(e) => { e.stopPropagation(); form.resetFields(); form.setFieldsValue({ parentId: node.data.id, sort: 0, status: 1, level: (node.data.level || 1) + 1 }); setModal({ open: true, data: {} }) }}>子分类</Button>
          <Button size="small" type="link" icon={<EditOutlined />} onClick={(e) => { e.stopPropagation(); form.setFieldsValue(node.data); setModal({ open: true, data: node.data }) }}>编辑</Button>
          <Button size="small" type="link" danger icon={<DeleteOutlined />} onClick={(e) => { e.stopPropagation(); handleDelete(node.data) }}>删除</Button>
        </Space>
      )} />
      <Modal title={modal.data.id ? '编辑分类' : '新增分类'} open={modal.open} onOk={handleSave} onCancel={() => setModal({ open: false, data: {} })}>
        <Form form={form} layout="vertical">
          <Form.Item name="name" label="分类名称" rules={[{ required: true }]}><Input /></Form.Item>
          <Form.Item name="parentId" label="父分类ID"><InputNumber disabled style={{ width: '100%' }} /></Form.Item>
          <Form.Item name="sort" label="排序"><InputNumber style={{ width: '100%' }} /></Form.Item>
          <Form.Item name="level" label="层级"><InputNumber disabled style={{ width: '100%' }} /></Form.Item>
          <Form.Item name="icon" label="图标URL"><Input /></Form.Item>
        </Form>
      </Modal>
    </Card>
  )
}
