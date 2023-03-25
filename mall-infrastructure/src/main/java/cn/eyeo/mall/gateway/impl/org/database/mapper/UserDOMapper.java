package cn.eyeo.mall.gateway.impl.org.database.mapper;

import cn.eyeo.mall.gateway.impl.org.database.dataobject.UserDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(UserDO row);

    UserDO selectByPrimaryKey(Long id);

    List<UserDO> selectAll();

    int updateByPrimaryKey(UserDO row);
}