package cn.eyeo.mall.client.product.api;

import cn.eyeo.mall.client.product.dto.ProductCreateCmd;
import cn.eyeo.mall.client.product.dto.ProductModifyCmd;
import cn.eyeo.mall.client.product.dto.data.ProductVO;
import cn.eyeo.mall.client.product.dto.query.ProductPageQuery;
import com.alibaba.cola.dto.PageResponse;
import com.alibaba.cola.dto.SingleResponse;

/**
 * 商品相关
 *
 * @author <a href="mailto:daoyuan0626@gmail.com">amos.wang</a>
 * @date 2023/3/25
 */
public interface IProductOpsService {

    /**
     * 创建商品
     *
     * @param cmd 入参
     * @return 结果
     */
    SingleResponse<ProductVO> create(ProductCreateCmd cmd);

    /**
     * 修改商品
     *
     * @param cmd 入参
     * @return 结果
     */
    SingleResponse<ProductVO> modify(ProductModifyCmd cmd);

    /**
     * 上架商品
     *
     * @param id 商品ID
     * @return 结果
     */
    SingleResponse<Boolean> online(Long id);

    /**
     * 下架商品
     *
     * @param id 商品ID
     * @return 结果
     */
    SingleResponse<Boolean> offline(Long id);

    /**
     * 搜索商品
     *
     * @param cmd 入参
     * @return 结果
     */
    PageResponse<ProductVO> pageQuery(ProductPageQuery cmd);

    /**
     * 根据ID查询商品详情
     *
     * @param id 入参
     * @return 结果
     */
    SingleResponse<ProductVO> getProductById(Long id);

}
