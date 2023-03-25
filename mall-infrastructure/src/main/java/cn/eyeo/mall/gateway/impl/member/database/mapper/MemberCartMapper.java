package cn.eyeo.mall.gateway.impl.member.database.mapper;

import cn.eyeo.mall.gateway.impl.member.database.dataobject.MemberCartDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MemberCartMapper {
    int deleteByPrimaryKey(Long id);

    int insert(MemberCartDO row);

    MemberCartDO selectByPrimaryKey(Long id);

    List<MemberCartDO> selectAll();

    int updateByPrimaryKey(MemberCartDO row);
}