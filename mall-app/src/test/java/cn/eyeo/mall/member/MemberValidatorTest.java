package cn.eyeo.mall.member;

import cn.eyeo.mall.client.member.dto.MemberRegisterCmd;
import org.junit.Test;

public class MemberValidatorTest {

    @Test
    public void testValidation() {
        MemberRegisterCmd memberRegisterCmd = new MemberRegisterCmd();
        memberRegisterCmd.setUsername("amos");
        memberRegisterCmd.setPassword("");

        System.out.println(memberRegisterCmd);
    }
}
