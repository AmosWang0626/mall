package cn.eyeo.mall.client.product.dto.data;

import cn.eyeo.mall.client.common.BaseErrorCode;

/**
 * 错误信息
 *
 * @author amos
 */
public enum CategoryErrorCode implements BaseErrorCode {

    /**
     * 尽量让code可以达意
     */
    B_CATEGORY_HAS_CHILD("B_Category_hasChildCategory", "不能删除，当前分类下有子分类"),
    B_CATEGORY_HAS_PRODUCT("B_Category_hasChildProduct", "不能删除，当前分类下有关联的商品"),
    B_CATEGORY_PARENT_NOT_EXIST("B_Category_parentId_notExist", "父分类ID不存在"),
    ;

    private final String errCode;
    private final String errDesc;

    CategoryErrorCode(String errCode, String errDesc) {
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
