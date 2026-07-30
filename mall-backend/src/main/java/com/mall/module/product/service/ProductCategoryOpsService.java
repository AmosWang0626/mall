package com.mall.module.product.service;

import com.mall.common.exception.BusinessException;
import com.mall.module.product.entity.ProductCategory;
import com.mall.module.product.mapper.ProductCategoryMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProductCategoryOpsService {

    @Autowired
    private ProductCategoryMapper categoryMapper;

    public List<ProductCategory> getCategoryTreeAll() {
        List<ProductCategory> all = categoryMapper.selectAll(null);
        return buildTree(all);
    }

    public ProductCategory getById(Long id) {
        ProductCategory category = categoryMapper.selectById(id);
        if (category == null) throw BusinessException.of("分类不存在");
        return category;
    }

    @Transactional
    public ProductCategory create(ProductCategory category) {
        if (category.getParentId() == null) category.setParentId(0L);
        if (category.getParentId() == 0) {
            category.setLevel(1);
        } else {
            ProductCategory parent = categoryMapper.selectById(category.getParentId());
            if (parent == null) throw BusinessException.of("父分类不存在");
            if (parent.getLevel() >= 3) throw BusinessException.of("分类层级最多支持三级");
            category.setLevel(parent.getLevel() + 1);
        }
        if (category.getSort() == null) {
            Integer maxSort = categoryMapper.selectMaxSort(category.getParentId());
            category.setSort(maxSort == null ? 1 : maxSort + 1);
        }
        if (category.getStatus() == null) category.setStatus(1);
        categoryMapper.insert(category);
        return category;
    }

    @Transactional
    public void update(Long id, ProductCategory category) {
        ProductCategory exist = categoryMapper.selectById(id);
        if (exist == null) throw BusinessException.of("分类不存在");
        category.setId(id);
        if (category.getParentId() != null && !category.getParentId().equals(exist.getParentId())) {
            if (category.getParentId() == 0) {
                category.setLevel(1);
            } else {
                ProductCategory parent = categoryMapper.selectById(category.getParentId());
                if (parent == null) throw BusinessException.of("父分类不存在");
                if (parent.getLevel() >= 3) throw BusinessException.of("分类层级最多支持三级");
                category.setLevel(parent.getLevel() + 1);
            }
        }
        categoryMapper.updateById(category);
    }

    @Transactional
    public void delete(Long id) {
        ProductCategory exist = categoryMapper.selectById(id);
        if (exist == null) throw BusinessException.of("分类不存在");
        List<ProductCategory> children = categoryMapper.selectByParentId(id, null);
        if (children != null && !children.isEmpty()) throw BusinessException.of("该分类下存在子分类, 无法删除");
        categoryMapper.deleteById(id);
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
