package cn.eyeo.mall.app.product.command;

import cn.eyeo.mall.client.common.SystemErrorCode;
import cn.eyeo.mall.common.exception.MallBizException;
import cn.eyeo.mall.gateway.impl.product.database.dataobject.ProductDO;
import cn.eyeo.mall.gateway.impl.product.database.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 商品修改状态
 *
 * @author <a href="mailto:daoyuan0626@gmail.com">amos.wang</a>
 * @date 2023/3/26
 */
@Component
public class ProductUpdateStatusCmdExe {

    @Autowired
    private ProductMapper productMapper;


    public Boolean execute(Long id, Integer status) {
        ProductDO productDO = new ProductDO();
        productDO.setId(id);
        productDO.setStatus(status);
        productDO.setGmtModified(LocalDateTime.now());
        int result = productMapper.updateStatusById(productDO);
        if (result <= 0) {
            throw new MallBizException(SystemErrorCode.S_DB_WRITE_ERROR);
        }

        return true;
    }
}
