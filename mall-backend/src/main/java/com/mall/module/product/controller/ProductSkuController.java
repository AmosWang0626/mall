package com.mall.module.product.controller;

import com.mall.common.Result;
import com.mall.module.product.entity.ProductSku;
import com.mall.module.product.service.ProductSkuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品SKU接口
 */
@RestController
@RequestMapping("/product/sku")
public class ProductSkuController {

    @Autowired
    private ProductSkuService skuService;

    /**
     * 按商品ID查询SKU列表 (公开)
     */
    @GetMapping("/list/{productId}")
    public Result<List<ProductSku>> listByProductId(@PathVariable Long productId) {
        return Result.success(skuService.listByProductId(productId));
    }

    /**
     * 根据ID查询SKU
     */
    @GetMapping("/{id}")
    public Result<ProductSku> getById(@PathVariable Long id) {
        return Result.success(skuService.getById(id));
    }

    /**
     * 新增SKU
     */
    @PostMapping
    public Result<ProductSku> create(@RequestBody ProductSku sku) {
        return Result.success(skuService.create(sku));
    }

    /**
     * 更新SKU
     */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody ProductSku sku) {
        skuService.update(id, sku);
        return Result.success();
    }

    /**
     * 删除SKU
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        skuService.delete(id);
        return Result.success();
    }
}
