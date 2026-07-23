# Mini-Mall 性能评估与高并发优化方案

## 一、现状诊断

对当前系统进行了全维度审查（88 个 Java 文件、19 个 Mapper XML、1 个 SQL Schema），按严重程度归类如下：

| 严重度 | 问题 | 影响范围 |
|--------|------|---------|
| **严重** | HikariCP 连接池未配置（默认仅 10 连接） | 全局 — 高并发下连接瞬间耗尽 |
| **严重** | Redis 仅用于系统配置缓存，商品/分类/活动全走 DB | 全局 — 所有高频读直击 MySQL |
| **严重** | 订单列表 N+1 查询（每页 N 条订单执行 N 次 SELECT） | 订单列表接口 |
| **严重** | 订单创建事务过大 + 冗余 SQL（SKU 查两次） | 下单接口 |
| **严重** | 无异步线程池，操作日志同步写入、订单超时取消未实现 | 全局 |
| **中等** | 优惠券领取存在竞态条件（无分布式锁/唯一约束） | 领券接口 |
| **中等** | 乐观锁 `version` 字段已定义但从未使用 | 积分操作 |
| **中等** | LIKE '%keyword%' 前导通配符导致全表扫描 | 全部搜索接口 |
| **中等** | 缺少 `create_time` 排序字段索引，需 filesort | 订单/积分日志列表 |
| **中等** | 营销活动列表 N+1、角色权限逐条插入 | 活动/权限管理 |
| **轻微** | 登录事务范围过大、未使用只读事务 | 部分接口 |

---

## 二、数据库连接池优化

### 问题

`application.yml` 和 `application-docker.yml` 中 `spring.datasource` 仅配置了 url/username/password，HikariCP 参数完全缺失。Spring Boot 2.7 默认最大连接数仅 10。

### 方案

同时修改两个配置文件：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/mini_mall?...  # 已有
    username: root                                   # 已有
    password: root                                   # 已有
    hikari:
      # 最大连接数：按 2 * CPU 核数 + 磁盘数 计算，4核约 20-30
      maximum-pool-size: 30
      # 最小空闲连接：预热连接，避免冷启动延迟
      minimum-idle: 10
      # 连接超时：获取连接最长等待时间
      connection-timeout: 5000
      # 空闲超时：空闲连接最大存活时间
      idle-timeout: 300000
      # 连接最大生命周期：略小于 MySQL wait_timeout
      max-lifetime: 1800000
      # 连接测试查询
      connection-test-query: SELECT 1
      # 连接池名称（便于监控识别）
      pool-name: MallHikariCP
```

关键指标计算：

| 场景 | 并发数 | 所需连接数 | 默认值 | 调整后 |
|------|--------|-----------|--------|--------|
| 平稳期 | 50 QPS | ~15 | 已满 | 充足 |
| 高峰期 | 200 QPS | ~25 | 耗尽 | 充足 |
| 秒杀场景 | 500 QPS | ~50 | 耗尽 | 辅助限流 |

---

## 三、多级缓存体系

### 3.1 本地缓存（Caffeine）

适用场景：数据量小、几乎不变、读取频率极高。

```java
// 添加到 pom.xml
<dependency>
    <groupId>com.github.ben-manes.caffeine</groupId>
    <artifactId>caffeine</artifactId>
</dependency>
```

**缓存规划：**

| 缓存对象 | 策略 | TTL | 最大条目 | 原因 |
|---------|------|-----|---------|------|
| 商品分类树 | `expireAfterWrite` | 30 min | 500 | 几乎不变，树构建成本高 |
| 权限列表 | `expireAfterWrite` | 30 min | 1000 | 几乎不变 |
| 角色列表 | `expireAfterWrite` | 10 min | 200 | 偶尔变更 |
| 系统配置 | `expireAfterWrite` | 10 min | 200 | 已有 Redis，增加本地缓存前置 |

实现示例 — 分类树缓存：

```java
// ProductCategoryService 改造
private final LoadingCache<String, List<ProductCategory>> categoryCache = Caffeine.newBuilder()
    .expireAfterWrite(30, TimeUnit.MINUTES)
    .maximumSize(1)
    .build(key -> buildTreeFromDB());

public List<ProductCategory> getTree() {
    return categoryCache.get("ALL");
}

public void add(CategoryForm form) {
    // 写操作后失效缓存
    categoryCache.invalidateAll();
    // ... 原有逻辑
}
```

### 3.2 Redis 分布式缓存

当前仅 `SysConfigService` 使用了 Redis。需建立分层缓存策略：

**优先接入缓存的接口：**

| 接口 | 当前 QPS 估算 | 缓存 Key | TTL | 更新策略 |
|------|-------------|----------|-----|---------|
| `GET /public/product/{id}` | 高 | `product:detail:{id}` | 10 min | 写时删除 |
| `GET /public/product/list` | 高 | `product:list:{hash(category,name,tags,page)}` | 5 min | 写时删除 |
| `GET /public/product/category/tree` | 极高 | `product:category:tree` | 30 min | 增删改时删除 |
| `GET /coupon/available` | 中 | `coupon:available` | 5 min | 发券/更新时删除 |
| `GET /marketing/active` | 中 | `marketing:active:{type}` | 5 min | 活动状态变更时删除 |

实现示例 — 商品详情缓存：

```java
// ProductService.getDetail()
public Product getDetail(Long id) {
    String key = CACHE_PREFIX + id;
    Product cached = (Product) redisService.get(key);
    if (cached != null) return cached;

    Product product = productMapper.selectById(id);
    if (product == null) throw new BusinessException("商品不存在");

    List<ProductSku> skuList = skuMapper.selectByProductId(id);
    product.setSkuList(skuList);
    redisService.set(key, product, 10, TimeUnit.MINUTES);
    return product;
}

// ProductService.create/update() 中增加缓存失效
public void update(ProductForm form) {
    // ... 原有逻辑
    redisService.delete(CACHE_PREFIX + form.getId());
}
```

### 3.3 缓存穿透 / 击穿 / 雪崩防护

| 问题 | 场景 | 方案 |
|------|------|------|
| **缓存穿透** | 查询不存在的商品 ID | 缓存空对象（TTL 1 分钟），或布隆过滤器预判 |
| **缓存击穿** | 热门商品过期瞬间大量请求 | Redis `SETNX` 互斥锁，仅允许一个线程回源 DB |
| **缓存雪崩** | 大量 key 同时过期 | TTL 加随机偏移 `+ random(0, 60s)` |

```java
// 防击穿 — 互斥锁回源
public Product getDetail(Long id) {
    String key = CACHE_PREFIX + id;
    Product cached = (Product) redisService.get(key);
    if (cached != null) return cached;

    // 空对象防穿透
    if (redisService.get(key + ":null") != null) return null;

    String lockKey = key + ":lock";
    boolean locked = redisService.setIfAbsent(lockKey, "1", 3, TimeUnit.SECONDS);
    if (!locked) {
        Thread.sleep(50);  // 自旋等待
        return getDetail(id);  // 重试
    }
    try {
        // 双重检查
        cached = (Product) redisService.get(key);
        if (cached != null) return cached;

        Product product = productMapper.selectById(id);
        if (product == null) {
            redisService.set(key + ":null", "1", 1, TimeUnit.MINUTES);
            return null;
        }
        redisService.set(key, product, 10, TimeUnit.MINUTES);
        return product;
    } finally {
        redisService.delete(lockKey);
    }
}
```

---

## 四、SQL 查询优化

### 4.1 消除 N+1 查询

**订单列表**（当前最严重的问题）：

```java
// 当前代码（OrderService.java 第 173 行）— 每次查 N 条订单额外执行 N 次查询
for (MallOrder o : orders) o.setItems(itemMapper.selectByOrderId(o.getId()));

// 优化后 — 一次批量查询
List<Long> orderIds = orders.stream().map(MallOrder::getId).collect(Collectors.toList());
List<OrderItem> allItems = itemMapper.selectByOrderIds(orderIds);  // 方法已存在，从未被调用

// 按 order_id 分组
Map<Long, List<OrderItem>> itemMap = allItems.stream()
    .collect(Collectors.groupingBy(OrderItem::getOrderId));
for (MallOrder o : orders) o.setItems(itemMap.getOrDefault(o.getId(), Collections.emptyList()));
```

**营销活动列表**：

```java
// 当前代码（MarketingService.java 第 64 行）
for (MarketingActivity a : list) a.setProducts(apMapper.selectByActivityId(a.getId()));

// 优化后 — ActivityProductMapper 增加批量查询方法
List<Long> activityIds = list.stream().map(MarketingActivity::getId).collect(Collectors.toList());
List<ActivityProduct> all = apMapper.selectByActivityIds(activityIds);
Map<Long, List<ActivityProduct>> grouped = all.stream()
    .collect(Collectors.groupingBy(ActivityProduct::getActivityId));
for (MarketingActivity a : list) a.setProducts(grouped.getOrDefault(a.getId(), Collections.emptyList()));
```

**角色权限逐条插入**：

```java
// 当前代码（SysAdminService 第 68 行）— N 次 INSERT
for (Long roleId : roleIds) adminMapper.insertAdminRole(adminId, roleId);

// 优化后 — 单次批量 INSERT
adminMapper.insertAdminRoles(adminId, roleIds);  // 需新增 Mapper 方法
```

```xml
<!-- SysAdminMapper.xml 新增 -->
<insert id="insertAdminRoles">
    INSERT INTO sys_admin_role (admin_id, role_id) VALUES
    <foreach collection="roleIds" item="rid" separator=",">
        (#{adminId}, #{rid})
    </foreach>
</insert>
```

### 4.2 订单创建事务精简

当前 `OrderService.create()` 事务体包含近 15 次 DB 操作，其中包含 N+1 和冗余查询。关键优化：

```java
@Transactional
public CreateOrderDTO create(Long userId) {
    // 1. 批量查询 — 一次 SQL 替代 N 次
    List<CartItem> selected = cartService.listSelected(userId);
    List<Long> productIds = selected.stream().map(CartItem::getProductId).distinct().collect(Collectors.toList());
    List<Long> skuIds = selected.stream().map(CartItem::getSkuId).filter(Objects::nonNull).collect(Collectors.toList());

    Map<Long, Product> productMap = productMapper.selectByIds(productIds).stream()
        .collect(Collectors.toMap(Product::getId, Function.identity()));
    Map<Long, ProductSku> skuMap = skuIds.isEmpty() ? Map.of() : skuMapper.selectByIds(skuIds).stream()
        .collect(Collectors.toMap(ProductSku::getId, Function.identity()));

    // 2. 消除冗余的 selectById 二次调用
    for (CartItem cart : selected) {
        ProductSku sku = cart.getSkuId() != null ? skuMap.get(cart.getSkuId()) : null;
        int stock = sku != null ? sku.getStock() : productMap.get(cart.getProductId()).getStock();
        if (stock < cart.getQuantity()) throw new BusinessException("库存不足");

        // 原子扣减库存（WHERE stock >= quantity 防超卖）
        int rows = cart.getSkuId() != null
            ? skuMapper.reduceStock(cart.getSkuId(), cart.getQuantity())
            : productMapper.reduceStock(cart.getProductId(), cart.getQuantity());
        if (rows == 0) throw new BusinessException("库存不足");
    }

    // 3. 优惠券和积分计算 — 可考虑降级为异步
    // ... (保持不变，但移除了冗余的 selectById)
}
```

### 4.3 LIKE 搜索优化

`LIKE '%keyword%'` 导致索引失效，以下方案按复杂度递进：

| 方案 | 适用阶段 | 改动量 | 效果 |
|------|---------|--------|------|
| 增加简单 `KEY(name)` 索引 | 立即 | 无代码改动 | 有限改善（前缀匹配时能用上） |
| MySQL FULLTEXT 全文索引 | 数据量 < 10 万 | SQL 改动 | 显著提升 |
| Elasticsearch 搜索引擎 | 数据量 > 10 万 | 新增服务 | 最佳方案 |

当前推荐方案 — MySQL FULLTEXT：

```sql
-- init.sql 中 product 表增加全文索引
ALTER TABLE product ADD FULLTEXT INDEX ft_name (name);
ALTER TABLE product ADD FULLTEXT INDEX ft_tags (tags);
```

```xml
<!-- ProductMapper.xml 搜索查询改为全文检索 -->
<select id="selectList" resultMap="ListResultMap">
    SELECT p.*, c.name as category_name
    FROM product p LEFT JOIN product_category c ON p.category_id = c.id
    WHERE p.deleted = 0
    <if test="name != null and name != ''">
        AND MATCH(p.name) AGAINST(#{name} IN BOOLEAN MODE)
    </if>
    ORDER BY p.sort_order ASC, p.create_time DESC
</select>
```

### 4.4 缺失索引补充

| 表 | DDL |
|-----|-----|
| `mall_order` | `ADD INDEX idx_user_time (user_id, create_time DESC)` |
| `mall_order` | `ADD INDEX idx_create_time (create_time DESC)` |
| `points_log` | `ADD INDEX idx_user_time (user_id, create_time DESC)` |
| `user_coupon` | `ADD INDEX idx_user_coupon (user_id, coupon_id)` |
| `user_coupon` | `ADD INDEX idx_user_status (user_id, status)` |
| `cart_item` | `ADD INDEX idx_user_time (user_id, update_time)` |
| `sys_admin_role` | `ADD PRIMARY KEY (admin_id, role_id), ADD INDEX idx_role (role_id)` |
| `sys_role_permission` | `ADD PRIMARY KEY (role_id, permission_id), ADD INDEX idx_perm (permission_id)` |

---

## 五、线程池与异步化

### 5.1 线程池配置

```java
@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);               // 核心线程数
        executor.setMaxPoolSize(20);               // 最大线程数
        executor.setQueueCapacity(200);            // 缓冲队列
        executor.setKeepAliveSeconds(60);          // 空闲线程存活时间
        executor.setThreadNamePrefix("mall-async-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
}
```

### 5.2 异步化改造清单

| 方法 | 改造方式 | 理由 |
|------|---------|------|
| `SysOperationLogService.save()` | `@Async` | 操作日志不应阻塞主流程 |
| `PointsService.addPoints()` | `@Async` + 补偿 | 积分发放可异步，失败用定时任务补偿 |
| `OrderService.cancelTimeoutOrders()` | `@Scheduled` | 每 5 分钟扫描超时未支付订单 |
| 库存回补 | `@Async` | 取消订单时异步回补库存 |

```java
// 操作日志异步化
@Service
public class SysOperationLogService {
    @Async
    public void save(String module, String action, String request, String response, Integer status) {
        // ... 原有逻辑
    }
}

// 订单超时自动取消
@Component
public class OrderTimeoutTask {
    @Scheduled(cron = "0 */5 * * * ?")  // 每 5 分钟
    public void cancelTimeoutOrders() {
        List<MallOrder> timeout = orderMapper.selectPendingAndExpired();
        for (MallOrder order : timeout) {
            orderService.cancel(order.getId());
        }
    }
}
```

---

## 六、Sentinel 限流与熔断

### 6.1 依赖引入

```xml
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-sentinel</artifactId>
    <version>2021.0.5.0</version>
</dependency>
```

> 注意：本项目基于 SpringBoot 2.7.18，非 Spring Cloud 环境。推荐直接使用 Sentinel Core，避免引入整个 Spring Cloud 体系。

```xml
<!-- 纯 Sentinel 方案，无需 Spring Cloud -->
<dependency>
    <groupId>com.alibaba.csp</groupId>
    <artifactId>sentinel-core</artifactId>
    <version>1.8.6</version>
</dependency>
<dependency>
    <groupId>com.alibaba.csp</groupId>
    <artifactId>sentinel-transport-simple-http</artifactId>
    <version>1.8.6</version>
</dependency>
<dependency>
    <groupId>com.alibaba.csp</groupId>
    <artifactId>sentinel-annotation-aspectj</artifactId>
    <version>1.8.6</version>
</dependency>
```

### 6.2 Sentinel 配置

```java
@Configuration
public class SentinelConfig {

    @PostConstruct
    public void init() {
        // 1. 初始化控制台（Dashboard）通信
        System.setProperty("csp.sentinel.dashboard.server", "localhost:8080");
        System.setProperty("project.name", "mini-mall");
        InitExecutor.doInit();
    }
}
```

### 6.3 限流规则

```java
// 在业务启动完成后加载规则
private static void initFlowRules() {
    List<FlowRule> rules = new ArrayList<>();

    // 下单接口 — 每秒最多 50 个请求
    rules.add(new FlowRule("createOrder")
        .setGrade(RuleConstant.FLOW_GRADE_QPS)
        .setCount(50));

    // 普通商品列表 — 每秒最多 200 个请求
    rules.add(new FlowRule("productList")
        .setGrade(RuleConstant.FLOW_GRADE_QPS)
        .setCount(200));

    // 秒杀商品 — 热点参数限流
    ParamFlowRule paramRule = new ParamFlowRule("productDetail")
        .setParamIdx(0)            // 第一个参数（商品ID）
        .setCount(100)             // 单个商品每秒 100 次
        .setDurationInSec(1);
    ParamFlowRuleManager.loadRules(Collections.singletonList(paramRule));

    FlowRuleManager.loadRules(rules);
}
```

### 6.4 接口接入

```java
// 方案一：@SentinelResource 注解
@GetMapping("/{id}")
@SentinelResource(
    value = "productDetail",
    blockHandler = "getDetailBlockHandler",
    fallback = "getDetailFallback"
)
public Result<Product> getDetail(@PathVariable Long id) {
    return Result.success(productService.getDetail(id));
}

// 限流 / 降级 处理方法
public Result<Product> getDetailBlockHandler(Long id, BlockException e) {
    return Result.error(429, "当前访问人数较多，请稍后再试");
}

public Result<Product> getDetailFallback(Long id, Throwable e) {
    log.error("商品详情查询异常", e);
    return Result.error(500, "服务繁忙，请稍后");
}
```

```java
// 方案二：代码手动埋点
public Product getDetail(Long id) {
    Entry entry = null;
    try {
        entry = SphU.entry("productDetail");
        // ... 原有业务逻辑
    } catch (BlockException e) {
        throw new BusinessException(429, "访问限流");
    } finally {
        if (entry != null) entry.exit();
    }
}
```

### 6.5 全局限流拦截器

```java
@Component
public class RateLimitFilter implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String uri = request.getRequestURI();

        // 特定接口限流
        if (uri.startsWith("/api/order/create")) {
            Entry entry = null;
            try {
                entry = SphU.entry("createOrder");
            } catch (BlockException e) {
                response.setContentType("application/json;charset=utf-8");
                response.setStatus(429);
                response.getWriter().write("{\"code\":429,\"msg\":\"当前下单人数较多，请稍后再试\"}");
                return false;
            } finally {
                if (entry != null) entry.exit();
            }
        }
        return true;
    }
}
```

### 6.6 限流分级策略

| 接口 | 正常 QPS | 限流阈值 | 排队策略 | 降级策略 |
|------|---------|---------|---------|---------|
| 商品列表 | 200 | 300 | 快速失败 | 返回缓存数据 |
| 商品详情 | 500 | 800 | Warm Up | Redis 缓存兜底 |
| 下单 | 50 | 80 | 匀速排队 | 提示"排队中" |
| 秒杀 | 500 | 1000 | 快速失败 | 返回"已售罄" |
| 领券 | 100 | 150 | 快速失败 | 返回缓存 |
| 登录 | 100 | 200 | 快速失败 | — |

---

## 七、分布式锁

### 7.1 Redisson 集成

```xml
<dependency>
    <groupId>org.redisson</groupId>
    <artifactId>redisson-spring-boot-starter</artifactId>
    <version>3.23.5</version>
</dependency>
```

```java
@Configuration
public class RedissonConfig {
    @Value("${spring.redis.host}")
    private String redisHost;
    @Value("${spring.redis.port}")
    private int redisPort;

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer()
            .setAddress("redis://" + redisHost + ":" + redisPort)
            .setDatabase(0)
            .setConnectionPoolSize(32)
            .setConnectionMinimumIdleSize(8);
        return Redisson.create(config);
    }
}
```

### 7.2 关键场景加锁

**优惠券领取 — 防止超发和重复领取：**

```java
public void receive(Long couponId) {
    Long userId = UserContext.get().getUserId();
    String lockKey = "coupon:receive:" + userId + ":" + couponId;
    RLock lock = redissonClient.getLock(lockKey);

    try {
        // 尝试加锁，最多等待 2 秒，锁 5 秒自动释放
        if (!lock.tryLock(2, 5, TimeUnit.SECONDS)) {
            throw new BusinessException("操作频繁，请稍后再试");
        }

        // 双重检查
        int count = userCouponMapper.countByUserAndCoupon(userId, couponId);
        CouponTemplate coupon = templateMapper.selectById(couponId);
        if (count >= coupon.getPerLimit()) throw new BusinessException("已达领取上限");
        if (coupon.getIssued() >= coupon.getTotal()) throw new BusinessException("已被领完");
        if (coupon.getStatus() != 1) throw new BusinessException("优惠券已下架");

        templateMapper.incrementIssued(couponId);
        UserCoupon uc = new UserCoupon();
        uc.setUserId(userId);
        uc.setCouponId(couponId);
        uc.setStatus(0);
        userCouponMapper.insert(uc);

    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        throw new BusinessException("操作被中断");
    } finally {
        if (lock.isHeldByCurrentThread()) lock.unlock();
    }
}
```

**库存扣减 — 防止超卖（配合 Redis 预扣减）：**

```java
// 秒杀场景：Redis 预扣减 + DB 异步扣减
public String seckill(Long userId, Long productId) {
    // 1. Redis 预扣库存（原子操作）
    String stockKey = "seckill:stock:" + productId;
    Long remaining = redisService.decrement(stockKey, 1);
    if (remaining < 0) {
        redisService.increment(stockKey, 1);  // 回补
        return "已售罄";
    }

    // 2. 写入消息队列，异步创建订单
    redisService.lpush("seckill:order:queue", userId + ":" + productId);

    return "排队中，请稍后查看订单";
}
```

### 7.3 锁使用规范

| 场景 | 锁 Key | 等待时间 | 持有时间 |
|------|--------|---------|---------|
| 领券 | `coupon:receive:{userId}:{couponId}` | 2s | 5s |
| 下单 | `order:create:{userId}` | 3s | 10s |
| 退款 | `order:refund:{orderId}` | 5s | 10s |
| 库存扣减 | `stock:reduce:{productId}` | 1s | 2s |

---

## 八、Seata 分布式事务

当系统拆分微服务时，下单流程涉及多个服务（订单、库存、优惠券、积分），需要分布式事务协调。

> 当前单体架构暂不需要，预留方案供后续拆分使用。

```yaml
# 后续拆分时添加
seata:
  enabled: true
  tx-service-group: mall-tx-group
  service:
    vgroup-mapping:
      mall-tx-group: default
    grouplist:
      default: localhost:8091
```

---

## 九、MySQL 配置优化

除应用层优化外，MySQL 容器也需调整：

```yaml
# docker-compose.yml 中 mysql 服务增加配置
command:
  - --character-set-server=utf8mb4
  - --collation-server=utf8mb4_unicode_ci
  - --default-time-zone=+08:00
  - --max_allowed_packet=64M
  - --innodb_buffer_pool_size=512M           # InnoDB 缓冲池（内存的 50-70%）
  - --innodb_log_file_size=256M              # 重做日志大小
  - --innodb_flush_log_at_trx_commit=2       # 适当降低持久化要求
  - --sync_binlog=0                          # 同上
  - --max_connections=200                    # 最大连接数
  - --thread_cache_size=64                   # 线程缓存
  - --table_open_cache=2000                  # 表缓存
  - --innodb_io_capacity=2000                # SSD IO 能力
```

---

## 十、数据库连接池优化

针对 Redis Lettuce 客户端的连接池配置：

```yaml
spring:
  redis:
    host: localhost
    port: 6379
    database: 0
    timeout: 5000ms
    lettuce:
      pool:
        max-active: 16       # 最大活跃连接
        max-idle: 8          # 最大空闲连接
        min-idle: 4          # 最小空闲连接
        max-wait: 3000ms     # 获取连接最大等待时间
```

---

## 十一、优化实施路线图

按投入产出比排序，建议分阶段实施：

```
第一阶段（1-2 天）— 快速见效
├── HikariCP 连接池参数调整
├── Redis Lettuce 连接池配置
├── 订单列表 N+1 → 批量查询（selectByOrderIds）
├── 订单创建消除冗余 SQL
├── 补充缺失索引（create_time 等 8 个索引）
└── 操作日志异步化

第二阶段（2-3 天）— 缓存体系
├── Redis 接入商品详情/列表/分类树缓存
├── Caffeine 本地缓存（分类、权限、配置）
├── 缓存穿透/击穿防护
└── 营销活动列表 N+1 → 批量查询

第三阶段（2-3 天）— 限流与锁
├── Sentinel 接入下单/秒杀/登录等关键接口
├── Redisson 分布式锁（领券、库存）
├── 优惠券领取竞态修复
└── 订单超时自动取消定时任务

第四阶段（1-2 天）— 搜索优化
├── MySQL FULLTEXT 索引
└── LIKE 查询改为全文检索

第五阶段（后续拆分时）— 架构升级
├── Seata 分布式事务
├── Elasticsearch 搜索
└── 消息队列异步解耦
```

---

## 十二、性能监控指标

建议接入以下监控，验证优化效果：

| 指标 | 工具 | 当前基线 | 第一阶段目标 | 最终目标 |
|------|------|---------|-------------|---------|
| 接口 P99 延迟 | Redis + AOP | 未采集 | < 500ms | < 200ms |
| DB 连接池利用率 | HikariCP metrics | 默认 | < 60% | < 40% |
| 缓存命中率 | Redis INFO | ~0% | > 80% | > 95% |
| 下单接口 QPS | Sentinel Dashboard | 未采集 | 50 | 200+ |
| GC 停顿时间 | JMX / Arthas | 未采集 | < 100ms | < 50ms |
| 限流触发次数 | Sentinel 日志 | 无 | 采集 | 按需调整 |

**快速接入监控的 AOP 方案：**

```java
@Aspect
@Component
public class ApiMetricsAspect {

    @Around("@annotation(org.springframework.web.bind.annotation.RequestMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.GetMapping) || ...")
    public Object measure(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.nanoTime();
        try {
            return pjp.proceed();
        } finally {
            long elapsed = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
            String api = pjp.getSignature().toShortString();
            // 推送到 Prometheus / 日志 / 内部监控系统
            log.info("API: {} elapsed: {}ms", api, elapsed);
        }
    }
}
```

---

## 总结

| 维度 | 当前状态 | 优化后 |
|------|---------|--------|
| 数据库连接 | 默认 10，高并发耗尽 | 30+，连接池管理 |
| 缓存 | 仅 1 个 Redis key | 多级缓存，命中率 > 95% |
| SQL 查询 | 多处 N+1，LIKE 全表扫描 | 批量查询 + 全文索引 |
| 并发控制 | 基本无 | Sentinel 限流 + Redisson 分布式锁 |
| 异步处理 | 完全同步阻塞 | 线程池异步化日志/积分/通知 |
| 数据持久层索引 | 缺少排序字段索引 | 8+ 补充索引 |
| 监控 | 无 | AOP 耗时统计 + Sentinel Dashboard |
