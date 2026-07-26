package com.mall.module.system.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mall.common.PageResult;
import com.mall.module.system.entity.SysOperationLog;
import com.mall.module.system.mapper.SysOperationLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SysOperationLogService {
    @Autowired private SysOperationLogMapper logMapper;

    public PageResult<SysOperationLog> list(String module, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        PageInfo<SysOperationLog> info = new PageInfo<>(logMapper.selectList(module));
        return PageResult.of(info.getList(), info.getTotal(), pageNum, pageSize);
    }

    public void save(SysOperationLog log) { logMapper.insert(log); }
    public void delete(Long id) { logMapper.deleteById(id); }
}
