package cn.eyeo.mall.start.test;

import cn.eyeo.mall.client.member.api.IMemberService;
import cn.eyeo.mall.client.member.dto.MemberModifyCmd;
import cn.eyeo.mall.client.member.dto.MemberRegisterCmd;
import cn.eyeo.mall.client.member.dto.data.MemberErrorCode;
import cn.eyeo.mall.client.member.dto.data.MemberVO;
import cn.eyeo.mall.client.member.dto.query.MemberLoginQuery;
import cn.eyeo.mall.common.exception.MallBizException;
import cn.eyeo.mall.start.Application;
import com.alibaba.cola.dto.SingleResponse;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

/**
 * MemberServiceTest
 *
 * @author <a href="mailto:daoyuan0626@gmail.com">amos.wang</a>
 * @date 2021/1/9
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MemberServiceTest {

    @Autowired
    private IMemberService iMemberService;

    private static final String username = "AMOS_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    private static final String password = "666666";
    private static Long id = null;

    @Before
    public void setUp() {
        log.info("test username is [" + username + "]");
    }

    @Test
    public void user_1_Register() {
        //1.prepare
        MemberRegisterCmd registerCmd = new MemberRegisterCmd();
        registerCmd.setUsername(username);
        registerCmd.setPassword(password);
        registerCmd.setNickname("amos.wang");
        registerCmd.setMobile("18907378888");
        registerCmd.setGender(1);
        registerCmd.setBirthday(LocalDate.of(2000, 1, 1));

        //2.execute
        SingleResponse<MemberVO> response = iMemberService.register(registerCmd);

        //3.assert
        MemberVO memberVO = response.getData();
        Assert.assertTrue(Objects.nonNull(memberVO.getUserId()));

        id = memberVO.getUserId();
    }

    @Test
    public void user_2_RegisterByRepeatUsername() {
        //1.prepare
        MemberRegisterCmd registerCmd = new MemberRegisterCmd();
        registerCmd.setUsername(username);
        registerCmd.setPassword(password);

        //2.execute
        try {
            iMemberService.register(registerCmd);
        } catch (MallBizException e) {
            Assert.assertEquals(MemberErrorCode.B_MEMBER_USERNAME_REPEAT.getErrCode(), e.getErrCode());
        }
    }

    @Test
    public void user_3_Login() {
        //1.prepare
        MemberLoginQuery userLoginQuery = new MemberLoginQuery();
        userLoginQuery.setUsername(username);
        userLoginQuery.setPassword(password);

        //2.execute
        iMemberService.login(userLoginQuery);
    }

    @Test
    public void user_4_Modify() {
        //1.prepare
        MemberModifyCmd modifyCmd = new MemberModifyCmd();
        modifyCmd.setUserId(id);
        modifyCmd.setUsername(username);
        modifyCmd.setNickname("小道远");
        modifyCmd.setMobile("189****8888");
        modifyCmd.setGender(0);
        modifyCmd.setBirthday(LocalDate.of(2000, 6, 26));

        //2.execute
        SingleResponse<MemberVO> response = iMemberService.modify(modifyCmd);

        //3.assert error
        Assert.assertTrue(Objects.nonNull(response.getData()));
    }

    @Test
    public void user_5_getUserInfo() {
        //1.prepare
        Long userId = id;

        //2.execute
        SingleResponse<MemberVO> response = iMemberService.getUserInfo(userId);

        System.out.println(JSON.toJSONString(response));

        //3.assert error
        Assert.assertTrue(response.isSuccess());
    }

}
