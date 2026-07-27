import { useState, useEffect, useCallback } from 'react'
import { useNavigate } from 'react-router-dom'
import { NavBar, Toast, Dialog, Empty, SpinLoading } from 'antd-mobile'
import { prizeApi } from '../../api'
import { useAuthStore } from '../../store'
import './index.css'

/**
 * 奖品卡片左侧展示（根据 prizeDisplayInfo 渲染）
 * 支持优惠券/积分等多种奖品类型
 */
function PrizeCardLeft({ pool }) {
  const info = pool.prizeDisplayInfo
  if (!info) {
    return (
      <div className="prize-card-left">
        <span className="coupon-type-tag">奖品</span>
        <div className="coupon-value-num">--</div>
      </div>
    )
  }

  // 积分类型用橙色渐变, 优惠券用红色渐变
  const gradient = pool.prizeType === 'POINTS'
    ? 'linear-gradient(135deg, #fa8c16, #faad14)'
    : 'linear-gradient(135deg, #ff4d4f, #ff7a45)'

  return (
    <div className="prize-card-left" style={{ background: gradient }}>
      <span className="coupon-type-tag">{info.typeLabel}</span>
      <div className="prize-card-value">
        <span className="coupon-value-num">{info.valueText}<span className="coupon-value-unit">{info.valueUnit}</span></span>
      </div>
      {info.conditionText && (
        <div className="prize-card-condition">{info.conditionText}</div>
      )}
    </div>
  )
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
      // PrizeResult: { prizeType, prizeTypeName, prizeName, displayValue, redirectUrl, remark }
      const emoji = d.prizeType === 'POINTS' ? '🎁' : '🎉'
      Dialog.alert({
        title: '领取成功',
        content: (
          <div style={{ textAlign: 'center', padding: '12px 0' }}>
            <div style={{ fontSize: 40, marginBottom: 8 }}>{emoji}</div>
            <div style={{ fontSize: 16, fontWeight: 600, marginBottom: 4 }}>{d.prizeName}</div>
            <div style={{ color: '#ff4d4f', fontSize: 20, fontWeight: 700 }}>{d.displayValue}</div>
            <div style={{ fontSize: 13, color: '#999', marginTop: 8 }}>{d.remark || '已发放到您的账户'}</div>
          </div>
        ),
        confirmText: '去查看',
        onConfirm: () => navigate(d.redirectUrl || '/mine')
      })
      // 刷新列表更新领取状态
      loadData()
    } catch (e) { /* toast handled in interceptor */ }
    setClaiming(null)
  }

  /** 判断领取按钮状态 */
  const getBtnState = (pool) => {
    if (!pool.prizeDisplayInfo) return { text: '暂无奖品', disabled: true }
    const remaining = pool.remainingStock
    if (remaining === 0) return { text: '已抢光', disabled: true }
    if (pool.perUserLimit > 0 && pool.userClaimedCount >= pool.perUserLimit) {
      return { text: `已领${pool.userClaimedCount}/${pool.perUserLimit}次`, disabled: true }
    }
    return {
      text: pool.userClaimedCount > 0 ? '再领一次' : '立即领取',
      disabled: false
    }
  }

  return (
    <div className="page prize-page">
      <NavBar onBack={() => navigate(-1)}>🎁 奖品中心</NavBar>

      {loading ? (
        <div style={{ textAlign: 'center', padding: 60 }}><SpinLoading /></div>
      ) : pools.length === 0 ? (
        <Empty description="暂无可领取的奖品" style={{ padding: 60 }} />
      ) : (
        <div className="prize-list">
          {pools.map(pool => {
            const btn = getBtnState(pool)
            const remaining = pool.remainingStock
            return (
              <div key={pool.id} className="prize-card">
                {/* 左侧奖品面 */}
                <PrizeCardLeft pool={pool} />

                {/* 右侧信息 */}
                <div className="prize-card-right">
                  <div className="prize-card-name">{pool.name}</div>
                  <div className="prize-card-desc">{pool.description}</div>
                  <div className="prize-card-meta">
                    {pool.perUserLimit > 0 && (
                      <span className="meta-tag">限领{pool.perUserLimit}次</span>
                    )}
                    {pool.perUserDailyLimit > 0 && (
                      <span className="meta-tag">每日{pool.perUserDailyLimit}次</span>
                    )}
                    {pool.dailyLimit > 0 && (
                      <span className="meta-tag">日限{pool.dailyLimit}次</span>
                    )}
                  </div>
                  <div className="prize-card-stock">
                    {remaining === -1 ? (
                      <span className="stock-infinite">库存充足</span>
                    ) : remaining === 0 ? (
                      <span className="stock-out">已抢光</span>
                    ) : (
                      <span className="stock-num">剩余 <b>{remaining}</b> 份</span>
                    )}
                    {pool.userClaimedCount > 0 && (
                      <span className="stock-claimed">已领{pool.userClaimedCount}次</span>
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
        <div className="tips-title">💡 领取说明</div>
        <ul>
          <li>每个奖池有独立的领取限制, 请在有效期内领取</li>
          <li>优惠券领取后可在「我的 - 优惠券」中查看</li>
          <li>积分领取后可在「我的 - 积分」中查看</li>
          <li>如遇"手慢了"提示, 说明该奖品已被其他用户抢完</li>
          <li>每日限领的奖池, 次日0点刷新领取次数</li>
        </ul>
      </div>
    </div>
  )
}
