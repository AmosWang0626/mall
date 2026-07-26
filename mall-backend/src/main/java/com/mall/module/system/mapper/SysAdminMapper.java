package com.mall.module.system.mapper;

import com.mall.module.system.entity.SysAdmin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface SysAdminMapper {

    int insert(SysAdmin admin);

    int updateById(SysAdmin admin);

    int deleteById(Long id);

    SysAdmin selectById(Long id);

    SysAdmin selectByUsername(@Param("username") String username);

    List<SysAdmin> selectList(@Param("keyword") String keyword);

    long count(@Param("keyword") String keyword);

    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    int updateLastLogin(@Param("id") Long id);

    List<String> selectPermissions(@Param("adminId") Long adminId);

    List<Long> selectRoleIds(@Param("adminId") Long adminId);
}
