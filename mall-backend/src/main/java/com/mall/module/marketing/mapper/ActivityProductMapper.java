package com.mall.module.marketing.mapper;

import com.mall.module.marketing.entity.ActivityProduct;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ActivityProductMapper {
    int insert(ActivityProduct ap);
    int batchInsert(@Param("list") List<ActivityProduct> list);
    int deleteByActivityId(@Param("activityId") Long activityId);
    List<ActivityProduct> selectByActivityId(@Param("activityId") Long activityId);
    List<ActivityProduct> selectByProductId(@Param("productId") Long productId);
}
