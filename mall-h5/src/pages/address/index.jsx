import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { NavBar, List, SwipeAction, Dialog, Toast, Form, Input, Switch, Button, Empty } from 'antd-mobile'
import { AddOutline, SetOutline } from 'antd-mobile-icons'
import { addressApi } from '../../api'
import './index.css'

export default function Address() {
  const navigate = useNavigate()
  const [list, setList] = useState([])
  const [loading, setLoading] = useState(false)
  const [editing, setEditing] = useState(null) // null=列表, {}=新增, {id}=编辑
  const [form] = Form.useForm()

  const loadList = async () => {
    setLoading(true)
    try {
      const res = await addressApi.list()
      setList(res.data || [])
    } catch {}
    setLoading(false)
  }

  useEffect(() => { loadList() }, [])

  const handleEdit = (addr) => {
    setEditing(addr || {})
    form.setFieldsValue(addr || {})
  }

  const handleDelete = async (id) => {
    const ok = await Dialog.confirm({ content: '确认删除该地址？' })
    if (!ok) return
    try {
      await addressApi.delete(id)
      Toast.show({ icon: 'success', content: '删除成功' })
      loadList()
    } catch {}
  }

  const handleSubmit = async (values) => {
    const data = { ...editing, ...values, isDefault: values.isDefault ? 1 : 0 }
    try {
      await addressApi.save(data)
      Toast.show({ icon: 'success', content: editing.id ? '修改成功' : '添加成功' })
      setEditing(null)
      form.resetFields()
      loadList()
    } catch {}
  }

  // ===== 编辑/新增表单 =====
  if (editing !== null) {
    return (
      <div className="page">
        <NavBar onBack={() => { setEditing(null); form.resetFields() }}>
          {editing.id ? '编辑地址' : '新增地址'}
        </NavBar>
        <Form
          form={form}
          layout="horizontal"
          onFinish={handleSubmit}
          footer={
            <Button block type="submit" color="danger" size="large">
              保存
            </Button>
          }
        >
          <Form.Item name="receiver" label="收货人" rules={[{ required: true, message: '请输入收货人姓名' }]}>
            <Input placeholder="请输入收货人姓名" clearable />
          </Form.Item>
          <Form.Item name="phone" label="手机号" rules={[
            { required: true, message: '请输入手机号' },
            { pattern: /^1\d{10}$/, message: '手机号格式不正确' }
          ]}>
            <Input placeholder="请输入手机号" clearable type="tel" maxLength={11} />
          </Form.Item>
          <Form.Item name="province" label="省份" rules={[{ required: true, message: '请输入省份' }]}>
            <Input placeholder="如：广东省" clearable />
          </Form.Item>
          <Form.Item name="city" label="城市" rules={[{ required: true, message: '请输入城市' }]}>
            <Input placeholder="如：深圳市" clearable />
          </Form.Item>
          <Form.Item name="district" label="区/县" rules={[{ required: true, message: '请输入区/县' }]}>
            <Input placeholder="如：南山区" clearable />
          </Form.Item>
          <Form.Item name="detail" label="详细地址" rules={[{ required: true, message: '请输入详细地址' }]}>
            <Input placeholder="街道、门牌号等" clearable />
          </Form.Item>
          <Form.Item name="isDefault" label="设为默认地址" childElementPosition="right">
            <Switch />
          </Form.Item>
        </Form>
      </div>
    )
  }

  // ===== 地址列表 =====
  return (
    <div className="page">
      <NavBar onBack={() => navigate('/mine')}>收货地址</NavBar>

      {list.length === 0 && !loading ? (
        <Empty description="暂无收货地址" />
      ) : (
        <List>
          {list.map(addr => (
            <List.Item
              key={addr.id}
              title={
                <div className="addr-item">
                  <div className="addr-header">
                    <span className="addr-name">{addr.receiver}</span>
                    <span className="addr-phone">{addr.phone}</span>
                    {addr.isDefault === 1 && <span className="addr-default">默认</span>}
                  </div>
                  <div className="addr-detail">
                    {addr.province}{addr.city}{addr.district} {addr.detail}
                  </div>
                </div>
              }
              arrow={false}
              swipeAction={[
                {
                  key: 'edit',
                  text: '编辑',
                  color: 'primary',
                  onClick: () => handleEdit(addr)
                },
                {
                  key: 'delete',
                  text: '删除',
                  color: 'danger',
                  onClick: () => handleDelete(addr.id)
                }
              ]}
            />
          ))}
        </List>
      )}

      <div className="addr-add-btn">
        <Button block color="danger" size="large" onClick={() => handleEdit({})}>
          <AddOutline /> 新增收货地址
        </Button>
      </div>
    </div>
  )
}
