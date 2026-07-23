package com.mall.module.system.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mall.common.PageResult;
import com.mall.common.exception.BusinessException;
import com.mall.module.system.entity.SysPermission;
import com.mall.module.system.entity.SysRole;
import com.mall.module.system.mapper.SysPermissionMapper;
import com.mall.module.system.mapper.SysRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class SysRoleService {
    @Autowired private SysRoleMapper roleMapper;
    @Autowired private SysPermissionMapper permissionMapper;

    public PageResult<SysRole> list(String keyword, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        PageInfo<SysRole> info = new PageInfo<>(roleMapper.selectList(keyword, (pageNum-1)*pageSize, pageSize));
        return PageResult.of(info.getList(), info.getTotal(), pageNum, pageSize);
    }

    public List<SysRole> all() { return roleMapper.selectAll(); }

    public SysRole getById(Long id) { return roleMapper.selectById(id); }

    @Transactional
    public void save(SysRole role) {
        if (role.getId() == null) {
            if (role.getStatus() == null) role.setStatus(1);
            if (role.getDataScope() == null) role.setDataScope(1);
            roleMapper.insert(role);
        } else {
            roleMapper.updateById(role);
        }
    }

    @Transactional
    public void delete(Long id) {
        roleMapper.deleteRolePermissions(id);
        roleMapper.deleteById(id);
    }

    @Transactional
    public void updateStatus(Long id, int status) { roleMapper.updateStatus(id, status); }

    @Transactional
    public void assignPermissions(Long roleId, List<Long> permissionIds) {
        roleMapper.deleteRolePermissions(roleId);
        if (permissionIds != null) {
            for (Long pid : permissionIds) roleMapper.insertRolePermission(roleId, pid);
        }
    }

    public List<Long> getPermissionIds(Long roleId) { return roleMapper.selectPermissionIds(roleId); }

    public List<SysPermission> getPermissionsByRoleIds(List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) return List.of();
        return permissionMapper.selectByRoleIds(roleIds);
    }
}
