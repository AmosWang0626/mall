package com.mall.module.product.controller;

import com.mall.common.PageResult;
import com.mall.common.Result;
import com.mall.module.product.dto.ProductQueryDTO;
import com.mall.module.product.entity.Product;
import com.mall.module.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 商品接口
 */
@RestController
public class ProductController {

    @Autowired
    private ProductService productService;

    // ==================== 公开接口 (无需登录) ====================

    /**
     * 公开 - 商品列表 (仅上架)
     */
    @GetMapping("/public/product/list")
    public Result<PageResult<Product>> publicList(ProductQueryDTO query) {
        return Result.success(productService.getPublicList(query));
    }

    /**
     * 公开 - 商品详情 (仅上架)
     */
    @GetMapping("/public/product/{id}")
    public Result<Product> publicDetail(@PathVariable Long id) {
        return Result.success(productService.getPublicDetail(id));
    }

    // ==================== 管理接口 (需登录) ====================

    /**
     * 管理端 - 商品分页列表
     */
    @GetMapping("/product/list")
    public Result<PageResult<Product>> list(ProductQueryDTO query) {
        return Result.success(productService.getList(query));
    }

    /**
     * 管理端 - 商品详情
     */
    @GetMapping("/product/{id}")
    public Result<Product> detail(@PathVariable Long id) {
        return Result.success(productService.getDetail(id));
    }

    /**
     * 管理端 - 新增商品
     */
    @PostMapping("/product")
    public Result<Product> create(@RequestBody Product product) {
        return Result.success(productService.create(product));
    }

    /**
     * 管理端 - 更新商品
     */
    @PutMapping("/product/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Product product) {
        productService.update(id, product);
        return Result.success();
    }

    /**
     * 管理端 - 删除商品
     */
    @DeleteMapping("/product/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return Result.success();
    }

    /**
     * 管理端 - 商品上下架
     */
    @PutMapping("/product/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        productService.updateStatus(id, status);
        return Result.success();
    }
}
