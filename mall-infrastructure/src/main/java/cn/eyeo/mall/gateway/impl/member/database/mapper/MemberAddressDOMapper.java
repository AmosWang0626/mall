package cn.eyeo.mall.gateway.impl.member.database.mapper;

import cn.eyeo.mall.gateway.impl.member.database.dataobject.MemberAddressDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MemberAddressDOMapper   {
    int deleteByPrimaryKey(Long id);

    int insert(MemberAddressDO row);

    MemberAddressDO selectByPrimaryKey(Long id);

    List<MemberAddressDO> selectAll();

    int updateByPrimaryKey(MemberAddressDO row);
}