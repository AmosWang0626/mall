package cn.eyeo.mall.app.product.command;

import cn.eyeo.mall.client.product.dto.data.CategoryErrorCode;
import cn.eyeo.mall.common.exception.MallBizException;
import cn.eyeo.mall.gateway.impl.product.database.mapper.ProductCategoryMapper;
import cn.eyeo.mall.gateway.impl.product.database.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 删除分类
 *
 * @author <a href="mailto:daoyuan0626@gmail.com">amos.wang</a>
 * @date 2023/3/26
 */
@Component
public class CategoryDeleteCmdExe {

    @Autowired
    private ProductCategoryMapper productCategoryMapper;
    @Autowired
    private ProductMapper productMapper;

    /**
     * 删除分类前置条件：
     * 1. 分类下没有子分类
     * 2. 分类下没有关联商品
     *
     * @param id 分类ID
     * @return true-成功; false-失败
     */
    public Boolean execute(Long id) {
        Integer countByParentId = productCategoryMapper.countByParentId(id);
        if (countByParentId > 0) {
            throw new MallBizException(CategoryErrorCode.B_CATEGORY_HAS_CHILD);
        }
        Integer countByCategoryId = productMapper.countByCategoryId(id);
        if (countByCategoryId > 0) {
            throw new MallBizException(CategoryErrorCode.B_CATEGORY_HAS_PRODUCT);
        }

        int i = productCategoryMapper.deleteByPrimaryKey(id);

        return i > 0;
    }
}
