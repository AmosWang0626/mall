package cn.eyeo.mall.client.common;

/**
 * 系统错误信息
 *
 * @author amos
 */
public enum SystemErrorCode implements BaseErrorCode {

    /**
     * 尽量让code可以达意
     */
    P_PARAM_ERROR("S_Param_Error", "参数错误"),
    S_DB_WRITE_ERROR("S_Database_write_Error", "数据库写异常，请重试"),
    S_DB_READ_ERROR("S_Database_read_Error", "数据库读异常，请重试"),
    S_UNKNOWN_ERROR("S_Unknown_Error", "未知系统错误");

    private final String errCode;
    private final String errDesc;

    SystemErrorCode(String errCode, String errDesc) {
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
