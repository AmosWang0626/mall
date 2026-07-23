import { useState, useEffect } from 'react'
import { Card, Form, Input, InputNumber, Select, Button, Upload, message, Space } from 'antd'
import { useNavigate, useParams } from 'react-router-dom'
import { productApi, categoryApi } from '../../api'

export default function ProductEdit() {
  const navigate = useNavigate()
  const { id } = useParams()
  const [form] = Form.useForm()
  const [categories, setCategories] = useState([])
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    categoryApi.tree().then(res => {
      const flat = []
      const walk = (list) => list.forEach(c => { flat.push({ value: c.id, label: c.name }); if (c.children) walk(c.children) })
      walk(res.data || [])
      setCategories(flat)
    })
    if (id) {
      productApi.detail(id).then(res => form.setFieldsValue(res.data))
    } else {
      form.setFieldsValue({ status: 2, sort: 0, stock: 0, price: 0 })
    }
  }, [id])

  const handleSubmit = async () => {
    const values = await form.validateFields()
    setLoading(true)
    try {
      if (id) { values.id = parseInt(id); await productApi.update(values) }
      else { await productApi.save(values) }
      message.success('保存成功')
      navigate('/product/list')
    } catch (e) {}
    setLoading(false)
  }

  return (
    <Card title={id ? '编辑商品' : '新增商品'}>
      <Form form={form} layout="vertical" style={{ maxWidth: 800 }}>
        <Form.Item name="name" label="商品名称" rules={[{ required: true }]}><Input /></Form.Item>
        <Form.Item name="subtitle" label="副标题"><Input /></Form.Item>
        <Form.Item name="categoryId" label="分类" rules={[{ required: true }]}><Select options={categories} /></Form.Item>
        <Space>
          <Form.Item name="price" label="售价" rules={[{ required: true }]}><InputNumber min={0} precision={2} prefix="¥" /></Form.Item>
          <Form.Item name="originalPrice" label="原价"><InputNumber min={0} precision={2} prefix="¥" /></Form.Item>
          <Form.Item name="cost" label="成本价"><InputNumber min={0} precision={2} prefix="¥" /></Form.Item>
          <Form.Item name="stock" label="库存" rules={[{ required: true }]}><InputNumber min={0} /></Form.Item>
          <Form.Item name="sort" label="排序"><InputNumber /></Form.Item>
        </Space>
        <Form.Item name="mainImage" label="主图URL"><Input /></Form.Item>
        <Form.Item name="subImages" label="子图URL(JSON)"><Input.TextArea rows={2} /></Form.Item>
        <Form.Item name="tags" label="标签(逗号分隔)"><Input /></Form.Item>
        <Form.Item name="status" label="状态" rules={[{ required: true }]}>
          <Select options={[{ value: 0, label: '下架' }, { value: 1, label: '上架' }, { value: 2, label: '草稿' }]} />
        </Form.Item>
        <Form.Item name="detail" label="商品详情(HTML)"><Input.TextArea rows={6} /></Form.Item>
        <Form.Item>
          <Space>
            <Button type="primary" onClick={handleSubmit} loading={loading}>保存</Button>
            <Button onClick={() => navigate('/product/list')}>取消</Button>
          </Space>
        </Form.Item>
      </Form>
    </Card>
  )
}
