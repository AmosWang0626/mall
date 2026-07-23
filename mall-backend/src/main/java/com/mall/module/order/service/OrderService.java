package com.mall.module.order.service;

import com.mall.common.PageResult;
import com.mall.common.exception.BusinessException;
import com.mall.module.cart.entity.CartItem;
import com.mall.module.cart.mapper.CartItemMapper;
import com.mall.module.order.dto.CreateOrderDTO;
import com.mall.module.order.entity.MallOrder;
import com.mall.module.order.entity.OrderItem;
import com.mall.module.order.mapper.MallOrderMapper;
import com.mall.module.order.mapper.OrderItemMapper;
import com.mall.module.product.entity.Product;
import com.mall.module.product.mapper.ProductMapper;
import com.mall.module.product.mapper.ProductSkuMapper;
import com.mall.module.product.entity.ProductSku;
import com.mall.module.user.entity.UserAddress;
import com.mall.module.user.mapper.UserAddressMapper;
import com.mall.security.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderService {
    @Autowired private MallOrderMapper orderMapper;
    @Autowired private OrderItemMapper itemMapper;
    @Autowired private CartItemMapper cartMapper;
    @Autowired private ProductMapper productMapper;
    @Autowired private ProductSkuMapper skuMapper;
    @Autowired private UserAddressMapper addressMapper;
    @Autowired private com.mall.module.points.service.PointsService pointsService;
    @Autowired private com.mall.module.coupon.service.CouponService couponService;

    @Transactional
    public MallOrder create(CreateOrderDTO dto) {
        Long userId = UserContext.require().getUserId();

        // 1. get cart items
        List<CartItem> allCart = cartMapper.selectByUserId(userId);
        List<CartItem> selected;
        if (dto.getCartItemIds() != null && !dto.getCartItemIds().isEmpty()) {
            Set<Long> idSet = new HashSet<>(dto.getCartItemIds());
            selected = allCart.stream().filter(c -> idSet.contains(c.getId())).collect(Collectors.toList());
        } else {
            selected = allCart.stream().filter(CartItem::getSelected).filter(c -> c.getSelected() == 1).collect(Collectors.toList());
        }
        if (selected.isEmpty()) throw BusinessException.of("购物车中没有选中的商品");

        // 2. validate stock & build order items
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cart : selected) {
            Product product = productMapper.selectById(cart.getProductId());
            if (product == null || product.getStatus() != 1) throw BusinessException.of("商品已下架: " + cart.getProductName());
            int stock = cart.getSkuId() != null ?
                    (skuMapper.selectById(cart.getSkuId()) != null ? skuMapper.selectById(cart.getSkuId()).getStock() : 0) :
                    product.getStock();
            if (stock < cart.getQuantity()) throw BusinessException.of("库存不足: " + cart.getProductName());

            // deduct stock
            if (cart.getSkuId() != null) {
                skuMapper.reduceStock(cart.getSkuId(), cart.getQuantity());
            } else {
                productMapper.reduceStock(cart.getProductId(), cart.getQuantity());
            }
            productMapper.addSales(cart.getProductId(), cart.getQuantity());

            OrderItem item = new OrderItem();
            item.setProductId(cart.getProductId());
            item.setSkuId(cart.getSkuId());
            item.setProductName(cart.getProductName());
            item.setProductImage(cart.getProductImage());
            item.setSkuName(cart.getSkuName());
            item.setPrice(cart.getPrice());
            item.setQuantity(cart.getQuantity());
            item.setTotalAmount(cart.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity())));
            totalAmount = totalAmount.add(item.getTotalAmount());
            orderItems.add(item);
        }

        // 3. address
        UserAddress addr = addressMapper.selectById(dto.getAddressId());
        if (addr == null) throw BusinessException.of("收货地址不存在");

        // 4. coupon discount
        BigDecimal discountAmount = BigDecimal.ZERO;
        Long couponId = dto.getCouponId();
        if (couponId != null) {
            discountAmount = couponService.calculateDiscount(couponId, userId, totalAmount);
            if (discountAmount.compareTo(BigDecimal.ZERO) > 0) {
                couponService.useCoupon(couponId, userId, null); // order id set later
            }
        }

        // 5. points
        BigDecimal pointsAmount = BigDecimal.ZERO;
        int pointsUsed = dto.getPointsUsed() != null ? dto.getPointsUsed() : 0;
        if (pointsUsed > 0) {
            pointsAmount = BigDecimal.valueOf(pointsUsed).divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP);
            if (pointsAmount.compareTo(totalAmount.subtract(discountAmount)) > 0) {
                pointsAmount = totalAmount.subtract(discountAmount);
                pointsUsed = pointsAmount.multiply(BigDecimal.valueOf(100)).intValue();
            }
            pointsService.deductPoints(userId, pointsUsed, "ORDER", null, "下单使用积分");
        }

        // 6. calculate pay amount
        BigDecimal payAmount = totalAmount.subtract(discountAmount).subtract(pointsAmount);
        if (payAmount.compareTo(BigDecimal.ZERO) < 0) payAmount = BigDecimal.ZERO;

        // 7. earned points
        int pointsEarned = payAmount.intValue(); // 1 yuan = 1 point

        // 8. create order
        String orderNo = generateOrderNo(userId);
        MallOrder order = new MallOrder();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setDiscountAmount(discountAmount);
        order.setPointsAmount(pointsAmount);
        order.setPayAmount(payAmount);
        order.setPointsUsed(pointsUsed);
        order.setPointsEarned(pointsEarned);
        order.setCouponId(couponId);
        order.setStatus(0); // pending payment
        order.setPayType(dto.getPayType() != null ? dto.getPayType() : 1);
        order.setReceiver(addr.getReceiver());
        order.setReceiverPhone(addr.getPhone());
        order.setReceiverAddress(addr.getProvince() + addr.getCity() + addr.getDistrict() + addr.getDetail());
        order.setRemark(dto.getRemark());
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, 30);
        order.setExpireTime(cal.getTime());
        orderMapper.insert(order);

        // 9. order items
        for (OrderItem item : orderItems) {
            item.setOrderId(order.getId());
            item.setOrderNo(orderNo);
        }
        itemMapper.batchInsert(orderItems);
        order.setItems(orderItems);

        // 10. clear cart
        for (CartItem c : selected) cartMapper.deleteById(c.getId());

        return order;
    }

    public MallOrder detail(Long id) {
        MallOrder order = orderMapper.selectById(id);
        if (order == null) throw BusinessException.of("订单不存在");
        order.setItems(itemMapper.selectByOrderId(id));
        return order;
    }

    public MallOrder detailByOrderNo(String orderNo) {
        MallOrder order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) throw BusinessException.of("订单不存在");
        order.setItems(itemMapper.selectByOrderId(order.getId()));
        return order;
    }

    public PageResult<MallOrder> myList(Integer status, int pageNum, int pageSize) {
        Long userId = UserContext.require().getUserId();
        List<MallOrder> orders = orderMapper.selectByUserId(userId, status, (pageNum-1)*pageSize, pageSize);
        long total = orderMapper.countByUserId(userId, status);
        for (MallOrder o : orders) o.setItems(itemMapper.selectByOrderId(o.getId()));
        return PageResult.of(orders, total, pageNum, pageSize);
    }

    public PageResult<MallOrder> adminList(String orderNo, Integer status, Long userId, int pageNum, int pageSize) {
        List<MallOrder> orders = orderMapper.selectList(orderNo, status, userId, (pageNum-1)*pageSize, pageSize);
        long total = orderMapper.count(orderNo, status, userId);
        for (MallOrder o : orders) o.setItems(itemMapper.selectByOrderId(o.getId()));
        return PageResult.of(orders, total, pageNum, pageSize);
    }

    @Transactional
    public void pay(Long id, Integer payType) {
        MallOrder order = orderMapper.selectById(id);
        if (order == null) throw BusinessException.of("订单不存在");
        if (order.getStatus() != 0) throw BusinessException.of("订单状态不正确");
        MallOrder update = new MallOrder();
        update.setId(id);
        update.setStatus(1);
        update.setPayType(payType);
        update.setPayTime(new Date());
        orderMapper.updateById(update);
        // award points
        if (order.getPointsEarned() > 0) {
            pointsService.addPoints(order.getUserId(), order.getPointsEarned(), "ORDER", order.getId(), "下单获得积分");
        }
    }

    @Transactional
    public void cancel(Long id) {
        MallOrder order = orderMapper.selectById(id);
        if (order == null) throw BusinessException.of("订单不存在");
        if (order.getStatus() != 0) throw BusinessException.of("只能取消待付款订单");
        // restore stock
        List<OrderItem> items = itemMapper.selectByOrderId(id);
        for (OrderItem item : items) {
            if (item.getSkuId() != null) {
                skuMapper.restoreStock(item.getSkuId(), item.getQuantity());
            } else {
                productMapper.restoreStock(item.getProductId(), item.getQuantity());
            }
        }
        // refund points
        if (order.getPointsUsed() > 0) {
            pointsService.refundPoints(order.getUserId(), order.getPointsUsed(), "ORDER_CANCEL", order.getId(), "订单取消退回积分");
        }
        MallOrder update = new MallOrder();
        update.setId(id);
        update.setStatus(4);
        update.setCloseTime(new Date());
        orderMapper.updateById(update);
    }

    @Transactional
    public void ship(Long id, String company, String no) {
        MallOrder order = orderMapper.selectById(id);
        if (order == null) throw BusinessException.of("订单不存在");
        if (order.getStatus() != 1) throw BusinessException.of("只能发货待发货订单");
        orderMapper.updateShip(id, company, no);
    }

    @Transactional
    public void receive(Long id) {
        MallOrder order = orderMapper.selectById(id);
        if (order == null) throw BusinessException.of("订单不存在");
        if (order.getStatus() != 2) throw BusinessException.of("只能确认待收货订单");
        MallOrder update = new MallOrder();
        update.setId(id);
        update.setStatus(3);
        update.setReceiveTime(new Date());
        orderMapper.updateById(update);
    }

    @Transactional
    public void refund(Long id) {
        MallOrder order = orderMapper.selectById(id);
        if (order == null) throw BusinessException.of("订单不存在");
        // restore stock
        List<OrderItem> items = itemMapper.selectByOrderId(id);
        for (OrderItem item : items) {
            if (item.getSkuId() != null) skuMapper.restoreStock(item.getSkuId(), item.getQuantity());
            else productMapper.restoreStock(item.getProductId(), item.getQuantity());
        }
        // refund points
        if (order.getPointsEarned() > 0) {
            pointsService.deductPoints(order.getUserId(), order.getPointsEarned(), "REFUND", order.getId(), "退款扣除积分");
        }
        if (order.getPointsUsed() > 0) {
            pointsService.refundPoints(order.getUserId(), order.getPointsUsed(), "REFUND", order.getId(), "退款退回使用积分");
        }
        MallOrder update = new MallOrder();
        update.setId(id);
        update.setStatus(5);
        update.setCloseTime(new Date());
        orderMapper.updateById(update);
    }

    private String generateOrderNo(Long userId) {
        return String.format("%d%06d%04d", System.currentTimeMillis() / 1000, userId % 1000000, new Random().nextInt(10000));
    }
}
