package com.mall.module.coupon.mapper;

import com.mall.module.coupon.entity.CouponTemplate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface CouponTemplateMapper {
    int insert(CouponTemplate coupon);
    int updateById(CouponTemplate coupon);
    int deleteById(Long id);
    CouponTemplate selectById(Long id);
    List<CouponTemplate> selectList(@Param("name") String name, @Param("status") Integer status, @Param("offset") int offset, @Param("limit") int limit);
    long count(@Param("name") String name, @Param("status") Integer status);
    int updateStatus(@Param("id") Long id, @Param("status") int status);
    int incrementIssued(@Param("id") Long id);
    List<CouponTemplate> selectAvailable();
}
