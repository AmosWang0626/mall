-- ============================================================
-- Mini-Mall 数据库初始化脚本
-- 数据库: MySQL 8.0+
-- 字符集: utf8mb4
-- ============================================================

CREATE DATABASE IF NOT EXISTS `mini_mall` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `mini_mall`;

SET NAMES utf8mb4;
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
('site_name', 'Mini-Mall 商城', 'string', '站点名称', '商城站点名称', 1),
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
(1, 0, '手机数码', 10, 1, 1),
(2, 0, '电脑办公', 9, 1, 1),
(3, 0, '家用电器', 8, 1, 1),
(4, 0, '服饰鞋包', 7, 1, 1),
(5, 0, '食品生鲜', 6, 1, 1),
(6, 1, '手机', 10, 1, 2),
(7, 1, '平板', 9, 1, 2),
(8, 1, '配件', 8, 1, 2),
(9, 2, '笔记本', 10, 1, 2),
(10, 2, '台式机', 9, 1, 2),
(11, 3, '大家电', 10, 1, 2),
(12, 3, '小家电', 9, 1, 2);

-- 示例商品
INSERT INTO `product` (`category_id`, `name`, `subtitle`, `main_image`, `price`, `original_price`, `cost`, `stock`, `sales`, `status`, `sort`, `tags`) VALUES
(6, '智能手机 Pro Max', '6.7英寸旗舰屏 | 1亿像素', 'https://placeholder.com/phone1.jpg', 4999.00, 5499.00, 3500.00, 500, 128, 1, 100, '热销,新品'),
(6, '智能手表 Series 9', '健康监测 | 超长续航', 'https://placeholder.com/watch1.jpg', 1999.00, 2299.00, 1200.00, 300, 89, 1, 90, '热销'),
(9, '超薄笔记本 Air', '16GB | 512GB SSD', 'https://placeholder.com/laptop1.jpg', 6999.00, 7999.00, 5000.00, 200, 56, 1, 80, '新品'),
(11, '4K智能电视 65寸', 'HDR10+ | 杜比音效', 'https://placeholder.com/tv1.jpg', 3299.00, 3999.00, 2200.00, 150, 34, 1, 70, ''),
(8, '无线蓝牙耳机', '主动降噪 | 30小时续航', 'https://placeholder.com/earphone1.jpg', 599.00, 799.00, 300.00, 1000, 256, 1, 60, '热销');

-- 示例优惠券
INSERT INTO `coupon_template` (`name`, `type`, `face_value`, `discount`, `min_spend`, `total_count`, `issued_count`, `per_limit`, `valid_type`, `valid_start`, `valid_end`, `valid_days`, `status`) VALUES
('新人满100减20', 1, 20.00, NULL, 100.00, 1000, 0, 1, 1, '2024-01-01 00:00:00', '2025-12-31 23:59:59', NULL, 1),
('全场9折券', 2, NULL, 0.90, 0.00, 500, 0, 1, 1, '2024-01-01 00:00:00', '2025-12-31 23:59:59', NULL, 1),
('无门槛10元券', 3, 10.00, NULL, 0.00, 200, 0, 1, 2, NULL, NULL, 30, 1),
('满500减50', 1, 50.00, NULL, 500.00, 300, 0, 2, 1, '2024-01-01 00:00:00', '2025-12-31 23:59:59', NULL, 1);

-- 示例营销活动
INSERT INTO `marketing_activity` (`name`, `type`, `description`, `start_time`, `end_time`, `status`, `enabled`, `rules`) VALUES
('限时秒杀-数码专场', 'FLASH_SALE', '精选数码产品限时秒杀', '2024-06-01 00:00:00', '2025-12-31 23:59:59', 1, 1, '{"limitPerUser":1}'),
('满300减30', 'FULL_REDUCTION', '全场满300减30元', '2024-01-01 00:00:00', '2025-12-31 23:59:59', 1, 1, '{"threshold":300,"reduction":30}');

SET FOREIGN_KEY_CHECKS = 1;
