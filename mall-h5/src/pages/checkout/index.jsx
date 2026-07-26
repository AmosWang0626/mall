import { useState, useEffect } from 'react'
import { useNavigate, useLocation } from 'react-router-dom'
import { NavBar, Toast, Button, Dialog, TextArea, Radio } from 'antd-mobile'
import { orderApi, addressApi } from '../../api'
import './index.css'

const formatPrice = (v) => Number(v || 0).toFixed(2)

export default function Checkout() {
  const navigate = useNavigate()
  const location = useLocation()
  const { cartItemIds, couponId, totalAmount, discountAmount, payAmount } = location.state || {}
  const [address, setAddress] = useState(null)
  const [remark, setRemark] = useState('')
  const [payType, setPayType] = useState(1)
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    addressApi.getDefault().then(res => setAddress(res.data)).catch(() => {})
  }, [])

  if (!cartItemIds) {
    navigate('/cart')
    return null
  }

  const handleSubmit = async () => {
    if (!address) {
      Toast.show({ content: '请先添加收货地址' })
      return
    }
    setLoading(true)
    try {
      const res = await orderApi.create({
        cartItemIds,
        addressId: address.id,
        couponId: couponId || null,
        remark,
        payType
      })
      Toast.show({ content: '下单成功', icon: 'success' })
      navigate(`/order/${res.data.id}`, { replace: true })
    } catch (e) { /* handled */ }
    setLoading(false)
  }

  return (
    <div className="page">
      <NavBar onBack={() => navigate(-1)}>确认订单</NavBar>

      {/* Address */}
      <div className="section checkout-address" onClick={() => navigate('/address')}>
        {address ? (
          <>
            <div className="addr-name">{address.receiver} {address.phone}</div>
            <div className="addr-detail">{address.province}{address.city}{address.district}{address.detail}</div>
          </>
        ) : (
          <div className="addr-empty">点击添加收货地址</div>
        )}
      </div>

      {/* Amount summary */}
      <div className="section" style={{ padding: 16 }}>
        <div className="checkout-row">
          <span>商品总额</span>
          <span>¥{formatPrice(totalAmount)}</span>
        </div>
        <div className="checkout-row">
          <span>优惠券抵扣</span>
          <span className="price-small">-¥{formatPrice(discountAmount)}</span>
        </div>
        <div className="checkout-row" style={{ borderBottom: 'none' }}>
          <span style={{ fontWeight: 600 }}>实付金额</span>
          <span className="price" style={{ fontSize: 18 }}>¥{formatPrice(payAmount)}</span>
        </div>
      </div>

      {/* Pay type */}
      <div className="section" style={{ padding: 16 }}>
        <div style={{ fontWeight: 600, marginBottom: 12 }}>支付方式</div>
        <Radio.Group value={payType} onChange={setPayType}>
          <div className="pay-option">
            <Radio value={1}>微信支付</Radio>
          </div>
          <div className="pay-option">
            <Radio value={2}>支付宝</Radio>
          </div>
          <div className="pay-option">
            <Radio value={3}>货到付款</Radio>
          </div>
        </Radio.Group>
      </div>

      {/* Remark */}
      <div className="section" style={{ padding: 16 }}>
        <div style={{ fontWeight: 600, marginBottom: 8 }}>订单备注</div>
        <TextArea placeholder="选填，请输入备注信息" value={remark} onChange={setRemark} maxLength={100} rows={2} />
      </div>

      <div style={{ height: 70 }} />
      <div className="checkout-bottom">
        <div>
          <span>实付: </span>
          <span className="price" style={{ fontSize: 20 }}>¥{formatPrice(payAmount)}</span>
        </div>
        <Button color="danger" size="large" loading={loading} onClick={handleSubmit} style={{ borderRadius: 20, minWidth: 120 }}>
          提交订单
        </Button>
      </div>
    </div>
  )
}
