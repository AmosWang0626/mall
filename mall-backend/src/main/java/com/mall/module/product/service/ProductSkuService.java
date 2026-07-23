package com.mall.module.product.service;

import com.mall.common.exception.BusinessException;
import com.mall.module.product.entity.ProductSku;
import com.mall.module.product.mapper.ProductSkuMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 商品SKU服务
 */
@Slf4j
@Service
public class ProductSkuService {

    @Autowired
    private ProductSkuMapper skuMapper;

    /**
     * 按商品ID查询SKU列表
     */
    public List<ProductSku> listByProductId(Long productId) {
        return skuMapper.selectByProductId(productId);
    }

    /**
     * 根据ID查询SKU
     */
    public ProductSku getById(Long id) {
        ProductSku sku = skuMapper.selectById(id);
        if (sku == null) {
            throw BusinessException.of("SKU不存在");
        }
        return sku;
    }

    /**
     * 新增SKU
     */
    @Transactional
    public ProductSku create(ProductSku sku) {
        if (sku.getStatus() == null) {
            sku.setStatus(1);
        }
        if (sku.getStock() == null) {
            sku.setStock(0);
        }
        skuMapper.insert(sku);
        return sku;
    }

    /**
     * 更新SKU
     */
    @Transactional
    public void update(Long id, ProductSku sku) {
        ProductSku exist = skuMapper.selectById(id);
        if (exist == null) {
            throw BusinessException.of("SKU不存在");
        }
        sku.setId(id);
        skuMapper.updateById(sku);
    }

    /**
     * 删除SKU
     */
    @Transactional
    public void delete(Long id) {
        ProductSku exist = skuMapper.selectById(id);
        if (exist == null) {
            throw BusinessException.of("SKU不存在");
        }
        skuMapper.deleteById(id);
    }

    /**
     * 扣减SKU库存
     * @param skuId SKU ID
     * @param quantity 扣减数量
     * @return true=成功, false=库存不足
     */
    public boolean reduceStock(Long skuId, int quantity) {
        if (quantity <= 0) {
            throw BusinessException.of("扣减数量必须大于0");
        }
        int rows = skuMapper.reduceStock(skuId, quantity);
        return rows > 0;
    }

    /**
     * 恢复SKU库存
     */
    public void restoreStock(Long skuId, int quantity) {
        if (quantity <= 0) {
            throw BusinessException.of("恢复数量必须大于0");
        }
        skuMapper.restoreStock(skuId, quantity);
    }
}
