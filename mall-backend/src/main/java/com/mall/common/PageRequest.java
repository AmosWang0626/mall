package com.mall.common;

import lombok.Data;
import java.io.Serializable;

/**
 * 基础分页查询参数
 */
@Data
public class PageRequest implements Serializable {

    private int pageNum = 1;
    private int pageSize = 10;
    private String orderBy;  // 排序字段
    private String orderDirection = "desc"; // asc/desc

    public int getOffset() {
        return (pageNum - 1) * pageSize;
    }
}
