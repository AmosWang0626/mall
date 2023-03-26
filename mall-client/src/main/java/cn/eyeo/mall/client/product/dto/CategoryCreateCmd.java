package cn.eyeo.mall.client.product.dto;

import com.alibaba.cola.dto.Command;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

/**
 * 创建分类
 *
 * @author <a href="mailto:daoyuan0626@gmail.com">amos.wang</a>
 * @date 2023/3/26
 */
@Getter
@Setter
public class CategoryCreateCmd extends Command {

    @Serial
    private static final long serialVersionUID = 2441678970753669018L;

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
