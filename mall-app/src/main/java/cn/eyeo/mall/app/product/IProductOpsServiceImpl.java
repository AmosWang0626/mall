package cn.eyeo.mall.app.product;

import cn.eyeo.mall.app.product.command.ProductCreateCmdExe;
import cn.eyeo.mall.app.product.command.ProductModifyCmdExe;
import cn.eyeo.mall.app.product.command.ProductUpdateStatusCmdExe;
import cn.eyeo.mall.app.product.command.query.ProductQueryExe;
import cn.eyeo.mall.client.product.api.IProductOpsService;
import cn.eyeo.mall.client.product.dto.ProductCreateCmd;
import cn.eyeo.mall.client.product.dto.ProductModifyCmd;
import cn.eyeo.mall.client.product.dto.data.ProductVO;
import cn.eyeo.mall.client.product.dto.query.ProductPageQuery;
import com.alibaba.cola.dto.PageResponse;
import com.alibaba.cola.dto.SingleResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 商品管理
 *
 * @author <a href="mailto:daoyuan0626@gmail.com">amos.wang</a>
 * @date 2023/3/26
 */
@Service
public class IProductOpsServiceImpl implements IProductOpsService {

    @Autowired
    private ProductCreateCmdExe productCreateCmdExe;
    @Autowired
    private ProductModifyCmdExe productModifyCmdExe;
    @Autowired
    private ProductUpdateStatusCmdExe productUpdateStatusCmdExe;
    @Autowired
    private ProductQueryExe productQueryExe;


    @Override
    public SingleResponse<ProductVO> create(ProductCreateCmd cmd) {
        ProductVO productVO = productCreateCmdExe.execute(cmd);
        return SingleResponse.of(productVO);
    }

    @Override
    public SingleResponse<ProductVO> modify(ProductModifyCmd cmd) {
        ProductVO productVO = productModifyCmdExe.execute(cmd);
        return SingleResponse.of(productVO);
    }

    @Override
    public SingleResponse<Boolean> online(Long id) {
        Boolean result = productUpdateStatusCmdExe.execute(id, 1);
        return SingleResponse.of(result);
    }

    @Override
    public SingleResponse<Boolean> offline(Long id) {
        Boolean result = productUpdateStatusCmdExe.execute(id, 0);
        return SingleResponse.of(result);
    }

    @Override
    public PageResponse<ProductVO> pageQuery(ProductPageQuery cmd) {
        return productQueryExe.pageQuery(cmd);
    }

    @Override
    public SingleResponse<ProductVO> getProductById(Long id) {
        ProductVO productVO = productQueryExe.getById(id);
        return SingleResponse.of(productVO);
    }
}
