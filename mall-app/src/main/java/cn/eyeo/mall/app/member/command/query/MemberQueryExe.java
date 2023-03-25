package cn.eyeo.mall.app.member.command.query;

import cn.eyeo.mall.app.member.assembler.MemberAssembler;
import cn.eyeo.mall.client.member.dto.data.MemberErrorCode;
import cn.eyeo.mall.client.member.dto.data.MemberVO;
import cn.eyeo.mall.common.exception.MallBizException;
import cn.eyeo.mall.gateway.impl.member.database.dataobject.MemberDO;
import cn.eyeo.mall.gateway.impl.member.database.mapper.MemberMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 用户信息查询
 *
 * @author daoyuan
 * @date 2021/2/14 23:27
 */
@Component
public class MemberQueryExe {

    @Autowired
    private MemberMapper memberMapper;

    public MemberVO execute(Long id) {
        MemberDO memberDO = memberMapper.selectByPrimaryKey(id);
        if (Objects.isNull(memberDO)) {
            throw new MallBizException(MemberErrorCode.B_MEMBER_UNDEFINED);
        }

        return MemberAssembler.toValueObject(memberDO);
    }

}
