package cn.eyeo.mall.adapter.web;

import cn.eyeo.mall.adapter.web.common.api.CurrentUserContext;
import cn.eyeo.mall.adapter.web.common.api.MallOpsResponse;
import cn.eyeo.mall.adapter.web.common.api.Token;
import cn.eyeo.mall.client.member.api.IMemberService;
import cn.eyeo.mall.client.member.dto.MemberModifyCmd;
import cn.eyeo.mall.client.member.dto.MemberRegisterCmd;
import cn.eyeo.mall.client.member.dto.data.MemberVO;
import cn.eyeo.mall.client.member.dto.query.MemberLoginQuery;
import com.alibaba.cola.dto.SingleResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * C端用户相关
 *
 * @author <a href="mailto:daoyuan0626@gmail.com">amos.wang</a>
 * @date 2021/1/8
 */
@RestController
@RequestMapping("user")
public class MemberController {

    @Autowired
    private IMemberService iMemberService;

    @PostMapping(value = "register")
    public MallOpsResponse<MemberVO> register(@RequestBody MemberRegisterCmd cmd) {
        SingleResponse<MemberVO> response = iMemberService.register(cmd);
        if (response.isSuccess()) {
            return MallOpsResponse.success(response.getData());
        }
        return MallOpsResponse.fail(response);
    }

    @PostMapping(value = "modify")
    public MallOpsResponse<MemberVO> modify(@RequestBody MemberModifyCmd cmd) {
        SingleResponse<MemberVO> response = iMemberService.modify(cmd);
        if (response.isSuccess()) {
            return MallOpsResponse.success(response.getData());
        }
        return MallOpsResponse.fail(response);
    }

    @Token(check = false)
    @PostMapping(value = "login")
    public MallOpsResponse<MemberVO> login(@RequestBody MemberLoginQuery userLoginQuery) {
        SingleResponse<MemberVO> response = iMemberService.login(userLoginQuery);
        if (response.isSuccess()) {
            return MallOpsResponse.success(response.getData());
        }
        return MallOpsResponse.fail(response);
    }

    @GetMapping(value = "getUserInfo")
    public MallOpsResponse<MemberVO> getUserInfo() {
        SingleResponse<MemberVO> response = iMemberService.getMemberInfo(CurrentUserContext.get());
        if (response.isSuccess()) {
            return MallOpsResponse.success(response.getData());
        }
        return MallOpsResponse.fail(response);
    }

}
