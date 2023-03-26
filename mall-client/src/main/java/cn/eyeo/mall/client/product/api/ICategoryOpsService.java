package cn.eyeo.mall.client.product.api;

import cn.eyeo.mall.client.product.dto.CategoryCreateCmd;
import cn.eyeo.mall.client.product.dto.CategoryModifyCmd;
import cn.eyeo.mall.client.product.dto.data.CategoryVO;
import com.alibaba.cola.dto.SingleResponse;

/**
 * 商品相关
 *
 * @author <a href="mailto:daoyuan0626@gmail.com">amos.wang</a>
 * @date 2023/3/25
 */
public interface ICategoryOpsService {

    /**
     * 创建分类
     *
     * @param cmd 入参
     * @return 结果
     */
    SingleResponse<CategoryVO> create(CategoryCreateCmd cmd);

    /**
     * 修改分类
     *
     * @param cmd 入参
     * @return 结果
     */
    SingleResponse<CategoryVO> modify(CategoryModifyCmd cmd);

    /**
     * 删除分类
     *
     * @param id 分类ID
     * @return 结果
     */
    SingleResponse<Boolean> delete(Long id);

}
