package com.mall.module.order.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mall.common.PageResult;
import com.mall.common.exception.BusinessException;
import com.mall.module.order.entity.MallOrder;
import com.mall.module.order.entity.OrderItem;
import com.mall.module.order.mapper.MallOrderMapper;
import com.mall.module.order.mapper.OrderItemMapper;
import com.mall.module.product.mapper.ProductMapper;
import com.mall.module.product.mapper.ProductSkuMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class OrderOpsService {

    @Autowired
    private MallOrderMapper orderMapper;
    @Autowired
    private OrderItemMapper itemMapper;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ProductSkuMapper skuMapper;
    @Autowired
    private com.mall.module.points.service.PointsService pointsService;

    public MallOrder getDetail(Long id) {
        MallOrder order = orderMapper.selectById(id);
        if (order == null) throw BusinessException.of("订单不存在");
        order.setItems(itemMapper.selectByOrderId(id));
        return order;
    }

    public PageResult<MallOrder> adminList(String orderNo, Integer status, Long userId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        PageInfo<MallOrder> info = new PageInfo<>(orderMapper.selectList(orderNo, status, userId));
        for (MallOrder o : info.getList()) o.setItems(itemMapper.selectByOrderId(o.getId()));
        return PageResult.of(info.getList(), info.getTotal(), pageNum, pageSize);
    }

    @Transactional
    public void ship(Long id, String company, String no) {
        MallOrder order = orderMapper.selectById(id);
        if (order == null) throw BusinessException.of("订单不存在");
        if (order.getStatus() != 1) throw BusinessException.of("只能发货待发货订单");
        orderMapper.updateShip(id, company, no);
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
}
