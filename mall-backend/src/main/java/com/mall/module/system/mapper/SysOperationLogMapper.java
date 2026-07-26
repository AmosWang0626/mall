package com.mall.module.system.mapper;

import com.mall.module.system.entity.SysOperationLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface SysOperationLogMapper {
    int insert(SysOperationLog log);
    List<SysOperationLog> selectList(@Param("module") String module);
    long count(@Param("module") String module);
    int deleteById(Long id);
}
