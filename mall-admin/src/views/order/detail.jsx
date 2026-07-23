import { useState, useEffect } from 'react'
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
