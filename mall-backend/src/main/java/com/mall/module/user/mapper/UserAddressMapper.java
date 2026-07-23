package com.mall.module.user.mapper;

import com.mall.module.user.entity.UserAddress;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface UserAddressMapper {
    int insert(UserAddress address);
    int updateById(UserAddress address);
    int deleteById(Long id);
    UserAddress selectById(Long id);
    List<UserAddress> selectByUserId(@Param("userId") Long userId);
    int clearDefault(@Param("userId") Long userId);
}
