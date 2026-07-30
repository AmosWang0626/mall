package com.mall.module.product.controller;

import com.mall.common.PageResult;
import com.mall.common.Result;
import com.mall.module.product.dto.ProductQueryDTO;
import com.mall.module.product.entity.Product;
import com.mall.module.product.service.ProductOpsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/product")
public class ProductAdminController {

    @Autowired
    private ProductOpsService productOpsService;

    @GetMapping("/list")
    public Result<PageResult<Product>> list(ProductQueryDTO query) {
        return Result.success(productOpsService.getList(query));
    }

    @GetMapping("/{id}")
    public Result<Product> detail(@PathVariable Long id) {
        return Result.success(productOpsService.getDetail(id));
    }

    @PostMapping
    public Result<Product> create(@RequestBody Product product) {
        return Result.success(productOpsService.create(product));
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Product product) {
        productOpsService.update(id, product);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        productOpsService.delete(id);
        return Result.success();
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        productOpsService.updateStatus(id, status);
        return Result.success();
    }
}
