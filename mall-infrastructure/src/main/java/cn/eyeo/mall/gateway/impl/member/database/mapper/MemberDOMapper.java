package cn.eyeo.mall.gateway.impl.member.database.mapper;

import cn.eyeo.mall.gateway.impl.member.database.dataobject.MemberDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MemberDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(MemberDO row);

    MemberDO selectByPrimaryKey(Long id);

    List<MemberDO> selectAll();

    int updateByPrimaryKey(MemberDO row);
}