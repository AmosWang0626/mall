-- ============================================================
-- Mall 数据库初始化脚本
-- 数据库: MySQL 8.0+
-- 字符集: utf8mb4
-- ============================================================

-- 务必最先设置字符集，防止中文乱码
SET NAMES utf8mb4;

CREATE DATABASE IF NOT EXISTS `mall` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `mall`;

SET FOREIGN_KEY_CHECKS = 0;

-- ============================================================
-- 1. 用户模块
-- ============================================================

-- 用户表
DROP TABLE IF EXISTS `mall_user`;
CREATE TABLE `mall_user` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username`    VARCHAR(50)  NOT NULL COMMENT '用户名',
    `password`    VARCHAR(100) NOT NULL COMMENT '密码(BCrypt)',
    `nickname`    VARCHAR(50)            DEFAULT NULL COMMENT '昵称',
    `phone`       VARCHAR(20)            DEFAULT NULL COMMENT '手机号',
    `email`       VARCHAR(100)           DEFAULT NULL COMMENT '邮箱',
    `avatar`      VARCHAR(255)           DEFAULT NULL COMMENT '头像URL',
    `gender`      TINYINT                DEFAULT 0 COMMENT '性别: 0-未知, 1-男, 2-女',
    `status`      TINYINT                DEFAULT 1 COMMENT '状态: 0-禁用, 1-正常',
    `register_ip` VARCHAR(50)            DEFAULT NULL COMMENT '注册IP',
    `last_login`  DATETIME               DEFAULT NULL COMMENT '最后登录时间',
    `create_time` DATETIME               DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME               DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`     TINYINT                DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商城用户表';

-- 用户收货地址表
DROP TABLE IF EXISTS `user_address`;
CREATE TABLE `user_address` (
    `id`           BIGINT       NOT NULL AUTO_INCREMENT,
    `user_id`      BIGINT       NOT NULL COMMENT '用户ID',
    `receiver`     VARCHAR(50)  NOT NULL COMMENT '收货人',
    `phone`        VARCHAR(20)  NOT NULL COMMENT '联系电话',
    `province`     VARCHAR(50)            DEFAULT NULL COMMENT '省',
    `city`         VARCHAR(50)            DEFAULT NULL COMMENT '市',
    `district`     VARCHAR(50)            DEFAULT NULL COMMENT '区',
    `detail`       VARCHAR(255)           DEFAULT NULL COMMENT '详细地址',
    `is_default`   TINYINT                DEFAULT 0 COMMENT '是否默认: 0-否, 1-是',
    `create_time`  DATETIME               DEFAULT CURRENT_TIMESTAMP,
    `update_time`  DATETIME               DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted`      TINYINT                DEFAULT 0,
    PRIMARY KEY (`id`),
    KEY `idx_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户收货地址';

-- ============================================================
-- 2. 积分模块
-- ============================================================

-- 用户积分账户表
DROP TABLE IF EXISTS `points_account`;
CREATE TABLE `points_account` (
    `id`          BIGINT  NOT NULL AUTO_INCREMENT,
    `user_id`     BIGINT  NOT NULL COMMENT '用户ID',
    `balance`     INT     NOT NULL DEFAULT 0 COMMENT '可用积分',
    `frozen`      INT     NOT NULL DEFAULT 0 COMMENT '冻结积分',
    `total_earned` BIGINT NOT NULL DEFAULT 0 COMMENT '累计获得积分',
    `total_used`   BIGINT NOT NULL DEFAULT 0 COMMENT '累计使用积分',
    `version`     INT     NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    `create_time` DATETIME        DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户积分账户';

-- 积分变动流水表
DROP TABLE IF EXISTS `points_log`;
CREATE TABLE `points_log` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `user_id`     BIGINT       NOT NULL COMMENT '用户ID',
    `change_type` VARCHAR(20)  NOT NULL COMMENT '变动类型: EARN-获得, USE-使用, FREEZE-冻结, UNFREEZE-解冻, REFUND-退回',
    `points`      INT          NOT NULL COMMENT '变动积分(正数)',
    `balance_after` INT        NOT NULL COMMENT '变动后余额',
    `source`      VARCHAR(30)           DEFAULT NULL COMMENT '来源: ORDER-下单, SIGN-签到, COUPON-兑换券, ADMIN-后台调整',
    `ref_id`      BIGINT                DEFAULT NULL COMMENT '关联业务ID',
    `remark`      VARCHAR(200)          DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME              DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_user` (`user_id`),
    KEY `idx_source` (`source`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='积分变动流水';

-- ============================================================
-- 3. 商品模块
-- ============================================================

-- 商品分类表
DROP TABLE IF EXISTS `product_category`;
CREATE TABLE `product_category` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `parent_id`   BIGINT       NOT NULL DEFAULT 0 COMMENT '父分类ID, 0为顶级',
    `name`        VARCHAR(50)  NOT NULL COMMENT '分类名称',
    `icon`        VARCHAR(255)          DEFAULT NULL COMMENT '分类图标',
    `sort`        INT                   DEFAULT 0 COMMENT '排序(越大越靠前)',
    `status`      TINYINT               DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    `level`       TINYINT               DEFAULT 1 COMMENT '层级: 1/2/3',
    `create_time` DATETIME              DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME              DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted`     TINYINT               DEFAULT 0,
    PRIMARY KEY (`id`),
    KEY `idx_parent` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类';

-- 商品SPU表
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product` (
    `id`            BIGINT        NOT NULL AUTO_INCREMENT,
    `category_id`   BIGINT        NOT NULL COMMENT '分类ID',
    `name`          VARCHAR(100)  NOT NULL COMMENT '商品名称',
    `subtitle`      VARCHAR(200)           DEFAULT NULL COMMENT '副标题',
    `main_image`    VARCHAR(255)           DEFAULT NULL COMMENT '主图URL',
    `sub_images`    TEXT                   DEFAULT NULL COMMENT '子图URL(JSON数组)',
    `detail`        LONGTEXT               DEFAULT NULL COMMENT '商品详情(富文本HTML)',
    `price`         DECIMAL(10,2) NOT NULL COMMENT '售价',
    `original_price` DECIMAL(10,2)         DEFAULT NULL COMMENT '原价(划线价)',
    `cost`          DECIMAL(10,2)          DEFAULT NULL COMMENT '成本价',
    `stock`         INT           NOT NULL DEFAULT 0 COMMENT '总库存',
    `sales`         INT           NOT NULL DEFAULT 0 COMMENT '销量',
    `status`        TINYINT       NOT NULL DEFAULT 0 COMMENT '状态: 0-下架, 1-上架, 2-草稿',
    `sort`          INT                    DEFAULT 0 COMMENT '排序',
    `tags`          VARCHAR(255)           DEFAULT NULL COMMENT '标签(逗号分隔)',
    `create_time`   DATETIME               DEFAULT CURRENT_TIMESTAMP,
    `update_time`   DATETIME               DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted`       TINYINT                DEFAULT 0,
    PRIMARY KEY (`id`),
    KEY `idx_category` (`category_id`),
    KEY `idx_status` (`status`),
    KEY `idx_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品SPU';

-- 商品SKU表
DROP TABLE IF EXISTS `product_sku`;
CREATE TABLE `product_sku` (
    `id`          BIGINT        NOT NULL AUTO_INCREMENT,
    `product_id`  BIGINT        NOT NULL COMMENT '商品ID',
    `sku_code`    VARCHAR(50)            DEFAULT NULL COMMENT 'SKU编码',
    `name`        VARCHAR(100) NOT NULL COMMENT 'SKU名称(如: 红色-XL)',
    `specs`       VARCHAR(500)           DEFAULT NULL COMMENT '规格JSON(如: {"颜色":"红","尺码":"XL"})',
    `price`       DECIMAL(10,2) NOT NULL COMMENT 'SKU价格',
    `stock`       INT           NOT NULL DEFAULT 0 COMMENT 'SKU库存',
    `image`       VARCHAR(255)           DEFAULT NULL COMMENT 'SKU图片',
    `status`      TINYINT                DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    `create_time` DATETIME               DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME               DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted`     TINYINT                DEFAULT 0,
    PRIMARY KEY (`id`),
    KEY `idx_product` (`product_id`),
    KEY `idx_code` (`sku_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品SKU';

-- ============================================================
-- 4. 购物车模块
-- ============================================================

DROP TABLE IF EXISTS `cart_item`;
CREATE TABLE `cart_item` (
    `id`          BIGINT        NOT NULL AUTO_INCREMENT,
    `user_id`     BIGINT        NOT NULL COMMENT '用户ID',
    `product_id`  BIGINT        NOT NULL COMMENT '商品ID',
    `sku_id`      BIGINT                 DEFAULT NULL COMMENT 'SKU ID',
    `product_name` VARCHAR(100)          DEFAULT NULL COMMENT '商品名称(冗余)',
    `product_image` VARCHAR(255)         DEFAULT NULL COMMENT '商品图片(冗余)',
    `sku_name`    VARCHAR(100)           DEFAULT NULL COMMENT 'SKU名称(冗余)',
    `price`       DECIMAL(10,2) NOT NULL COMMENT '加入时价格',
    `quantity`    INT           NOT NULL DEFAULT 1 COMMENT '数量',
    `selected`    TINYINT                DEFAULT 1 COMMENT '是否选中: 0-否, 1-是',
    `create_time` DATETIME               DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME               DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_user` (`user_id`),
    UNIQUE KEY `uk_user_product_sku` (`user_id`, `product_id`, `sku_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车';

-- ============================================================
-- 5. 订单模块
-- ============================================================

-- 订单主表
DROP TABLE IF EXISTS `mall_order`;
CREATE TABLE `mall_order` (
    `id`              BIGINT        NOT NULL AUTO_INCREMENT,
    `order_no`        VARCHAR(32)   NOT NULL COMMENT '订单号',
    `user_id`         BIGINT        NOT NULL COMMENT '用户ID',
    `total_amount`    DECIMAL(10,2) NOT NULL COMMENT '商品总金额',
    `discount_amount` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '优惠金额',
    `points_amount`   DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '积分抵扣金额',
    `pay_amount`      DECIMAL(10,2) NOT NULL COMMENT '实付金额',
    `points_used`     INT           NOT NULL DEFAULT 0 COMMENT '使用积分',
    `points_earned`   INT           NOT NULL DEFAULT 0 COMMENT '获得积分',
    `coupon_id`       BIGINT                 DEFAULT NULL COMMENT '使用的优惠券ID',
    `status`          TINYINT       NOT NULL DEFAULT 0 COMMENT '状态: 0-待付款, 1-待发货, 2-待收货, 3-已完成, 4-已取消, 5-已退款',
    `pay_type`        TINYINT                DEFAULT NULL COMMENT '支付方式: 1-微信, 2-支付宝, 3-余额',
    `pay_time`        DATETIME               DEFAULT NULL COMMENT '支付时间',
    `ship_time`       DATETIME               DEFAULT NULL COMMENT '发货时间',
    `receive_time`    DATETIME               DEFAULT NULL COMMENT '收货时间',
    `close_time`      DATETIME               DEFAULT NULL COMMENT '关闭时间',
    `receiver`        VARCHAR(50)            DEFAULT NULL COMMENT '收货人',
    `receiver_phone`  VARCHAR(20)            DEFAULT NULL COMMENT '收货电话',
    `receiver_address` VARCHAR(500)          DEFAULT NULL COMMENT '收货地址',
    `ship_company`    VARCHAR(50)            DEFAULT NULL COMMENT '物流公司',
    `ship_no`         VARCHAR(50)            DEFAULT NULL COMMENT '物流单号',
    `remark`          VARCHAR(500)           DEFAULT NULL COMMENT '订单备注',
    `expire_time`     DATETIME               DEFAULT NULL COMMENT '过期时间(超时自动取消)',
    `create_time`     DATETIME               DEFAULT CURRENT_TIMESTAMP,
    `update_time`     DATETIME               DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted`         TINYINT                DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_user` (`user_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单主表';

-- 订单明细表
DROP TABLE IF EXISTS `order_item`;
CREATE TABLE `order_item` (
    `id`            BIGINT        NOT NULL AUTO_INCREMENT,
    `order_id`      BIGINT        NOT NULL COMMENT '订单ID',
    `order_no`      VARCHAR(32)   NOT NULL COMMENT '订单号(冗余)',
    `product_id`    BIGINT        NOT NULL COMMENT '商品ID',
    `sku_id`        BIGINT                 DEFAULT NULL COMMENT 'SKU ID',
    `product_name`  VARCHAR(100)  NOT NULL COMMENT '商品名称',
    `product_image` VARCHAR(255)           DEFAULT NULL COMMENT '商品图片',
    `sku_name`      VARCHAR(100)           DEFAULT NULL COMMENT 'SKU名称',
    `price`         DECIMAL(10,2) NOT NULL COMMENT '购买单价',
    `quantity`      INT           NOT NULL COMMENT '购买数量',
    `total_amount`  DECIMAL(10,2) NOT NULL COMMENT '小计金额',
    `create_time`   DATETIME               DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_order` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单明细';

-- ============================================================
-- 6. 优惠券模块
-- ============================================================

-- 优惠券模板表
DROP TABLE IF EXISTS `coupon_template`;
CREATE TABLE `coupon_template` (
    `id`            BIGINT        NOT NULL AUTO_INCREMENT,
    `name`          VARCHAR(100)  NOT NULL COMMENT '优惠券名称',
    `type`          TINYINT       NOT NULL COMMENT '类型: 1-满减券, 2-折扣券, 3-无门槛券',
    `face_value`    DECIMAL(10,2)          DEFAULT NULL COMMENT '面值(满减/无门槛)',
    `discount`      DECIMAL(3,2)           DEFAULT NULL COMMENT '折扣率(折扣券, 如0.85)',
    `min_spend`     DECIMAL(10,2)          DEFAULT 0.00 COMMENT '最低消费(满减券)',
    `category_limit` BIGINT                DEFAULT NULL COMMENT '限定分类ID(NULL不限)',
    `total_count`   INT           NOT NULL DEFAULT -1 COMMENT '发放总量(-1不限)',
    `issued_count`  INT           NOT NULL DEFAULT 0 COMMENT '已发放数量',
    `per_limit`     INT           NOT NULL DEFAULT 1 COMMENT '每人限领数量',
    `valid_type`    TINYINT       NOT NULL DEFAULT 1 COMMENT '有效期类型: 1-固定日期, 2-领取后N天',
    `valid_start`   DATETIME               DEFAULT NULL COMMENT '有效期开始(固定日期)',
    `valid_end`     DATETIME               DEFAULT NULL COMMENT '有效期结束(固定日期)',
    `valid_days`    INT                    DEFAULT NULL COMMENT '领取后有效天数',
    `status`        TINYINT       NOT NULL DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    `create_time`   DATETIME               DEFAULT CURRENT_TIMESTAMP,
    `update_time`   DATETIME               DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted`       TINYINT                DEFAULT 0,
    PRIMARY KEY (`id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='优惠券模板';

-- 用户优惠券表
DROP TABLE IF EXISTS `user_coupon`;
CREATE TABLE `user_coupon` (
    `id`          BIGINT  NOT NULL AUTO_INCREMENT,
    `user_id`     BIGINT  NOT NULL COMMENT '用户ID',
    `coupon_id`   BIGINT  NOT NULL COMMENT '优惠券模板ID',
    `status`      TINYINT NOT NULL DEFAULT 0 COMMENT '状态: 0-未使用, 1-已使用, 2-已过期',
    `order_id`    BIGINT           DEFAULT NULL COMMENT '使用的订单ID',
    `valid_start` DATETIME         DEFAULT NULL COMMENT '有效期开始',
    `valid_end`   DATETIME         DEFAULT NULL COMMENT '有效期结束',
    `receive_time` DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '领取时间',
    `use_time`    DATETIME         DEFAULT NULL COMMENT '使用时间',
    `create_time` DATETIME         DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME         DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_user` (`user_id`),
    KEY `idx_coupon` (`coupon_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户优惠券';

-- ============================================================
-- 7. 营销活动模块
-- ============================================================

-- 营销活动表 (限时秒杀/满减活动等)
DROP TABLE IF EXISTS `marketing_activity`;
CREATE TABLE `marketing_activity` (
    `id`            BIGINT        NOT NULL AUTO_INCREMENT,
    `name`          VARCHAR(100)  NOT NULL COMMENT '活动名称',
    `type`          VARCHAR(20)   NOT NULL COMMENT '类型: FLASH_SALE-限时秒杀, FULL_REDUCTION-满减, DISCOUNT-折扣',
    `description`   VARCHAR(500)           DEFAULT NULL COMMENT '活动描述',
    `start_time`    DATETIME      NOT NULL COMMENT '开始时间',
    `end_time`      DATETIME      NOT NULL COMMENT '结束时间',
    `status`        TINYINT       NOT NULL DEFAULT 0 COMMENT '状态: 0-未开始, 1-进行中, 2-已结束, 3-已终止',
    `enabled`       TINYINT       NOT NULL DEFAULT 1 COMMENT '是否启用: 0-禁用, 1-启用',
    `rules`         TEXT                   DEFAULT NULL COMMENT '活动规则JSON',
    `create_time`   DATETIME               DEFAULT CURRENT_TIMESTAMP,
    `update_time`   DATETIME               DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted`       TINYINT                DEFAULT 0,
    PRIMARY KEY (`id`),
    KEY `idx_type` (`type`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='营销活动';

-- 营销活动-商品关联表
DROP TABLE IF EXISTS `activity_product`;
CREATE TABLE `activity_product` (
    `id`            BIGINT        NOT NULL AUTO_INCREMENT,
    `activity_id`   BIGINT        NOT NULL COMMENT '活动ID',
    `product_id`    BIGINT        NOT NULL COMMENT '商品ID',
    `sku_id`        BIGINT                 DEFAULT NULL COMMENT 'SKU ID',
    `activity_price` DECIMAL(10,2)         DEFAULT NULL COMMENT '活动价格(秒杀价)',
    `activity_stock` INT                   DEFAULT NULL COMMENT '活动库存',
    `limit_per_user` INT                   DEFAULT 1 COMMENT '每人限购数量',
    `sort`          INT                   DEFAULT 0,
    `create_time`   DATETIME               DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_activity` (`activity_id`),
    KEY `idx_product` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='活动商品关联';

-- ============================================================
-- 8. 后台管理模块 (RBAC)
-- ============================================================

-- 管理员表
DROP TABLE IF EXISTS `sys_admin`;
CREATE TABLE `sys_admin` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `username`    VARCHAR(50)  NOT NULL COMMENT '用户名',
    `password`    VARCHAR(100) NOT NULL COMMENT '密码(BCrypt)',
    `nickname`    VARCHAR(50)            DEFAULT NULL COMMENT '昵称',
    `avatar`      VARCHAR(255)           DEFAULT NULL COMMENT '头像',
    `email`       VARCHAR(100)           DEFAULT NULL COMMENT '邮箱',
    `phone`       VARCHAR(20)            DEFAULT NULL COMMENT '手机号',
    `status`      TINYINT                DEFAULT 1 COMMENT '状态: 0-禁用, 1-正常',
    `last_login`  DATETIME               DEFAULT NULL COMMENT '最后登录时间',
    `create_time` DATETIME               DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME               DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted`     TINYINT                DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统管理员';

-- 角色表
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `name`        VARCHAR(50)  NOT NULL COMMENT '角色名称',
    `code`        VARCHAR(50)  NOT NULL COMMENT '角色编码',
    `description` VARCHAR(200)           DEFAULT NULL COMMENT '描述',
    `sort`        INT                    DEFAULT 0 COMMENT '排序',
    `status`      TINYINT                DEFAULT 1 COMMENT '状态: 0-禁用, 1-正常',
    `data_scope`  TINYINT                DEFAULT 1 COMMENT '数据权限范围: 1-全部, 2-自定义, 3-本人',
    `create_time` DATETIME               DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME               DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted`     TINYINT                DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统角色';

-- 权限表
DROP TABLE IF EXISTS `sys_permission`;
CREATE TABLE `sys_permission` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `parent_id`   BIGINT       NOT NULL DEFAULT 0 COMMENT '父权限ID',
    `name`        VARCHAR(50)  NOT NULL COMMENT '权限名称',
    `code`        VARCHAR(100) NOT NULL COMMENT '权限编码(如 product:list)',
    `type`        TINYINT      NOT NULL DEFAULT 1 COMMENT '类型: 1-菜单, 2-按钮, 3-接口',
    `path`        VARCHAR(200)           DEFAULT NULL COMMENT '前端路由路径',
    `component`   VARCHAR(200)           DEFAULT NULL COMMENT '前端组件路径',
    `icon`        VARCHAR(50)            DEFAULT NULL COMMENT '菜单图标',
    `sort`        INT                    DEFAULT 0 COMMENT '排序',
    `status`      TINYINT                DEFAULT 1 COMMENT '状态: 0-禁用, 1-正常',
    `visible`     TINYINT                DEFAULT 1 COMMENT '是否可见: 0-隐藏, 1-显示',
    `create_time` DATETIME               DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME               DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted`     TINYINT                DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`),
    KEY `idx_parent` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统权限';

-- 角色-权限关联表
DROP TABLE IF EXISTS `sys_role_permission`;
CREATE TABLE `sys_role_permission` (
    `role_id`       BIGINT NOT NULL COMMENT '角色ID',
    `permission_id` BIGINT NOT NULL COMMENT '权限ID',
    PRIMARY KEY (`role_id`, `permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联';

-- 管理员-角色关联表
DROP TABLE IF EXISTS `sys_admin_role`;
CREATE TABLE `sys_admin_role` (
    `admin_id` BIGINT NOT NULL COMMENT '管理员ID',
    `role_id`  BIGINT NOT NULL COMMENT '角色ID',
    PRIMARY KEY (`admin_id`, `role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员角色关联';

-- 系统配置表
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config` (
    `id`           BIGINT       NOT NULL AUTO_INCREMENT,
    `config_key`   VARCHAR(100) NOT NULL COMMENT '配置键',
    `config_value` TEXT                  DEFAULT NULL COMMENT '配置值',
    `config_type`  VARCHAR(20)           DEFAULT 'string' COMMENT '值类型: string/number/boolean/json',
    `name`         VARCHAR(100)          DEFAULT NULL COMMENT '配置名称',
    `description`  VARCHAR(255)          DEFAULT NULL COMMENT '描述',
    `is_system`    TINYINT               DEFAULT 0 COMMENT '是否系统内置: 0-否, 1-是',
    `create_time`  DATETIME              DEFAULT CURRENT_TIMESTAMP,
    `update_time`  DATETIME              DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统配置';

-- 操作日志表
DROP TABLE IF EXISTS `sys_operation_log`;
CREATE TABLE `sys_operation_log` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `admin_id`    BIGINT                DEFAULT NULL COMMENT '操作人ID',
    `admin_name`  VARCHAR(50)           DEFAULT NULL COMMENT '操作人名称',
    `module`      VARCHAR(50)           DEFAULT NULL COMMENT '操作模块',
    `operation`   VARCHAR(100)          DEFAULT NULL COMMENT '操作描述',
    `method`      VARCHAR(200)          DEFAULT NULL COMMENT '请求方法',
    `request_url` VARCHAR(255)          DEFAULT NULL COMMENT '请求URL',
    `request_param` TEXT                DEFAULT NULL COMMENT '请求参数',
    `ip`          VARCHAR(50)           DEFAULT NULL COMMENT 'IP地址',
    `cost_time`   BIGINT                DEFAULT NULL COMMENT '耗时(ms)',
    `create_time` DATETIME              DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_admin` (`admin_id`),
    KEY `idx_create` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志';

-- ============================================================
-- 9. 初始化数据
-- ============================================================

-- 默认管理员 (密码: admin123, BCrypt加密)
INSERT INTO `sys_admin` (`username`, `password`, `nickname`, `status`) VALUES
('admin', '$2a$10$GJ0.jc.y9culuivwtj6XjerBh9ZlsyKhdybRMcO9xPfDN5P66EiWy', '超级管理员', 1);

-- 默认角色
INSERT INTO `sys_role` (`name`, `code`, `description`, `sort`, `status`, `data_scope`) VALUES
('超级管理员', 'super_admin', '拥有所有权限', 1, 1, 1),
('运营', 'operator', '商品、订单、营销管理', 2, 1, 1),
('客服', 'service', '仅查看订单和用户', 3, 1, 3);

-- 默认权限菜单
INSERT INTO `sys_permission` (`parent_id`, `name`, `code`, `type`, `path`, `component`, `icon`, `sort`, `status`, `visible`) VALUES
(0, '仪表盘', 'dashboard', 1, '/dashboard', 'dashboard/index', 'Odometer', 1, 1, 1),
(0, '商品管理', 'product', 1, '/product', 'layout', 'Goods', 2, 1, 1),
(0, '商品列表', 'product:list', 1, '/product/list', 'product/list', 'List', 1, 1, 1),
(0, '添加商品', 'product:add', 2, NULL, NULL, NULL, 2, 1, 0),
(0, '编辑商品', 'product:edit', 2, NULL, NULL, NULL, 3, 1, 0),
(0, '删除商品', 'product:delete', 2, NULL, NULL, NULL, 4, 1, 0),
(0, '分类管理', 'product:category', 1, '/product/category', 'product/category', 'Grid', 3, 1, 1),
(0, '订单管理', 'order', 1, '/order', 'layout', 'Document', 4, 1, 1),
(0, '订单列表', 'order:list', 1, '/order/list', 'order/list', 'List', 1, 1, 1),
(0, '订单详情', 'order:detail', 2, NULL, NULL, NULL, 2, 1, 0),
(0, '发货', 'order:ship', 2, NULL, NULL, NULL, 3, 1, 0),
(0, '用户管理', 'user', 1, '/user', 'layout', 'User', 5, 1, 1),
(0, '用户列表', 'user:list', 1, '/user/list', 'user/list', 'List', 1, 1, 1),
(0, '积分管理', 'points', 1, '/points', 'layout', 'Coin', 6, 1, 1),
(0, '积分账户', 'points:account', 1, '/points/account', 'points/account', 'Wallet', 1, 1, 1),
(0, '积分流水', 'points:log', 1, '/points/log', 'points/log', 'Tickets', 2, 1, 1),
(0, '优惠券管理', 'coupon', 1, '/coupon', 'layout', 'Ticket', 7, 1, 1),
(0, '优惠券模板', 'coupon:template', 1, '/coupon/template', 'coupon/template', 'Document', 1, 1, 1),
(0, '领取记录', 'coupon:record', 1, '/coupon/record', 'coupon/record', 'List', 2, 1, 1),
(0, '营销活动', 'marketing', 1, '/marketing', 'layout', 'TrendCharts', 8, 1, 1),
(0, '活动列表', 'marketing:activity', 1, '/marketing/activity', 'marketing/activity', 'List', 1, 1, 1),
(0, '系统管理', 'system', 1, '/system', 'layout', 'Setting', 9, 1, 1),
(0, '管理员管理', 'system:admin', 1, '/system/admin', 'system/admin', 'UserFilled', 1, 1, 1),
(0, '角色管理', 'system:role', 1, '/system/role', 'system/role', 'Avatar', 2, 1, 1),
(0, '权限管理', 'system:permission', 1, '/system/permission', 'system/permission', 'Key', 3, 1, 1),
(0, '系统配置', 'system:config', 1, '/system/config', 'system/config', 'Tools', 4, 1, 1),
(0, '操作日志', 'system:log', 1, '/system/log', 'system/log', 'Document', 5, 1, 1);

-- 超级管理员角色关联
INSERT INTO `sys_admin_role` (`admin_id`, `role_id`) VALUES (1, 1);

-- 超级管理员拥有所有权限
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`)
SELECT 1, id FROM `sys_permission`;

-- 默认系统配置
INSERT INTO `sys_config` (`config_key`, `config_value`, `config_type`, `name`, `description`, `is_system`) VALUES
('site_name', 'Mall 商城', 'string', '站点名称', '商城站点名称', 1),
('site_logo', '', 'string', '站点Logo', '商城Logo地址', 0),
('order_timeout', '30', 'number', '订单超时时间', '订单未支付自动取消时间(分钟)', 1),
('points_rate', '1', 'number', '积分兑换比例', '1元=N积分', 1),
('points_use_rate', '100', 'number', '积分抵扣比例', '100积分=1元', 1),
('auto_receive_days', '7', 'number', '自动收货天数', '发货后自动确认收货天数', 1),
('sign_points', '10', 'number', '签到积分', '每日签到获得积分数', 1),
('free_shipping_threshold', '99', 'number', '免邮门槛', '满X元免邮费', 0),
('default_shipping_fee', '10', 'number', '默认运费', '默认运费金额', 0);

-- 默认商品分类
INSERT INTO `product_category` (`id`, `parent_id`, `name`, `sort`, `status`, `level`) VALUES
(1, 0, 'Apple 苹果', 100, 1, 1),
(2, 0, '华为 Huawei', 90, 1, 1),
(3, 0, '虚拟商品', 80, 1, 1),
(11, 1, 'iPhone', 100, 1, 2),
(12, 1, 'Mac 电脑', 90, 1, 2),
(13, 1, 'iPad 平板', 80, 1, 2),
(14, 1, 'Apple Watch', 70, 1, 2),
(21, 2, '华为手机', 100, 1, 2),
(22, 2, '华为手表', 90, 1, 2),
(23, 2, '华为耳机', 80, 1, 2),
(31, 3, '手机充值', 100, 1, 2),
(32, 3, '视频会员', 90, 1, 2),
(33, 3, '购物娱乐会员', 80, 1, 2);

-- ========== Apple iPhone ==========
INSERT INTO `product` (`id`, `category_id`, `name`, `subtitle`, `main_image`, `price`, `original_price`, `cost`, `stock`, `sales`, `status`, `sort`, `tags`) VALUES
(1, 11, 'iPhone 15 Pro Max', 'A17 Pro芯片 | 6.7英寸 | 5倍光学变焦', 'https://picsum.photos/seed/iphone15promax/400/400', 9999.00, 10999.00, 7500.00, 500, 328, 1, 1000, '热销,新品,5G'),
(2, 11, 'iPhone 15 Pro', 'A17 Pro芯片 | 6.1英寸 | 钛金属设计', 'https://picsum.photos/seed/iphone15pro/400/400', 7999.00, 8999.00, 6000.00, 600, 256, 1, 990, '热销,新品,5G'),
(3, 11, 'iPhone 15', 'A16仿生芯片 | 6.1英寸 | 灵动岛', 'https://picsum.photos/seed/iphone15/400/400', 5999.00, 6499.00, 4500.00, 800, 189, 1, 980, '热销,5G'),

-- ========== Apple Mac ==========
(4, 12, 'MacBook Pro 14 M3 Pro', 'M3 Pro芯片 | 18GB统一内存 | Liquid视网膜XDR', 'https://picsum.photos/seed/macbookpro14/400/400', 15999.00, 16999.00, 12000.00, 200, 78, 1, 900, '新品,M系列,专业'),
(5, 12, 'MacBook Air 15 M3', 'M3芯片 | 8GB统一内存 | 15.3英寸轻薄本', 'https://picsum.photos/seed/macbookair15/400/400', 8999.00, 9499.00, 6500.00, 300, 156, 1, 890, '热销,轻薄,M系列'),
(6, 12, 'iMac 24 M3', 'M3芯片 | 24英寸4.5K视网膜 | 七彩设计', 'https://picsum.photos/seed/imac24/400/400', 10999.00, 11499.00, 8000.00, 150, 45, 1, 880, '新品,一体机,M系列'),

-- ========== Apple iPad ==========
(7, 13, 'iPad Pro 12.9 M2', 'M2芯片 | 12.9英寸Liquid视网膜XDR | ProMotion', 'https://picsum.photos/seed/ipadpro129/400/400', 8999.00, 9499.00, 6500.00, 250, 92, 1, 800, '热销,专业,生产力'),
(8, 13, 'iPad Air 5', 'M1芯片 | 10.9英寸Liquid视网膜 | 支持Apple Pencil', 'https://picsum.photos/seed/ipadair5/400/400', 4799.00, 4999.00, 3500.00, 400, 167, 1, 790, '热销,轻薄,生产力'),
(9, 13, 'iPad 10', 'A14芯片 | 10.9英寸视网膜 | 全屏幕设计', 'https://picsum.photos/seed/ipad10/400/400', 3599.00, 3799.00, 2500.00, 500, 234, 1, 780, '热销,入门'),

-- ========== Apple Watch ==========
(10, 14, 'Apple Watch Series 9', 'S9芯片 | 亮度翻倍 | 双指互点手势', 'https://picsum.photos/seed/applewatchs9/400/400', 3199.00, 3499.00, 2200.00, 350, 145, 1, 700, '热销,健康,运动'),
(11, 14, 'Apple Watch SE', 'S8芯片 | 跌倒检测 | 性价比之选', 'https://picsum.photos/seed/applewatchse/400/400', 1999.00, 2199.00, 1400.00, 400, 98, 1, 690, '热销,入门,健康'),

-- ========== 华为手机 ==========
(12, 21, 'HUAWEI Mate 60 Pro', '麒麟9000S | 卫星通话 | 昆仑玻璃', 'https://picsum.photos/seed/mate60pro/400/400', 6999.00, 7299.00, 5000.00, 300, 412, 1, 600, '热销,新品,5G,国产之光'),
(13, 21, 'HUAWEI P60 Pro', '超聚光XMAGE | 4810万像素 | 洛可可白', 'https://picsum.photos/seed/p60pro/400/400', 5488.00, 5988.00, 4000.00, 400, 267, 1, 590, '热销,影像,5G'),
(14, 21, 'HUAWEI nova 12', '骁龙8 Gen3 | 6000万像素追焦 | 轻薄设计', 'https://picsum.photos/seed/nova12/400/400', 2999.00, 3299.00, 2000.00, 600, 178, 1, 580, '新品,自拍,5G'),

-- ========== 华为手表 ==========
(15, 22, 'HUAWEI Watch GT4', '14天超长续航 | 双频GPS | 健康监测', 'https://picsum.photos/seed/watchgt4/400/400', 1588.00, 1788.00, 1000.00, 500, 203, 1, 500, '热销,运动,长续航'),
(16, 22, 'HUAWEI Watch Fit 3', '超轻薄设计 | 10天续航 | 100+运动模式', 'https://picsum.photos/seed/watchfit3/400/400', 999.00, 1099.00, 650.00, 800, 312, 1, 490, '热销,轻薄,性价比'),

-- ========== 华为耳机 ==========
(17, 23, 'HUAWEI FreeBuds Pro 3', '通话降噪2.0 | LDAC高清音质 | 智慧音频', 'https://picsum.photos/seed/freebudspro3/400/400', 1499.00, 1599.00, 1000.00, 600, 189, 1, 400, '热销,降噪,新品'),
(18, 23, 'HUAWEI FreeBuds 5', '双单元设计 | 主动降噪 | 舒适佩戴', 'https://picsum.photos/seed/freebuds5/400/400', 699.00, 799.00, 450.00, 1000, 356, 1, 390, '热销,性价比'),

-- ========== 虚拟商品 - 手机充值 ==========
(19, 31, '手机充值卡 50元', '全国三网通用 | 即时到账', 'https://picsum.photos/seed/recharge50/400/400', 50.00, NULL, 48.00, 99999, 1567, 1, 300, '即时发货,虚拟商品'),
(20, 31, '手机充值卡 100元', '全国三网通用 | 即时到账', 'https://picsum.photos/seed/recharge100/400/400', 100.00, NULL, 97.00, 99999, 2345, 1, 290, '即时发货,虚拟商品'),
(21, 31, '手机充值卡 200元', '全国三网通用 | 即时到账', 'https://picsum.photos/seed/recharge200/400/400', 200.00, NULL, 194.00, 99999, 876, 1, 280, '即时发货,虚拟商品'),
(22, 31, '手机充值卡 500元', '全国三网通用 | 即时到账', 'https://picsum.photos/seed/recharge500/400/400', 500.00, NULL, 485.00, 99999, 345, 1, 270, '即时发货,虚拟商品'),

-- ========== 虚拟商品 - 视频会员 ==========
(23, 32, '爱奇艺黄金VIP 月卡', '去广告 | 1080P | 4台设备', 'https://picsum.photos/seed/iqiyim/400/400', 25.00, 30.00, 18.00, 99999, 3421, 1, 200, '即时发货,虚拟商品'),
(24, 32, '爱奇艺黄金VIP 年卡', '去广告 | 1080P | 4台设备 | 超值年卡', 'https://picsum.photos/seed/iqiyiy/400/400', 218.00, 248.00, 160.00, 99999, 1876, 1, 190, '即时发货,虚拟商品,超值'),
(25, 32, '腾讯视频VIP 月卡', '专属内容 | 4K | 5台设备', 'https://picsum.photos/seed/tencentm/400/400', 25.00, 30.00, 18.00, 99999, 2987, 1, 180, '即时发货,虚拟商品'),
(26, 32, '腾讯视频VIP 年卡', '专属内容 | 4K | 5台设备 | 超值年卡', 'https://picsum.photos/seed/tencenty/400/400', 233.00, 253.00, 170.00, 99999, 1543, 1, 170, '即时发货,虚拟商品,超值'),
(27, 32, '优酷VIP 月卡', '免广告 | 1080P | 3台设备', 'https://picsum.photos/seed/youkum/400/400', 25.00, 30.00, 18.00, 99999, 1234, 1, 160, '即时发货,虚拟商品'),
(28, 32, '优酷VIP 年卡', '免广告 | 1080P | 3台设备 | 超值年卡', 'https://picsum.photos/seed/youkuy/400/400', 218.00, 248.00, 160.00, 99999, 678, 1, 150, '即时发货,虚拟商品,超值'),
(29, 32, 'B站大会员 月卡', '4K画质 | 专属番剧 | 优惠券礼包', 'https://picsum.photos/seed/bilibilim/400/400', 25.00, 30.00, 18.00, 99999, 2109, 1, 140, '即时发货,虚拟商品'),
(30, 32, 'B站大会员 年卡', '4K画质 | 专属番剧 | 超值年卡', 'https://picsum.photos/seed/bilibiliy/400/400', 148.00, 168.00, 110.00, 99999, 987, 1, 130, '即时发货,虚拟商品,超值'),

-- ========== 虚拟商品 - 购物娱乐会员 ==========
(31, 33, '京东PLUS会员 年卡', 'PLUS专享价 | 免邮券 | 专属客服', 'https://picsum.photos/seed/jdplus/400/400', 148.00, 198.00, 100.00, 99999, 1456, 1, 100, '即时发货,虚拟商品,超值'),
(32, 33, '大麦VIP 年卡', '演出优先购 | 专属折扣 | 会员日', 'https://picsum.photos/seed/damai/400/400', 128.00, 158.00, 90.00, 99999, 567, 1, 90, '即时发货,虚拟商品');

-- ========== 商品SKU ==========
INSERT INTO `product_sku` (`id`, `product_id`, `sku_code`, `name`, `specs`, `price`, `stock`, `image`, `status`) VALUES
-- iPhone 15 Pro Max
(1, 1, 'IP15PM-256-BK', '256GB 黑色钛金属', '{"存储":"256GB","颜色":"黑色钛金属"}', 9999.00, 200, 'https://picsum.photos/seed/iphone15promax/400/400', 1),
(2, 1, 'IP15PM-512-BK', '512GB 黑色钛金属', '{"存储":"512GB","颜色":"黑色钛金属"}', 10999.00, 150, 'https://picsum.photos/seed/iphone15promax/400/400', 1),
(3, 1, 'IP15PM-1TB-BL', '1TB 蓝色钛金属', '{"存储":"1TB","颜色":"蓝色钛金属"}', 11999.00, 100, 'https://picsum.photos/seed/iphone15promax/400/400', 1),
(4, 1, 'IP15PM-256-NL', '256GB 原色钛金属', '{"存储":"256GB","颜色":"原色钛金属"}', 9999.00, 150, 'https://picsum.photos/seed/iphone15promax/400/400', 1),
-- iPhone 15 Pro
(5, 2, 'IP15P-128-BK', '128GB 黑色钛金属', '{"存储":"128GB","颜色":"黑色钛金属"}', 7999.00, 200, 'https://picsum.photos/seed/iphone15pro/400/400', 1),
(6, 2, 'IP15P-256-NL', '256GB 原色钛金属', '{"存储":"256GB","颜色":"原色钛金属"}', 8999.00, 200, 'https://picsum.photos/seed/iphone15pro/400/400', 1),
(7, 2, 'IP15P-512-BL', '512GB 蓝色钛金属', '{"存储":"512GB","颜色":"蓝色钛金属"}', 9999.00, 150, 'https://picsum.photos/seed/iphone15pro/400/400', 1),
-- iPhone 15
(8, 3, 'IP15-128-BK', '128GB 黑色', '{"存储":"128GB","颜色":"黑色"}', 5999.00, 300, 'https://picsum.photos/seed/iphone15/400/400', 1),
(9, 3, 'IP15-256-BL', '256GB 蓝色', '{"存储":"256GB","颜色":"蓝色"}', 6999.00, 250, 'https://picsum.photos/seed/iphone15/400/400', 1),
(10, 3, 'IP15-128-PK', '128GB 粉色', '{"存储":"128GB","颜色":"粉色"}', 5999.00, 200, 'https://picsum.photos/seed/iphone15/400/400', 1),
-- MacBook Pro 14
(11, 4, 'MBP14-18-512-BK', '18GB+512GB 深空黑', '{"内存":"18GB","存储":"512GB","颜色":"深空黑"}', 15999.00, 100, 'https://picsum.photos/seed/macbookpro14/400/400', 1),
(12, 4, 'MBP14-36-1TB-BK', '36GB+1TB 深空黑', '{"内存":"36GB","存储":"1TB","颜色":"深空黑"}', 19999.00, 80, 'https://picsum.photos/seed/macbookpro14/400/400', 1),
-- MacBook Air 15
(13, 5, 'MBA15-8-256-MD', '8GB+256GB 午夜色', '{"内存":"8GB","存储":"256GB","颜色":"午夜色"}', 8999.00, 150, 'https://picsum.photos/seed/macbookair15/400/400', 1),
(14, 5, 'MBA15-16-512-SL', '16GB+512GB 星光色', '{"内存":"16GB","存储":"512GB","颜色":"星光色"}', 10499.00, 120, 'https://picsum.photos/seed/macbookair15/400/400', 1),
-- iMac 24
(15, 6, 'IMAC24-8-256-SL', '8GB+256GB 星光色', '{"内存":"8GB","存储":"256GB","颜色":"星光色"}', 10999.00, 80, 'https://picsum.photos/seed/imac24/400/400', 1),
(16, 6, 'IMAC24-16-512-BL', '16GB+512GB 蓝色', '{"内存":"16GB","存储":"512GB","颜色":"蓝色"}', 12999.00, 50, 'https://picsum.photos/seed/imac24/400/400', 1),
-- iPad Pro 12.9
(17, 7, 'IPP129-256-SL', '256GB 银色', '{"存储":"256GB","颜色":"银色"}', 8999.00, 100, 'https://picsum.photos/seed/ipadpro129/400/400', 1),
(18, 7, 'IPP129-512-SG', '512GB 深空灰', '{"存储":"512GB","颜色":"深空灰"}', 9999.00, 100, 'https://picsum.photos/seed/ipadpro129/400/400', 1),
-- iPad Air 5
(19, 8, 'IPA5-64-SL', '64GB 星光色', '{"存储":"64GB","颜色":"星光色"}', 4799.00, 200, 'https://picsum.photos/seed/ipadair5/400/400', 1),
(20, 8, 'IPA5-256-SG', '256GB 深空灰', '{"存储":"256GB","颜色":"深空灰"}', 5599.00, 150, 'https://picsum.photos/seed/ipadair5/400/400', 1),
-- iPad 10
(21, 9, 'IP10-64-SL', '64GB 银色', '{"存储":"64GB","颜色":"银色"}', 3599.00, 250, 'https://picsum.photos/seed/ipad10/400/400', 1),
(22, 9, 'IP10-256-BL', '256GB 蓝色', '{"存储":"256GB","颜色":"蓝色"}', 4399.00, 200, 'https://picsum.photos/seed/ipad10/400/400', 1),
-- Apple Watch Series 9
(23, 10, 'AWS9-41-MD', '41mm 午夜色', '{"表盘":"41mm","颜色":"午夜色"}', 3199.00, 150, 'https://picsum.photos/seed/applewatchs9/400/400', 1),
(24, 10, 'AWS9-45-SL', '45mm 星光色', '{"表盘":"45mm","颜色":"星光色"}', 3499.00, 150, 'https://picsum.photos/seed/applewatchs9/400/400', 1),
-- Apple Watch SE
(25, 11, 'AWSE-40-SL', '40mm 银色', '{"表盘":"40mm","颜色":"银色"}', 1999.00, 200, 'https://picsum.photos/seed/applewatchse/400/400', 1),
(26, 11, 'AWSE-44-SG', '44mm 深空灰', '{"表盘":"44mm","颜色":"深空灰"}', 2299.00, 150, 'https://picsum.photos/seed/applewatchse/400/400', 1),
-- HUAWEI Mate 60 Pro
(27, 12, 'M60P-256-GN', '256GB 雅川青', '{"存储":"256GB","颜色":"雅川青"}', 6999.00, 150, 'https://picsum.photos/seed/mate60pro/400/400', 1),
(28, 12, 'M60P-512-WT', '512GB 白色', '{"存储":"512GB","颜色":"白色"}', 7999.00, 100, 'https://picsum.photos/seed/mate60pro/400/400', 1),
-- HUAWEI P60 Pro
(29, 13, 'P60P-256-WT', '256GB 洛可可白', '{"存储":"256GB","颜色":"洛可可白"}', 5488.00, 200, 'https://picsum.photos/seed/p60pro/400/400', 1),
(30, 13, 'P60P-512-BK', '512GB 羽砂黑', '{"存储":"512GB","颜色":"羽砂黑"}', 5988.00, 150, 'https://picsum.photos/seed/p60pro/400/400', 1),
-- HUAWEI nova 12
(31, 14, 'NOVA12-256-12', '256GB 12号色', '{"存储":"256GB","颜色":"12号色"}', 2999.00, 300, 'https://picsum.photos/seed/nova12/400/400', 1),
-- HUAWEI Watch GT4
(32, 15, 'GT4-46-BK', '46mm 曜石黑', '{"表盘":"46mm","颜色":"曜石黑"}', 1588.00, 250, 'https://picsum.photos/seed/watchgt4/400/400', 1),
(33, 15, 'GT4-41-WT', '41mm 凝霜白', '{"表盘":"41mm","颜色":"凝霜白"}', 1388.00, 200, 'https://picsum.photos/seed/watchgt4/400/400', 1),
-- HUAWEI Watch Fit 3
(34, 16, 'FIT3-STD', '标准版', '{"版本":"标准版"}', 999.00, 400, 'https://picsum.photos/seed/watchfit3/400/400', 1),
-- HUAWEI FreeBuds Pro 3
(35, 17, 'FBP3-GN', '雅川青', '{"颜色":"雅川青"}', 1499.00, 300, 'https://picsum.photos/seed/freebudspro3/400/400', 1),
-- HUAWEI FreeBuds 5
(36, 18, 'FB5-SL', '冰霜银', '{"颜色":"冰霜银"}', 699.00, 500, 'https://picsum.photos/seed/freebuds5/400/400', 1),

-- ========== 虚拟商品SKU (单SKU) ==========
(37, 19, 'RECH-50', '50元面值', '{"面值":"50元"}', 50.00, 99999, 'https://picsum.photos/seed/recharge50/400/400', 1),
(38, 20, 'RECH-100', '100元面值', '{"面值":"100元"}', 100.00, 99999, 'https://picsum.photos/seed/recharge100/400/400', 1),
(39, 21, 'RECH-200', '200元面值', '{"面值":"200元"}', 200.00, 99999, 'https://picsum.photos/seed/recharge200/400/400', 1),
(40, 22, 'RECH-500', '500元面值', '{"面值":"500元"}', 500.00, 99999, 'https://picsum.photos/seed/recharge500/400/400', 1),
(41, 23, 'IQY-M', '月卡', '{"时长":"月卡"}', 25.00, 99999, 'https://picsum.photos/seed/iqiyim/400/400', 1),
(42, 24, 'IQY-Y', '年卡', '{"时长":"年卡"}', 218.00, 99999, 'https://picsum.photos/seed/iqiyiy/400/400', 1),
(43, 25, 'TX-M', '月卡', '{"时长":"月卡"}', 25.00, 99999, 'https://picsum.photos/seed/tencentm/400/400', 1),
(44, 26, 'TX-Y', '年卡', '{"时长":"年卡"}', 233.00, 99999, 'https://picsum.photos/seed/tencenty/400/400', 1),
(45, 27, 'YK-M', '月卡', '{"时长":"月卡"}', 25.00, 99999, 'https://picsum.photos/seed/youkum/400/400', 1),
(46, 28, 'YK-Y', '年卡', '{"时长":"年卡"}', 218.00, 99999, 'https://picsum.photos/seed/youkuy/400/400', 1),
(47, 29, 'BILI-M', '月卡', '{"时长":"月卡"}', 25.00, 99999, 'https://picsum.photos/seed/bilibilim/400/400', 1),
(48, 30, 'BILI-Y', '年卡', '{"时长":"年卡"}', 148.00, 99999, 'https://picsum.photos/seed/bilibiliy/400/400', 1),
(49, 31, 'JD-PLUS-Y', '年卡', '{"时长":"年卡"}', 148.00, 99999, 'https://picsum.photos/seed/jdplus/400/400', 1),
(50, 32, 'DM-VIP-Y', '年卡', '{"时长":"年卡"}', 128.00, 99999, 'https://picsum.photos/seed/damai/400/400', 1);

-- 优惠券模板
INSERT INTO `coupon_template` (`id`, `name`, `type`, `face_value`, `discount`, `min_spend`, `category_limit`, `total_count`, `issued_count`, `per_limit`, `valid_type`, `valid_start`, `valid_end`, `valid_days`, `status`) VALUES
(1, '新人满100减20', 1, 20.00, NULL, 100.00, NULL, 1000, 0, 1, 1, '2024-01-01 00:00:00', '2027-12-31 23:59:59', NULL, 1),
(2, '全场9折券', 2, NULL, 0.90, 0.00, NULL, 500, 0, 2, 1, '2024-01-01 00:00:00', '2027-12-31 23:59:59', NULL, 1),
(3, '无门槛10元券', 3, 10.00, NULL, 0.00, NULL, 200, 0, 1, 2, NULL, NULL, 30, 1),
(4, '满500减50', 1, 50.00, NULL, 500.00, NULL, 300, 0, 2, 1, '2024-01-01 00:00:00', '2027-12-31 23:59:59', NULL, 1),
(5, 'Apple专场满5000减300', 1, 300.00, NULL, 5000.00, 1, 200, 0, 1, 1, '2024-01-01 00:00:00', '2027-12-31 23:59:59', NULL, 1),
(6, '华为专场满3000减200', 1, 200.00, NULL, 3000.00, 2, 200, 0, 1, 1, '2024-01-01 00:00:00', '2027-12-31 23:59:59', NULL, 1),
(7, '虚拟商品满100减10', 1, 10.00, NULL, 100.00, 3, 500, 0, 3, 1, '2024-01-01 00:00:00', '2027-12-31 23:59:59', NULL, 1);

-- 营销活动
INSERT INTO `marketing_activity` (`name`, `type`, `description`, `start_time`, `end_time`, `status`, `enabled`, `rules`) VALUES
('限时秒杀-Apple专场', 'FLASH_SALE', 'iPhone/Mac/iPad 限时特价', '2024-06-01 00:00:00', '2027-12-31 23:59:59', 1, 1, '{"limitPerUser":1}'),
('限时秒杀-华为专场', 'FLASH_SALE', 'Mate/P系列 限时特价', '2024-06-01 00:00:00', '2027-12-31 23:59:59', 1, 1, '{"limitPerUser":1}'),
('数码满3000减200', 'FULL_REDUCTION', '全场数码产品满3000减200', '2024-01-01 00:00:00', '2027-12-31 23:59:59', 1, 1, '{"threshold":3000,"reduction":200}'),
('视频会员特惠', 'DISCOUNT', '爱奇艺/腾讯/优酷/B站 会员8.5折', '2024-01-01 00:00:00', '2027-12-31 23:59:59', 1, 1, '{"discount":0.85}');

-- 活动商品关联 (秒杀商品)
INSERT INTO `activity_product` (`activity_id`, `product_id`, `sku_id`, `activity_price`, `activity_stock`, `limit_per_user`, `sort`) VALUES
(1, 1, 1, 9499.00, 50, 1, 100),
(1, 4, 11, 15499.00, 30, 1, 90),
(1, 7, 17, 8499.00, 40, 1, 80),
(2, 12, 27, 6499.00, 50, 1, 100),
(2, 13, 29, 4988.00, 50, 1, 90),
(2, 17, 35, 1299.00, 80, 1, 80);

-- ============================================================
-- 10. 奖池模块
-- ============================================================

-- 奖池表 (通过奖池发放奖品, 支持优惠券/积分等多种类型, SPI扩展)
DROP TABLE IF EXISTS `marketing_prize_pool`;
CREATE TABLE `marketing_prize_pool` (
    `id`                  BIGINT        NOT NULL AUTO_INCREMENT,
    `name`                VARCHAR(100)  NOT NULL COMMENT '奖池名称',
    `description`         VARCHAR(500)           DEFAULT NULL COMMENT '奖池描述',
    `prize_type`          VARCHAR(20)   NOT NULL DEFAULT 'COUPON' COMMENT '奖品类型: COUPON-优惠券, POINTS-积分',
    `prize_ref_id`        BIGINT                 DEFAULT NULL COMMENT '奖品关联ID(优惠券→coupon_template.id, 积分→NULL)',
    `prize_value`         INT                    DEFAULT NULL COMMENT '奖品面值(积分→数量, 优惠券→NULL)',
    `prize_name`          VARCHAR(100)           DEFAULT NULL COMMENT '奖品展示名称',
    `prize_desc`          VARCHAR(200)           DEFAULT NULL COMMENT '奖品展示描述',
    `total_stock`         INT           NOT NULL DEFAULT -1 COMMENT '总库存(-1不限)',
    `claimed_count`       INT           NOT NULL DEFAULT 0 COMMENT '已领取数量',
    `per_user_limit`      INT           NOT NULL DEFAULT 1 COMMENT '每人限领总数(0不限)',
    `per_user_daily_limit` INT          NOT NULL DEFAULT 0 COMMENT '每人每日限领(0不限)',
    `daily_limit`         INT           NOT NULL DEFAULT 0 COMMENT '每日总限领(0不限)',
    `start_time`          DATETIME      NOT NULL COMMENT '开始时间',
    `end_time`            DATETIME      NOT NULL COMMENT '结束时间',
    `status`              TINYINT       NOT NULL DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    `banner_text`         VARCHAR(100)           DEFAULT NULL COMMENT 'Banner文案',
    `banner_color`        VARCHAR(20)            DEFAULT '#ff4d4f' COMMENT 'Banner渐变起始色',
    `banner_color_end`    VARCHAR(20)            DEFAULT '#ff7a45' COMMENT 'Banner渐变结束色',
    `sort`                INT                    DEFAULT 0 COMMENT '排序(越大越靠前)',
    `create_time`         DATETIME               DEFAULT CURRENT_TIMESTAMP,
    `update_time`         DATETIME               DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted`             TINYINT                DEFAULT 0,
    PRIMARY KEY (`id`),
    KEY `idx_status` (`status`),
    KEY `idx_prize_type` (`prize_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='营销奖池';

-- 奖池领取记录表
DROP TABLE IF EXISTS `marketing_prize_claim_log`;
CREATE TABLE `marketing_prize_claim_log` (
    `id`          BIGINT    NOT NULL AUTO_INCREMENT,
    `pool_id`     BIGINT    NOT NULL COMMENT '奖池ID',
    `user_id`     BIGINT    NOT NULL COMMENT '用户ID',
    `prize_type`  VARCHAR(20) NOT NULL DEFAULT 'COUPON' COMMENT '奖品类型',
    `prize_ref_id` BIGINT   DEFAULT NULL COMMENT '奖品关联ID',
    `claim_time`  DATETIME  NOT NULL COMMENT '领取时间',
    `create_time` DATETIME           DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_pool` (`pool_id`),
    KEY `idx_user` (`user_id`),
    KEY `idx_pool_user` (`pool_id`, `user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='奖池领取记录';

-- 奖池数据 (5个优惠券奖池 + 2个积分奖池)
INSERT INTO `marketing_prize_pool` (`name`, `description`, `prize_type`, `prize_ref_id`, `prize_value`, `prize_name`, `prize_desc`, `total_stock`, `claimed_count`, `per_user_limit`, `per_user_daily_limit`, `daily_limit`, `start_time`, `end_time`, `status`, `banner_text`, `banner_color`, `banner_color_end`, `sort`) VALUES
('新人专享礼', '新用户专享无门槛10元券', 'COUPON', 3, NULL, '无门槛10元券', '无门槛抵扣10元', 100, 0, 1, 0, 0, '2024-01-01 00:00:00', '2027-12-31 23:59:59', 1, '新人专享 · 无门槛10元券', '#ff4d4f', '#ff7a45', 100),
('限时抢券', '满500减50大额券限时抢', 'COUPON', 4, NULL, '满500减50', '满500元可用', 50, 0, 1, 1, 20, '2024-01-01 00:00:00', '2027-12-31 23:59:59', 1, '限时抢券 · 满500减50', '#722ed1', '#1890ff', 90),
('会员福利日', '全场9折券会员专享', 'COUPON', 2, NULL, '全场9折券', '全场商品9折', 200, 0, 2, 1, 0, '2024-01-01 00:00:00', '2027-12-31 23:59:59', 1, '会员福利 · 全场9折', '#13c2c2', '#52c41a', 80),
('Apple大额券', '满5000减300 Apple专场', 'COUPON', 5, NULL, 'Apple满5000减300', 'Apple专场满5000元可用', 80, 0, 1, 1, 10, '2024-01-01 00:00:00', '2027-12-31 23:59:59', 1, 'Apple专场 · 满5000减300', '#1890ff', '#722ed1', 75),
('华为大额券', '满3000减200 华为专场', 'COUPON', 6, NULL, '华为满3000减200', '华为专场满3000元可用', 80, 0, 1, 1, 10, '2024-01-01 00:00:00', '2027-12-31 23:59:59', 1, '华为专场 · 满3000减200', '#52c41a', '#13c2c2', 72),
('签到积分雨', '每日领取50积分', 'POINTS', NULL, 50, '50积分', '可用于积分兑换', -1, 0, 1, 1, 0, '2024-01-01 00:00:00', '2027-12-31 23:59:59', 1, '每日福利 · 50积分', '#fa8c16', '#faad14', 70),
('新人积分包', '新用户专享100积分', 'POINTS', NULL, 100, '100积分', '新人专享积分礼包', 50, 0, 1, 0, 0, '2024-01-01 00:00:00', '2027-12-31 23:59:59', 1, '新人专享 · 100积分', '#eb2f96', '#f759ab', 60);

SET FOREIGN_KEY_CHECKS = 1;
