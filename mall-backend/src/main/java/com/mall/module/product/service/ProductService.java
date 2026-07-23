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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 商品服务
 */
@Slf4j
@Service
public class ProductService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ProductSkuMapper skuMapper;

    /**
     * 分页查询商品列表 (管理端, 含下架商品)
     */
    public PageResult<Product> getList(ProductQueryDTO query) {
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        List<Product> list = productMapper.selectList(
                query.getCategoryId(),
                query.getName(),
                query.getStatus(),
                query.getTags()
        );
        PageInfo<Product> pageInfo = new PageInfo<>(list);
        return PageResult.of(pageInfo.getList(), pageInfo.getTotal(), query.getPageNum(), query.getPageSize());
    }

    /**
     * 分页查询上架商品 (公开接口, 仅查上架商品)
     */
    public PageResult<Product> getPublicList(ProductQueryDTO query) {
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        List<Product> list = productMapper.selectList(
                query.getCategoryId(),
                query.getName(),
                1,  // 只查上架商品
                query.getTags()
        );
        PageInfo<Product> pageInfo = new PageInfo<>(list);
        return PageResult.of(pageInfo.getList(), pageInfo.getTotal(), query.getPageNum(), query.getPageSize());
    }

    /**
     * 商品详情 (含SKU列表)
     */
    public Product getDetail(Long id) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw BusinessException.of("商品不存在");
        }
        // 填充SKU列表
        List<ProductSku> skuList = skuMapper.selectByProductId(id);
        product.setSkuList(skuList);
        return product;
    }

    /**
     * 公开商品详情 (仅返回上架商品)
     */
    public Product getPublicDetail(Long id) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw BusinessException.of("商品不存在");
        }
        if (product.getStatus() != 1) {
            throw BusinessException.of("商品已下架");
        }
        List<ProductSku> skuList = skuMapper.selectByProductId(id);
        product.setSkuList(skuList);
        return product;
    }

    /**
     * 根据ID查询商品 (不含SKU)
     */
    public Product getById(Long id) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw BusinessException.of("商品不存在");
        }
        return product;
    }

    /**
     * 新增商品
     */
    @Transactional
    public Product create(Product product) {
        if (product.getStatus() == null) {
            product.setStatus(0); // 默认下架
        }
        if (product.getStock() == null) {
            product.setStock(0);
        }
        if (product.getSales() == null) {
            product.setSales(0);
        }
        if (product.getSort() == null) {
            product.setSort(0);
        }
        productMapper.insert(product);
        // 如果有SKU, 批量插入并更新商品总库存
        if (product.getSkuList() != null && !product.getSkuList().isEmpty()) {
            for (ProductSku sku : product.getSkuList()) {
                sku.setProductId(product.getId());
                if (sku.getStatus() == null) {
                    sku.setStatus(1);
                }
            }
            skuMapper.batchInsert(product.getSkuList());
            // 同步商品总库存
            int totalStock = skuMapper.sumStockByProductId(product.getId());
            productMapper.updateStock(product.getId(), totalStock);
        }
        return product;
    }

    /**
     * 更新商品
     */
    @Transactional
    public void update(Long id, Product product) {
        Product exist = productMapper.selectById(id);
        if (exist == null) {
            throw BusinessException.of("商品不存在");
        }
        product.setId(id);
        productMapper.updateById(product);
    }

    /**
     * 删除商品 (逻辑删除, 同时删除关联SKU)
     */
    @Transactional
    public void delete(Long id) {
        Product exist = productMapper.selectById(id);
        if (exist == null) {
            throw BusinessException.of("商品不存在");
        }
        productMapper.deleteById(id);
        skuMapper.deleteByProductId(id);
    }

    /**
     * 商品上下架
     */
    @Transactional
    public void updateStatus(Long id, Integer status) {
        Product exist = productMapper.selectById(id);
        if (exist == null) {
            throw BusinessException.of("商品不存在");
        }
        if (status == null || (status != 0 && status != 1)) {
            throw BusinessException.of("状态值无效: 0-下架, 1-上架");
        }
        productMapper.updateStatus(id, status);
    }

    /**
     * 扣减商品库存 (SPU级别)
     * @param productId 商品ID
     * @param quantity 数量
     * @return true=成功, false=库存不足
     */
    public boolean reduceStock(Long productId, int quantity) {
        if (quantity <= 0) {
            throw BusinessException.of("扣减数量必须大于0");
        }
        int rows = productMapper.reduceStock(productId, quantity);
        return rows > 0;
    }

    /**
     * 恢复商品库存 (SPU级别)
     */
    public void restoreStock(Long productId, int quantity) {
        if (quantity <= 0) {
            throw BusinessException.of("恢复数量必须大于0");
        }
        productMapper.restoreStock(productId, quantity);
    }

    /**
     * 扣减库存并增加销量 (下单时调用)
     * @param productId 商品ID
     * @param quantity 购买数量
     */
    @Transactional
    public void reduceStockAndIncreaseSales(Long productId, int quantity) {
        boolean success = reduceStock(productId, quantity);
        if (!success) {
            throw BusinessException.of("商品库存不足");
        }
        productMapper.increaseSales(productId, quantity);
    }

    /**
     * 恢复库存并减少销量 (取消订单时调用)
     */
    @Transactional
    public void restoreStockAndDecreaseSales(Long productId, int quantity) {
        restoreStock(productId, quantity);
        productMapper.increaseSales(productId, -quantity);
    }
}
