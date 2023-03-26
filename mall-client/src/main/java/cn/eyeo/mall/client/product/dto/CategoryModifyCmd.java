package cn.eyeo.mall.client.product.dto;

import com.alibaba.cola.dto.Command;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

/**
 * 修改分类
 *
 * @author <a href="mailto:daoyuan0626@gmail.com">amos.wang</a>
 * @date 2023/3/26
 */
@Getter
@Setter
public class CategoryModifyCmd extends Command {

    @Serial
    private static final long serialVersionUID = -5617749709863891971L;

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
     * 分类描述
     */
    private String description;

}
