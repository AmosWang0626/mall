#!/usr/bin/env python3
"""Generate system RBAC module files."""
import os

BASE = "/Users/dorian/WorkBuddy/2026-07-21-23-31-52/mall-backend/src/main"
JAVA = BASE + "/java/com/mall/module/system"
XML = BASE + "/resources/mapper"

def w(path, content):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, 'w') as f:
        f.write(content)
    print(f"  + {path}")

print("=== System RBAC Module ===")

# Entities
w(f"{JAVA}/entity/SysRole.java", '''package com.mall.module.system.entity;

import com.mall.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SysRole extends BaseEntity {
    private Long id;
    private String name;
    private String code;
    private String description;
    private Integer sort;
    private Integer status;
    private Integer dataScope;
}
''')

w(f"{JAVA}/entity/SysPermission.java", '''package com.mall.module.system.entity;

import com.mall.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SysPermission extends BaseEntity {
    private Long id;
    private Long parentId;
    private String name;
    private String code;
    private Integer type; // 1-menu, 2-button, 3-api
    private String path;
    private String component;
    private String icon;
    private Integer sort;
    private Integer status;
    private Integer visible;
    private java.util.List<SysPermission> children;
}
''')

w(f"{JAVA}/entity/SysConfig.java", '''package com.mall.module.system.entity;

import lombok.Data;
import java.util.Date;

@Data
public class SysConfig {
    private Long id;
    private String configKey;
    private String configValue;
    private String configType;
    private String name;
    private String description;
    private Integer isSystem;
    private Date createTime;
    private Date updateTime;
}
''')

w(f"{JAVA}/entity/SysOperationLog.java", '''package com.mall.module.system.entity;

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
''')

# Mappers
w(f"{JAVA}/mapper/SysRoleMapper.java", '''package com.mall.module.system.mapper;

import com.mall.module.system.entity.SysRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface SysRoleMapper {
    int insert(SysRole role);
    int updateById(SysRole role);
    int deleteById(Long id);
    SysRole selectById(Long id);
    SysRole selectByCode(@Param("code") String code);
    List<SysRole> selectList(@Param("keyword") String keyword, @Param("offset") int offset, @Param("limit") int limit);
    long count(@Param("keyword") String keyword);
    List<SysRole> selectAll();
    int updateStatus(@Param("id") Long id, @Param("status") int status);
    int insertRolePermission(@Param("roleId") Long roleId, @Param("permissionId") Long permissionId);
    int deleteRolePermissions(@Param("roleId") Long roleId);
    List<Long> selectPermissionIds(@Param("roleId") Long roleId);
    List<SysRole> selectByAdminId(@Param("adminId") Long adminId);
    int insertAdminRole(@Param("adminId") Long adminId, @Param("roleId") Long roleId);
    int deleteAdminRoles(@Param("adminId") Long adminId);
    List<Long> selectRoleIdsByAdminId(@Param("adminId") Long adminId);
}
''')

w(f"{JAVA}/mapper/SysPermissionMapper.java", '''package com.mall.module.system.mapper;

import com.mall.module.system.entity.SysPermission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface SysPermissionMapper {
    int insert(SysPermission permission);
    int updateById(SysPermission permission);
    int deleteById(Long id);
    SysPermission selectById(Long id);
    List<SysPermission> selectAll();
    List<SysPermission> selectByParentId(@Param("parentId") Long parentId);
    List<SysPermission> selectByRoleIds(@Param("roleIds") List<Long> roleIds);
    int updateStatus(@Param("id") Long id, @Param("status") int status);
    long countByCode(@Param("code") String code, @Param("excludeId") Long excludeId);
}
''')

w(f"{JAVA}/mapper/SysConfigMapper.java", '''package com.mall.module.system.mapper;

import com.mall.module.system.entity.SysConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface SysConfigMapper {
    int insert(SysConfig config);
    int updateById(SysConfig config);
    int deleteById(Long id);
    SysConfig selectById(Long id);
    SysConfig selectByKey(@Param("key") String key);
    List<SysConfig> selectList(@Param("keyword") String keyword, @Param("offset") int offset, @Param("limit") int limit);
    long count(@Param("keyword") String keyword);
    List<SysConfig> selectAll();
}
''')

w(f"{JAVA}/mapper/SysOperationLogMapper.java", '''package com.mall.module.system.mapper;

import com.mall.module.system.entity.SysOperationLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface SysOperationLogMapper {
    int insert(SysOperationLog log);
    List<SysOperationLog> selectList(@Param("module") String module, @Param("offset") int offset, @Param("limit") int limit);
    long count(@Param("module") String module);
    int deleteById(Long id);
}
''')

# Mapper XMLs
w(f"{XML}/SysRoleMapper.xml", '''<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.mall.module.system.mapper.SysRoleMapper">
    <resultMap id="BaseResultMap" type="com.mall.module.system.entity.SysRole">
        <id column="id" property="id"/><result column="name" property="name"/><result column="code" property="code"/>
        <result column="description" property="description"/><result column="sort" property="sort"/>
        <result column="status" property="status"/><result column="data_scope" property="dataScope"/>
        <result column="create_time" property="createTime"/><result column="update_time" property="updateTime"/><result column="deleted" property="deleted"/>
    </resultMap>
    <sql id="cols">id,name,code,description,sort,status,data_scope,create_time,update_time,deleted</sql>
    <insert id="insert" parameterType="com.mall.module.system.entity.SysRole" useGeneratedKeys="true" keyProperty="id">
        INSERT INTO sys_role(name,code,description,sort,status,data_scope) VALUES(#{name},#{code},#{description},#{sort},#{status},#{dataScope})
    </insert>
    <update id="updateById" parameterType="com.mall.module.system.entity.SysRole">
        UPDATE sys_role<set><if test="name!=null">name=#{name},</if><if test="code!=null">code=#{code},</if>
        <if test="description!=null">description=#{description},</if><if test="sort!=null">sort=#{sort},</if>
        <if test="status!=null">status=#{status},</if><if test="dataScope!=null">data_scope=#{dataScope},</if></set>WHERE id=#{id} AND deleted=0
    </update>
    <update id="deleteById">UPDATE sys_role SET deleted=1 WHERE id=#{id}</update>
    <select id="selectById" resultMap="BaseResultMap">SELECT <include refid="cols"/> FROM sys_role WHERE id=#{id} AND deleted=0</select>
    <select id="selectByCode" resultMap="BaseResultMap">SELECT <include refid="cols"/> FROM sys_role WHERE code=#{code} AND deleted=0</select>
    <select id="selectList" resultMap="BaseResultMap">SELECT <include refid="cols"/> FROM sys_role WHERE deleted=0<if test="keyword!=null and keyword!=''">AND(name LIKE CONCAT('%',#{keyword},'%') OR code LIKE CONCAT('%',#{keyword},'%'))</if>ORDER BY sort LIMIT #{offset},#{limit}</select>
    <select id="count" resultType="long">SELECT COUNT(*) FROM sys_role WHERE deleted=0<if test="keyword!=null and keyword!=''">AND(name LIKE CONCAT('%',#{keyword},'%') OR code LIKE CONCAT('%',#{keyword},'%'))</if></select>
    <select id="selectAll" resultMap="BaseResultMap">SELECT <include refid="cols"/> FROM sys_role WHERE deleted=0 AND status=1 ORDER BY sort</select>
    <update id="updateStatus">UPDATE sys_role SET status=#{status} WHERE id=#{id} AND deleted=0</update>
    <insert id="insertRolePermission">INSERT INTO sys_role_permission(role_id,permission_id) VALUES(#{roleId},#{permissionId})</insert>
    <delete id="deleteRolePermissions">DELETE FROM sys_role_permission WHERE role_id=#{roleId}</delete>
    <select id="selectPermissionIds" resultType="long">SELECT permission_id FROM sys_role_permission WHERE role_id=#{roleId}</select>
    <select id="selectByAdminId" resultMap="BaseResultMap">SELECT r.* FROM sys_role r JOIN sys_admin_role ar ON r.id=ar.role_id WHERE ar.admin_id=#{adminId} AND r.deleted=0</select>
    <insert id="insertAdminRole">INSERT INTO sys_admin_role(admin_id,role_id) VALUES(#{adminId},#{roleId})</insert>
    <delete id="deleteAdminRoles">DELETE FROM sys_admin_role WHERE admin_id=#{adminId}</delete>
    <select id="selectRoleIdsByAdminId" resultType="long">SELECT role_id FROM sys_admin_role WHERE admin_id=#{adminId}</select>
</mapper>
''')

w(f"{XML}/SysPermissionMapper.xml", '''<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.mall.module.system.mapper.SysPermissionMapper">
    <resultMap id="BaseResultMap" type="com.mall.module.system.entity.SysPermission">
        <id column="id" property="id"/><result column="parent_id" property="parentId"/><result column="name" property="name"/>
        <result column="code" property="code"/><result column="type" property="type"/><result column="path" property="path"/>
        <result column="component" property="component"/><result column="icon" property="icon"/><result column="sort" property="sort"/>
        <result column="status" property="status"/><result column="visible" property="visible"/>
        <result column="create_time" property="createTime"/><result column="update_time" property="updateTime"/><result column="deleted" property="deleted"/>
    </resultMap>
    <sql id="cols">id,parent_id,name,code,type,path,component,icon,sort,status,visible,create_time,update_time,deleted</sql>
    <insert id="insert" parameterType="com.mall.module.system.entity.SysPermission" useGeneratedKeys="true" keyProperty="id">
        INSERT INTO sys_permission(parent_id,name,code,type,path,component,icon,sort,status,visible) VALUES(#{parentId},#{name},#{code},#{type},#{path},#{component},#{icon},#{sort},#{status},#{visible})
    </insert>
    <update id="updateById" parameterType="com.mall.module.system.entity.SysPermission">
        UPDATE sys_permission<set><if test="parentId!=null">parent_id=#{parentId},</if><if test="name!=null">name=#{name},</if>
        <if test="code!=null">code=#{code},</if><if test="type!=null">type=#{type},</if><if test="path!=null">path=#{path},</if>
        <if test="component!=null">component=#{component},</if><if test="icon!=null">icon=#{icon},</if>
        <if test="sort!=null">sort=#{sort},</if><if test="status!=null">status=#{status},</if><if test="visible!=null">visible=#{visible},</if></set>WHERE id=#{id} AND deleted=0
    </update>
    <update id="deleteById">UPDATE sys_permission SET deleted=1 WHERE id=#{id}</update>
    <select id="selectById" resultMap="BaseResultMap">SELECT <include refid="cols"/> FROM sys_permission WHERE id=#{id} AND deleted=0</select>
    <select id="selectAll" resultMap="BaseResultMap">SELECT <include refid="cols"/> FROM sys_permission WHERE deleted=0 ORDER BY sort</select>
    <select id="selectByParentId" resultMap="BaseResultMap">SELECT <include refid="cols"/> FROM sys_permission WHERE parent_id=#{parentId} AND deleted=0 ORDER BY sort</select>
    <select id="selectByRoleIds" resultMap="BaseResultMap">SELECT DISTINCT p.* FROM sys_role_permission rp JOIN sys_permission p ON rp.permission_id=p.id WHERE rp.role_id IN<foreach collection="roleIds" item="id" open="(" separator="," close=")">#{id}</foreach>AND p.deleted=0 ORDER BY p.sort</select>
    <update id="updateStatus">UPDATE sys_permission SET status=#{status} WHERE id=#{id} AND deleted=0</update>
    <select id="countByCode" resultType="long">SELECT COUNT(*) FROM sys_permission WHERE code=#{code} AND deleted=0<if test="excludeId!=null">AND id!=#{excludeId}</if></select>
</mapper>
''')

w(f"{XML}/SysConfigMapper.xml", '''<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.mall.module.system.mapper.SysConfigMapper">
    <resultMap id="BaseResultMap" type="com.mall.module.system.entity.SysConfig">
        <id column="id" property="id"/><result column="config_key" property="configKey"/><result column="config_value" property="configValue"/>
        <result column="config_type" property="configType"/><result column="name" property="name"/><result column="description" property="description"/>
        <result column="is_system" property="isSystem"/><result column="create_time" property="createTime"/><result column="update_time" property="updateTime"/>
    </resultMap>
    <sql id="cols">id,config_key,config_value,config_type,name,description,is_system,create_time,update_time</sql>
    <insert id="insert" parameterType="com.mall.module.system.entity.SysConfig" useGeneratedKeys="true" keyProperty="id">
        INSERT INTO sys_config(config_key,config_value,config_type,name,description,is_system) VALUES(#{configKey},#{configValue},#{configType},#{name},#{description},#{isSystem})
    </insert>
    <update id="updateById" parameterType="com.mall.module.system.entity.SysConfig">
        UPDATE sys_config<set><if test="configKey!=null">config_key=#{configKey},</if><if test="configValue!=null">config_value=#{configValue},</if>
        <if test="configType!=null">config_type=#{configType},</if><if test="name!=null">name=#{name},</if><if test="description!=null">description=#{description},</if></set>WHERE id=#{id}
    </update>
    <delete id="deleteById">DELETE FROM sys_config WHERE id=#{id} AND is_system=0</delete>
    <select id="selectById" resultMap="BaseResultMap">SELECT <include refid="cols"/> FROM sys_config WHERE id=#{id}</select>
    <select id="selectByKey" resultMap="BaseResultMap">SELECT <include refid="cols"/> FROM sys_config WHERE config_key=#{key}</select>
    <select id="selectList" resultMap="BaseResultMap">SELECT <include refid="cols"/> FROM sys_config WHERE 1=1<if test="keyword!=null and keyword!=''">AND(config_key LIKE CONCAT('%',#{keyword},'%') OR name LIKE CONCAT('%',#{keyword},'%'))</if>ORDER BY create_time DESC LIMIT #{offset},#{limit}</select>
    <select id="count" resultType="long">SELECT COUNT(*) FROM sys_config WHERE 1=1<if test="keyword!=null and keyword!=''">AND(config_key LIKE CONCAT('%',#{keyword},'%') OR name LIKE CONCAT('%',#{keyword},'%'))</if></select>
    <select id="selectAll" resultMap="BaseResultMap">SELECT <include refid="cols"/> FROM sys_config</select>
</mapper>
''')

w(f"{XML}/SysOperationLogMapper.xml", '''<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.mall.module.system.mapper.SysOperationLogMapper">
    <resultMap id="BaseResultMap" type="com.mall.module.system.entity.SysOperationLog">
        <id column="id" property="id"/><result column="admin_id" property="adminId"/><result column="admin_name" property="adminName"/>
        <result column="module" property="module"/><result column="operation" property="operation"/><result column="method" property="method"/>
        <result column="request_url" property="requestUrl"/><result column="request_param" property="requestParam"/><result column="ip" property="ip"/>
        <result column="cost_time" property="costTime"/><result column="create_time" property="createTime"/>
    </resultMap>
    <insert id="insert" parameterType="com.mall.module.system.entity.SysOperationLog" useGeneratedKeys="true" keyProperty="id">
        INSERT INTO sys_operation_log(admin_id,admin_name,module,operation,method,request_url,request_param,ip,cost_time) VALUES(#{adminId},#{adminName},#{module},#{operation},#{method},#{requestUrl},#{requestParam},#{ip},#{costTime})
    </insert>
    <select id="selectList" resultMap="BaseResultMap">SELECT * FROM sys_operation_log WHERE 1=1<if test="module!=null and module!=''">AND module=#{module}</if>ORDER BY create_time DESC LIMIT #{offset},#{limit}</select>
    <select id="count" resultType="long">SELECT COUNT(*) FROM sys_operation_log WHERE 1=1<if test="module!=null and module!=''">AND module=#{module}</if></select>
    <delete id="deleteById">DELETE FROM sys_operation_log WHERE id=#{id}</delete>
</mapper>
''')

# Services
w(f"{JAVA}/service/SysAdminService.java", '''package com.mall.module.system.service;

import cn.hutool.crypto.digest.BCrypt;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mall.common.PageResult;
import com.mall.common.exception.BusinessException;
import com.mall.module.system.entity.SysAdmin;
import com.mall.module.system.entity.SysRole;
import com.mall.module.system.mapper.SysAdminMapper;
import com.mall.module.system.mapper.SysRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;

@Service
public class SysAdminService {
    @Autowired private SysAdminMapper adminMapper;
    @Autowired private SysRoleMapper roleMapper;

    public PageResult<SysAdmin> list(String keyword, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<SysAdmin> list = adminMapper.selectList(keyword, (pageNum-1)*pageSize, pageSize);
        list.forEach(a -> a.setPassword(null));
        PageInfo<SysAdmin> info = new PageInfo<>(list);
        return PageResult.of(info.getList(), info.getTotal(), pageNum, pageSize);
    }

    public SysAdmin getById(Long id) {
        SysAdmin admin = adminMapper.selectById(id);
        if (admin != null) admin.setPassword(null);
        return admin;
    }

    @Transactional
    public void save(SysAdmin admin) {
        if (admin.getPassword() != null && !admin.getPassword().isEmpty()) {
            admin.setPassword(BCrypt.hashpw(admin.getPassword()));
        } else {
            admin.setPassword(BCrypt.hashpw("123456"));
        }
        if (admin.getStatus() == null) admin.setStatus(1);
        adminMapper.insert(admin);
    }

    @Transactional
    public void update(SysAdmin admin) {
        if (admin.getPassword() != null && !admin.getPassword().isEmpty()) {
            admin.setPassword(BCrypt.hashpw(admin.getPassword()));
        } else {
            admin.setPassword(null);
        }
        adminMapper.updateById(admin);
    }

    @Transactional
    public void updateStatus(Long id, int status) { adminMapper.updateStatus(id, status); }

    @Transactional
    public void delete(Long id) { adminMapper.deleteById(id); }

    @Transactional
    public void assignRoles(Long adminId, List<Long> roleIds) {
        adminMapper.deleteAdminRoles(adminId);
        if (roleIds != null) {
            for (Long roleId : roleIds) adminMapper.insertAdminRole(adminId, roleId);
        }
    }

    public List<Long> getRoleIds(Long adminId) { return adminMapper.selectRoleIds(adminId); }
    public List<String> getPermissions(Long adminId) { return adminMapper.selectPermissions(adminId); }
    public List<SysRole> getRoles(Long adminId) { return roleMapper.selectByAdminId(adminId); }

    @Transactional
    public void resetPassword(Long id, String newPwd) {
        SysAdmin admin = new SysAdmin();
        admin.setId(id);
        admin.setPassword(BCrypt.hashpw(newPwd));
        adminMapper.updateById(admin);
    }
}
''')

w(f"{JAVA}/service/SysRoleService.java", '''package com.mall.module.system.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mall.common.PageResult;
import com.mall.common.exception.BusinessException;
import com.mall.module.system.entity.SysPermission;
import com.mall.module.system.entity.SysRole;
import com.mall.module.system.mapper.SysPermissionMapper;
import com.mall.module.system.mapper.SysRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class SysRoleService {
    @Autowired private SysRoleMapper roleMapper;
    @Autowired private SysPermissionMapper permissionMapper;

    public PageResult<SysRole> list(String keyword, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        PageInfo<SysRole> info = new PageInfo<>(roleMapper.selectList(keyword, (pageNum-1)*pageSize, pageSize));
        return PageResult.of(info.getList(), info.getTotal(), pageNum, pageSize);
    }

    public List<SysRole> all() { return roleMapper.selectAll(); }

    public SysRole getById(Long id) { return roleMapper.selectById(id); }

    @Transactional
    public void save(SysRole role) {
        if (role.getId() == null) {
            if (role.getStatus() == null) role.setStatus(1);
            if (role.getDataScope() == null) role.setDataScope(1);
            roleMapper.insert(role);
        } else {
            roleMapper.updateById(role);
        }
    }

    @Transactional
    public void delete(Long id) {
        roleMapper.deleteRolePermissions(id);
        roleMapper.deleteById(id);
    }

    @Transactional
    public void updateStatus(Long id, int status) { roleMapper.updateStatus(id, status); }

    @Transactional
    public void assignPermissions(Long roleId, List<Long> permissionIds) {
        roleMapper.deleteRolePermissions(roleId);
        if (permissionIds != null) {
            for (Long pid : permissionIds) roleMapper.insertRolePermission(roleId, pid);
        }
    }

    public List<Long> getPermissionIds(Long roleId) { return roleMapper.selectPermissionIds(roleId); }

    public List<SysPermission> getPermissionsByRoleIds(List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) return List.of();
        return permissionMapper.selectByRoleIds(roleIds);
    }
}
''')

w(f"{JAVA}/service/SysPermissionService.java", '''package com.mall.module.system.service;

import com.mall.common.exception.BusinessException;
import com.mall.module.system.entity.SysPermission;
import com.mall.module.system.mapper.SysPermissionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class SysPermissionService {
    @Autowired private SysPermissionMapper permissionMapper;

    public List<SysPermission> tree() {
        List<SysPermission> all = permissionMapper.selectAll();
        return buildTree(all, 0L);
    }

    private List<SysPermission> buildTree(List<SysPermission> all, Long parentId) {
        List<SysPermission> tree = new ArrayList<>();
        for (SysPermission p : all) {
            if (parentId.equals(p.getParentId())) {
                p.setChildren(buildTree(all, p.getId()));
                tree.add(p);
            }
        }
        return tree;
    }

    public List<SysPermission> list() { return permissionMapper.selectAll(); }

    public SysPermission getById(Long id) { return permissionMapper.selectById(id); }

    @Transactional
    public void save(SysPermission permission) {
        if (permission.getStatus() == null) permission.setStatus(1);
        if (permission.getVisible() == null) permission.setVisible(1);
        if (permission.getParentId() == null) permission.setParentId(0L);
        if (permission.getId() == null) {
            permissionMapper.insert(permission);
        } else {
            permissionMapper.updateById(permission);
        }
    }

    @Transactional
    public void delete(Long id) { permissionMapper.deleteById(id); }

    @Transactional
    public void updateStatus(Long id, int status) { permissionMapper.updateStatus(id, status); }

    public List<SysPermission> getByRoleIds(List<Long> roleIds) {
        return permissionMapper.selectByRoleIds(roleIds);
    }
}
''')

w(f"{JAVA}/service/SysConfigService.java", '''package com.mall.module.system.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mall.common.PageResult;
import com.mall.common.RedisService;
import com.mall.module.system.entity.SysConfig;
import com.mall.module.system.mapper.SysConfigMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class SysConfigService {
    @Autowired private SysConfigMapper configMapper;
    @Autowired private RedisService redisService;
    private static final String CACHE_PREFIX = "sys:config:";

    public PageResult<SysConfig> list(String keyword, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        PageInfo<SysConfig> info = new PageInfo<>(configMapper.selectList(keyword, (pageNum-1)*pageSize, pageSize));
        return PageResult.of(info.getList(), info.getTotal(), pageNum, pageSize);
    }

    public SysConfig getById(Long id) { return configMapper.selectById(id); }

    public String getValue(String key) {
        Object cached = redisService.get(CACHE_PREFIX + key);
        if (cached != null) return cached.toString();
        SysConfig config = configMapper.selectByKey(key);
        if (config != null) {
            redisService.set(CACHE_PREFIX + key, config.getConfigValue(), 1, TimeUnit.HOURS);
            return config.getConfigValue();
        }
        return null;
    }

    @Transactional
    public void save(SysConfig config) {
        if (config.getId() == null) {
            if (config.getIsSystem() == null) config.setIsSystem(0);
            if (config.getConfigType() == null) config.setConfigType("string");
            configMapper.insert(config);
        } else {
            configMapper.updateById(config);
        }
        redisService.delete(CACHE_PREFIX + config.getConfigKey());
    }

    @Transactional
    public void delete(Long id) {
        SysConfig config = configMapper.selectById(id);
        if (config != null && config.getIsSystem() == 1) {
            throw new RuntimeException("系统内置配置不可删除");
        }
        configMapper.deleteById(id);
        if (config != null) redisService.delete(CACHE_PREFIX + config.getConfigKey());
    }

    public List<SysConfig> all() { return configMapper.selectAll(); }
}
''')

w(f"{JAVA}/service/SysOperationLogService.java", '''package com.mall.module.system.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mall.common.PageResult;
import com.mall.module.system.entity.SysOperationLog;
import com.mall.module.system.mapper.SysOperationLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SysOperationLogService {
    @Autowired private SysOperationLogMapper logMapper;

    public PageResult<SysOperationLog> list(String module, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        PageInfo<SysOperationLog> info = new PageInfo<>(logMapper.selectList(module, (pageNum-1)*pageSize, pageSize));
        return PageResult.of(info.getList(), info.getTotal(), pageNum, pageSize);
    }

    public void save(SysOperationLog log) { logMapper.insert(log); }
    public void delete(Long id) { logMapper.deleteById(id); }
}
''')

# Controllers
w(f"{JAVA}/controller/SysAdminController.java", '''package com.mall.module.system.controller;

import com.mall.common.PageResult;
import com.mall.common.Result;
import com.mall.module.system.entity.SysAdmin;
import com.mall.module.system.service.SysAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/system/admin")
public class SysAdminController {
    @Autowired private SysAdminService adminService;

    @GetMapping("/list")
    public Result<PageResult<SysAdmin>> list(@RequestParam(defaultValue="") String keyword,
                                              @RequestParam(defaultValue="1") int pageNum,
                                              @RequestParam(defaultValue="10") int pageSize) {
        return Result.success(adminService.list(keyword, pageNum, pageSize));
    }
    @GetMapping("/{id}")
    public Result<SysAdmin> detail(@PathVariable Long id) { return Result.success(adminService.getById(id)); }
    @PostMapping
    public Result<Void> save(@RequestBody SysAdmin admin) { adminService.save(admin); return Result.success(); }
    @PutMapping
    public Result<Void> update(@RequestBody SysAdmin admin) { adminService.update(admin); return Result.success(); }
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam int status) { adminService.updateStatus(id, status); return Result.success(); }
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) { adminService.delete(id); return Result.success(); }
    @PutMapping("/{id}/roles")
    public Result<Void> assignRoles(@PathVariable Long id, @RequestBody Map<String,List<Long>> body) { adminService.assignRoles(id, body.get("roleIds")); return Result.success(); }
    @GetMapping("/{id}/roles")
    public Result<List<Long>> getRoleIds(@PathVariable Long id) { return Result.success(adminService.getRoleIds(id)); }
    @PutMapping("/{id}/password")
    public Result<Void> resetPassword(@PathVariable Long id, @RequestBody Map<String,String> body) { adminService.resetPassword(id, body.get("password")); return Result.success(); }
}
''')

w(f"{JAVA}/controller/SysRoleController.java", '''package com.mall.module.system.controller;

import com.mall.common.PageResult;
import com.mall.common.Result;
import com.mall.module.system.entity.SysRole;
import com.mall.module.system.service.SysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/system/role")
public class SysRoleController {
    @Autowired private SysRoleService roleService;

    @GetMapping("/list")
    public Result<PageResult<SysRole>> list(@RequestParam(defaultValue="") String keyword,
                                             @RequestParam(defaultValue="1") int pageNum,
                                             @RequestParam(defaultValue="10") int pageSize) {
        return Result.success(roleService.list(keyword, pageNum, pageSize));
    }
    @GetMapping("/all")
    public Result<List<SysRole>> all() { return Result.success(roleService.all()); }
    @GetMapping("/{id}")
    public Result<SysRole> detail(@PathVariable Long id) { return Result.success(roleService.getById(id)); }
    @PostMapping
    public Result<Void> save(@RequestBody SysRole role) { roleService.save(role); return Result.success(); }
    @PutMapping
    public Result<Void> update(@RequestBody SysRole role) { roleService.save(role); return Result.success(); }
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam int status) { roleService.updateStatus(id, status); return Result.success(); }
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) { roleService.delete(id); return Result.success(); }
    @PutMapping("/{id}/permissions")
    public Result<Void> assignPermissions(@PathVariable Long id, @RequestBody Map<String,List<Long>> body) { roleService.assignPermissions(id, body.get("permissionIds")); return Result.success(); }
    @GetMapping("/{id}/permissions")
    public Result<List<Long>> getPermissionIds(@PathVariable Long id) { return Result.success(roleService.getPermissionIds(id)); }
}
''')

w(f"{JAVA}/controller/SysPermissionController.java", '''package com.mall.module.system.controller;

import com.mall.common.Result;
import com.mall.module.system.entity.SysPermission;
import com.mall.module.system.service.SysPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/system/permission")
public class SysPermissionController {
    @Autowired private SysPermissionService permissionService;

    @GetMapping("/tree")
    public Result<List<SysPermission>> tree() { return Result.success(permissionService.tree()); }
    @GetMapping("/list")
    public Result<List<SysPermission>> list() { return Result.success(permissionService.list()); }
    @GetMapping("/{id}")
    public Result<SysPermission> detail(@PathVariable Long id) { return Result.success(permissionService.getById(id)); }
    @PostMapping
    public Result<Void> save(@RequestBody SysPermission permission) { permissionService.save(permission); return Result.success(); }
    @PutMapping
    public Result<Void> update(@RequestBody SysPermission permission) { permissionService.save(permission); return Result.success(); }
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam int status) { permissionService.updateStatus(id, status); return Result.success(); }
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) { permissionService.delete(id); return Result.success(); }
}
''')

w(f"{JAVA}/controller/SysConfigController.java", '''package com.mall.module.system.controller;

import com.mall.common.PageResult;
import com.mall.common.Result;
import com.mall.module.system.entity.SysConfig;
import com.mall.module.system.service.SysConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/system/config")
public class SysConfigController {
    @Autowired private SysConfigService configService;

    @GetMapping("/list")
    public Result<PageResult<SysConfig>> list(@RequestParam(defaultValue="") String keyword,
                                               @RequestParam(defaultValue="1") int pageNum,
                                               @RequestParam(defaultValue="10") int pageSize) {
        return Result.success(configService.list(keyword, pageNum, pageSize));
    }
    @GetMapping("/all")
    public Result<List<SysConfig>> all() { return Result.success(configService.all()); }
    @GetMapping("/key/{key}")
    public Result<String> getByKey(@PathVariable String key) { return Result.success(configService.getValue(key)); }
    @GetMapping("/{id}")
    public Result<SysConfig> detail(@PathVariable Long id) { return Result.success(configService.getById(id)); }
    @PostMapping
    public Result<Void> save(@RequestBody SysConfig config) { configService.save(config); return Result.success(); }
    @PutMapping
    public Result<Void> update(@RequestBody SysConfig config) { configService.save(config); return Result.success(); }
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) { configService.delete(id); return Result.success(); }
}
''')

w(f"{JAVA}/controller/SysOperationLogController.java", '''package com.mall.module.system.controller;

import com.mall.common.PageResult;
import com.mall.common.Result;
import com.mall.module.system.entity.SysOperationLog;
import com.mall.module.system.service.SysOperationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/system/log")
public class SysOperationLogController {
    @Autowired private SysOperationLogService logService;

    @GetMapping("/list")
    public Result<PageResult<SysOperationLog>> list(@RequestParam(required=false) String module,
                                                     @RequestParam(defaultValue="1") int pageNum,
                                                     @RequestParam(defaultValue="10") int pageSize) {
        return Result.success(logService.list(module, pageNum, pageSize));
    }
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) { logService.delete(id); return Result.success(); }
}
''')

print("\n=== System RBAC module generated! ===")
