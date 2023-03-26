package cn.eyeo.mall.app.product.command;

import cn.eyeo.mall.app.product.assembler.ProductAssembler;
import cn.eyeo.mall.client.common.SystemErrorCode;
import cn.eyeo.mall.client.product.dto.ProductModifyCmd;
import cn.eyeo.mall.client.product.dto.data.ProductErrorCode;
import cn.eyeo.mall.client.product.dto.data.ProductVO;
import cn.eyeo.mall.common.exception.MallBizException;
import cn.eyeo.mall.gateway.impl.product.database.dataobject.ProductCategoryDO;
import cn.eyeo.mall.gateway.impl.product.database.dataobject.ProductDO;
import cn.eyeo.mall.gateway.impl.product.database.mapper.ProductCategoryMapper;
import cn.eyeo.mall.gateway.impl.product.database.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 商品修改
 *
 * @author <a href="mailto:daoyuan0626@gmail.com">amos.wang</a>
 * @date 2023/3/26
 */
@Component
public class ProductModifyCmdExe {

    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ProductCategoryMapper productCategoryMapper;


    public ProductVO execute(ProductModifyCmd cmd) {
        Long categoryId = cmd.getCategoryId();
        ProductCategoryDO categoryDO = productCategoryMapper.selectByPrimaryKey(categoryId);
        if (Objects.isNull(categoryDO)) {
            throw new MallBizException(ProductErrorCode.B_PRODUCT_CATEGORY_NOT_EXIST);
        }

        ProductDO productDO = ProductAssembler.toDO(cmd);
        int result = productMapper.updateByPrimaryKey(productDO);
        if (result <= 0) {
            throw new MallBizException(SystemErrorCode.S_DB_WRITE_ERROR);
        }

        return ProductAssembler.toVO(productDO);
    }
}
