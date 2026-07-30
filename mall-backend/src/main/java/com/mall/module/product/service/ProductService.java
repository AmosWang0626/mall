package com.mall.module.product.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mall.common.PageResult;
import com.mall.common.exception.BusinessException;
import com.mall.module.product.dto.ProductQueryDTO;
import com.mall.module.product.entity.Product;
import com.mall.module.product.entity.ProductSku;
import com.mall.module.product.mapper.ProductMapper;
import com.mall.module.product.mapper.ProductSkuMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 商品服务 — C端公开接口 + 库存操作
 */
@Slf4j
@Service
public class ProductService {

    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ProductSkuMapper skuMapper;

    /** 公开 - 商品列表 (仅上架) */
    public PageResult<Product> getPublicList(ProductQueryDTO query) {
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        List<Product> list = productMapper.selectList(
                query.getCategoryId(), query.getName(), 1, query.getTags());
        PageInfo<Product> pageInfo = new PageInfo<>(list);
        return PageResult.of(pageInfo.getList(), pageInfo.getTotal(), query.getPageNum(), query.getPageSize());
    }

    /** 公开 - 商品详情 (仅上架, 含 SKU) */
    public Product getPublicDetail(Long id) {
        Product product = productMapper.selectById(id);
        if (product == null) throw BusinessException.of("商品不存在");
        if (product.getStatus() != 1) throw BusinessException.of("商品已下架");
        List<ProductSku> skuList = skuMapper.selectByProductId(id);
        product.setSkuList(skuList);
        return product;
    }
}
