package com.mall.module.product.controller;

import com.mall.common.Result;
import com.mall.module.product.entity.ProductSku;
import com.mall.module.product.service.ProductSkuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product/sku")
public class ProductSkuController {

    @Autowired
    private ProductSkuService skuService;

    @GetMapping("/list/{productId}")
    public Result<List<ProductSku>> listByProductId(@PathVariable Long productId) {
        return Result.success(skuService.listByProductId(productId));
    }

    @GetMapping("/{id}")
    public Result<ProductSku> getById(@PathVariable Long id) {
        return Result.success(skuService.getById(id));
    }
}
