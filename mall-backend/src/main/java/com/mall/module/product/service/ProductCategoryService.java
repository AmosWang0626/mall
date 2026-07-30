package com.mall.module.product.service;

import com.mall.module.product.entity.ProductCategory;
import com.mall.module.product.mapper.ProductCategoryMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 商品分类服务 — C端公开接口
 */
@Slf4j
@Service
public class ProductCategoryService {

    @Autowired
    private ProductCategoryMapper categoryMapper;

    public List<ProductCategory> getCategoryTree() {
        List<ProductCategory> all = categoryMapper.selectAll(1);
        return buildTree(all);
    }

    private List<ProductCategory> buildTree(List<ProductCategory> all) {
        if (all == null || all.isEmpty()) return new ArrayList<>();
        return all.stream()
                .filter(c -> c.getParentId() == null || c.getParentId() == 0)
                .peek(c -> c.setChildren(findChildren(c.getId(), all)))
                .collect(Collectors.toList());
    }

    private List<ProductCategory> findChildren(Long parentId, List<ProductCategory> all) {
        return all.stream()
                .filter(c -> parentId.equals(c.getParentId()))
                .peek(c -> c.setChildren(findChildren(c.getId(), all)))
                .collect(Collectors.toList());
    }
}
