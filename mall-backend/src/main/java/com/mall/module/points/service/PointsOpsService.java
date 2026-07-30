package com.mall.module.points.service;

import com.mall.common.PageResult;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mall.module.points.entity.PointsAccount;
import com.mall.module.points.entity.PointsLog;
import com.mall.module.points.mapper.PointsAccountMapper;
import com.mall.module.points.mapper.PointsLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PointsOpsService {

    @Autowired
    private PointsAccountMapper accountMapper;
    @Autowired
    private PointsLogMapper logMapper;

    public PointsAccount getAccount(Long userId) {
        PointsAccount acc = accountMapper.selectByUserId(userId);
        if (acc == null) {
            acc = new PointsAccount();
            acc.setUserId(userId);
            acc.setBalance(0);
            acc.setFrozen(0);
            acc.setTotalEarned(0L);
            acc.setTotalUsed(0L);
            acc.setVersion(0);
            accountMapper.insert(acc);
        }
        return acc;
    }

    public PageResult<PointsLog> adminLogs(Long userId, String source, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        PageInfo<PointsLog> info = new PageInfo<>(logMapper.selectByUserId(userId, source));
        return PageResult.of(info.getList(), info.getTotal(), pageNum, pageSize);
    }
}
