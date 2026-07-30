package com.mall.module.marketing.service;

import com.mall.common.PageResult;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mall.module.marketing.entity.ActivityProduct;
import com.mall.module.marketing.entity.MarketingActivity;
import com.mall.module.marketing.mapper.ActivityProductMapper;
import com.mall.module.marketing.mapper.MarketingActivityMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class MarketingOpsService {

    @Autowired
    private MarketingActivityMapper activityMapper;
    @Autowired
    private ActivityProductMapper apMapper;

    public PageResult<MarketingActivity> list(String name, String type, Integer status, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        PageInfo<MarketingActivity> info = new PageInfo<>(activityMapper.selectList(name, type, status));
        return PageResult.of(info.getList(), info.getTotal(), pageNum, pageSize);
    }

    public MarketingActivity getById(Long id) {
        MarketingActivity activity = activityMapper.selectById(id);
        if (activity != null) activity.setProducts(apMapper.selectByActivityId(id));
        return activity;
    }

    @Transactional
    public void save(MarketingActivity activity) {
        Date now = new Date();
        if (activity.getStartTime().after(now)) activity.setStatus(0);
        else if (activity.getEndTime().before(now)) activity.setStatus(2);
        else activity.setStatus(1);

        if (activity.getEnabled() == null) activity.setEnabled(1);

        if (activity.getId() == null) {
            activityMapper.insert(activity);
        } else {
            activityMapper.updateById(activity);
            apMapper.deleteByActivityId(activity.getId());
        }
        if (activity.getProducts() != null && !activity.getProducts().isEmpty()) {
            for (ActivityProduct ap : activity.getProducts()) {
                ap.setActivityId(activity.getId());
            }
            apMapper.batchInsert(activity.getProducts());
        }
    }

    @Transactional
    public void delete(Long id) {
        activityMapper.deleteById(id);
    }

    @Transactional
    public void updateStatus(Long id, int status) {
        activityMapper.updateStatus(id, status);
    }
}
