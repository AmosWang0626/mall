package com.mall.module.product.controller;

import com.mall.common.Result;
import com.mall.module.product.entity.ProductCategory;
import com.mall.module.product.service.ProductCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product/category")
public class ProductCategoryController {

    @Autowired
    private ProductCategoryService categoryService;

    @GetMapping("/tree")
    public Result<List<ProductCategory>> tree() {
        return Result.success(categoryService.getCategoryTree());
    }
}
