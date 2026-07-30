package com.mall.module.product.service;

import com.mall.common.exception.BusinessException;
import com.mall.module.product.entity.ProductSku;
import com.mall.module.product.mapper.ProductSkuMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 商品SKU服务 — C端查询 + 库存操作
 */
@Slf4j
@Service
public class ProductSkuService {

    @Autowired
    private ProductSkuMapper skuMapper;

    public List<ProductSku> listByProductId(Long productId) {
        return skuMapper.selectByProductId(productId);
    }

    public ProductSku getById(Long id) {
        ProductSku sku = skuMapper.selectById(id);
        if (sku == null) throw BusinessException.of("SKU不存在");
        return sku;
    }
}
