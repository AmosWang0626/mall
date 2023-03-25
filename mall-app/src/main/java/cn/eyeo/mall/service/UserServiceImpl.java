package cn.eyeo.mall.service;

import cn.eyeo.mall.api.IUserService;
import cn.eyeo.mall.dto.UserModifyCmd;
import cn.eyeo.mall.dto.UserRegisterCmd;
import cn.eyeo.mall.dto.data.UserVO;
import cn.eyeo.mall.dto.query.UserListByParamQuery;
import cn.eyeo.mall.dto.query.UserLoginQuery;
import cn.eyeo.mall.user.command.UserModifyCmdExe;
import cn.eyeo.mall.user.command.UserRegisterCmdExe;
import cn.eyeo.mall.user.command.query.UserInfoQueryExe;
import cn.eyeo.mall.user.command.query.UserListByParamQueryExe;
import cn.eyeo.mall.user.command.query.UserLoginQueryExe;
import com.alibaba.cola.catchlog.CatchAndLog;
import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.SingleResponse;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 用户相关
 *
 * @author <a href="mailto:daoyuan0626@gmail.com">amos.wang</a>
 * @date 2021/1/8
 */
@Service
@CatchAndLog
public class UserServiceImpl implements IUserService {

    /**
     * xxxExe 避免 Service 膨胀利器
     */
    @Resource
    private UserRegisterCmdExe userRegisterCmdExe;
    @Resource
    private UserModifyCmdExe userModifyCmdExe;
    @Resource
    private UserLoginQueryExe userLoginQueryExe;
    @Resource
    private UserInfoQueryExe userInfoQueryExe;
    @Resource
    private UserListByParamQueryExe userListByParamQueryExe;

    @Override
    public UserVO register(UserRegisterCmd cmd) {
//        return userRegisterCmdExe.execute(cmd);
        return null;
    }

    @Override
    public UserVO modify(UserModifyCmd cmd) {
//        return userModifyCmdExe.execute(cmd);
        return null;
    }

    @Override
    public void login(UserLoginQuery query) {
//        userLoginQueryExe.execute(query);
    }

    @Override
    public SingleResponse<UserVO> getUserInfo(Long id) {
//        return userInfoQueryExe.execute(id);
        return null;
    }

    @Override
    public MultiResponse<UserVO> listByName(UserListByParamQuery query) {
//        return userListByParamQueryExe.execute(query);
        return null;
    }

}
