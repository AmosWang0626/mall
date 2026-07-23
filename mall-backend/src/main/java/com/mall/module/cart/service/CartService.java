package com.mall.module.cart.service;

import com.mall.common.exception.BusinessException;
import com.mall.module.cart.entity.CartItem;
import com.mall.module.cart.mapper.CartItemMapper;
import com.mall.module.product.entity.Product;
import com.mall.module.product.entity.ProductSku;
import com.mall.module.product.mapper.ProductMapper;
import com.mall.module.product.mapper.ProductSkuMapper;
import com.mall.security.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Service
public class CartService {
    @Autowired private CartItemMapper cartMapper;
    @Autowired private ProductMapper productMapper;
    @Autowired private ProductSkuMapper skuMapper;

    public List<CartItem> list() { return cartMapper.selectByUserId(UserContext.require().getUserId()); }

    @Transactional
    public void add(CartItem item) {
        Long userId = UserContext.require().getUserId();
        item.setUserId(userId);
        // validate product
        Product product = productMapper.selectById(item.getProductId());
        if (product == null || product.getStatus() != 1) throw BusinessException.of("商品不存在或已下架");
        if (item.getSkuId() != null) {
            ProductSku sku = skuMapper.selectById(item.getSkuId());
            if (sku == null) throw BusinessException.of("SKU不存在");
            item.setSkuName(sku.getName());
            item.setPrice(sku.getPrice());
        } else {
            item.setPrice(product.getPrice());
        }
        item.setProductName(product.getName());
        item.setProductImage(product.getMainImage());
        item.setSelected(1);
        // check existing
        CartItem existing = cartMapper.selectByUserAndProduct(userId, item.getProductId(), item.getSkuId());
        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + item.getQuantity());
            cartMapper.updateById(existing);
        } else {
            cartMapper.insert(item);
        }
    }

    @Transactional
    public void updateQuantity(Long id, int quantity) {
        if (quantity <= 0) throw BusinessException.of("数量必须大于0");
        cartMapper.updateQuantity(id, quantity);
    }

    @Transactional
    public void updateSelected(int selected) {
        cartMapper.updateSelected(UserContext.require().getUserId(), selected);
    }

    @Transactional
    public void remove(Long id) { cartMapper.deleteById(id); }

    @Transactional
    public void clear() { cartMapper.clearByUserId(UserContext.require().getUserId()); }

    public int count() { return cartMapper.countByUserId(UserContext.require().getUserId()); }

    public List<CartItem> getSelected() { return cartMapper.selectSelectedByUserId(UserContext.require().getUserId()); }
}
