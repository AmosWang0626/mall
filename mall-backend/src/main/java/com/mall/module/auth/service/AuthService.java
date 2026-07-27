package com.mall.module.auth.service;

import cn.hutool.crypto.digest.BCrypt;
import com.mall.common.exception.BusinessException;
import com.mall.module.auth.dto.LoginDTO;
import com.mall.module.auth.dto.RegisterDTO;
import com.mall.module.auth.vo.LoginVO;
import com.mall.module.system.entity.SysAdmin;
import com.mall.module.system.mapper.SysAdminMapper;
import com.mall.module.user.entity.MallUser;
import com.mall.module.user.mapper.MallUserMapper;
import com.mall.security.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 认证服务
 */
@Slf4j
@Service
public class AuthService {

    @Autowired
    private MallUserMapper userMapper;

    @Autowired
    private SysAdminMapper adminMapper;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 用户登录
     */
    @Transactional
    public LoginVO login(LoginDTO dto) {
        if ("admin".equals(dto.getType())) {
            return adminLogin(dto);
        }
        return userLogin(dto);
    }

    private LoginVO userLogin(LoginDTO dto) {
        MallUser user = userMapper.selectByUsername(dto.getUsername());
        if (user == null) {
            throw BusinessException.of(401, "用户不存在");
        }
        if (user.getStatus() == 0) {
            throw BusinessException.of(403, "账号已被禁用");
        }
        if (!BCrypt.checkpw(dto.getPassword(), user.getPassword())) {
            throw BusinessException.of(401, "密码错误");
        }
        userMapper.updateLastLogin(user.getId());

        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), "user");
        LoginVO vo = new LoginVO();
        vo.setToken(token);
        vo.setUserId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setAvatar(user.getAvatar());
        vo.setType("user");
        return vo;
    }

    private LoginVO adminLogin(LoginDTO dto) {
        SysAdmin admin = adminMapper.selectByUsername(dto.getUsername());
        if (admin == null) {
            throw BusinessException.of(401, "管理员不存在");
        }
        if (admin.getStatus() == 0) {
            throw BusinessException.of(403, "账号已被禁用");
        }
        if (!BCrypt.checkpw(dto.getPassword(), admin.getPassword())) {
            throw BusinessException.of(401, "密码错误");
        }
        adminMapper.updateLastLogin(admin.getId());

        String token = jwtUtil.generateToken(admin.getId(), admin.getUsername(), "admin");
        LoginVO vo = new LoginVO();
        vo.setToken(token);
        vo.setUserId(admin.getId());
        vo.setUsername(admin.getUsername());
        vo.setNickname(admin.getNickname());
        vo.setAvatar(admin.getAvatar());
        vo.setType("admin");
        return vo;
    }

    /**
     * 用户注册
     */
    @Transactional
    public LoginVO register(RegisterDTO dto) {
        MallUser exist = userMapper.selectByUsername(dto.getUsername());
        if (exist != null) {
            throw BusinessException.of(400, "用户名已存在");
        }

        MallUser user = new MallUser();
        user.setUsername(dto.getUsername());
        user.setPassword(BCrypt.hashpw(dto.getPassword()));
        user.setNickname(dto.getNickname() != null ? dto.getNickname() : dto.getUsername());
        user.setPhone(dto.getPhone());
        user.setAvatar(dto.getAvatar());
        user.setGender(0);
        user.setStatus(1);
        user.setRegisterIp("0.0.0.0");
        user.setLastLogin(new Date());
        userMapper.insert(user);

        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), "user");
        LoginVO vo = new LoginVO();
        vo.setToken(token);
        vo.setUserId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setAvatar(user.getAvatar());
        vo.setType("user");
        return vo;
    }

    /**
     * 获取当前管理员权限列表
     */
    public List<String> getPermissions(Long adminId) {
        return adminMapper.selectPermissions(adminId);
    }
}
