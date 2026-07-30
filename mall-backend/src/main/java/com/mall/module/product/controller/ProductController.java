package com.mall.module.product.controller;

import com.mall.common.PageResult;
import com.mall.common.Result;
import com.mall.module.product.dto.ProductQueryDTO;
import com.mall.module.product.entity.Product;
import com.mall.module.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/public/product/list")
    public Result<PageResult<Product>> publicList(ProductQueryDTO query) {
        return Result.success(productService.getPublicList(query));
    }

    @GetMapping("/public/product/{id}")
    public Result<Product> publicDetail(@PathVariable Long id) {
        return Result.success(productService.getPublicDetail(id));
    }
}
