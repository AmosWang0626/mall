package com.mall.module.system.controller;

import com.mall.common.PageResult;
import com.mall.common.Result;
import com.mall.module.system.entity.SysOperationLog;
import com.mall.module.system.service.SysOperationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/system/log")
public class SysOperationLogController {
    @Autowired private SysOperationLogService logService;

    @GetMapping("/list")
    public Result<PageResult<SysOperationLog>> list(@RequestParam(required=false) String module,
                                                     @RequestParam(defaultValue="1") int pageNum,
                                                     @RequestParam(defaultValue="10") int pageSize) {
        return Result.success(logService.list(module, pageNum, pageSize));
    }
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) { logService.delete(id); return Result.success(); }
}
