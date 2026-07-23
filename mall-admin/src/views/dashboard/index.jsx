import { Card, Col, Row, Statistic, Table } from 'antd'
import { ShoppingOutlined, ShoppingCartOutlined, DollarOutlined, UserOutlined } from '@ant-design/icons'

export default function Dashboard() {
  const recentOrders = [
    { key: 1, orderNo: '20240701001', amount: '299.00', status: '已完成' },
    { key: 2, orderNo: '20240701002', amount: '1299.00', status: '待发货' },
    { key: 3, orderNo: '20240701003', amount: '59.00', status: '待付款' },
  ]

  return (
    <div>
      <Row gutter={16} style={{ marginBottom: 24 }}>
        <Col span={6}>
          <Card><Statistic title="商品总数" value={128} prefix={<ShoppingOutlined />} /></Card>
        </Col>
        <Col span={6}>
          <Card><Statistic title="今日订单" value={36} prefix={<ShoppingCartOutlined />} valueStyle={{ color: '#3f8600' }} /></Card>
        </Col>
        <Col span={6}>
          <Card><Statistic title="今日销售额" value={5289} prefix={<DollarOutlined />} precision={2} valueStyle={{ color: '#cf1322' }} /></Card>
        </Col>
        <Col span={6}>
          <Card><Statistic title="注册用户" value={1024} prefix={<UserOutlined />} /></Card>
        </Col>
      </Row>
      <Card title="最近订单">
        <Table
          dataSource={recentOrders}
          pagination={false}
          columns={[
            { title: '订单号', dataIndex: 'orderNo' },
            { title: '金额', dataIndex: 'amount' },
            { title: '状态', dataIndex: 'status' }
          ]}
        />
      </Card>
    </div>
  )
}
