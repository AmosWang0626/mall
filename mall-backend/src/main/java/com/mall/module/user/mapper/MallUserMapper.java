package com.mall.module.user.mapper;

import com.mall.module.user.entity.MallUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface MallUserMapper {

    int insert(MallUser user);

    int updateById(MallUser user);

    int deleteById(Long id);

    MallUser selectById(Long id);

    MallUser selectByUsername(@Param("username") String username);

    List<MallUser> selectList(@Param("keyword") String keyword,
                              @Param("status") Integer status,
                              @Param("offset") int offset,
                              @Param("limit") int limit);

    long count(@Param("keyword") String keyword, @Param("status") Integer status);

    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    int updateLastLogin(@Param("id") Long id);
}
