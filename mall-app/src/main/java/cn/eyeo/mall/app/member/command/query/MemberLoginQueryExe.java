package cn.eyeo.mall.app.member.command.query;

import cn.eyeo.mall.app.member.assembler.MemberAssembler;
import cn.eyeo.mall.client.member.dto.data.MemberErrorCode;
import cn.eyeo.mall.client.member.dto.data.MemberVO;
import cn.eyeo.mall.client.member.dto.query.MemberLoginQuery;
import cn.eyeo.mall.common.exception.MallBizException;
import cn.eyeo.mall.gateway.impl.member.database.dataobject.MemberDO;
import cn.eyeo.mall.gateway.impl.member.database.mapper.MemberMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 用户登录校验
 *
 * @author <a href="mailto:daoyuan0626@gmail.com">amos.wang</a>
 * @date 2021/1/10
 */
@Component
public class MemberLoginQueryExe {

    @Autowired
    private MemberMapper memberMapper;

    public MemberVO execute(MemberLoginQuery query) {
        MemberDO memberDO = memberMapper.selectByUsername(query.getUsername());
        if (Objects.isNull(memberDO)) {
            throw new MallBizException(MemberErrorCode.B_MEMBER_UNDEFINED);
        }

        // 校验密码是否正确
        if (!memberDO.getPassword().equals(query.getPassword())) {
            throw new MallBizException(MemberErrorCode.B_MEMBER_PASSWORD_ERROR);
        }

        return MemberAssembler.toValueObject(memberDO);
    }

}
