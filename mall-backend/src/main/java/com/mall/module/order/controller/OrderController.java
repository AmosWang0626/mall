package com.mall.module.order.controller;

import com.mall.common.PageResult;
import com.mall.common.Result;
import com.mall.module.order.dto.CreateOrderDTO;
import com.mall.module.order.entity.MallOrder;
import com.mall.module.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired private OrderService orderService;

    @PostMapping
    public Result<MallOrder> create(@RequestBody CreateOrderDTO dto) { return Result.success(orderService.create(dto)); }

    @GetMapping("/{id}")
    public Result<MallOrder> detail(@PathVariable Long id) { return Result.success(orderService.detail(id)); }

    @GetMapping("/no/{orderNo}")
    public Result<MallOrder> detailByNo(@PathVariable String orderNo) { return Result.success(orderService.detailByOrderNo(orderNo)); }

    @GetMapping("/my")
    public Result<PageResult<MallOrder>> myList(@RequestParam(required = false) Integer status,
                                                 @RequestParam(defaultValue = "1") int pageNum,
                                                 @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(orderService.myList(status, pageNum, pageSize));
    }

    @PutMapping("/{id}/pay")
    public Result<Void> pay(@PathVariable Long id, @RequestParam Integer payType) { orderService.pay(id, payType); return Result.success(); }

    @PutMapping("/{id}/cancel")
    public Result<Void> cancel(@PathVariable Long id) { orderService.cancel(id); return Result.success(); }

    @PutMapping("/{id}/receive")
    public Result<Void> receive(@PathVariable Long id) { orderService.receive(id); return Result.success(); }

    // ===== Admin =====
    @GetMapping("/list")
    public Result<PageResult<MallOrder>> adminList(@RequestParam(required = false) String orderNo,
                                                    @RequestParam(required = false) Integer status,
                                                    @RequestParam(required = false) Long userId,
                                                    @RequestParam(defaultValue = "1") int pageNum,
                                                    @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(orderService.adminList(orderNo, status, userId, pageNum, pageSize));
    }

    @PutMapping("/{id}/ship")
    public Result<Void> ship(@PathVariable Long id, @RequestParam String shipCompany, @RequestParam String shipNo) {
        orderService.ship(id, shipCompany, shipNo); return Result.success();
    }

    @PutMapping("/{id}/refund")
    public Result<Void> refund(@PathVariable Long id) { orderService.refund(id); return Result.success(); }
}
