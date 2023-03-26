package cn.eyeo.mall.app.product.command.query;

import cn.eyeo.mall.app.product.assembler.ProductAssembler;
import cn.eyeo.mall.client.product.dto.data.ProductErrorCode;
import cn.eyeo.mall.client.product.dto.data.ProductVO;
import cn.eyeo.mall.client.product.dto.query.ProductPageQuery;
import cn.eyeo.mall.common.exception.MallBizException;
import cn.eyeo.mall.gateway.impl.product.database.dataobject.ProductDO;
import cn.eyeo.mall.gateway.impl.product.database.mapper.ProductMapper;
import com.alibaba.cola.dto.PageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 用户信息查询
 *
 * @author daoyuan
 * @date 2021/2/14 23:27
 */
@Component
public class ProductQueryExe {

    @Autowired
    private ProductMapper productMapper;

    public ProductVO getById(Long id) {
        ProductDO productDO = productMapper.selectByPrimaryKey(id);
        if (Objects.isNull(productDO)) {
            throw new MallBizException(ProductErrorCode.B_PRODUCT_ID_NOT_EXIST);
        }

        return ProductAssembler.toValueObject(productDO);
    }

    public PageResponse<ProductVO> pageQuery(ProductPageQuery cmd) {
        List<ProductDO> list = productMapper.selectByParam(cmd);
        int total = productMapper.countByParam(cmd);

        List<ProductVO> voList = list.stream().map(ProductAssembler::toVO).collect(Collectors.toList());

        return PageResponse.of(voList, total, cmd.getPageSize(), cmd.getPageSize());
    }
}
