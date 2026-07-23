package com.mall.module.marketing.mapper;

import com.mall.module.marketing.entity.MarketingActivity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface MarketingActivityMapper {
    int insert(MarketingActivity activity);
    int updateById(MarketingActivity activity);
    int deleteById(Long id);
    MarketingActivity selectById(Long id);
    List<MarketingActivity> selectList(@Param("name") String name, @Param("type") String type, @Param("status") Integer status, @Param("offset") int offset, @Param("limit") int limit);
    long count(@Param("name") String name, @Param("type") String type, @Param("status") Integer status);
    int updateStatus(@Param("id") Long id, @Param("status") int status);
    List<MarketingActivity> selectActive(@Param("type") String type);
}
