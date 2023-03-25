package cn.eyeo.mall.gateway.impl.org.database.mapper;

import cn.eyeo.mall.gateway.impl.org.database.dataobject.RolePermissionDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RolePermissionDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(RolePermissionDO row);

    RolePermissionDO selectByPrimaryKey(Long id);

    List<RolePermissionDO> selectAll();

    int updateByPrimaryKey(RolePermissionDO row);
}