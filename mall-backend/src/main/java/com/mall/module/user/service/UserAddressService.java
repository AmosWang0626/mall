package com.mall.module.user.service;

import com.mall.common.exception.BusinessException;
import com.mall.module.user.entity.UserAddress;
import com.mall.module.user.mapper.UserAddressMapper;
import com.mall.security.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class UserAddressService {
    @Autowired
    private UserAddressMapper addressMapper;

    public List<UserAddress> list() {
        return addressMapper.selectByUserId(UserContext.require().getUserId());
    }

    @Transactional
    public void save(UserAddress addr) {
        Long userId = UserContext.require().getUserId();
        addr.setUserId(userId);
        if (addr.getIsDefault() != null && addr.getIsDefault() == 1) {
            addressMapper.clearDefault(userId);
        }
        if (addr.getId() == null) {
            addressMapper.insert(addr);
        } else {
            addressMapper.updateById(addr);
        }
    }

    @Transactional
    public void delete(Long id) {
        addressMapper.deleteById(id);
    }

    public UserAddress getById(Long id) {
        return addressMapper.selectById(id);
    }

    public UserAddress getDefault() {
        List<UserAddress> list = addressMapper.selectByUserId(UserContext.require().getUserId());
        return list.stream().filter(a -> a.getIsDefault() == 1).findFirst().orElse(list.isEmpty() ? null : list.get(0));
    }
}
