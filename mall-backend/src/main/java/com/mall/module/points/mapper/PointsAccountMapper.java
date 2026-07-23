package com.mall.module.points.mapper;

import com.mall.module.points.entity.PointsAccount;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PointsAccountMapper {
    int insert(PointsAccount account);
    int updateById(PointsAccount account);
    PointsAccount selectByUserId(@Param("userId") Long userId);
    int addPoints(@Param("userId") Long userId, @Param("points") int points);
    int deductPoints(@Param("userId") Long userId, @Param("points") int points);
}
