package com.mall.module.system.service;

import cn.hutool.crypto.digest.BCrypt;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mall.common.PageResult;
import com.mall.common.exception.BusinessException;
import com.mall.module.system.entity.SysAdmin;
import com.mall.module.system.entity.SysRole;
import com.mall.module.system.mapper.SysAdminMapper;
import com.mall.module.system.mapper.SysRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;

@Service
public class SysAdminService {
    @Autowired private SysAdminMapper adminMapper;
    @Autowired private SysRoleMapper roleMapper;

    public PageResult<SysAdmin> list(String keyword, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<SysAdmin> list = adminMapper.selectList(keyword, (pageNum-1)*pageSize, pageSize);
        list.forEach(a -> a.setPassword(null));
        PageInfo<SysAdmin> info = new PageInfo<>(list);
        return PageResult.of(info.getList(), info.getTotal(), pageNum, pageSize);
    }

    public SysAdmin getById(Long id) {
        SysAdmin admin = adminMapper.selectById(id);
        if (admin != null) admin.setPassword(null);
        return admin;
    }

    @Transactional
    public void save(SysAdmin admin) {
        if (admin.getPassword() != null && !admin.getPassword().isEmpty()) {
            admin.setPassword(BCrypt.hashpw(admin.getPassword()));
        } else {
            admin.setPassword(BCrypt.hashpw("123456"));
        }
        if (admin.getStatus() == null) admin.setStatus(1);
        adminMapper.insert(admin);
    }

    @Transactional
    public void update(SysAdmin admin) {
        if (admin.getPassword() != null && !admin.getPassword().isEmpty()) {
            admin.setPassword(BCrypt.hashpw(admin.getPassword()));
        } else {
            admin.setPassword(null);
        }
        adminMapper.updateById(admin);
    }

    @Transactional
    public void updateStatus(Long id, int status) { adminMapper.updateStatus(id, status); }

    @Transactional
    public void delete(Long id) { adminMapper.deleteById(id); }

    @Transactional
    public void assignRoles(Long adminId, List<Long> roleIds) {
        roleMapper.deleteAdminRoles(adminId);
        if (roleIds != null) {
            for (Long roleId : roleIds) roleMapper.insertAdminRole(adminId, roleId);
        }
    }

    public List<Long> getRoleIds(Long adminId) { return adminMapper.selectRoleIds(adminId); }
    public List<String> getPermissions(Long adminId) { return adminMapper.selectPermissions(adminId); }
    public List<SysRole> getRoles(Long adminId) { return roleMapper.selectByAdminId(adminId); }

    @Transactional
    public void resetPassword(Long id, String newPwd) {
        SysAdmin admin = new SysAdmin();
        admin.setId(id);
        admin.setPassword(BCrypt.hashpw(newPwd));
        adminMapper.updateById(admin);
    }
}
