package cn.eyeo.mall.client.product.dto.data;

import cn.eyeo.mall.client.common.BaseVO;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

/**
 * 商品分类
 *
 * @author <a href="mailto:daoyuan0626@gmail.com">amos.wang</a>
 * @date 2023/3/26
 */
@Getter
@Setter
public class CategoryVO extends BaseVO {

    @Serial
    private static final long serialVersionUID = -8274970076689811640L;

    /**
     * 分类ID
     */
    private Long id;

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
    private Integer status;

    /**
     * 是否叶子节点：0-非叶子节点，1-叶子节点
     */
    private Boolean isLeaf;

    /**
     * 分类描述
     */
    private String description;

}
