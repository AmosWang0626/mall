package cn.eyeo.mall.app.product.assembler;

import cn.eyeo.mall.client.product.dto.CategoryCreateCmd;
import cn.eyeo.mall.client.product.dto.CategoryModifyCmd;
import cn.eyeo.mall.client.product.dto.data.CategoryVO;
import cn.eyeo.mall.gateway.impl.product.database.dataobject.ProductCategoryDO;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * application <--> domain
 *
 * @author <a href="mailto:daoyuan0626@gmail.com">amos.wang</a>
 * @date 2023/3/26
 */
public class CategoryAssembler {

    public static ProductCategoryDO toDO(CategoryCreateCmd cmd) {
        ProductCategoryDO categoryDO = new ProductCategoryDO();
        categoryDO.setName(cmd.getName());
        categoryDO.setParentId(cmd.getParentId());
        categoryDO.setStatus(1);
        categoryDO.setIsLeaf(true);
        categoryDO.setDescription(cmd.getDescription());
        categoryDO.setGmtCreate(LocalDateTime.now());
        categoryDO.setGmtModified(LocalDateTime.now());
        return categoryDO;
    }

    public static ProductCategoryDO toDO(CategoryModifyCmd cmd) {
        ProductCategoryDO productCategoryDO = new ProductCategoryDO();
        productCategoryDO.setId(cmd.getId());
        productCategoryDO.setName(cmd.getName());
        productCategoryDO.setParentId(cmd.getParentId());
        productCategoryDO.setDescription(cmd.getDescription());
        productCategoryDO.setGmtModified(LocalDateTime.now());
        return productCategoryDO;
    }

    /**
     * 父分类ID解析（只要不是>0的ID，都 return 0）
     */
    public static long parseParentId(Long parentId) {
        if (Objects.isNull(parentId) || parentId <= 0) {
            return 0L;
        }
        return parentId;
    }

    public static CategoryVO toVO(ProductCategoryDO categoryDO) {
        CategoryVO categoryVO = new CategoryVO();
        categoryVO.setId(categoryDO.getId());
        categoryVO.setName(categoryDO.getName());
        categoryVO.setParentId(categoryDO.getParentId());
        categoryVO.setLevel(categoryDO.getLevel());
        categoryVO.setStatus(categoryDO.getStatus());
        categoryVO.setIsLeaf(categoryDO.getIsLeaf());
        categoryVO.setDescription(categoryDO.getDescription());
        return categoryVO;
    }

}
