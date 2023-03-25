package cn.eyeo.mall.app.member.command;

import cn.eyeo.mall.app.member.assembler.MemberAssembler;
import cn.eyeo.mall.client.common.SystemErrorCode;
import cn.eyeo.mall.client.member.dto.MemberRegisterCmd;
import cn.eyeo.mall.client.member.dto.data.MemberErrorCode;
import cn.eyeo.mall.client.member.dto.data.MemberVO;
import cn.eyeo.mall.common.exception.MallBizException;
import cn.eyeo.mall.gateway.impl.member.database.dataobject.MemberDO;
import cn.eyeo.mall.gateway.impl.member.database.mapper.MemberMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * UserAddCmdExe
 *
 * @author <a href="mailto:daoyuan0626@gmail.com">amos.wang</a>
 * @date 2021/1/10
 */
@Component
public class MemberRegisterCmdExe {

    @Autowired
    private MemberMapper memberMapper;

    public MemberVO execute(MemberRegisterCmd cmd) {
        // check 用户名是否重复
        if (memberMapper.existByUsername(null, cmd.getUsername()) > 0) {
            throw new MallBizException(MemberErrorCode.B_MEMBER_USERNAME_REPEAT);
        }

        MemberDO memberDO = MemberAssembler.toDO(cmd);
        int result = memberMapper.insert(memberDO);
        if (result <= 0) {
            throw new MallBizException(SystemErrorCode.S_DB_WRITE_ERROR);
        }

        return MemberAssembler.toValueObject(memberDO);
    }

}
