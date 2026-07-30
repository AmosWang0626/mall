package com.mall.module.prize.service;

import com.mall.common.PageResult;
import com.mall.common.exception.BusinessException;
import com.mall.module.prize.entity.PrizePool;
import com.mall.module.prize.mapper.PrizePoolMapper;
import com.mall.module.prize.spi.PrizeDisplayInfo;
import com.mall.module.prize.spi.PrizeProvider;
import com.mall.module.prize.spi.PrizeProviderRegistry;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PrizePoolOpsService {

    private static final Logger log = LoggerFactory.getLogger(PrizePoolOpsService.class);

    @Autowired
    private PrizePoolMapper poolMapper;
    @Autowired
    private PrizeProviderRegistry providerRegistry;

    /** 获取所有奖品类型 (SPI 动态获取) */
    public List<Map<String, String>> getPrizeTypes() {
        List<Map<String, String>> result = new ArrayList<>();
        for (PrizeProvider provider : providerRegistry.getAllProviders()) {
            Map<String, String> item = new HashMap<>();
            item.put("type", provider.getType());
            item.put("displayName", provider.getDisplayName());
            result.add(item);
        }
        return result;
    }

    public PageResult<PrizePool> list(String name, Integer status, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        PageInfo<PrizePool> info = new PageInfo<>(poolMapper.selectList(name, status));
        for (PrizePool pool : info.getList()) {
            fillDisplayInfo(pool);
        }
        return PageResult.of(info.getList(), info.getTotal(), pageNum, pageSize);
    }

    public PrizePool getById(Long id) {
        PrizePool pool = poolMapper.selectById(id);
        if (pool == null) throw BusinessException.of("奖池不存在");
        fillDisplayInfo(pool);
        return pool;
    }

    @Transactional
    public void save(PrizePool pool) {
        if (pool.getId() == null) {
            if (pool.getStatus() == null) pool.setStatus(1);
            if (pool.getClaimedCount() == null) pool.setClaimedCount(0);
            if (pool.getPrizeType() == null) pool.setPrizeType("COUPON");
            poolMapper.insert(pool);
        } else {
            poolMapper.updateById(pool);
        }
    }

    @Transactional
    public void delete(Long id) {
        poolMapper.deleteById(id);
    }

    @Transactional
    public void updateStatus(Long id, int status) {
        poolMapper.updateStatus(id, status);
    }

    private void fillDisplayInfo(PrizePool pool) {
        try {
            PrizeProvider provider = providerRegistry.get(pool.getPrizeType());
            PrizeDisplayInfo info = provider.getDisplayInfo(pool);
            pool.setPrizeDisplayInfo(info);
        } catch (Exception e) {
            log.warn("填充奖品展示信息失败: poolId={}, prizeType={}, error={}",
                    pool.getId(), pool.getPrizeType(), e.getMessage());
        }
    }
}
