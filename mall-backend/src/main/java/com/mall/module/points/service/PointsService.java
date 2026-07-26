package com.mall.module.points.service;

import com.mall.common.PageResult;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mall.common.exception.BusinessException;
import com.mall.module.points.entity.PointsAccount;
import com.mall.module.points.entity.PointsLog;
import com.mall.module.points.mapper.PointsAccountMapper;
import com.mall.module.points.mapper.PointsLogMapper;
import com.mall.security.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class PointsService {
    @Autowired private PointsAccountMapper accountMapper;
    @Autowired private PointsLogMapper logMapper;

    private PointsAccount getOrCreate(Long userId) {
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

    public PointsAccount getAccount(Long userId) { return getOrCreate(userId); }

    public PointsAccount myAccount() { return getOrCreate(UserContext.require().getUserId()); }

    @Transactional
    public void addPoints(Long userId, int points, String source, Long refId, String remark) {
        if (points <= 0) return;
        getOrCreate(userId);
        int rows = accountMapper.addPoints(userId, points);
        if (rows == 0) throw BusinessException.of("积分增加失败");
        PointsAccount acc = accountMapper.selectByUserId(userId);
        PointsLog log = new PointsLog();
        log.setUserId(userId);
        log.setChangeType("EARN");
        log.setPoints(points);
        log.setBalanceAfter(acc.getBalance());
        log.setSource(source);
        log.setRefId(refId);
        log.setRemark(remark);
        logMapper.insert(log);
    }

    @Transactional
    public void deductPoints(Long userId, int points, String source, Long refId, String remark) {
        if (points <= 0) return;
        getOrCreate(userId);
        int rows = accountMapper.deductPoints(userId, points);
        if (rows == 0) throw BusinessException.of("积分不足");
        PointsAccount acc = accountMapper.selectByUserId(userId);
        PointsLog log = new PointsLog();
        log.setUserId(userId);
        log.setChangeType("USE");
        log.setPoints(points);
        log.setBalanceAfter(acc.getBalance());
        log.setSource(source);
        log.setRefId(refId);
        log.setRemark(remark);
        logMapper.insert(log);
    }

    @Transactional
    public void refundPoints(Long userId, int points, String source, Long refId, String remark) {
        getOrCreate(userId);
        accountMapper.addPoints(userId, points);
        PointsAccount acc = accountMapper.selectByUserId(userId);
        PointsLog log = new PointsLog();
        log.setUserId(userId);
        log.setChangeType("REFUND");
        log.setPoints(points);
        log.setBalanceAfter(acc.getBalance());
        log.setSource(source);
        log.setRefId(refId);
        log.setRemark(remark);
        logMapper.insert(log);
    }

    @Transactional
    public void dailySign() {
        Long userId = UserContext.require().getUserId();
        addPoints(userId, 10, "SIGN", null, "每日签到");
    }

    public PageResult<PointsLog> myLogs(String source, int pageNum, int pageSize) {
        Long userId = UserContext.require().getUserId();
        PageHelper.startPage(pageNum, pageSize);
        PageInfo<PointsLog> info = new PageInfo<>(logMapper.selectByUserId(userId, source));
        return PageResult.of(info.getList(), info.getTotal(), pageNum, pageSize);
    }

    public PageResult<PointsLog> adminLogs(Long userId, String source, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        PageInfo<PointsLog> info = new PageInfo<>(logMapper.selectByUserId(userId, source));
        return PageResult.of(info.getList(), info.getTotal(), pageNum, pageSize);
    }
}
