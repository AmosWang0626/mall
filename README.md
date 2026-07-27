# Mall 简易Java商城

> 蚂蚁虽小，五脏俱全。一个适合快速二次开发的Java商城系统。

## 技术栈

### 后端
| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 3.3.2 | 核心框架 |
| MyBatis | 3.0.4 | ORM框架 |
| MySQL | 8.0+ | 数据库 |
| Redis | 6.0+ | 缓存 |
| PageHelper | 2.1.0 | 分页插件 |
| JWT (jjwt) | 0.11.5 | 认证授权 |
| Hutool | 5.8.25 | 工具库 |
| Knife4j | 4.5.0 | API文档 |
| Lombok | - | 简化代码 |
| Maven | - | 构建工具 |

### 前端
| 技术 | 版本 | 说明 |
|------|------|------|
| React | 18.2 | UI框架 |
| Ant Design | 5.12 | UI组件库 |
| React Router | 6.20 | 路由 |
| Axios | 1.6 | HTTP请求 |
| Vite | 5.0 | 构建工具 |
| Zustand | 4.4 | 状态管理 |

> **注意**: 用户要求的是 Element UI，但 Element UI 是 Vue 专用组件库，React 生态中对应的是 Ant Design，功能和使用方式非常接近。

## 功能模块

### 商城核心功能
- **商品管理**: 商品SPU/SKU管理、分类管理(树形)、上下架、库存管理
- **用户系统**: 注册登录、个人资料、收货地址管理
- **购物车**: 加购、修改数量、选中/取消、清空
- **订单系统**: 创建订单(库存扣减+积分+优惠券)、支付、取消、发货、收货、退款
- **积分系统**: 积分账户、签到获得、下单获得/使用、退款退回、流水记录
- **优惠券系统**: 满减券/折扣券/无门槛券、领取、使用、有效期管理
- **营销活动**: 限时秒杀、满减活动、折扣活动、活动商品关联

### 后台管理功能
- **仪表盘**: 关键数据概览
- **RBAC权限**: 管理员-角色-权限三级体系、菜单/按钮/接口权限、数据权限
- **系统配置**: 键值对配置、Redis缓存、系统内置配置保护
- **操作日志**: 后台操作记录

## 项目结构

```
mall-backend/                          # 后端项目
├── pom.xml                            # Maven配置
└── src/main/
    ├── java/com/mall/
    │   ├── MallApplication.java       # 启动类
    │   ├── common/                    # 通用模块
    │   │   ├── Result.java            # 统一响应
    │   │   ├── PageResult.java        # 分页结果
    │   │   ├── PageRequest.java       # 分页请求
    │   │   ├── BaseEntity.java        # 基础实体
    │   │   ├── RedisService.java      # Redis工具
    │   │   └── exception/             # 异常处理
    │   ├── config/                    # 配置类
    │   │   ├── RedisConfig.java
    │   │   └── WebMvcConfig.java      # CORS + 拦截器
    │   ├── security/                  # 安全模块
    │   │   ├── JwtUtil.java           # JWT工具
    │   │   ├── JwtFilter.java         # 认证拦截器
    │   │   └── UserContext.java       # 用户上下文
    │   └── module/                    # 业务模块
    │       ├── auth/                  # 认证模块
    │       ├── user/                  # 用户模块
    │       ├── product/               # 商品模块
    │       ├── cart/                  # 购物车模块
    │       ├── order/                 # 订单模块
    │       ├── points/                # 积分模块
    │       ├── coupon/                # 优惠券模块
    │       ├── marketing/             # 营销模块
    │       └── system/                # 系统管理模块
    └── resources/
        ├── application.yml            # 应用配置
        ├── mapper/                    # MyBatis XML
        └── sql/init.sql               # 数据库初始化脚本

mall-admin/                            # 前端项目
├── package.json
├── vite.config.js
├── index.html
└── src/
    ├── main.jsx                       # 入口
    ├── App.jsx                        # 路由配置
    ├── index.css                      # 全局样式
    ├── api/                           # API请求
    │   ├── request.js                 # Axios封装
    │   └── index.js                   # API定义
    ├── store/                         # 状态管理
    │   └── index.js
    ├── layout/                        # 布局组件
    │   └── index.jsx
    └── views/                         # 页面
        ├── login/                     # 登录
        ├── dashboard/                 # 仪表盘
        ├── product/                   # 商品管理
        ├── order/                     # 订单管理
        ├── user/                      # 用户管理
        ├── points/                    # 积分管理
        ├── coupon/                    # 优惠券管理
        ├── marketing/                 # 营销活动
        └── system/                    # 系统管理
```

## 快速开始

### 环境要求
- JDK 21+
- MySQL 8.0+
- Redis 6.0+
- Node.js 18+
- Maven 3.6+

### 1. 初始化数据库

```bash
# 登录MySQL，执行初始化脚本
mysql -u root -p < mall-backend/src/main/resources/sql/init.sql
```

### 2. 启动Redis

```bash
redis-server
```

### 3. 启动后端

```bash
cd mall-backend

# 修改数据库配置 (如有需要)
# vim src/main/resources/application.yml

# 编译启动
mvn spring-boot:run

# 后端运行在 http://localhost:8080
```

### 4. 启动前端

```bash
cd mall-admin

# 安装依赖
npm install

# 启动开发服务器
npm run dev

# 前端运行在 http://localhost:3000
```

### 5. 默认管理员账号

```
用户名: admin
密码: admin123
```

## 架构设计

### 后端分层架构

每个业务模块遵循统一的分层结构：

```
module/xxx/
├── controller/    # 控制层 - 接收请求、参数校验、返回响应
├── service/       # 业务层 - 核心业务逻辑
├── mapper/        # 数据层 - MyBatis Mapper接口
├── entity/        # 实体类 - 数据库映射
├── dto/           # 数据传输对象 - 请求参数
└── vo/            # 视图对象 - 响应数据
```

### 统一响应格式

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

### 认证流程

```
1. 客户端 POST /auth/login → 服务端验证账号密码
2. 服务端生成 JWT Token 返回
3. 客户端存储 Token，后续请求 Header 携带 Authorization: Bearer <token>
4. JwtFilter 拦截器解析 Token → UserContext 设置当前用户
5. Controller/Service 通过 UserContext.require() 获取当前登录用户
```

### 订单创建流程

```
1. 校验购物车选中商品
2. 校验商品库存 → 扣减库存 → 增加销量
3. 获取收货地址
4. 计算优惠券优惠金额
5. 计算积分抵扣金额
6. 计算实付金额
7. 创建订单 + 订单明细
8. 清空购物车已购项
9. 返回订单信息(含支付倒计时)
```

## API 接口概览

| 模块 | 前缀 | 主要接口 |
|------|------|---------|
| 认证 | /auth | login, register, info, logout |
| 商品 | /product | list, detail, save, update, delete, status |
| 分类 | /product/category | tree, save, update, delete |
| SKU | /product/sku | list/{productId}, save, delete |
| 购物车 | /cart | list, add, updateQuantity, remove, clear, count |
| 订单 | /order | create, detail, my, pay, cancel, ship, receive, refund |
| 用户 | /user | info, profile, password, list, update, status, delete |
| 地址 | /user/address | list, save, delete, default |
| 积分 | /points | account, sign, logs |
| 优惠券 | /coupon | available, mine, receive, list, save, delete |
| 营销 | /marketing | active, list, detail, save, delete |
| 管理员 | /system/admin | list, save, update, delete, roles, password |
| 角色 | /system/role | list, all, save, delete, permissions |
| 权限 | /system/permission | tree, list, save, delete |
| 配置 | /system/config | list, all, key/{key}, save, delete |
| 日志 | /system/log | list, delete |

## 二次开发指南

### 新增一个业务模块

以"商品评论"模块为例：

**1. 创建数据库表**

```sql
CREATE TABLE product_comment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    rating INT DEFAULT 5,
    status TINYINT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
);
```

**2. 创建Entity** (`module/comment/entity/ProductComment.java`)

```java
@Data
@EqualsAndHashCode(callSuper = true)
public class ProductComment extends BaseEntity {
    private Long id;
    private Long productId;
    private Long userId;
    private String content;
    private Integer rating;
    private Integer status;
}
```

**3. 创建Mapper接口** (`module/comment/mapper/ProductCommentMapper.java`)

```java
@Mapper
public interface ProductCommentMapper {
    int insert(ProductComment comment);
    List<ProductComment> selectByProductId(@Param("productId") Long productId);
    // ...
}
```

**4. 创建Mapper XML** (`resources/mapper/ProductCommentMapper.xml`)

**5. 创建Service** (`module/comment/service/ProductCommentService.java`)

```java
@Service
public class ProductCommentService {
    @Autowired private ProductCommentMapper commentMapper;
    // 业务逻辑
}
```

**6. 创建Controller** (`module/comment/controller/ProductCommentController.java`)

```java
@RestController
@RequestMapping("/comment")
public class ProductCommentController {
    @Autowired private ProductCommentService commentService;
    // 接口方法
}
```

**7. 前端添加页面** (`views/comment/list.jsx`)

**8. 配置路由** (在 `App.jsx` 中添加)

### 开发约定

1. **命名规范**: 数据库表名小写下划线，Java类名大驼峰，字段名小驼峰
2. **统一响应**: 所有接口返回 `Result<T>`
3. **分页**: 使用 PageHelper，返回 `PageResult<T>`
4. **异常处理**: 业务异常抛 `BusinessException`，全局异常处理器统一捕获
5. **逻辑删除**: 所有表包含 `deleted` 字段，使用 `UPDATE SET deleted=1`
6. **时间字段**: 所有表包含 `create_time` 和 `update_time`
7. **缓存**: 使用 `RedisService`，系统配置类使用 `sys:config:` 前缀
8. **认证**: 通过 `UserContext.require()` 获取当前用户

### 配置修改

- 数据库连接: `application.yml` → `spring.datasource`
- Redis连接: `application.yml` → `spring.redis`
- JWT密钥: `application.yml` → `jwt.secret`
- 端口: `application.yml` → `server.port`
- 跨域: `WebMvcConfig.java` → `corsFilter()`

## License

MIT
