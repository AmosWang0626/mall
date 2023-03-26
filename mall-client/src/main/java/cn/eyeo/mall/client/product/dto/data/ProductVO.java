package cn.eyeo.mall.client.product.dto.data;

import cn.eyeo.mall.client.common.BaseVO;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.math.BigDecimal;

/**
 * 商品
 *
 * @author <a href="mailto:daoyuan0626@gmail.com">amos.wang</a>
 * @date 2023/3/26
 */
@Getter
@Setter
public class ProductVO extends BaseVO {

    @Serial
    private static final long serialVersionUID = -5826788060517679620L;

    /**
     * 商品ID
     */
    private Long id;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 图片
     */
    private String image;

    /**
     * 商品价格
     */
    private BigDecimal price;

    /**
     * 商品库存
     */
    private Long stock;

    /**
     * 商品状态：0-下架，1-上架
     */
    private Integer status;

    /**
     * 商品分类ID
     */
    private Long categoryId;

    /**
     * 商品描述
     */
    private String description;

}
