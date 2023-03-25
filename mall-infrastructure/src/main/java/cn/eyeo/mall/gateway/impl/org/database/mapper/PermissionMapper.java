package cn.eyeo.mall.gateway.impl.org.database.mapper;

import cn.eyeo.mall.gateway.impl.org.database.dataobject.PermissionDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PermissionMapper {
    int deleteByPrimaryKey(Long id);

    int insert(PermissionDO row);

    PermissionDO selectByPrimaryKey(Long id);

    List<PermissionDO> selectAll();

    int updateByPrimaryKey(PermissionDO row);
}