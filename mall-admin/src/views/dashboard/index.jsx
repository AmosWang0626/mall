import { useState, useEffect } from 'react'
import { Card, Col, Row, Statistic, Table, Spin, Tag } from 'antd'
import { ShoppingOutlined, ShoppingCartOutlined, DollarOutlined, UserOutlined } from '@ant-design/icons'
import { dashboardApi } from '../../api'

const ORDER_STATUS_MAP = {
  0: { text: '待付款', color: 'orange' },
  1: { text: '待发货', color: 'blue' },
  2: { text: '待收货', color: 'cyan' },
  3: { text: '已完成', color: 'green' },
  4: { text: '已取消', color: 'default' },
  5: { text: '退款中', color: 'processing' },
  6: { text: '已退款', color: 'default' },
}

export default function Dashboard() {
  const [loading, setLoading] = useState(true)
  const [data, setData] = useState(null)

  useEffect(() => {
    dashboardApi.stats()
      .then(res => setData(res.data))
      .catch(() => {})
      .finally(() => setLoading(false))
  }, [])

  if (loading) {
    return <div style={{ textAlign: 'center', padding: 100 }}><Spin size="large" /></div>
  }

  const recentOrders = (data?.recentOrders || []).map((order, i) => ({
    ...order,
    key: order.orderNo,
    _index: i + 1,
  }))

  return (
    <div>
      <Row gutter={16} style={{ marginBottom: 24 }}>
        <Col span={6}>
          <Card>
            <Statistic title="商品总数" value={data?.productCount ?? 0} prefix={<ShoppingOutlined />} />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic title="今日订单" value={data?.todayOrderCount ?? 0} prefix={<ShoppingCartOutlined />} valueStyle={{ color: '#3f8600' }} />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic title="今日销售额" value={data?.todaySales ?? 0} prefix={<DollarOutlined />} precision={2} valueStyle={{ color: '#cf1322' }} />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic title="注册用户" value={data?.userCount ?? 0} prefix={<UserOutlined />} />
          </Card>
        </Col>
      </Row>
      <Card title="最近订单">
        <Table
          dataSource={recentOrders}
          pagination={false}
          locale={{ emptyText: '暂无订单' }}
          columns={[
            { title: '#', dataIndex: '_index', width: 50 },
            { title: '订单号', dataIndex: 'orderNo' },
            {
              title: '金额',
              dataIndex: 'payAmount',
              render: val => `¥${(val || 0).toFixed(2)}`,
            },
            {
              title: '状态',
              dataIndex: 'status',
              render: val => {
                const s = ORDER_STATUS_MAP[val]
                return s ? <Tag color={s.color}>{s.text}</Tag> : '-'
              },
            },
          ]}
        />
      </Card>
    </div>
  )
}
