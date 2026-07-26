import { useState, useEffect, useRef } from 'react'
import { useNavigate } from 'react-router-dom'
import { SearchBar, InfiniteScroll, Toast } from 'antd-mobile'
import { productApi, cartApi, prizeApi } from '../../api'
import { useAuthStore } from '../../store'
import './index.css'

const formatPrice = (v) => Number(v || 0).toFixed(2)

export default function Home() {
  const navigate = useNavigate()
  const { token, setCartCount } = useAuthStore()
  const [list, setList] = useState([])
  const [keyword, setKeyword] = useState('')
  const [page, setPage] = useState(1)
  const [hasMore, setHasMore] = useState(true)
  const [banners, setBanners] = useState([])
  const bannerIdx = useRef(0)
  const bannerTimer = useRef(null)

  // 加载营销Banner
  useEffect(() => {
    prizeApi.banners().then(res => {
      setBanners(res.data || [])
      if ((res.data || []).length > 1) {
        bannerTimer.current = setInterval(() => {
          bannerIdx.current = (bannerIdx.current + 1) % (res.data || []).length
          const track = document.querySelector('.banner-track')
          if (track) track.style.transform = `translateX(-${bannerIdx.current * 100}%)`
        }, 3500)
      }
    }).catch(() => {})
    return () => { if (bannerTimer.current) clearInterval(bannerTimer.current) }
  }, [])

  const handleBannerClick = (pool) => {
    if (!token) { navigate('/login'); return }
    navigate('/prize')
  }

  const loadMore = async () => {
    const res = await productApi.list({ pageNum: page, pageSize: 10, keyword })
    const items = res.data?.list || []
    setList(prev => page === 1 ? items : [...prev, ...items])
    setHasMore(items.length >= 10)
    setPage(p => p + 1)
  }

  const handleSearch = () => {
    setList([])
    setPage(1)
    setHasMore(true)
  }

  const handleAddCart = async (e, product) => {
    e.stopPropagation()
    if (!token) { navigate('/login'); return }
    try {
      await cartApi.add({
        productId: product.id,
        productName: product.name,
        productImage: product.mainImage,
        price: product.price,
        quantity: 1,
        selected: 1
      })
      Toast.show({ content: '已加入购物车', icon: 'success' })
      const countRes = await cartApi.count()
      setCartCount(countRes.data || 0)
    } catch (e) { /* handled */ }
  }

  return (
    <div className="page">
      <div className="home-header">
        <SearchBar
          placeholder="搜索商品"
          value={keyword}
          onChange={setKeyword}
          onSearch={handleSearch}
          onClear={handleSearch}
          style={{ flex: 1 }}
        />
      </div>

      {/* 营销Banner轮播 */}
      {banners.length > 0 ? (
        <div className="banner-carousel">
          <div className="banner-track">
            {banners.map(b => (
              <div
                key={b.id}
                className="banner-slide"
                style={{ background: `linear-gradient(135deg, ${b.bannerColor || '#ff4d4f'}, ${b.bannerColorEnd || '#ff7a45'})` }}
                onClick={() => handleBannerClick(b)}
              >
                <div className="banner-slide-content">
                  <div className="banner-slide-title">{b.bannerText || b.name}</div>
                  <div className="banner-slide-desc">{b.description || '点击立即领取'}</div>
                  <div className="banner-slide-btn">立即领取 &rsaquo;</div>
                </div>
                <div className="banner-slide-icon">&#127873;</div>
              </div>
            ))}
          </div>
          {banners.length > 1 && (
            <div className="banner-dots">
              {banners.map((_, i) => (
                <span key={i} className="banner-dot" />
              ))}
            </div>
          )}
        </div>
      ) : (
        <div className="banner">
          <div className="banner-text">
            <div style={{ fontSize: 22, fontWeight: 700 }}>欢迎光临</div>
            <div style={{ fontSize: 13, opacity: 0.9, marginTop: 4 }}>精选好物 · 品质保证</div>
          </div>
        </div>
      )}

      <div className="product-grid">
        {list.map(item => (
          <div key={item.id} className="product-card" onClick={() => navigate(`/product/${item.id}`)}>
            <img src={item.mainImage || 'data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 200 200"><rect fill="%23f0f0f0" width="200" height="200"/><text x="50%25" y="50%25" font-size="14" fill="%23ccc" text-anchor="middle" dy=".3em">暂无图片</text></svg>'} alt={item.name} />
            <div className="info">
              <div className="name">{item.name}</div>
              <div className="flex-between" style={{ marginTop: 6 }}>
                <span className="price">¥{formatPrice(item.price)}</span>
                <span
                  className="add-cart-btn"
                  onClick={(e) => handleAddCart(e, item)}
                >+</span>
              </div>
            </div>
          </div>
        ))}
      </div>
      <InfiniteScroll loadMore={loadMore} hasMore={hasMore} />
    </div>
  )
}
