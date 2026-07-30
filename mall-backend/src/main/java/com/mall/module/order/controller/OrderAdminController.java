package com.mall.module.order.controller;

import com.mall.common.PageResult;
import com.mall.common.Result;
import com.mall.module.order.entity.MallOrder;
import com.mall.module.order.service.OrderOpsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/order")
public class OrderAdminController {

    @Autowired
    private OrderOpsService orderOpsService;

    @GetMapping("/list")
    public Result<PageResult<MallOrder>> list(@RequestParam(required = false) String orderNo,
                                               @RequestParam(required = false) Integer status,
                                               @RequestParam(required = false) Long userId,
                                               @RequestParam(defaultValue = "1") int pageNum,
                                               @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(orderOpsService.adminList(orderNo, status, userId, pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public Result<MallOrder> detail(@PathVariable Long id) {
        return Result.success(orderOpsService.getDetail(id));
    }

    @PutMapping("/{id}/ship")
    public Result<Void> ship(@PathVariable Long id,
                              @RequestParam String shipCompany,
                              @RequestParam String shipNo) {
        orderOpsService.ship(id, shipCompany, shipNo);
        return Result.success();
    }

    @PutMapping("/{id}/refund")
    public Result<Void> refund(@PathVariable Long id) {
        orderOpsService.refund(id);
        return Result.success();
    }
}
