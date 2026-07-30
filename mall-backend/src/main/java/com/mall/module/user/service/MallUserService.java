package com.mall.module.user.service;

import com.mall.common.exception.BusinessException;
import com.mall.module.user.entity.MallUser;
import com.mall.module.user.mapper.MallUserMapper;
import cn.hutool.crypto.digest.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MallUserService {

    @Autowired
    private MallUserMapper userMapper;

    public MallUser getById(Long id) {
        MallUser user = userMapper.selectById(id);
        if (user == null) throw BusinessException.of("用户不存在");
        user.setPassword(null);
        return user;
    }

    public MallUser getCurrentUser() {
        return getById(com.mall.security.UserContext.require().getUserId());
    }

    @Transactional
    public void updateProfile(MallUser user) {
        user.setId(com.mall.security.UserContext.require().getUserId());
        user.setPassword(null);
        user.setStatus(null);
        userMapper.updateById(user);
    }

    @Transactional
    public void changePassword(String oldPassword, String newPassword) {
        Long userId = com.mall.security.UserContext.require().getUserId();
        MallUser user = userMapper.selectById(userId);
        if (!BCrypt.checkpw(oldPassword, user.getPassword())) {
            throw BusinessException.of("原密码错误");
        }
        MallUser update = new MallUser();
        update.setId(userId);
        update.setPassword(BCrypt.hashpw(newPassword));
        userMapper.updateById(update);
    }
}
