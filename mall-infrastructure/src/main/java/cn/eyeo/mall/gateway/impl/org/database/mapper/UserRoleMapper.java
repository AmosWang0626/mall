package cn.eyeo.mall.gateway.impl.org.database.mapper;

import cn.eyeo.mall.gateway.impl.org.database.dataobject.UserRoleDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserRoleMapper {
    int deleteByPrimaryKey(Long id);

    int insert(UserRoleDO row);

    UserRoleDO selectByPrimaryKey(Long id);

    List<UserRoleDO> selectAll();

    int updateByPrimaryKey(UserRoleDO row);
}