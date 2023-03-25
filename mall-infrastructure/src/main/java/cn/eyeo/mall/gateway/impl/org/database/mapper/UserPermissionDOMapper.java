package cn.eyeo.mall.gateway.impl.org.database.mapper;

import cn.eyeo.mall.gateway.impl.org.database.dataobject.UserPermissionDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserPermissionDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(UserPermissionDO row);

    UserPermissionDO selectByPrimaryKey(Long id);

    List<UserPermissionDO> selectAll();

    int updateByPrimaryKey(UserPermissionDO row);
}