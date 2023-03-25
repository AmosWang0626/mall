package cn.eyeo.mall.gateway.impl.product.database.dataobject;

import cn.eyeo.mall.common.api.BaseDO;
import lombok.Getter;
import lombok.Setter;

/**
 * 评价表
 *
 * @author amos.wang
 * @date 2023-03-25 20:17
 */
@Getter
@Setter
public class ProductCommentDO extends BaseDO {
    /**
     * 用户ID
     */
    private Long memberId;

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 评价内容
     */
    private String content;
}