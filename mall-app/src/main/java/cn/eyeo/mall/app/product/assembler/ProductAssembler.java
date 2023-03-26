package cn.eyeo.mall.app.product.assembler;

import cn.eyeo.mall.client.product.dto.ProductCreateCmd;
import cn.eyeo.mall.client.product.dto.ProductModifyCmd;
import cn.eyeo.mall.client.product.dto.data.ProductVO;
import cn.eyeo.mall.gateway.impl.product.database.dataobject.ProductDO;

import java.time.LocalDateTime;

/**
 * application <--> domain
 *
 * @author <a href="mailto:daoyuan0626@gmail.com">amos.wang</a>
 * @date 2023/3/26
 */
public class ProductAssembler {

    public static ProductDO toDO(ProductCreateCmd cmd) {
        ProductDO productDO = new ProductDO();
        productDO.setName(cmd.getName());
        productDO.setImage(cmd.getImage());
        productDO.setPrice(cmd.getPrice());
        productDO.setStock(cmd.getStock());
        productDO.setStatus(cmd.getStatus());
        productDO.setCategoryId(cmd.getCategoryId());
        productDO.setDescription(cmd.getDescription());
        productDO.setGmtCreate(LocalDateTime.now());
        productDO.setGmtModified(LocalDateTime.now());
        return productDO;
    }

    public static ProductDO toDO(ProductModifyCmd cmd) {
        ProductDO productDO = new ProductDO();
        productDO.setId(cmd.getId());
        productDO.setName(cmd.getName());
        productDO.setImage(cmd.getImage());
        productDO.setPrice(cmd.getPrice());
        productDO.setStock(cmd.getStock());
        productDO.setCategoryId(cmd.getCategoryId());
        productDO.setDescription(cmd.getDescription());
        productDO.setGmtModified(LocalDateTime.now());
        return productDO;
    }

    public static ProductVO toVO(ProductDO productDO) {
        ProductVO productVO = new ProductVO();
        productVO.setId(productDO.getId());
        productVO.setName(productDO.getName());
        productVO.setImage(productDO.getImage());
        productVO.setPrice(productDO.getPrice());
        productVO.setStock(productDO.getStock());
        productVO.setStatus(productDO.getStatus());
        productVO.setCategoryId(productDO.getCategoryId());
        productVO.setDescription(productDO.getDescription());
        return productVO;
    }

    public static ProductVO toValueObject(ProductDO productDO) {
        ProductVO productVO = new ProductVO();
        productVO.setId(productDO.getId());
        productVO.setName(productDO.getName());
        productVO.setImage(productDO.getImage());
        productVO.setPrice(productDO.getPrice());
        productVO.setStock(productDO.getStock());
        productVO.setStatus(productDO.getStatus());
        productVO.setCategoryId(productDO.getCategoryId());
        productVO.setDescription(productDO.getDescription());
        return productVO;
    }
}
