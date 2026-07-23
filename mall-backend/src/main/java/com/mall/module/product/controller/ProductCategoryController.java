package com.mall.module.product.controller;

import com.mall.common.Result;
import com.mall.module.product.entity.ProductCategory;
import com.mall.module.product.service.ProductCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品分类接口
 */
@RestController
@RequestMapping("/product/category")
public class ProductCategoryController {

    @Autowired
    private ProductCategoryService categoryService;

    /**
     * 获取启用的分类树 (公开)
     */
    @GetMapping("/tree")
    public Result<List<ProductCategory>> tree() {
        return Result.success(categoryService.getCategoryTree());
    }

    /**
     * 获取全部分类树含禁用 (管理端)
     */
    @GetMapping("/tree/all")
    public Result<List<ProductCategory>> treeAll() {
        return Result.success(categoryService.getCategoryTreeAll());
    }

    /**
     * 根据ID查询分类
     */
    @GetMapping("/{id}")
    public Result<ProductCategory> getById(@PathVariable Long id) {
        return Result.success(categoryService.getById(id));
    }

    /**
     * 新增分类
     */
    @PostMapping
    public Result<ProductCategory> create(@RequestBody ProductCategory category) {
        return Result.success(categoryService.create(category));
    }

    /**
     * 更新分类
     */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody ProductCategory category) {
        categoryService.update(id, category);
        return Result.success();
    }

    /**
     * 删除分类
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return Result.success();
    }
}
