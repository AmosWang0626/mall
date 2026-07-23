package com.mall.module.user.controller;

import com.mall.common.Result;
import com.mall.module.user.entity.UserAddress;
import com.mall.module.user.service.UserAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/user/address")
public class UserAddressController {
    @Autowired
    private UserAddressService addressService;

    @GetMapping("/list")
    public Result<List<UserAddress>> list() { return Result.success(addressService.list()); }

    @PostMapping
    public Result<Void> save(@RequestBody UserAddress addr) { addressService.save(addr); return Result.success(); }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) { addressService.delete(id); return Result.success(); }

    @GetMapping("/default")
    public Result<UserAddress> getDefault() { return Result.success(addressService.getDefault()); }
}
