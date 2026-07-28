import { useState, useEffect } from 'react'
import { Card, Table, Button, Input, Space, Tag, Modal, Form, InputNumber, DatePicker, Select, ColorPicker, Popconfirm, message, Tooltip } from 'antd'
import { PlusOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'
import { prizeApi, couponApi } from '../../api'

const statusMap = { 0: { text: '禁用', color: 'default' }, 1: { text: '启用', color: 'green' } }

const presetGradients = [
  { label: '橙红', start: '#ff6034', end: '#ff1744' },
  { label: '紫粉', start: '#a18cd1', end: '#fbc2eb' },
  { label: '蓝青', start: '#4facfe', end: '#00f2fe' },
  { label: '翠绿', start: '#43e97b', end: '#38f9d7' },
  { label: '暖金', start: '#f6d365', end: '#fda085' },
  { label: '靛蓝', start: '#667eea', end: '#764ba2' }
]

export default function PrizePoolPage() {
  const [data, setData] = useState({ list: [], total: 0 })
  const [params, setParams] = useState({ pageNum: 1, pageSize: 10, name: '', status: undefined })
  const [loading, setLoading] = useState(false)
  const [modal, setModal] = useState({ open: false, data: {} })
  const [prizeTypes, setPrizeTypes] = useState([])
  const [coupons, setCoupons] = useState([])
  const [form] = Form.useForm()

  const loadData = async () => {
    setLoading(true)
    try { const res = await prizeApi.list(params); setData(res.data) } catch (e) {}
    setLoading(false)
  }
  useEffect(() => { loadData() }, [params])

  useEffect(() => {
    prizeApi.types().then(res => setPrizeTypes(res.data || [])).catch(() => {})
    couponApi.list({ pageNum: 1, pageSize: 200, status: 1 }).then(res => setCoupons(res.data?.list || [])).catch(() => {})
  }, [])

  const toHex = (v) => (v && typeof v === 'object' && v.toHexString) ? v.toHexString() : (v || undefined)

  const handleSave = async () => {
    const values = await form.validateFields()
    const payload = { ...values }
    if (values.timeRange) {
      payload.startTime = values.timeRange[0]?.format('YYYY-MM-DD HH:mm:ss')
      payload.endTime = values.timeRange[1]?.format('YYYY-MM-DD HH:mm:ss')
    }
    delete payload.timeRange
    const pt = values.prizeType
    if (pt === 'COUPON') {
      payload.prizeRefId = values.couponId ?? null
      payload.prizeValue = null
    } else if (pt === 'POINTS') {
      payload.prizeValue = values.pointsValue ?? null
      payload.prizeRefId = null
    }
    delete payload.couponId
    delete payload.pointsValue
    payload.bannerColor = toHex(values.bannerColor)
    payload.bannerColorEnd = toHex(values.bannerColorEnd)
    if (modal.data.id) payload.id = modal.data.id
    try {
      await prizeApi.save(payload)
      message.success('保存成功')
      setModal({ open: false, data: {} })
      loadData()
    } catch (e) {}
  }

  const openEdit = (record) => {
    const v = { ...record }
    v.timeRange = record.startTime && record.endTime ? [dayjs(record.startTime), dayjs(record.endTime)] : undefined
    if (record.prizeType === 'COUPON') v.couponId = record.prizeRefId
    else if (record.prizeType === 'POINTS') v.pointsValue = record.prizeValue
    form.setFieldsValue(v)
    setModal({ open: true, data: record })
  }

  const openAdd = () => {
    form.resetFields()
    form.setFieldsValue({
      prizeType: (prizeTypes[0]?.type) || 'COUPON', status: 1, sort: 0,
      totalStock: 100, perUserLimit: 1, perUserDailyLimit: 1, dailyLimit: 0,
      bannerColor: '#ff6034', bannerColorEnd: '#ff1744'
    })
    setModal({ open: true, data: {} })
  }

  const toggleStatus = async (record) => {
    await prizeApi.updateStatus(record.id, record.status === 1 ? 0 : 1)
    message.success('状态已更新')
    loadData()
  }

  const handleDelete = async (record) => {
    await prizeApi.delete(record.id)
    message.success('删除成功')
    loadData()
  }

  const couponLabel = (c) => {
    const typeTxt = { 1: '满减', 2: '折扣', 3: '无门槛' }[c.type] || ''
    const valTxt = c.type === 2 ? `${(c.discount * 10).toFixed(1)}折` : `¥${Number(c.faceValue || 0).toFixed(2)}`
    return `${c.name}（${typeTxt} ${valTxt}）`
  }

  const stockText = (r) => r.totalStock === -1 ? `${r.claimedCount} / 不限` : `${r.claimedCount} / ${r.totalStock}`

  const renderPrizeInfo = (r) => {
    const info = r.prizeDisplayInfo
    if (!info) return r.prizeType
    return (
      <div>
        <Tag color={r.prizeType === 'COUPON' ? 'red' : 'orange'}>{info.typeLabel || r.prizeType}</Tag>
        <span style={{ fontSize: 13 }}>
          {info.valueText}{info.valueUnit}
          {info.conditionText && <span style={{ color: '#999', marginLeft: 4 }}>({info.conditionText})</span>}
        </span>
      </div>
    )
  }

  return (
    <Card>
      <div className="search-bar">
        <Input.Search placeholder="奖池名称" onSearch={v => setParams({ ...params, name: v, pageNum: 1 })} style={{ width: 200 }} allowClear />
        <Select
          placeholder="状态" allowClear style={{ width: 120 }}
          options={[{ value: 0, label: '禁用' }, { value: 1, label: '启用' }]}
          onChange={v => setParams({ ...params, status: v, pageNum: 1 })}
        />
        <Button type="primary" icon={<PlusOutlined />} onClick={openAdd}>新增奖池</Button>
      </div>
      <Table rowKey="id" loading={loading} dataSource={data.list} size="middle"
        pagination={{ current: params.pageNum, pageSize: params.pageSize, total: data.total, showSizeChanger: true, onChange: (p, s) => setParams({ ...params, pageNum: p, pageSize: s }) }}
        columns={[
          { title: 'ID', dataIndex: 'id', width: 60 },
          { title: '奖池名称', dataIndex: 'name', width: 160, render: (v, r) => (
            <div>
              <div style={{ fontWeight: 500 }}>{v}</div>
              {r.bannerText && <div style={{ fontSize: 12, color: '#999' }}>{r.bannerText}</div>}
            </div>
          )},
          { title: '奖品', key: 'prize', width: 200, render: (_, r) => renderPrizeInfo(r) },
          { title: '库存', key: 'stock', width: 110, render: (_, r) => stockText(r) },
          { title: '频控', key: 'limits', width: 130, render: (_, r) => (
            <Tooltip title={`每人${r.perUserLimit === 0 ? '不限' : r.perUserLimit}次 / 每人每日${r.perUserDailyLimit === 0 ? '不限' : r.perUserDailyLimit}次 / 每日总量${r.dailyLimit === 0 ? '不限' : r.dailyLimit}`}>
              <span style={{ fontSize: 12 }}>
                人{r.perUserLimit === 0 ? '∞' : r.perUserLimit} / 日{r.perUserDailyLimit === 0 ? '∞' : r.perUserDailyLimit} / 总{r.dailyLimit === 0 ? '∞' : r.dailyLimit}
              </span>
            </Tooltip>
          )},
          { title: '时间', key: 'time', width: 180, render: (_, r) => (
            <div style={{ fontSize: 12 }}>
              <div>{r.startTime?.replace('T', ' ').substring(0, 16)}</div>
              <div>{r.endTime?.replace('T', ' ').substring(0, 16)}</div>
            </div>
          )},
          { title: '状态', dataIndex: 'status', width: 80, render: v => { const s = statusMap[v] || statusMap[0]; return <Tag color={s.color}>{s.text}</Tag> } },
          { title: '排序', dataIndex: 'sort', width: 60 },
          { title: '操作', width: 160, fixed: 'right', render: (_, r) => (
            <Space>
              <Button size="small" onClick={() => openEdit(r)}>编辑</Button>
              <Button size="small" onClick={() => toggleStatus(r)}>{r.status === 1 ? '停用' : '启用'}</Button>
              <Popconfirm title="确认删除该奖池？" onConfirm={() => handleDelete(r)}>
                <Button size="small" danger>删除</Button>
              </Popconfirm>
            </Space>
          )}
        ]}
      />

      <Modal title={modal.data.id ? '编辑奖池' : '新增奖池'} open={modal.open} onOk={handleSave} onCancel={() => setModal({ open: false, data: {} })} width={720} destroyOnClose>
        <Form form={form} layout="vertical">
          <Form.Item name="name" label="奖池名称" rules={[{ required: true, message: '请输入奖池名称' }]}>
            <Input placeholder="如：新人专享优惠券" maxLength={50} />
          </Form.Item>
          <Form.Item name="description" label="奖池描述">
            <Input.TextArea rows={2} placeholder="活动说明（选填）" maxLength={200} />
          </Form.Item>

          <div style={{ fontWeight: 600, margin: '8px 0', color: '#1677ff' }}>奖品配置</div>
          <Form.Item name="prizeType" label="奖品类型" rules={[{ required: true }]}>
            <Select options={prizeTypes.map(t => ({ value: t.type, label: t.displayName }))} placeholder="选择奖品类型" />
          </Form.Item>

          <Form.Item shouldUpdate={(prev, cur) => prev.prizeType !== cur.prizeType} noStyle>
            {({ getFieldValue }) => {
              const pt = getFieldValue('prizeType')
              if (pt === 'COUPON') {
                return (
                  <Form.Item name="couponId" label="关联优惠券模板" rules={[{ required: true, message: '请选择优惠券' }]}>
                    <Select showSearch optionFilterProp="label" placeholder="选择优惠券模板" options={coupons.map(c => ({ value: c.id, label: couponLabel(c) }))} />
                  </Form.Item>
                )
              }
              if (pt === 'POINTS') {
                return (
                  <Form.Item name="pointsValue" label="积分数量" rules={[{ required: true, message: '请输入积分数量' }]}>
                    <InputNumber min={1} max={100000} style={{ width: '100%' }} placeholder="如：50" />
                  </Form.Item>
                )
              }
              return null
            }}
          </Form.Item>

          <Space style={{ display: 'flex' }}>
            <Form.Item name="prizeName" label="奖品展示名（选填）" style={{ flex: 1 }}>
              <Input placeholder="留空则自动生成" />
            </Form.Item>
            <Form.Item name="prizeDesc" label="奖品展示描述（选填）" style={{ flex: 1 }}>
              <Input placeholder="如：有效期30天" />
            </Form.Item>
          </Space>

          <div style={{ fontWeight: 600, margin: '8px 0', color: '#1677ff' }}>库存与频控</div>
          <Space style={{ display: 'flex', flexWrap: 'wrap' }}>
            <Form.Item name="totalStock" label="总库存" rules={[{ required: true }]} tooltip="-1 表示不限库存">
              <InputNumber min={-1} max={999999} style={{ width: 140 }} />
            </Form.Item>
            <Form.Item name="perUserLimit" label="每人限领" tooltip="0 表示不限">
              <InputNumber min={0} max={999} style={{ width: 140 }} />
            </Form.Item>
            <Form.Item name="perUserDailyLimit" label="每人每日限领" tooltip="0 表示不限">
              <InputNumber min={0} max={999} style={{ width: 140 }} />
            </Form.Item>
            <Form.Item name="dailyLimit" label="每日总量" tooltip="0 表示不限">
              <InputNumber min={0} max={999999} style={{ width: 140 }} />
            </Form.Item>
          </Space>

          <div style={{ fontWeight: 600, margin: '8px 0', color: '#1677ff' }}>时间窗口</div>
          <Form.Item name="timeRange" label="活动时间" rules={[{ required: true, message: '请选择活动时间' }]}>
            <DatePicker.RangePicker showTime style={{ width: '100%' }} />
          </Form.Item>

          <div style={{ fontWeight: 600, margin: '8px 0', color: '#1677ff' }}>首页 Banner</div>
          <Form.Item name="bannerText" label="Banner 文案">
            <Input placeholder="如：新人专享 满100减20" maxLength={30} />
          </Form.Item>
          <Space style={{ display: 'flex' }}>
            <Form.Item name="bannerColor" label="Banner 起始色">
              <ColorPicker showText format="hex" />
            </Form.Item>
            <Form.Item name="bannerColorEnd" label="Banner 结束色">
              <ColorPicker showText format="hex" />
            </Form.Item>
          </Space>
          <div style={{ marginBottom: 16 }}>
            <span style={{ fontSize: 12, color: '#999', marginRight: 8 }}>预设配色：</span>
            {presetGradients.map(g => (
              <span key={g.label} onClick={() => form.setFieldsValue({ bannerColor: g.start, bannerColorEnd: g.end })}
                style={{ display: 'inline-block', width: 40, height: 20, marginRight: 6, borderRadius: 4, cursor: 'pointer',
                  background: `linear-gradient(135deg, ${g.start}, ${g.end})` }} title={g.label} />
            ))}
          </div>

          <Space style={{ display: 'flex' }}>
            <Form.Item name="status" label="状态" rules={[{ required: true }]}>
              <Select options={[{ value: 0, label: '禁用' }, { value: 1, label: '启用' }]} style={{ width: 140 }} />
            </Form.Item>
            <Form.Item name="sort" label="排序" tooltip="数值越大越靠前">
              <InputNumber min={0} max={999} style={{ width: 140 }} />
            </Form.Item>
          </Space>
        </Form>
      </Modal>
    </Card>
  )
}
