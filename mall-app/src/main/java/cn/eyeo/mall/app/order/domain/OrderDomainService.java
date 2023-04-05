package cn.eyeo.mall.app.order.domain;

import cn.eyeo.mall.app.order.inventory.InventoryApi;
import cn.eyeo.mall.client.product.dto.data.ProductErrorCode;
import cn.eyeo.mall.common.exception.MallBizException;
import cn.eyeo.mall.domain.order.model.OrderEntity;
import cn.eyeo.mall.domain.order.model.OrderItem;
import cn.eyeo.mall.gateway.impl.product.database.dataobject.ProductDO;
import cn.eyeo.mall.gateway.impl.product.database.mapper.ProductMapper;
import com.alibaba.cola.exception.BizException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Order 领域服务
 *
 * @author <a href="mailto:daoyuan0626@gmail.com">amos.wang</a>
 * @date 2021/12/2
 */
@Service
public class OrderDomainService {

    @Autowired
    private InventoryApi inventoryApi;

    @Autowired
    private ProductMapper productMapper;

    public void calculatePrice(OrderEntity entity) {
        List<OrderItem> orderItems = entity.getOrderItems();
        Set<Long> productIds = orderItems.stream()
                .map(OrderItem::getGoodsSkuId).collect(Collectors.toSet());
        List<ProductDO> productList = productMapper.selectByIds(productIds);
        Set<Integer> productStatues = productList.stream().map(ProductDO::getStatus).collect(Collectors.toSet());
        if (productStatues.contains(0)) {
            throw new MallBizException(ProductErrorCode.B_PRODUCT_ALREADY_OFFLINE);
        }
        Map<Long, BigDecimal> productPriceMap = new HashMap<>(productList.size());
        for (ProductDO productDO : productList) {
            productPriceMap.put(productDO.getId(), productDO.getPrice());
        }

        // 计算订单总金额
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (OrderItem orderItem : orderItems) {
            BigDecimal price = productPriceMap.get(orderItem.getGoodsSkuId());
            orderItem.setPrice(price);

            totalPrice = totalPrice.add(price.multiply(new BigDecimal(orderItem.getQuantity())));
        }

        entity.setTotalPrice(totalPrice);

        // FIXME 默认运费为0
        entity.setShippingFee(BigDecimal.ZERO);
    }

    /**
     * 校验商品库存
     *
     * @param orderEntity 订单实体
     */
    public void checkStockEnough(OrderEntity orderEntity) {
        for (OrderItem orderItem : orderEntity.getOrderItems()) {
            boolean stockEnough = inventoryApi.stockEnough(orderItem.getGoodsSkuId(), orderItem.getQuantity());
            if (!stockEnough) {
                throw new BizException("商品库存不足");
            }
        }
    }

    /**
     * 锁定商品库存
     *
     * @param orderEntity 订单实体
     */
    public void lockStock(OrderEntity orderEntity) {
        List<OrderItem> lockedOrderItem = new ArrayList<>();
        for (OrderItem orderItem : orderEntity.getOrderItems()) {
            boolean lockResult = inventoryApi.lockStock(orderItem.getGoodsSkuId(), orderItem.getQuantity());
            if (lockResult) {
                lockedOrderItem.add(orderItem);
                continue;
            }
            for (OrderItem item : lockedOrderItem) {
                inventoryApi.unlockStock(item.getGoodsSkuId(), item.getQuantity());
            }

            throw new BizException("锁定库存失败，请稍后重试吧～");
        }
    }

    /**
     * 扣减库存
     *
     * @param orderEntity 订单实体
     */
    public void reduceStock(OrderEntity orderEntity) {
        List<OrderItem> deductedOrderItem = new ArrayList<>();
        for (OrderItem orderItem : orderEntity.getOrderItems()) {
            boolean lockResult = inventoryApi.reduceStock(orderItem.getGoodsSkuId(), orderItem.getQuantity());
            if (lockResult) {
                deductedOrderItem.add(orderItem);
                continue;
            }
            for (OrderItem item : deductedOrderItem) {
                inventoryApi.addStock(item.getGoodsSkuId(), item.getQuantity());
            }

            throw new BizException("扣减库存失败，请稍后重试吧～");
        }
    }
}
