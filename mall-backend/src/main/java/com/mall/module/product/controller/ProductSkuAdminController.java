package com.mall.module.product.controller;

import com.mall.common.Result;
import com.mall.module.product.entity.ProductSku;
import com.mall.module.product.service.ProductSkuOpsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/product/sku")
public class ProductSkuAdminController {

    @Autowired
    private ProductSkuOpsService skuOpsService;

    @PostMapping
    public Result<ProductSku> create(@RequestBody ProductSku sku) {
        return Result.success(skuOpsService.create(sku));
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody ProductSku sku) {
        skuOpsService.update(id, sku);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        skuOpsService.delete(id);
        return Result.success();
    }
}
