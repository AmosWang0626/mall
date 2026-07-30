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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductOpsService {

    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ProductSkuMapper skuMapper;

    /** 管理端 - 商品分页列表 */
    public PageResult<Product> getList(ProductQueryDTO query) {
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        List<Product> list = productMapper.selectList(
                query.getCategoryId(), query.getName(), query.getStatus(), query.getTags());
        PageInfo<Product> pageInfo = new PageInfo<>(list);
        return PageResult.of(pageInfo.getList(), pageInfo.getTotal(), query.getPageNum(), query.getPageSize());
    }

    /** 管理端 - 商品详情 (含 SKU) */
    public Product getDetail(Long id) {
        Product product = productMapper.selectById(id);
        if (product == null) throw BusinessException.of("商品不存在");
        List<ProductSku> skuList = skuMapper.selectByProductId(id);
        product.setSkuList(skuList);
        return product;
    }

    @Transactional
    public Product create(Product product) {
        if (product.getStatus() == null) product.setStatus(0);
        if (product.getStock() == null) product.setStock(0);
        if (product.getSales() == null) product.setSales(0);
        if (product.getSort() == null) product.setSort(0);
        productMapper.insert(product);
        if (product.getSkuList() != null && !product.getSkuList().isEmpty()) {
            for (ProductSku sku : product.getSkuList()) {
                sku.setProductId(product.getId());
                if (sku.getStatus() == null) sku.setStatus(1);
            }
            skuMapper.batchInsert(product.getSkuList());
            int totalStock = skuMapper.sumStockByProductId(product.getId());
            productMapper.updateStock(product.getId(), totalStock);
        }
        return product;
    }

    @Transactional
    public void update(Long id, Product product) {
        Product exist = productMapper.selectById(id);
        if (exist == null) throw BusinessException.of("商品不存在");
        product.setId(id);
        productMapper.updateById(product);
    }

    @Transactional
    public void delete(Long id) {
        Product exist = productMapper.selectById(id);
        if (exist == null) throw BusinessException.of("商品不存在");
        productMapper.deleteById(id);
        skuMapper.deleteByProductId(id);
    }

    @Transactional
    public void updateStatus(Long id, Integer status) {
        Product exist = productMapper.selectById(id);
        if (exist == null) throw BusinessException.of("商品不存在");
        if (status == null || (status != 0 && status != 1)) {
            throw BusinessException.of("状态值无效: 0-下架, 1-上架");
        }
        productMapper.updateStatus(id, status);
    }
}
