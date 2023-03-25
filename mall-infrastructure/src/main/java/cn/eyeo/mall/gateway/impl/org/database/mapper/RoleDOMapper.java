package cn.eyeo.mall.gateway.impl.org.database.mapper;

import cn.eyeo.mall.gateway.impl.org.database.dataobject.RoleDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RoleDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(RoleDO row);

    RoleDO selectByPrimaryKey(Long id);

    List<RoleDO> selectAll();

    int updateByPrimaryKey(RoleDO row);
}