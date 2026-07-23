package com.mall.module.points.mapper;

import com.mall.module.points.entity.PointsLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface PointsLogMapper {
    int insert(PointsLog log);
    List<PointsLog> selectByUserId(@Param("userId") Long userId, @Param("source") String source, @Param("offset") int offset, @Param("limit") int limit);
    long countByUserId(@Param("userId") Long userId, @Param("source") String source);
}
