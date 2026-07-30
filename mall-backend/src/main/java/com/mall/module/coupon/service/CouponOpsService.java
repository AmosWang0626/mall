package com.mall.module.coupon.service;

import com.mall.common.PageResult;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mall.module.coupon.entity.CouponTemplate;
import com.mall.module.coupon.mapper.CouponTemplateMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CouponOpsService {

    @Autowired
    private CouponTemplateMapper templateMapper;

    public PageResult<CouponTemplate> list(String name, Integer status, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        PageInfo<CouponTemplate> info = new PageInfo<>(templateMapper.selectList(name, status));
        return PageResult.of(info.getList(), info.getTotal(), pageNum, pageSize);
    }

    public CouponTemplate getById(Long id) {
        return templateMapper.selectById(id);
    }

    @Transactional
    public void save(CouponTemplate coupon) {
        if (coupon.getId() == null) {
            coupon.setIssuedCount(0);
            if (coupon.getStatus() == null) coupon.setStatus(1);
            templateMapper.insert(coupon);
        } else {
            templateMapper.updateById(coupon);
        }
    }

    @Transactional
    public void delete(Long id) {
        templateMapper.deleteById(id);
    }

    @Transactional
    public void updateStatus(Long id, int status) {
        templateMapper.updateStatus(id, status);
    }
}
