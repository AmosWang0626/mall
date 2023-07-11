package cn.eyeo.mall.gateway.impl.member.database.mapper;

import cn.eyeo.mall.gateway.impl.member.database.dataobject.MemberDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MemberMapper {

    int deleteByPrimaryKey(Long id);

    int insert(MemberDO row);

    MemberDO selectByPrimaryKey(Long id);

    List<MemberDO> selectAll();

    int updateByPrimaryKey(MemberDO row);

    int existByUsername(@Param("userId") Long userId,
                        @Param("username") String username);

    MemberDO selectByUsername(String username);
}