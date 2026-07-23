#!/usr/bin/env python3
"""Generate all React view components for mall-admin."""
import os
SRC = "/Users/dorian/WorkBuddy/2026-07-21-23-31-52/mall-admin/src/views"

def w(path, content):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, 'w') as f:
        f.write(content)
    print(f"  + {path}")

print("=== Generating View Components ===")

# Product List
w(f"{SRC}/product/list.jsx", '''import { useState, useEffect } from 'react'
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
''')

# Product Category
w(f"{SRC}/product/category.jsx", '''import { useState, useEffect } from 'react'
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
    await categoryApi.save(values)
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
''')

# Product Edit
w(f"{SRC}/product/edit.jsx", '''import { useState, useEffect } from 'react'
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
''')

# Order List
w(f"{SRC}/order/list.jsx", '''import { useState, useEffect } from 'react'
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
''')

# Order Detail
w(f"{SRC}/order/detail.jsx", '''import { useState, useEffect } from 'react'
import { Card, Descriptions, Table, Tag, Button, Spin } from 'antd'
import { useParams, useNavigate } from 'react-router-dom'
import { orderApi } from '../../api'

const statusMap = { 0: '待付款', 1: '待发货', 2: '待收货', 3: '已完成', 4: '已取消', 5: '已退款' }

export default function OrderDetail() {
  const { id } = useParams()
  const navigate = useNavigate()
  const [order, setOrder] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    orderApi.detail(id).then(res => setOrder(res.data)).finally(() => setLoading(false))
  }, [id])

  if (loading) return <Spin />
  if (!order) return <div>订单不存在</div>

  return (
    <div>
      <Card title="订单信息" style={{ marginBottom: 16 }} extra={<Button onClick={() => navigate('/order/list')}>返回列表</Button>}>
        <Descriptions column={3} bordered>
          <Descriptions.Item label="订单号">{order.orderNo}</Descriptions.Item>
          <Descriptions.Item label="状态"><Tag>{statusMap[order.status]}</Tag></Descriptions.Item>
          <Descriptions.Item label="下单时间">{order.createTime}</Descriptions.Item>
          <Descriptions.Item label="商品总额">¥{order.totalAmount}</Descriptions.Item>
          <Descriptions.Item label="优惠金额">¥{order.discountAmount}</Descriptions.Item>
          <Descriptions.Item label="积分抵扣">¥{order.pointsAmount}</Descriptions.Item>
          <Descriptions.Item label="实付金额">¥{order.payAmount}</Descriptions.Item>
          <Descriptions.Item label="使用积分">{order.pointsUsed}</Descriptions.Item>
          <Descriptions.Item label="获得积分">{order.pointsEarned}</Descriptions.Item>
          <Descriptions.Item label="收货人">{order.receiver}</Descriptions.Item>
          <Descriptions.Item label="联系电话">{order.receiverPhone}</Descriptions.Item>
          <Descriptions.Item label="收货地址" span={2}>{order.receiverAddress}</Descriptions.Item>
          {order.shipCompany && <Descriptions.Item label="物流公司">{order.shipCompany}</Descriptions.Item>}
          {order.shipNo && <Descriptions.Item label="物流单号">{order.shipNo}</Descriptions.Item>}
          {order.remark && <Descriptions.Item label="备注" span={3}>{order.remark}</Descriptions.Item>}
        </Descriptions>
      </Card>
      <Card title="商品明细">
        <Table rowKey="id" dataSource={order.items || []} pagination={false} columns={[
          { title: '商品名称', dataIndex: 'productName' },
          { title: 'SKU', dataIndex: 'skuName' },
          { title: '单价', dataIndex: 'price', render: v => '¥' + v },
          { title: '数量', dataIndex: 'quantity' },
          { title: '小计', dataIndex: 'totalAmount', render: v => '¥' + v }
        ]} />
      </Card>
    </div>
  )
}
''')

# User List
w(f"{SRC}/user/list.jsx", '''import { useState, useEffect } from 'react'
import { Card, Table, Button, Input, Select, Space, Tag, Switch, Modal, message } from 'antd'
import { userApi } from '../../api'

export default function UserList() {
  const [data, setData] = useState([])
  const [loading, setLoading] = useState(false)
  const [params, setParams] = useState({ pageNum: 1, pageSize: 10, keyword: '', status: null })

  const loadData = async () => {
    setLoading(true)
    try { const res = await userApi.list(params); setData(res.data) } catch (e) {}
    setLoading(false)
  }
  useEffect(() => { loadData() }, [params])

  const handleStatus = async (id, checked) => {
    await userApi.updateStatus(id, checked ? 1 : 0)
    message.success('操作成功')
    loadData()
  }

  const handleDelete = (id) => {
    Modal.confirm({ title: '确认删除', content: '删除后不可恢复', onOk: async () => { await userApi.delete(id); message.success('删除成功'); loadData() } })
  }

  return (
    <Card>
      <div className="search-bar">
        <Input.Search placeholder="用户名/昵称/手机号" allowClear onSearch={v => setParams({ ...params, keyword: v, pageNum: 1 })} style={{ width: 250 }} />
        <Select placeholder="状态" allowClear style={{ width: 120 }} onChange={v => setParams({ ...params, status: v, pageNum: 1 })} options={[{ value: 0, label: '禁用' }, { value: 1, label: '正常' }]} />
      </div>
      <Table rowKey="id" loading={loading} dataSource={data.list || []}
        pagination={{ current: params.pageNum, pageSize: params.pageSize, total: data.total || 0, onChange: (p, s) => setParams({ ...params, pageNum: p, pageSize: s }) }}
        columns={[
          { title: 'ID', dataIndex: 'id', width: 60 },
          { title: '用户名', dataIndex: 'username', width: 120 },
          { title: '昵称', dataIndex: 'nickname', width: 120 },
          { title: '手机号', dataIndex: 'phone', width: 130 },
          { title: '邮箱', dataIndex: 'email', width: 180 },
          { title: '注册时间', dataIndex: 'createTime', width: 170 },
          { title: '最后登录', dataIndex: 'lastLogin', width: 170 },
          { title: '状态', dataIndex: 'status', width: 80, render: (v, r) => <Switch checked={v === 1} onChange={(c) => handleStatus(r.id, c)} /> },
          { title: '操作', width: 80, render: (_, r) => <Button size="small" danger onClick={() => handleDelete(r.id)}>删除</Button> }
        ]}
      />
    </Card>
  )
}
''')

# Points Account & Log
w(f"{SRC}/points/account.jsx", '''import { useState, useEffect } from 'react'
import { Card, Table, Input, Button } from 'antd'
import { pointsApi } from '../../api'

export default function PointsAccount() {
  const [data, setData] = useState({ list: [], total: 0 })
  const [userId, setUserId] = useState('')
  const [loading, setLoading] = useState(false)

  const load = async () => {
    if (!userId) return
    setLoading(true)
    try { const res = await pointsApi.account(userId); setData({ list: [res.data], total: 1 }) } catch (e) {}
    setLoading(false)
  }

  return (
    <Card title="积分账户">
      <div className="search-bar">
        <Input.Search placeholder="输入用户ID" onSearch={load} value={userId} onChange={e => setUserId(e.target.value)} style={{ width: 200 }} />
        <Button type="primary" onClick={load}>查询</Button>
      </div>
      <Table rowKey="userId" loading={loading} dataSource={data.list} pagination={false} columns={[
        { title: '用户ID', dataIndex: 'userId' },
        { title: '可用积分', dataIndex: 'balance' },
        { title: '冻结积分', dataIndex: 'frozen' },
        { title: '累计获得', dataIndex: 'totalEarned' },
        { title: '累计使用', dataIndex: 'totalUsed' }
      ]} />
    </Card>
  )
}
''')

w(f"{SRC}/points/log.jsx", '''import { useState, useEffect } from 'react'
import { Card, Table, Input, Tag, Button } from 'antd'
import { pointsApi } from '../../api'

const typeMap = { EARN: { text: '获得', color: 'green' }, USE: { text: '使用', color: 'orange' }, FREEZE: { text: '冻结', color: 'blue' }, UNFREEZE: { text: '解冻', color: 'cyan' }, REFUND: { text: '退回', color: 'purple' } }

export default function PointsLog() {
  const [data, setData] = useState({ list: [], total: 0 })
  const [userId, setUserId] = useState('')
  const [params, setParams] = useState({ pageNum: 1, pageSize: 10, userId: '' })
  const [loading, setLoading] = useState(false)

  const load = async () => {
    if (!params.userId) return
    setLoading(true)
    try { const res = await pointsApi.logs(params); setData(res.data) } catch (e) {}
    setLoading(false)
  }

  return (
    <Card title="积分流水">
      <div className="search-bar">
        <Input.Search placeholder="输入用户ID" onSearch={v => setParams({ ...params, userId: v, pageNum: 1 })} style={{ width: 200 }} />
        <Button type="primary" onClick={load}>查询</Button>
      </div>
      <Table rowKey="id" loading={loading} dataSource={data.list}
        pagination={{ current: params.pageNum, pageSize: params.pageSize, total: data.total, onChange: (p, s) => setParams({ ...params, pageNum: p, pageSize: s }) }}
        columns={[
          { title: 'ID', dataIndex: 'id', width: 60 },
          { title: '用户ID', dataIndex: 'userId', width: 80 },
          { title: '变动类型', dataIndex: 'changeType', width: 80, render: v => { const t = typeMap[v]; return <Tag color={t.color}>{t.text}</Tag> } },
          { title: '积分', dataIndex: 'points', width: 80 },
          { title: '变动后余额', dataIndex: 'balanceAfter', width: 100 },
          { title: '来源', dataIndex: 'source', width: 80 },
          { title: '备注', dataIndex: 'remark' },
          { title: '时间', dataIndex: 'createTime', width: 170 }
        ]}
      />
    </Card>
  )
}
''')

# Coupon Template & Record
w(f"{SRC}/coupon/template.jsx", '''import { useState, useEffect } from 'react'
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
''')

w(f"{SRC}/coupon/record.jsx", '''import { Card, Empty } from 'antd'
export default function CouponRecord() {
  return <Card title="优惠券领取记录"><Empty description="请在前端用户端查看领取记录" /></Card>
}
''')

# Marketing Activity
w(f"{SRC}/marketing/activity.jsx", '''import { useState, useEffect } from 'react'
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
''')

# System Admin
w(f"{SRC}/system/admin.jsx", '''import { useState, useEffect } from 'react'
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
''')

# System Role
w(f"{SRC}/system/role.jsx", '''import { useState, useEffect } from 'react'
import { Card, Table, Button, Input, Space, Modal, Form, Tag, Tree, message } from 'antd'
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
          <Form.Item name="sort" label="排序"><InputNumber /></Form.Item>
          <Form.Item name="dataScope" label="数据权限范围"><InputNumber /></Form.Item>
          <Form.Item name="status" label="状态"><InputNumber /></Form.Item>
        </Form>
      </Modal>
      <Modal title="分配权限" open={permModal.open} onOk={handleAssignPerms} onCancel={() => setPermModal({ open: false, id: null })} width={500}>
        <Tree check checkStrictly treeData={permTree} checkedKeys={checkedKeys} onCheck={setCheckedKeys} defaultExpandAll />
      </Modal>
    </Card>
  )
}
''')

# System Permission
w(f"{SRC}/system/permission.jsx", '''import { useState, useEffect } from 'react'
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
''')

# System Config
w(f"{SRC}/system/config.jsx", '''import { useState, useEffect } from 'react'
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
''')

# System Log
w(f"{SRC}/system/log.jsx", '''import { useState, useEffect } from 'react'
import { Card, Table, Input, Button } from 'antd'
import { sysLogApi } from '../../api'

export default function SysLog() {
  const [data, setData] = useState({ list: [], total: 0 })
  const [params, setParams] = useState({ pageNum: 1, pageSize: 10, module: '' })
  const [loading, setLoading] = useState(false)

  const loadData = async () => {
    setLoading(true)
    try { const res = await sysLogApi.list(params); setData(res.data) } catch (e) {}
    setLoading(false)
  }
  useEffect(() => { loadData() }, [params])

  return (
    <Card title="操作日志">
      <div className="search-bar">
        <Input.Search placeholder="模块" onSearch={v => setParams({ ...params, module: v, pageNum: 1 })} style={{ width: 200 }} />
        <Button onClick={loadData}>刷新</Button>
      </div>
      <Table rowKey="id" loading={loading} dataSource={data.list}
        pagination={{ current: params.pageNum, pageSize: params.pageSize, total: data.total, onChange: (p, s) => setParams({ ...params, pageNum: p, pageSize: s }) }}
        columns={[
          { title: 'ID', dataIndex: 'id', width: 60 },
          { title: '操作人', dataIndex: 'adminName', width: 100 },
          { title: '模块', dataIndex: 'module', width: 100 },
          { title: '操作', dataIndex: 'operation', width: 150 },
          { title: '请求URL', dataIndex: 'requestUrl', ellipsis: true },
          { title: 'IP', dataIndex: 'ip', width: 120 },
          { title: '耗时(ms)', dataIndex: 'costTime', width: 90 },
          { title: '时间', dataIndex: 'createTime', width: 170 }
        ]}
      />
    </Card>
  )
}
''')

print("\n=== All view components generated! ===")
