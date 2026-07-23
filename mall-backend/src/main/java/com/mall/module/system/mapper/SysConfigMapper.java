package com.mall.module.system.mapper;

import com.mall.module.system.entity.SysConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface SysConfigMapper {
    int insert(SysConfig config);
    int updateById(SysConfig config);
    int deleteById(Long id);
    SysConfig selectById(Long id);
    SysConfig selectByKey(@Param("key") String key);
    List<SysConfig> selectList(@Param("keyword") String keyword, @Param("offset") int offset, @Param("limit") int limit);
    long count(@Param("keyword") String keyword);
    List<SysConfig> selectAll();
}
