package cn.eyeo.mall.client.product.dto.data;

import cn.eyeo.mall.client.common.BaseErrorCode;


/**
 * 错误信息
 *
 * @author amos
 */
public enum ProductErrorCode implements BaseErrorCode {

    /**
     * 尽量让code可以达意
     */
    B_PRODUCT_CATEGORY_NOT_EXIST("B_PRODUCT_category_notExist", "分类不存在"),
    B_PRODUCT_ID_NOT_EXIST("B_PRODUCT_id_notExist", "无效的商品"),
    ;

    private final String errCode;
    private final String errDesc;

    ProductErrorCode(String errCode, String errDesc) {
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
