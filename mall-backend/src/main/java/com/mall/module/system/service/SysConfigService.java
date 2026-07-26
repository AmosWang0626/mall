package com.mall.module.system.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mall.common.PageResult;
import com.mall.common.RedisService;
import com.mall.module.system.entity.SysConfig;
import com.mall.module.system.mapper.SysConfigMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class SysConfigService {
    @Autowired private SysConfigMapper configMapper;
    @Autowired private RedisService redisService;
    private static final String CACHE_PREFIX = "sys:config:";

    public PageResult<SysConfig> list(String keyword, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        PageInfo<SysConfig> info = new PageInfo<>(configMapper.selectList(keyword));
        return PageResult.of(info.getList(), info.getTotal(), pageNum, pageSize);
    }

    public SysConfig getById(Long id) { return configMapper.selectById(id); }

    public String getValue(String key) {
        Object cached = redisService.get(CACHE_PREFIX + key);
        if (cached != null) return cached.toString();
        SysConfig config = configMapper.selectByKey(key);
        if (config != null) {
            redisService.set(CACHE_PREFIX + key, config.getConfigValue(), 1, TimeUnit.HOURS);
            return config.getConfigValue();
        }
        return null;
    }

    @Transactional
    public void save(SysConfig config) {
        if (config.getId() == null) {
            if (config.getIsSystem() == null) config.setIsSystem(0);
            if (config.getConfigType() == null) config.setConfigType("string");
            configMapper.insert(config);
        } else {
            configMapper.updateById(config);
        }
        redisService.delete(CACHE_PREFIX + config.getConfigKey());
    }

    @Transactional
    public void delete(Long id) {
        SysConfig config = configMapper.selectById(id);
        if (config != null && config.getIsSystem() == 1) {
            throw new RuntimeException("系统内置配置不可删除");
        }
        configMapper.deleteById(id);
        if (config != null) redisService.delete(CACHE_PREFIX + config.getConfigKey());
    }

    public List<SysConfig> all() { return configMapper.selectAll(); }
}
