package com.mall.module.system.mapper;

import com.mall.module.system.entity.SysPermission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface SysPermissionMapper {
    int insert(SysPermission permission);
    int updateById(SysPermission permission);
    int deleteById(Long id);
    SysPermission selectById(Long id);
    List<SysPermission> selectAll();
    List<SysPermission> selectByParentId(@Param("parentId") Long parentId);
    List<SysPermission> selectByRoleIds(@Param("roleIds") List<Long> roleIds);
    int updateStatus(@Param("id") Long id, @Param("status") int status);
    long countByCode(@Param("code") String code, @Param("excludeId") Long excludeId);
}
