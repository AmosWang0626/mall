import { useState, useEffect } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { NavBar, Toast, Stepper, Button, Dialog } from 'antd-mobile'
import { productApi, cartApi } from '../../api'
import { useAuthStore } from '../../store'
import './detail.css'

const formatPrice = (v) => Number(v || 0).toFixed(2)

export default function ProductDetail() {
  const { id } = useParams()
  const navigate = useNavigate()
  const { token, setCartCount } = useAuthStore()
  const [product, setProduct] = useState(null)
  const [skus, setSkus] = useState([])
  const [selectedSku, setSelectedSku] = useState(null)
  const [quantity, setQuantity] = useState(1)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    productApi.detail(id).then(res => {
      setProduct(res.data)
      setSkus(res.data?.skuList || [])
      if (res.data?.skuList?.length > 0) setSelectedSku(res.data.skuList[0])
    }).finally(() => setLoading(false))
  }, [id])

  const handleAddCart = async () => {
    if (!token) { navigate('/login'); return }
    try {
      await cartApi.add({
        productId: product.id,
        skuId: selectedSku?.id,
        productName: product.name,
        productImage: product.mainImage,
        skuName: selectedSku?.skuName || '',
        price: selectedSku?.price || product.price,
        quantity,
        selected: 1
      })
      Toast.show({ content: '已加入购物车', icon: 'success' })
      const countRes = await cartApi.count()
      setCartCount(countRes.data || 0)
    } catch (e) { /* handled */ }
  }

  const handleBuyNow = async () => {
    if (!token) { navigate('/login'); return }
    await handleAddCart()
    navigate('/cart')
  }

  if (loading) return <div style={{ padding: 40, textAlign: 'center', color: '#999' }}>加载中...</div>
  if (!product) return <div style={{ padding: 40, textAlign: 'center', color: '#999' }}>商品不存在</div>

  const currentPrice = selectedSku?.price || product.price

  return (
    <div className="page">
      <NavBar onBack={() => navigate(-1)}>{product.name}</NavBar>
      <img src={product.mainImage || 'data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 400 400"><rect fill="%23f5f5f5" width="400" height="400"/></svg>'} alt={product.name} style={{ width: '100%', aspectRatio: 1, objectFit: 'cover' }} />
      <div className="detail-info">
        <div className="detail-price">&#165;{formatPrice(currentPrice)}</div>
        <div className="detail-name">{product.name}</div>
        {product.subtitle && <div className="detail-subtitle">{product.subtitle}</div>}
        <div className="detail-stats">
          <span>销量 {product.sales || 0}</span>
          <span>库存 {selectedSku?.stock || product.stock || 0}</span>
        </div>
      </div>
      {skus.length > 0 && (
        <div className="section" style={{ padding: 12 }}>
          <div style={{ fontWeight: 600, marginBottom: 10 }}>规格选择</div>
          <div className="sku-list">
            {skus.map(sku => (
              <div
                key={sku.id}
                className={`sku-tag ${selectedSku?.id === sku.id ? 'active' : ''}`}
                onClick={() => setSelectedSku(sku)}
              >
                {sku.skuName}
              </div>
            ))}
          </div>
        </div>
      )}
      <div className="section" style={{ padding: 12, display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
        <span style={{ fontWeight: 600 }}>购买数量</span>
        <Stepper value={quantity} onChange={setQuantity} min={1} max={selectedSku?.stock || 99} />
      </div>
      <div className="section" style={{ padding: 12 }}>
        <div style={{ fontWeight: 600, marginBottom: 8 }}>商品详情</div>
        <div style={{ fontSize: 14, lineHeight: 1.8, color: '#666', whiteSpace: 'pre-wrap' }}>
          {product.description || '暂无详情'}
        </div>
      </div>
      <div style={{ height: 60 }} />
      <div className="detail-bottom">
        <Button color="warning" onClick={handleAddCart} style={{ flex: 1 }}>加入购物车</Button>
        <Button color="danger" onClick={handleBuyNow} style={{ flex: 1 }}>立即购买</Button>
      </div>
    </div>
  )
}
