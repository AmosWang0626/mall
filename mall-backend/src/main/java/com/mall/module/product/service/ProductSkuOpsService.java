package com.mall.module.product.service;

import com.mall.common.exception.BusinessException;
import com.mall.module.product.entity.ProductSku;
import com.mall.module.product.mapper.ProductSkuMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class ProductSkuOpsService {

    @Autowired
    private ProductSkuMapper skuMapper;

    @Transactional
    public ProductSku create(ProductSku sku) {
        if (sku.getStatus() == null) sku.setStatus(1);
        if (sku.getStock() == null) sku.setStock(0);
        skuMapper.insert(sku);
        return sku;
    }

    @Transactional
    public void update(Long id, ProductSku sku) {
        ProductSku exist = skuMapper.selectById(id);
        if (exist == null) throw BusinessException.of("SKU不存在");
        sku.setId(id);
        skuMapper.updateById(sku);
    }

    @Transactional
    public void delete(Long id) {
        ProductSku exist = skuMapper.selectById(id);
        if (exist == null) throw BusinessException.of("SKU不存在");
        skuMapper.deleteById(id);
    }
}
