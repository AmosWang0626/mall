package cn.eyeo.mall.client.product.dto.query;

import com.alibaba.cola.dto.PageQuery;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.math.BigDecimal;

/**
 * 商品查询
 *
 * @author <a href="mailto:daoyuan0626@gmail.com">amos.wang</a>
 * @date 2023/3/26
 */
@Getter
@Setter
public class ProductPageQuery extends PageQuery {

    @Serial
    private static final long serialVersionUID = 6774467503726958096L;

    /**
     * 商品ID
     */
    private Long id;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 商品价格（最低）
     */
    private BigDecimal minPrice;

    /**
     * 商品价格（最高）
     */
    private BigDecimal maxPrice;

    /**
     * 商品分类ID
     */
    private Long categoryId;

    /**
     * 商品状态：0-下架，1-上架
     */
    private Integer status;

}
