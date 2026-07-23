package com.mall.module.system.service;

import com.mall.common.exception.BusinessException;
import com.mall.module.system.entity.SysPermission;
import com.mall.module.system.mapper.SysPermissionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class SysPermissionService {
    @Autowired private SysPermissionMapper permissionMapper;

    public List<SysPermission> tree() {
        List<SysPermission> all = permissionMapper.selectAll();
        return buildTree(all, 0L);
    }

    private List<SysPermission> buildTree(List<SysPermission> all, Long parentId) {
        List<SysPermission> tree = new ArrayList<>();
        for (SysPermission p : all) {
            if (parentId.equals(p.getParentId())) {
                p.setChildren(buildTree(all, p.getId()));
                tree.add(p);
            }
        }
        return tree;
    }

    public List<SysPermission> list() { return permissionMapper.selectAll(); }

    public SysPermission getById(Long id) { return permissionMapper.selectById(id); }

    @Transactional
    public void save(SysPermission permission) {
        if (permission.getStatus() == null) permission.setStatus(1);
        if (permission.getVisible() == null) permission.setVisible(1);
        if (permission.getParentId() == null) permission.setParentId(0L);
        if (permission.getId() == null) {
            permissionMapper.insert(permission);
        } else {
            permissionMapper.updateById(permission);
        }
    }

    @Transactional
    public void delete(Long id) { permissionMapper.deleteById(id); }

    @Transactional
    public void updateStatus(Long id, int status) { permissionMapper.updateStatus(id, status); }

    public List<SysPermission> getByRoleIds(List<Long> roleIds) {
        return permissionMapper.selectByRoleIds(roleIds);
    }
}
