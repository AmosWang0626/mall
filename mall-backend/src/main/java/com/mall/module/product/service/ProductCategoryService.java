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

/**
 * 商品分类服务
 */
@Slf4j
@Service
public class ProductCategoryService {

    @Autowired
    private ProductCategoryMapper categoryMapper;

    /**
     * 获取分类树形结构
     */
    public List<ProductCategory> getCategoryTree() {
        List<ProductCategory> all = categoryMapper.selectAll(1);
        return buildTree(all);
    }

    /**
     * 获取全部分类树 (含禁用, 管理端使用)
     */
    public List<ProductCategory> getCategoryTreeAll() {
        List<ProductCategory> all = categoryMapper.selectAll(null);
        return buildTree(all);
    }

    /**
     * 构建树形结构
     */
    private List<ProductCategory> buildTree(List<ProductCategory> all) {
        if (all == null || all.isEmpty()) {
            return new ArrayList<>();
        }
        // 一级分类
        return all.stream()
                .filter(c -> c.getParentId() == null || c.getParentId() == 0)
                .peek(c -> c.setChildren(findChildren(c.getId(), all)))
                .collect(Collectors.toList());
    }

    /**
     * 递归查找子分类
     */
    private List<ProductCategory> findChildren(Long parentId, List<ProductCategory> all) {
        return all.stream()
                .filter(c -> parentId.equals(c.getParentId()))
                .peek(c -> c.setChildren(findChildren(c.getId(), all)))
                .collect(Collectors.toList());
    }

    /**
     * 根据ID查询分类
     */
    public ProductCategory getById(Long id) {
        ProductCategory category = categoryMapper.selectById(id);
        if (category == null) {
            throw BusinessException.of("分类不存在");
        }
        return category;
    }

    /**
     * 新增分类
     */
    @Transactional
    public ProductCategory create(ProductCategory category) {
        if (category.getParentId() == null) {
            category.setParentId(0L);
        }
        // 计算层级: 父分类为0则是一级, 否则取父分类层级+1
        if (category.getParentId() == 0) {
            category.setLevel(1);
        } else {
            ProductCategory parent = categoryMapper.selectById(category.getParentId());
            if (parent == null) {
                throw BusinessException.of("父分类不存在");
            }
            if (parent.getLevel() >= 3) {
                throw BusinessException.of("分类层级最多支持三级");
            }
            category.setLevel(parent.getLevel() + 1);
        }
        if (category.getSort() == null) {
            Integer maxSort = categoryMapper.selectMaxSort(category.getParentId());
            category.setSort(maxSort == null ? 1 : maxSort + 1);
        }
        if (category.getStatus() == null) {
            category.setStatus(1);
        }
        categoryMapper.insert(category);
        return category;
    }

    /**
     * 更新分类
     */
    @Transactional
    public void update(Long id, ProductCategory category) {
        ProductCategory exist = categoryMapper.selectById(id);
        if (exist == null) {
            throw BusinessException.of("分类不存在");
        }
        category.setId(id);
        // 如果修改了父分类, 需要重新计算层级
        if (category.getParentId() != null && !category.getParentId().equals(exist.getParentId())) {
            if (category.getParentId() == 0) {
                category.setLevel(1);
            } else {
                ProductCategory parent = categoryMapper.selectById(category.getParentId());
                if (parent == null) {
                    throw BusinessException.of("父分类不存在");
                }
                if (parent.getLevel() >= 3) {
                    throw BusinessException.of("分类层级最多支持三级");
                }
                category.setLevel(parent.getLevel() + 1);
            }
        }
        categoryMapper.updateById(category);
    }

    /**
     * 删除分类 (逻辑删除)
     */
    @Transactional
    public void delete(Long id) {
        ProductCategory exist = categoryMapper.selectById(id);
        if (exist == null) {
            throw BusinessException.of("分类不存在");
        }
        // 检查是否有子分类
        List<ProductCategory> children = categoryMapper.selectByParentId(id, null);
        if (children != null && !children.isEmpty()) {
            throw BusinessException.of("该分类下存在子分类, 无法删除");
        }
        categoryMapper.deleteById(id);
    }
}
