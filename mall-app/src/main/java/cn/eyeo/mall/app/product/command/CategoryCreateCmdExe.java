package cn.eyeo.mall.app.product.command;

import cn.eyeo.mall.app.product.assembler.CategoryAssembler;
import cn.eyeo.mall.client.common.SystemErrorCode;
import cn.eyeo.mall.client.product.dto.CategoryCreateCmd;
import cn.eyeo.mall.client.product.dto.data.CategoryErrorCode;
import cn.eyeo.mall.client.product.dto.data.CategoryVO;
import cn.eyeo.mall.common.exception.MallBizException;
import cn.eyeo.mall.gateway.impl.product.database.dataobject.ProductCategoryDO;
import cn.eyeo.mall.gateway.impl.product.database.mapper.ProductCategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 创建分类
 *
 * @author <a href="mailto:daoyuan0626@gmail.com">amos.wang</a>
 * @date 2023/3/26
 */
@Component
public class CategoryCreateCmdExe {

    @Autowired
    private ProductCategoryMapper productCategoryMapper;


    public CategoryVO execute(CategoryCreateCmd cmd) {
        ProductCategoryDO categoryDO = CategoryAssembler.toDO(cmd);
        long parentId = CategoryAssembler.parseParentId(categoryDO.getParentId());
        if (parentId <= 0L) {
            categoryDO.setLevel(1);
        } else {
            ProductCategoryDO parentCategoryDO = productCategoryMapper.selectByPrimaryKey(parentId);
            if (parentCategoryDO == null) {
                throw new MallBizException(CategoryErrorCode.B_CATEGORY_PARENT_NOT_EXIST);
            }
            categoryDO.setLevel(parentCategoryDO.getLevel() + 1);

            // 更新父分类的isLeaf字段
            updateParentIsLeaf(parentCategoryDO);
        }

        int result = productCategoryMapper.insert(categoryDO);
        if (result <= 0) {
            throw new MallBizException(SystemErrorCode.S_DB_WRITE_ERROR);
        }

        return CategoryAssembler.toVO(categoryDO);
    }

    /**
     * 更新父分类的isLeaf字段
     */
    public void updateParentIsLeaf(ProductCategoryDO parentCategoryDO) {
        if (Boolean.TRUE.equals(parentCategoryDO.getIsLeaf())) {
            int result = productCategoryMapper.updateIsLeafById(false, parentCategoryDO.getId());
            if (result <= 0) {
                throw new MallBizException(SystemErrorCode.S_DB_WRITE_ERROR);
            }
        }
    }
}
