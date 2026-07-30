package com.mall.module.marketing.service;

import com.mall.module.marketing.entity.ActivityProduct;
import com.mall.module.marketing.entity.MarketingActivity;
import com.mall.module.marketing.mapper.ActivityProductMapper;
import com.mall.module.marketing.mapper.MarketingActivityMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MarketingService {

    @Autowired
    private MarketingActivityMapper activityMapper;
    @Autowired
    private ActivityProductMapper apMapper;

    public List<MarketingActivity> activeList(String type) {
        List<MarketingActivity> list = activityMapper.selectActive(type);
        for (MarketingActivity a : list) a.setProducts(apMapper.selectByActivityId(a.getId()));
        return list;
    }

    public List<ActivityProduct> getActivityProducts(Long activityId) {
        return apMapper.selectByActivityId(activityId);
    }
}
