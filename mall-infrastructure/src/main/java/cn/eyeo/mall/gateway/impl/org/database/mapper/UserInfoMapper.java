package cn.eyeo.mall.gateway.impl.org.database.mapper;

import cn.eyeo.mall.gateway.impl.org.database.dataobject.UserInfoDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserInfoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(UserInfoDO row);

    UserInfoDO selectByPrimaryKey(Long id);

    List<UserInfoDO> selectAll();

    int updateByPrimaryKey(UserInfoDO row);
}