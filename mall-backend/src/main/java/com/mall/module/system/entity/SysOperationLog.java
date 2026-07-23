package com.mall.module.system.entity;

import lombok.Data;
import java.util.Date;

@Data
public class SysOperationLog {
    private Long id;
    private Long adminId;
    private String adminName;
    private String module;
    private String operation;
    private String method;
    private String requestUrl;
    private String requestParam;
    private String ip;
    private Long costTime;
    private Date createTime;
}
