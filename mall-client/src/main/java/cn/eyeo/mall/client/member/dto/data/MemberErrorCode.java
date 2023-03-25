package cn.eyeo.mall.client.member.dto.data;

import cn.eyeo.mall.client.common.BaseErrorCode;

/**
 * 错误信息
 *
 * @author amos
 */
public enum MemberErrorCode implements BaseErrorCode {

    /**
     * 尽量让code可以达意
     */
    B_MEMBER_USERNAME_REPEAT("B_MEMBER_usernameRepeat", "用户名重复"),
    B_MEMBER_UNDEFINED("B_MEMBER_undefined", "用户不存在"),
    B_MEMBER_PASSWORD_ERROR("B_MEMBER_passwordError", "用户名或密码不正确");

    private final String errCode;
    private final String errDesc;

    MemberErrorCode(String errCode, String errDesc) {
        this.errCode = errCode;
        this.errDesc = errDesc;
    }

    @Override
    public String getErrCode() {
        return errCode;
    }

    @Override
    public String getErrDesc() {
        return errDesc;
    }

    @Override
    public String getErrDetail() {
        return null;
    }
}
