package com.mall.module.system.mapper;

import com.mall.module.system.entity.SysRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface SysRoleMapper {
    int insert(SysRole role);
    int updateById(SysRole role);
    int deleteById(Long id);
    SysRole selectById(Long id);
    SysRole selectByCode(@Param("code") String code);
    List<SysRole> selectList(@Param("keyword") String keyword);
    long count(@Param("keyword") String keyword);
    List<SysRole> selectAll();
    int updateStatus(@Param("id") Long id, @Param("status") int status);
    int insertRolePermission(@Param("roleId") Long roleId, @Param("permissionId") Long permissionId);
    int deleteRolePermissions(@Param("roleId") Long roleId);
    List<Long> selectPermissionIds(@Param("roleId") Long roleId);
    List<SysRole> selectByAdminId(@Param("adminId") Long adminId);
    int insertAdminRole(@Param("adminId") Long adminId, @Param("roleId") Long roleId);
    int deleteAdminRoles(@Param("adminId") Long adminId);
    List<Long> selectRoleIdsByAdminId(@Param("adminId") Long adminId);
}
