package com.mall.module.marketing.entity;

import com.mall.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.Date;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class MarketingActivity extends BaseEntity {
    private Long id;
    private String name;
    private String type; // FLASH_SALE, FULL_REDUCTION, DISCOUNT
    private String description;
    private Date startTime;
    private Date endTime;
    private Integer status; // 0-未开始, 1-进行中, 2-已结束, 3-已终止
    private Integer enabled;
    private String rules;
    private List<ActivityProduct> products;
}
