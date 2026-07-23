package com.mall.module.order.mapper;

import com.mall.module.order.entity.MallOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface MallOrderMapper {
    int insert(MallOrder order);
    int updateById(MallOrder order);
    MallOrder selectById(Long id);
    MallOrder selectByOrderNo(@Param("orderNo") String orderNo);
    List<MallOrder> selectByUserId(@Param("userId") Long userId, @Param("status") Integer status, @Param("offset") int offset, @Param("limit") int limit);
    long countByUserId(@Param("userId") Long userId, @Param("status") Integer status);
    List<MallOrder> selectList(@Param("orderNo") String orderNo, @Param("status") Integer status, @Param("userId") Long userId, @Param("offset") int offset, @Param("limit") int limit);
    long count(@Param("orderNo") String orderNo, @Param("status") Integer status, @Param("userId") Long userId);
    int updateStatus(@Param("id") Long id, @Param("status") int status);
    int updateShip(@Param("id") Long id, @Param("shipCompany") String shipCompany, @Param("shipNo") String shipNo);
}
