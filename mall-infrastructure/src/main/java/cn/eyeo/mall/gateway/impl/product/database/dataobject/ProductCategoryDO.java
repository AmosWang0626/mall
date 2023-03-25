package cn.eyeo.mall.gateway.impl.product.database.dataobject;

import cn.eyeo.mall.common.api.BaseDO;
import lombok.Getter;
import lombok.Setter;

/**
 * 商品分类表
 *
 * @author amos.wang
 * @date 2023-03-25 20:17
 */
@Getter
@Setter
public class ProductCategoryDO extends BaseDO {
    /**
     * 分类名称
     */
    private String name;

    /**
     * 父分类ID
     */
    private Long parentId;

    /**
     * 分类层级
     */
    private Integer level;

    /**
     * 分类状态：0-不显示，1-显示
     */
    private Boolean status;

    /**
     * 是否叶子节点：0-非叶子节点，1-叶子节点
     */
    private Boolean isLeaf;

    /**
     * 分类描述
     */
    private String description;
}