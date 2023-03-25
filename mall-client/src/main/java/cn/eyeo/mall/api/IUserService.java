package cn.eyeo.mall.api;

import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.SingleResponse;
import cn.eyeo.mall.dto.UserModifyCmd;
import cn.eyeo.mall.dto.UserRegisterCmd;
import cn.eyeo.mall.dto.data.UserVO;
import cn.eyeo.mall.dto.query.UserListByParamQuery;
import cn.eyeo.mall.dto.query.UserLoginQuery;

/**
 * 用户相关
 *
 * @author <a href="mailto:daoyuan0626@gmail.com">amos.wang</a>
 * @date 2021/1/8
 */
public interface IUserService {

    /**
     * 注册用户
     *
     * @param cmd 用户注册请求
     * @return Response
     */
    UserVO register(UserRegisterCmd cmd);

    /**
     * 用户信息修改
     *
     * @param cmd 用户信息修改请求
     * @return Response
     */
    UserVO modify(UserModifyCmd cmd);

    /**
     * 用户登录
     *
     * @param query 用户登录请求
     */
    void login(UserLoginQuery query);

    /**
     * 获取用户信息
     *
     * @param id 用户ID
     * @return 用户信息
     */
    SingleResponse<UserVO> getUserInfo(Long id);

    /**
     * 根据用户名称查询
     *
     * @param query 用户查询请求
     * @return MultiResponse
     */
    MultiResponse<UserVO> listByName(UserListByParamQuery query);

}
