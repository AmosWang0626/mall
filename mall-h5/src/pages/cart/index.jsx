import { useState, useEffect, useMemo } from 'react'
import { useNavigate } from 'react-router-dom'
import { NavBar, Toast, Dialog, Checkbox, Stepper, Button, Popup, Radio, Empty } from 'antd-mobile'
import { cartApi, couponApi } from '../../api'
import './index.css'

const formatPrice = (v) => Number(v || 0).toFixed(2)

const COUPON_TYPE = { 1: '满减', 2: '折扣', 3: '无门槛' }

function calcDiscount(coupon, total) {
  if (!coupon) return 0
  if (coupon.type === 1) {
    if (total >= coupon.minSpend) return Number(coupon.faceValue)
    return 0
  }
  if (coupon.type === 2) {
    return Math.max(0, total - total * Number(coupon.discount))
  }
  if (coupon.type === 3) {
    return Math.min(Number(coupon.faceValue), total)
  }
  return 0
}

export default function Cart() {
  const navigate = useNavigate()
  const [items, setItems] = useState([])
  const [loading, setLoading] = useState(true)
  const [allSelected, setAllSelected] = useState(false)
  const [couponPopup, setCouponPopup] = useState(false)
  const [coupons, setCoupons] = useState([])
  const [selectedCouponId, setSelectedCouponId] = useState(null)

  const loadData = async () => {
    setLoading(true)
    try {
      const [cartRes, couponRes] = await Promise.all([
        cartApi.list(),
        couponApi.mine(0)
      ])
      setItems(cartRes.data || [])
      setCoupons(couponRes.data || [])
    } catch (e) { /* handled */ }
    setLoading(false)
  }

  useEffect(() => { loadData() }, [])

  const selectedItems = useMemo(() => items.filter(i => i.selected === 1), [items])
  const totalAmount = useMemo(() => selectedItems.reduce((s, i) => s + Number(i.price) * i.quantity, 0), [selectedItems])

  const selectedCoupon = useMemo(() => {
    if (!selectedCouponId) return null
    return coupons.find(c => c.id === selectedCouponId)
  }, [selectedCouponId, coupons])

  const usableCoupons = useMemo(() => {
    return coupons.filter(c => {
      const discount = calcDiscount(c.coupon || c, totalAmount)
      return discount > 0
    })
  }, [coupons, totalAmount])

  const discountAmount = useMemo(() => calcDiscount(selectedCoupon?.coupon || selectedCoupon, totalAmount), [selectedCoupon, totalAmount])
  const payAmount = Math.max(0, totalAmount - discountAmount)

  const handleSelectAll = async (checked) => {
    try {
      await cartApi.updateSelected(checked ? 1 : 0)
      setItems(items.map(i => ({ ...i, selected: checked ? 1 : 0 })))
      setAllSelected(checked)
    } catch (e) { /* handled */ }
  }

  const handleSelectItem = async (item) => {
    const newSelected = item.selected === 1 ? 0 : 1
    // Optimistic update
    setItems(items.map(i => i.id === item.id ? { ...i, selected: newSelected } : i))
    // Update via API (updateSelected updates all, so we need a different approach)
    // Actually the API updates all items at once, so we'll handle it client-side
    // and sync on checkout. For now, just update locally.
    // TODO: backend should support per-item selection
  }

  const handleQuantity = async (item, qty) => {
    setItems(items.map(i => i.id === item.id ? { ...i, quantity: qty } : i))
    try { await cartApi.updateQuantity(item.id, qty) } catch (e) { /* handled */ }
  }

  const handleRemove = async (item) => {
    const ok = await Dialog.confirm({ content: '确认删除该商品？' })
    if (!ok) return
    try {
      await cartApi.remove(item.id)
      setItems(items.filter(i => i.id !== item.id))
      Toast.show({ content: '已删除', icon: 'success' })
    } catch (e) { /* handled */ }
  }

  const handleCheckout = () => {
    if (selectedItems.length === 0) {
      Toast.show({ content: '请选择商品' })
      return
    }
    const cartItemIds = selectedItems.map(i => i.id)
    const couponId = selectedCoupon?.id || null
    navigate('/checkout', { state: { cartItemIds, couponId, totalAmount, discountAmount, payAmount } })
  }

  return (
    <div className="page">
      <NavBar back={null}>购物车</NavBar>
      {items.length === 0 && !loading ? (
        <Empty description="购物车空空如也" style={{ padding: 60 }} />
      ) : (
        <>
          {items.map(item => (
            <div key={item.id} className="cart-item">
              <Checkbox
                checked={item.selected === 1}
                onChange={() => handleSelectItem(item)}
              />
              <img src={item.productImage || 'data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 80 80"><rect fill="%23f5f5f5" width="80" height="80"/></svg>'} alt="" />
              <div style={{ flex: 1, minWidth: 0 }}>
                <div style={{ fontSize: 14, fontWeight: 500, overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>{item.productName}</div>
                {item.skuName && <div className="muted" style={{ marginTop: 2 }}>{item.skuName}</div>}
                <div className="flex-between" style={{ marginTop: 8 }}>
                  <span className="price">&#165;{formatPrice(item.price)}</span>
                  <Stepper
                    value={item.quantity}
                    onChange={(v) => handleQuantity(item, v)}
                    min={1}
                    max={99}
                    style={{ '--width': '100px' }}
                  />
                </div>
              </div>
              <span className="cart-delete" onClick={() => handleRemove(item)}>删</span>
            </div>
          ))}

          {/* Coupon selection */}
          <div className="coupon-bar" onClick={() => setCouponPopup(true)}>
            <span>优惠券</span>
            <span className={selectedCoupon ? 'price-small' : 'muted'}>
              {selectedCoupon
                ? `已选: ${(selectedCoupon.coupon || selectedCoupon).name} (-&#165;${formatPrice(discountAmount)})`
                : usableCoupons.length > 0
                  ? `${usableCoupons.length}张可用`
                  : '暂无可用'}
            </span>
          </div>

          <Popup visible={couponPopup} onMaskClick={() => setCouponPopup(false)} bodyStyle={{ borderTopLeftRadius: 8, borderTopRightRadius: 8 }}>
            <div className="coupon-popup">
              <NavBar onBack={() => setCouponPopup(false)}>选择优惠券</NavBar>
              {usableCoupons.length === 0 ? (
                <Empty description="暂无可用优惠券" style={{ padding: 40 }} />
              ) : (
                <>
                  <div className="coupon-option" onClick={() => { setSelectedCouponId(null); setCouponPopup(false) }}>
                    <Radio checked={!selectedCouponId}>不使用优惠券</Radio>
                  </div>
                  {usableCoupons.map(c => {
                    const cp = c.coupon || c
                    const discount = calcDiscount(cp, totalAmount)
                    return (
                      <div key={c.id} className="coupon-option" onClick={() => { setSelectedCouponId(c.id); setCouponPopup(false) }}>
                        <Radio checked={selectedCouponId === c.id}>
                          <div className="coupon-card-h5">
                            <div className="coupon-left">
                              <span className="coupon-value">&#165;{cp.type === 2 ? (Number(cp.discount) * 10).toFixed(1) + '折' : formatPrice(cp.faceValue)}</span>
                              <span className="coupon-type">{COUPON_TYPE[cp.type]}</span>
                            </div>
                            <div className="coupon-right">
                              <div className="coupon-name">{cp.name}</div>
                              <div className="muted">
                                {cp.type === 1 ? `满${formatPrice(cp.minSpend)}可用` : cp.type === 2 ? `满${formatPrice(cp.minSpend)}可用` : '无门槛'}
                                {' · '}优惠 &#165;{formatPrice(discount)}
                              </div>
                            </div>
                          </div>
                        </Radio>
                      </div>
                    )
                  })}
                </>
              )}
            </div>
          </Popup>
        </>
      )}

      {/* Bottom bar */}
      {items.length > 0 && (
        <div className="cart-bottom">
          <Checkbox checked={allSelected} onChange={handleSelectAll}>全选</Checkbox>
          <div className="cart-total">
            <span>合计: </span>
            <span className="price" style={{ fontSize: 18 }}>&#165;{formatPrice(payAmount)}</span>
            {discountAmount > 0 && <span className="muted" style={{ marginLeft: 6 }}>已优惠 &#165;{formatPrice(discountAmount)}</span>}
          </div>
          <Button color="danger" size="large" onClick={handleCheckout} style={{ borderRadius: 20, minWidth: 100 }}>
            结算({selectedItems.length})
          </Button>
        </div>
      )}
    </div>
  )
}
