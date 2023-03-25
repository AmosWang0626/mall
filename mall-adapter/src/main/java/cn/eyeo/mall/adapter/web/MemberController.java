package cn.eyeo.mall.adapter.web;

import cn.eyeo.mall.client.member.api.IMemberService;
import cn.eyeo.mall.client.member.dto.MemberModifyCmd;
import cn.eyeo.mall.client.member.dto.MemberRegisterCmd;
import cn.eyeo.mall.client.member.dto.query.MemberLoginQuery;
import com.alibaba.cola.dto.Response;
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
    public Response register(@RequestBody MemberRegisterCmd cmd) {
        return iMemberService.register(cmd);
    }

    @PostMapping(value = "modify")
    public Response modify(@RequestBody MemberModifyCmd cmd) {
        return iMemberService.modify(cmd);
    }

    @PostMapping(value = "login")
    public Response login(@RequestBody MemberLoginQuery userLoginQuery) {
        iMemberService.login(userLoginQuery);
        return Response.buildSuccess();
    }

    @GetMapping(value = "getUserInfo/{userId}")
    public Response login(@PathVariable("userId") Long userId) {
        return iMemberService.getUserInfo(userId);
    }

}

