import { useState, useEffect, useCallback } from 'react'
import { useNavigate } from 'react-router-dom'
import { NavBar, Toast, Dialog, Empty, SpinLoading } from 'antd-mobile'
import { prizeApi } from '../../api'
import { useAuthStore } from '../../store'
import './index.css'

const formatPrice = (v) => Number(v || 0).toFixed(2)

/** 优惠券类型标签 */
function CouponTypeTag({ type }) {
  const map = { 1: '满减券', 2: '折扣券', 3: '无门槛券' }
  return <span className="coupon-type-tag">{map[type] || '优惠券'}</span>
}

/** 优惠券面值展示 */
function CouponValue({ coupon }) {
  if (!coupon) return null
  if (coupon.type === 2) {
    // 折扣券
    return <span className="coupon-value-num">{(Number(coupon.discount) * 10).toFixed(1)}<span className="coupon-value-unit">折</span></span>
  }
  // 满减 / 无门槛
  return <span className="coupon-value-num">{formatPrice(coupon.faceValue)}<span className="coupon-value-unit">元</span></span>
}

/** 优惠券使用条件 */
function CouponCondition({ coupon }) {
  if (!coupon) return null
  if (coupon.type === 1) return `满${formatPrice(coupon.minSpend)}元可用`
  if (coupon.type === 2) return `无门槛, 最多${formatPrice(coupon.minSpend)}元`
  return '无门槛'
}

export default function PrizeCenter() {
  const navigate = useNavigate()
  const { token } = useAuthStore()
  const [pools, setPools] = useState([])
  const [loading, setLoading] = useState(true)
  const [claiming, setClaiming] = useState(null)

  const loadData = useCallback(async () => {
    setLoading(true)
    try {
      const res = await prizeApi.list()
      setPools(res.data || [])
    } catch (e) { /* handled */ }
    setLoading(false)
  }, [])

  useEffect(() => { if (token) loadData() }, [token, loadData])

  const handleClaim = async (pool) => {
    if (!token) { navigate('/login'); return }
    setClaiming(pool.id)
    try {
      const res = await prizeApi.claim(pool.id)
      const d = res.data
      const valueText = d.couponType === 2
        ? `${(Number(d.discount) * 10).toFixed(1)}折`
        : `${formatPrice(d.faceValue)}元`
      Dialog.alert({
        title: '领取成功',
        content: (
          <div style={{ textAlign: 'center', padding: '12px 0' }}>
            <div style={{ fontSize: 40, marginBottom: 8 }}>&#127881;</div>
            <div style={{ fontSize: 16, fontWeight: 600, marginBottom: 4 }}>{d.couponName}</div>
            <div style={{ color: '#ff4d4f', fontSize: 20, fontWeight: 700 }}>{valueText}</div>
            <div style={{ fontSize: 13, color: '#999', marginTop: 8 }}>优惠券已放入您的账户</div>
          </div>
        ),
        confirmText: '去查看',
        onConfirm: () => navigate('/coupons')
      })
      // 刷新列表更新领取状态
      loadData()
    } catch (e) { /* toast handled in interceptor */ }
    setClaiming(null)
  }

  /** 判断领取按钮状态 */
  const getBtnState = (pool) => {
    if (!pool.couponTemplate) return { text: '暂无优惠券', disabled: true }
    const remaining = pool.remainingStock
    if (remaining === 0) return { text: '已抢光', disabled: true }
    if (pool.perUserLimit > 0 && pool.userClaimedCount >= pool.perUserLimit) {
      return { text: `已领${pool.userClaimedCount}/${pool.perUserLimit}次`, disabled: true }
    }
    return {
      text: pool.userClaimedCount > 0 ? '再领一张' : '立即领取',
      disabled: false
    }
  }

  return (
    <div className="page prize-page">
      <NavBar onBack={() => navigate(-1)}>&#127873; 领券中心</NavBar>

      {loading ? (
        <div style={{ textAlign: 'center', padding: 60 }}><SpinLoading /></div>
      ) : pools.length === 0 ? (
        <Empty description="暂无可领取的优惠券" style={{ padding: 60 }} />
      ) : (
        <div className="prize-list">
          {pools.map(pool => {
            const btn = getBtnState(pool)
            const ct = pool.couponTemplate
            const remaining = pool.remainingStock
            return (
              <div key={pool.id} className="prize-card">
                {/* 左侧券面 */}
                <div className="prize-card-left">
                  <CouponTypeTag type={ct?.type} />
                  <div className="prize-card-value">
                    <CouponValue coupon={ct} />
                  </div>
                  <div className="prize-card-condition">
                    <CouponCondition coupon={ct} />
                  </div>
                </div>

                {/* 右侧信息 */}
                <div className="prize-card-right">
                  <div className="prize-card-name">{pool.name}</div>
                  <div className="prize-card-desc">{pool.description}</div>
                  <div className="prize-card-meta">
                    {pool.perUserLimit > 0 && (
                      <span className="meta-tag">限领{pool.perUserLimit}张</span>
                    )}
                    {pool.perUserDailyLimit > 0 && (
                      <span className="meta-tag">每日{pool.perUserDailyLimit}张</span>
                    )}
                    {pool.dailyLimit > 0 && (
                      <span className="meta-tag">日限{pool.dailyLimit}张</span>
                    )}
                  </div>
                  <div className="prize-card-stock">
                    {remaining === -1 ? (
                      <span className="stock-infinite">库存充足</span>
                    ) : remaining === 0 ? (
                      <span className="stock-out">已抢光</span>
                    ) : (
                      <span className="stock-num">剩余 <b>{remaining}</b> 张</span>
                    )}
                    {pool.userClaimedCount > 0 && (
                      <span className="stock-claimed">已领{pool.userClaimedCount}张</span>
                    )}
                  </div>
                  <button
                    className={`prize-claim-btn ${btn.disabled ? 'disabled' : ''}`}
                    disabled={btn.disabled || claiming === pool.id}
                    onClick={() => handleClaim(pool)}
                  >
                    {claiming === pool.id ? '领取中...' : btn.text}
                  </button>
                </div>

                {/* 装饰波浪 */}
                <div className="prize-card-wave" />
              </div>
            )
          })}
        </div>
      )}

      <div className="prize-tips">
        <div className="tips-title">&#128161; 领券说明</div>
        <ul>
          <li>每个奖池有独立的领取限制, 请在有效期内领取</li>
          <li>优惠券领取后可在「我的 - 优惠券」中查看</li>
          <li>如遇"手慢了"提示, 说明该优惠券已被其他用户抢完</li>
          <li>每日限领的奖池, 次日0点刷新领取次数</li>
        </ul>
      </div>
    </div>
  )
}
