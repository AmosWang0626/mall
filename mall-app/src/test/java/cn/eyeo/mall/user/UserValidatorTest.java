package cn.eyeo.mall.user;

import cn.eyeo.mall.dto.UserRegisterCmd;
import org.junit.Test;

public class UserValidatorTest {

    @Test
    public void testValidation() {
        new UserRegisterCmd("amos", "");
    }
}
