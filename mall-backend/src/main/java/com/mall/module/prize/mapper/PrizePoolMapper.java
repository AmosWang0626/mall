package com.mall.module.prize.mapper;

import com.mall.module.prize.entity.PrizePool;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PrizePoolMapper {

    /** 新增 */
    void insert(PrizePool pool);

    /** 更新 */
    void updateById(PrizePool pool);

    /** 逻辑删除 */
    void deleteById(@Param("id") Long id);

    /** 按ID查询 */
    PrizePool selectById(@Param("id") Long id);

    /** 查询启用的奖池列表(含优惠券详情), 按sort降序 */
    List<PrizePool> selectActiveList();

    /** 查询Banner列表(仅id/name/bannerText/bannerColor等, 不含优惠券详情) */
    List<PrizePool> selectBannerList();

    /** 原子扣减库存: claimed_count+1, WHERE id=? AND (total_stock=-1 OR claimed_count<total_stock) */
    int incrementClaimed(@Param("id") Long id);

    /** 更新状态 */
    void updateStatus(@Param("id") Long id, @Param("status") int status);

    /** 管理端分页查询 */
    List<PrizePool> selectList(@Param("name") String name, @Param("status") Integer status);
}
