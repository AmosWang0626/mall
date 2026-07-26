package com.mall.module.prize.mapper;

import com.mall.module.prize.entity.PrizeClaimLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PrizeClaimLogMapper {

    /** 插入领取记录 */
    void insert(PrizeClaimLog log);

    /** 统计用户在某奖池的领取次数 */
    int countByPoolAndUser(@Param("poolId") Long poolId, @Param("userId") Long userId);
}
