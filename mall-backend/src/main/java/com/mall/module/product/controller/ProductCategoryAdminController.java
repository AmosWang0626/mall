package com.mall.module.product.controller;

import com.mall.common.Result;
import com.mall.module.product.entity.ProductCategory;
import com.mall.module.product.service.ProductCategoryOpsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/product/category")
public class ProductCategoryAdminController {

    @Autowired
    private ProductCategoryOpsService categoryOpsService;

    @GetMapping("/tree/all")
    public Result<List<ProductCategory>> treeAll() {
        return Result.success(categoryOpsService.getCategoryTreeAll());
    }

    @GetMapping("/{id}")
    public Result<ProductCategory> getById(@PathVariable Long id) {
        return Result.success(categoryOpsService.getById(id));
    }

    @PostMapping
    public Result<ProductCategory> create(@RequestBody ProductCategory category) {
        return Result.success(categoryOpsService.create(category));
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody ProductCategory category) {
        categoryOpsService.update(id, category);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        categoryOpsService.delete(id);
        return Result.success();
    }
}
