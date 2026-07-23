package com.mall.module.cart.controller;

import com.mall.common.Result;
import com.mall.module.cart.entity.CartItem;
import com.mall.module.cart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {
    @Autowired private CartService cartService;

    @GetMapping("/list")
    public Result<List<CartItem>> list() { return Result.success(cartService.list()); }

    @PostMapping
    public Result<Void> add(@RequestBody CartItem item) { cartService.add(item); return Result.success(); }

    @PutMapping("/{id}/quantity")
    public Result<Void> updateQuantity(@PathVariable Long id, @RequestParam int quantity) { cartService.updateQuantity(id, quantity); return Result.success(); }

    @PutMapping("/selected")
    public Result<Void> updateSelected(@RequestParam int selected) { cartService.updateSelected(selected); return Result.success(); }

    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) { cartService.remove(id); return Result.success(); }

    @DeleteMapping("/clear")
    public Result<Void> clear() { cartService.clear(); return Result.success(); }

    @GetMapping("/count")
    public Result<Integer> count() { return Result.success(cartService.count()); }
}
