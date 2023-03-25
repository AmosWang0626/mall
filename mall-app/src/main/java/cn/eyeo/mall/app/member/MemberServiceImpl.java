package cn.eyeo.mall.app.member;

import cn.eyeo.mall.app.member.command.MemberModifyCmdExe;
import cn.eyeo.mall.app.member.command.MemberRegisterCmdExe;
import cn.eyeo.mall.app.member.command.query.MemberLoginQueryExe;
import cn.eyeo.mall.app.member.command.query.MemberQueryExe;
import cn.eyeo.mall.client.member.api.IMemberService;
import cn.eyeo.mall.client.member.dto.MemberModifyCmd;
import cn.eyeo.mall.client.member.dto.MemberRegisterCmd;
import cn.eyeo.mall.client.member.dto.data.MemberVO;
import cn.eyeo.mall.client.member.dto.query.MemberLoginQuery;
import com.alibaba.cola.dto.SingleResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * C端用户相关
 *
 * @author <a href="mailto:daoyuan0626@gmail.com">amos.wang</a>
 * @date 2023/3/25
 */
@Service
public class MemberServiceImpl implements IMemberService {

    @Autowired
    private MemberRegisterCmdExe memberRegisterCmdExe;
    @Autowired
    private MemberModifyCmdExe memberModifyCmdExe;
    @Autowired
    private MemberQueryExe memberQueryExe;
    @Autowired
    private MemberLoginQueryExe memberLoginQueryExe;

    @Override
    public SingleResponse<MemberVO> register(MemberRegisterCmd cmd) {
        return SingleResponse.of(memberRegisterCmdExe.execute(cmd));
    }

    @Override
    public SingleResponse<MemberVO> modify(MemberModifyCmd cmd) {
        return SingleResponse.of(memberModifyCmdExe.execute(cmd));
    }

    @Override
    public void login(MemberLoginQuery query) {
        memberLoginQueryExe.execute(query);
    }

    @Override
    public SingleResponse<MemberVO> getUserInfo(Long id) {
        return SingleResponse.of(memberQueryExe.execute(id));
    }
}
