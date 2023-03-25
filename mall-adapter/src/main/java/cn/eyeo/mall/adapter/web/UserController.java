package cn.eyeo.mall.adapter.web;

import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.Response;
import cn.eyeo.mall.api.IUserService;
import cn.eyeo.mall.dto.UserRegisterCmd;
import cn.eyeo.mall.dto.data.UserVO;
import cn.eyeo.mall.dto.query.UserListByParamQuery;
import cn.eyeo.mall.dto.query.UserLoginQuery;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 用户相关
 *
 * @author <a href="mailto:daoyuan0626@gmail.com">amos.wang</a>
 * @date 2021/1/8
 */
@RestController
@RequestMapping("user")
public class UserController {

    @Resource
    private IUserService userService;


    @GetMapping(value = "/hello")
    public String hello() {
        return "Hello, welcome to COLA world!";
    }

    @PostMapping(value = "/register")
    public Response register(@RequestBody UserRegisterCmd cmd) {
        userService.register(cmd);
        return Response.buildSuccess();
    }

    @PostMapping(value = "/login")
    public Response login(@RequestBody UserLoginQuery userLoginQuery) {
        userService.login(userLoginQuery);
        return Response.buildSuccess();
    }

    @GetMapping(value = "/list")
    public MultiResponse<UserVO> list(@RequestParam(required = false) String name) {
        return userService.listByName(UserListByParamQuery.builder().name(name).build());
    }

}

