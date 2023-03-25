package cn.eyeo.mall.client.member.api;

import cn.eyeo.mall.client.member.dto.MemberModifyCmd;
import cn.eyeo.mall.client.member.dto.MemberRegisterCmd;
import cn.eyeo.mall.client.member.dto.data.MemberVO;
import cn.eyeo.mall.client.member.dto.query.MemberLoginQuery;
import com.alibaba.cola.dto.SingleResponse;

/**
 * C端用户相关
 *
 * @author <a href="mailto:daoyuan0626@gmail.com">amos.wang</a>
 * @date 2021/1/8
 */
public interface IMemberService {

    /**
     * 注册用户
     *
     * @param cmd 用户注册请求
     * @return Response
     */
    SingleResponse<MemberVO> register(MemberRegisterCmd cmd);

    /**
     * 用户信息修改
     *
     * @param cmd 用户信息修改请求
     * @return Response
     */
    SingleResponse<MemberVO> modify(MemberModifyCmd cmd);

    /**
     * 用户登录
     *
     * @param query 用户登录请求
     */
    void login(MemberLoginQuery query);

    /**
     * 获取用户信息
     *
     * @param id 用户ID
     * @return 用户信息
     */
    SingleResponse<MemberVO> getUserInfo(Long id);

}
