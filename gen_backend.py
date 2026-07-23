#!/usr/bin/env python3
"""Generate all remaining backend Java files and mapper XMLs for mini-mall project."""
import os

BASE = "/Users/dorian/WorkBuddy/2026-07-21-23-31-52/mall-backend/src/main"
JAVA = BASE + "/java/com/mall/module"
XML = BASE + "/resources/mapper"

def w(path, content):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, 'w') as f:
        f.write(content)
    print(f"  + {path}")

# ============================================================
# User Address Module
# ============================================================
print("=== User Address Module ===")

w(f"{JAVA}/user/entity/UserAddress.java", '''package com.mall.module.user.entity;

import com.mall.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserAddress extends BaseEntity {
    private Long id;
    private Long userId;
    private String receiver;
    private String phone;
    private String province;
    private String city;
    private String district;
    private String detail;
    private Integer isDefault;
}
''')

w(f"{JAVA}/user/mapper/UserAddressMapper.java", '''package com.mall.module.user.mapper;

import com.mall.module.user.entity.UserAddress;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface UserAddressMapper {
    int insert(UserAddress address);
    int updateById(UserAddress address);
    int deleteById(Long id);
    UserAddress selectById(Long id);
    List<UserAddress> selectByUserId(@Param("userId") Long userId);
    int clearDefault(@Param("userId") Long userId);
}
''')

w(f"{XML}/UserAddressMapper.xml", '''<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.mall.module.user.mapper.UserAddressMapper">
    <resultMap id="BaseResultMap" type="com.mall.module.user.entity.UserAddress">
        <id column="id" property="id"/>
        <result column="user_id" property="userId"/>
        <result column="receiver" property="receiver"/>
        <result column="phone" property="phone"/>
        <result column="province" property="province"/>
        <result column="city" property="city"/>
        <result column="district" property="district"/>
        <result column="detail" property="detail"/>
        <result column="is_default" property="isDefault"/>
        <result column="create_time" property="createTime"/>
        <result column="update_time" property="updateTime"/>
        <result column="deleted" property="deleted"/>
    </resultMap>
    <sql id="cols">id,user_id,receiver,phone,province,city,district,detail,is_default,create_time,update_time,deleted</sql>

    <insert id="insert" parameterType="com.mall.module.user.entity.UserAddress" useGeneratedKeys="true" keyProperty="id">
        INSERT INTO user_address(user_id,receiver,phone,province,city,district,detail,is_default)
        VALUES(#{userId},#{receiver},#{phone},#{province},#{city},#{district},#{detail},#{isDefault})
    </insert>
    <update id="updateById" parameterType="com.mall.module.user.entity.UserAddress">
        UPDATE user_address<set>
            <if test="receiver!=null">receiver=#{receiver},</if>
            <if test="phone!=null">phone=#{phone},</if>
            <if test="province!=null">province=#{province},</if>
            <if test="city!=null">city=#{city},</if>
            <if test="district!=null">district=#{district},</if>
            <if test="detail!=null">detail=#{detail},</if>
            <if test="isDefault!=null">is_default=#{isDefault},</if>
        </set>WHERE id=#{id} AND deleted=0
    </update>
    <update id="deleteById">UPDATE user_address SET deleted=1 WHERE id=#{id}</update>
    <select id="selectById" resultMap="BaseResultMap">SELECT <include refid="cols"/> FROM user_address WHERE id=#{id} AND deleted=0</select>
    <select id="selectByUserId" resultMap="BaseResultMap">SELECT <include refid="cols"/> FROM user_address WHERE user_id=#{userId} AND deleted=0 ORDER BY is_default DESC,update_time DESC</select>
    <update id="clearDefault">UPDATE user_address SET is_default=0 WHERE user_id=#{userId} AND deleted=0</update>
</mapper>
''')

w(f"{JAVA}/user/service/UserAddressService.java", '''package com.mall.module.user.service;

import com.mall.common.exception.BusinessException;
import com.mall.module.user.entity.UserAddress;
import com.mall.module.user.mapper.UserAddressMapper;
import com.mall.security.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class UserAddressService {
    @Autowired
    private UserAddressMapper addressMapper;

    public List<UserAddress> list() {
        return addressMapper.selectByUserId(UserContext.require().getUserId());
    }

    @Transactional
    public void save(UserAddress addr) {
        Long userId = UserContext.require().getUserId();
        addr.setUserId(userId);
        if (addr.getIsDefault() != null && addr.getIsDefault() == 1) {
            addressMapper.clearDefault(userId);
        }
        if (addr.getId() == null) {
            addressMapper.insert(addr);
        } else {
            addressMapper.updateById(addr);
        }
    }

    @Transactional
    public void delete(Long id) {
        addressMapper.deleteById(id);
    }

    public UserAddress getById(Long id) {
        return addressMapper.selectById(id);
    }

    public UserAddress getDefault() {
        List<UserAddress> list = addressMapper.selectByUserId(UserContext.require().getUserId());
        return list.stream().filter(a -> a.getIsDefault() == 1).findFirst().orElse(list.isEmpty() ? null : list.get(0));
    }
}
''')

w(f"{JAVA}/user/controller/UserAddressController.java", '''package com.mall.module.user.controller;

import com.mall.common.Result;
import com.mall.module.user.entity.UserAddress;
import com.mall.module.user.service.UserAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/user/address")
public class UserAddressController {
    @Autowired
    private UserAddressService addressService;

    @GetMapping("/list")
    public Result<List<UserAddress>> list() { return Result.success(addressService.list()); }

    @PostMapping
    public Result<Void> save(@RequestBody UserAddress addr) { addressService.save(addr); return Result.success(); }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) { addressService.delete(id); return Result.success(); }

    @GetMapping("/default")
    public Result<UserAddress> getDefault() { return Result.success(addressService.getDefault()); }
}
''')

# ============================================================
# Cart Module
# ============================================================
print("=== Cart Module ===")

w(f"{JAVA}/cart/entity/CartItem.java", '''package com.mall.module.cart.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class CartItem {
    private Long id;
    private Long userId;
    private Long productId;
    private Long skuId;
    private String productName;
    private String productImage;
    private String skuName;
    private BigDecimal price;
    private Integer quantity;
    private Integer selected;
    private Date createTime;
    private Date updateTime;
}
''')

w(f"{JAVA}/cart/mapper/CartItemMapper.java", '''package com.mall.module.cart.mapper;

import com.mall.module.cart.entity.CartItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface CartItemMapper {
    int insert(CartItem item);
    int updateById(CartItem item);
    int deleteById(Long id);
    int deleteByUserIdAndProduct(@Param("userId") Long userId, @Param("productId") Long productId, @Param("skuId") Long skuId);
    CartItem selectById(Long id);
    CartItem selectByUserAndProduct(@Param("userId") Long userId, @Param("productId") Long productId, @Param("skuId") Long skuId);
    List<CartItem> selectByUserId(@Param("userId") Long userId);
    List<CartItem> selectSelectedByUserId(@Param("userId") Long userId);
    int updateQuantity(@Param("id") Long id, @Param("quantity") int quantity);
    int updateSelected(@Param("userId") Long userId, @Param("selected") int selected);
    int clearByUserId(Long userId);
    int countByUserId(Long userId);
}
''')

w(f"{XML}/CartItemMapper.xml", '''<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.mall.module.cart.mapper.CartItemMapper">
    <resultMap id="BaseResultMap" type="com.mall.module.cart.entity.CartItem">
        <id column="id" property="id"/>
        <result column="user_id" property="userId"/>
        <result column="product_id" property="productId"/>
        <result column="sku_id" property="skuId"/>
        <result column="product_name" property="productName"/>
        <result column="product_image" property="productImage"/>
        <result column="sku_name" property="skuName"/>
        <result column="price" property="price"/>
        <result column="quantity" property="quantity"/>
        <result column="selected" property="selected"/>
        <result column="create_time" property="createTime"/>
        <result column="update_time" property="updateTime"/>
    </resultMap>
    <sql id="cols">id,user_id,product_id,sku_id,product_name,product_image,sku_name,price,quantity,selected,create_time,update_time</sql>

    <insert id="insert" parameterType="com.mall.module.cart.entity.CartItem" useGeneratedKeys="true" keyProperty="id">
        INSERT INTO cart_item(user_id,product_id,sku_id,product_name,product_image,sku_name,price,quantity,selected)
        VALUES(#{userId},#{productId},#{skuId},#{productName},#{productImage},#{skuName},#{price},#{quantity},#{selected})
    </insert>
    <update id="updateById" parameterType="com.mall.module.cart.entity.CartItem">
        UPDATE cart_item<set>
            <if test="quantity!=null">quantity=#{quantity},</if>
            <if test="selected!=null">selected=#{selected},</if>
            <if test="price!=null">price=#{price},</if>
        </set>WHERE id=#{id}
    </update>
    <delete id="deleteById">DELETE FROM cart_item WHERE id=#{id}</delete>
    <delete id="deleteByUserIdAndProduct">DELETE FROM cart_item WHERE user_id=#{userId} AND product_id=#{productId} AND (sku_id=#{skuId} OR (#{skuId} IS NULL AND sku_id IS NULL))</delete>
    <select id="selectById" resultMap="BaseResultMap">SELECT <include refid="cols"/> FROM cart_item WHERE id=#{id}</select>
    <select id="selectByUserAndProduct" resultMap="BaseResultMap">SELECT <include refid="cols"/> FROM cart_item WHERE user_id=#{userId} AND product_id=#{productId} AND (sku_id=#{skuId} OR (#{skuId} IS NULL AND sku_id IS NULL))</select>
    <select id="selectByUserId" resultMap="BaseResultMap">SELECT <include refid="cols"/> FROM cart_item WHERE user_id=#{userId} ORDER BY update_time DESC</select>
    <select id="selectSelectedByUserId" resultMap="BaseResultMap">SELECT <include refid="cols"/> FROM cart_item WHERE user_id=#{userId} AND selected=1 ORDER BY update_time DESC</select>
    <update id="updateQuantity">UPDATE cart_item SET quantity=#{quantity} WHERE id=#{id}</update>
    <update id="updateSelected">UPDATE cart_item SET selected=#{selected} WHERE user_id=#{userId}</update>
    <delete id="clearByUserId">DELETE FROM cart_item WHERE user_id=#{userId}</delete>
    <select id="countByUserId" resultType="int">SELECT COUNT(*) FROM cart_item WHERE user_id=#{userId}</select>
</mapper>
''')

w(f"{JAVA}/cart/service/CartService.java", '''package com.mall.module.cart.service;

import com.mall.common.exception.BusinessException;
import com.mall.module.cart.entity.CartItem;
import com.mall.module.cart.mapper.CartItemMapper;
import com.mall.module.product.entity.Product;
import com.mall.module.product.entity.ProductSku;
import com.mall.module.product.mapper.ProductMapper;
import com.mall.module.product.mapper.ProductSkuMapper;
import com.mall.security.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Service
public class CartService {
    @Autowired private CartItemMapper cartMapper;
    @Autowired private ProductMapper productMapper;
    @Autowired private ProductSkuMapper skuMapper;

    public List<CartItem> list() { return cartMapper.selectByUserId(UserContext.require().getUserId()); }

    @Transactional
    public void add(CartItem item) {
        Long userId = UserContext.require().getUserId();
        item.setUserId(userId);
        // validate product
        Product product = productMapper.selectById(item.getProductId());
        if (product == null || product.getStatus() != 1) throw BusinessException.of("商品不存在或已下架");
        if (item.getSkuId() != null) {
            ProductSku sku = skuMapper.selectById(item.getSkuId());
            if (sku == null) throw BusinessException.of("SKU不存在");
            item.setSkuName(sku.getName());
            item.setPrice(sku.getPrice());
        } else {
            item.setPrice(product.getPrice());
        }
        item.setProductName(product.getName());
        item.setProductImage(product.getMainImage());
        item.setSelected(1);
        // check existing
        CartItem existing = cartMapper.selectByUserAndProduct(userId, item.getProductId(), item.getSkuId());
        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + item.getQuantity());
            cartMapper.updateById(existing);
        } else {
            cartMapper.insert(item);
        }
    }

    @Transactional
    public void updateQuantity(Long id, int quantity) {
        if (quantity <= 0) throw BusinessException.of("数量必须大于0");
        cartMapper.updateQuantity(id, quantity);
    }

    @Transactional
    public void updateSelected(int selected) {
        cartMapper.updateSelected(UserContext.require().getUserId(), selected);
    }

    @Transactional
    public void remove(Long id) { cartMapper.deleteById(id); }

    @Transactional
    public void clear() { cartMapper.clearByUserId(UserContext.require().getUserId()); }

    public int count() { return cartMapper.countByUserId(UserContext.require().getUserId()); }

    public List<CartItem> getSelected() { return cartMapper.selectSelectedByUserId(UserContext.require().getUserId()); }
}
''')

w(f"{JAVA}/cart/controller/CartController.java", '''package com.mall.module.cart.controller;

import com.mall.common.Result;
import com.mall.module.cart.entity.CartItem;
import com.mall.module.cart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {
    @Autowired private CartService cartService;

    @GetMapping("/list")
    public Result<List<CartItem>> list() { return Result.success(cartService.list()); }

    @PostMapping
    public Result<Void> add(@RequestBody CartItem item) { cartService.add(item); return Result.success(); }

    @PutMapping("/{id}/quantity")
    public Result<Void> updateQuantity(@PathVariable Long id, @RequestParam int quantity) { cartService.updateQuantity(id, quantity); return Result.success(); }

    @PutMapping("/selected")
    public Result<Void> updateSelected(@RequestParam int selected) { cartService.updateSelected(selected); return Result.success(); }

    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) { cartService.remove(id); return Result.success(); }

    @DeleteMapping("/clear")
    public Result<Void> clear() { cartService.clear(); return Result.success(); }

    @GetMapping("/count")
    public Result<Integer> count() { return Result.success(cartService.count()); }
}
''')

# ============================================================
# Order Module
# ============================================================
print("=== Order Module ===")

w(f"{JAVA}/order/entity/MallOrder.java", '''package com.mall.module.order.entity;

import com.mall.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class MallOrder extends BaseEntity {
    private Long id;
    private String orderNo;
    private Long userId;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private BigDecimal pointsAmount;
    private BigDecimal payAmount;
    private Integer pointsUsed;
    private Integer pointsEarned;
    private Long couponId;
    private Integer status;
    private Integer payType;
    private Date payTime;
    private Date shipTime;
    private Date receiveTime;
    private Date closeTime;
    private String receiver;
    private String receiverPhone;
    private String receiverAddress;
    private String shipCompany;
    private String shipNo;
    private String remark;
    private Date expireTime;
    // transient
    private List<com.mall.module.order.entity.OrderItem> items;
}
''')

w(f"{JAVA}/order/entity/OrderItem.java", '''package com.mall.module.order.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class OrderItem {
    private Long id;
    private Long orderId;
    private String orderNo;
    private Long productId;
    private Long skuId;
    private String productName;
    private String productImage;
    private String skuName;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal totalAmount;
    private Date createTime;
}
''')

w(f"{JAVA}/order/dto/CreateOrderDTO.java", '''package com.mall.module.order.dto;

import lombok.Data;
import java.util.List;

@Data
public class CreateOrderDTO {
    /** 购物车项ID列表, 为空则下单全部选中商品 */
    private List<Long> cartItemIds;
    private Long addressId;
    private Long couponId;
    private Integer pointsUsed;
    private String remark;
    private Integer payType;
}
''')

w(f"{JAVA}/order/mapper/MallOrderMapper.java", '''package com.mall.module.order.mapper;

import com.mall.module.order.entity.MallOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface MallOrderMapper {
    int insert(MallOrder order);
    int updateById(MallOrder order);
    MallOrder selectById(Long id);
    MallOrder selectByOrderNo(@Param("orderNo") String orderNo);
    List<MallOrder> selectByUserId(@Param("userId") Long userId, @Param("status") Integer status, @Param("offset") int offset, @Param("limit") int limit);
    long countByUserId(@Param("userId") Long userId, @Param("status") Integer status);
    List<MallOrder> selectList(@Param("orderNo") String orderNo, @Param("status") Integer status, @Param("userId") Long userId, @Param("offset") int offset, @Param("limit") int limit);
    long count(@Param("orderNo") String orderNo, @Param("status") Integer status, @Param("userId") Long userId);
    int updateStatus(@Param("id") Long id, @Param("status") int status);
    int updateShip(@Param("id") Long id, @Param("shipCompany") String shipCompany, @Param("shipNo") String shipNo);
}
''')

w(f"{JAVA}/order/mapper/OrderItemMapper.java", '''package com.mall.module.order.mapper;

import com.mall.module.order.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface OrderItemMapper {
    int batchInsert(@Param("list") List<OrderItem> list);
    List<OrderItem> selectByOrderId(@Param("orderId") Long orderId);
    List<OrderItem> selectByOrderIds(@Param("orderIds") List<Long> orderIds);
}
''')

w(f"{XML}/MallOrderMapper.xml", '''<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.mall.module.order.mapper.MallOrderMapper">
    <resultMap id="BaseResultMap" type="com.mall.module.order.entity.MallOrder">
        <id column="id" property="id"/>
        <result column="order_no" property="orderNo"/>
        <result column="user_id" property="userId"/>
        <result column="total_amount" property="totalAmount"/>
        <result column="discount_amount" property="discountAmount"/>
        <result column="points_amount" property="pointsAmount"/>
        <result column="pay_amount" property="payAmount"/>
        <result column="points_used" property="pointsUsed"/>
        <result column="points_earned" property="pointsEarned"/>
        <result column="coupon_id" property="couponId"/>
        <result column="status" property="status"/>
        <result column="pay_type" property="payType"/>
        <result column="pay_time" property="payTime"/>
        <result column="ship_time" property="shipTime"/>
        <result column="receive_time" property="receiveTime"/>
        <result column="close_time" property="closeTime"/>
        <result column="receiver" property="receiver"/>
        <result column="receiver_phone" property="receiverPhone"/>
        <result column="receiver_address" property="receiverAddress"/>
        <result column="ship_company" property="shipCompany"/>
        <result column="ship_no" property="shipNo"/>
        <result column="remark" property="remark"/>
        <result column="expire_time" property="expireTime"/>
        <result column="create_time" property="createTime"/>
        <result column="update_time" property="updateTime"/>
        <result column="deleted" property="deleted"/>
    </resultMap>
    <sql id="cols">id,order_no,user_id,total_amount,discount_amount,points_amount,pay_amount,points_used,points_earned,coupon_id,status,pay_type,pay_time,ship_time,receive_time,close_time,receiver,receiver_phone,receiver_address,ship_company,ship_no,remark,expire_time,create_time,update_time,deleted</sql>

    <insert id="insert" parameterType="com.mall.module.order.entity.MallOrder" useGeneratedKeys="true" keyProperty="id">
        INSERT INTO mall_order(order_no,user_id,total_amount,discount_amount,points_amount,pay_amount,points_used,points_earned,coupon_id,status,pay_type,receiver,receiver_phone,receiver_address,remark,expire_time)
        VALUES(#{orderNo},#{userId},#{totalAmount},#{discountAmount},#{pointsAmount},#{payAmount},#{pointsUsed},#{pointsEarned},#{couponId},#{status},#{payType},#{receiver},#{receiverPhone},#{receiverAddress},#{remark},#{expireTime})
    </insert>
    <update id="updateById" parameterType="com.mall.module.order.entity.MallOrder">
        UPDATE mall_order<set>
            <if test="status!=null">status=#{status},</if>
            <if test="payType!=null">pay_type=#{payType},</if>
            <if test="payTime!=null">pay_time=#{payTime},</if>
            <if test="shipTime!=null">ship_time=#{shipTime},</if>
            <if test="receiveTime!=null">receive_time=#{receiveTime},</if>
            <if test="closeTime!=null">close_time=#{closeTime},</if>
            <if test="shipCompany!=null">ship_company=#{shipCompany},</if>
            <if test="shipNo!=null">ship_no=#{shipNo},</if>
            <if test="couponId!=null">coupon_id=#{couponId},</if>
        </set>WHERE id=#{id} AND deleted=0
    </update>
    <select id="selectById" resultMap="BaseResultMap">SELECT <include refid="cols"/> FROM mall_order WHERE id=#{id} AND deleted=0</select>
    <select id="selectByOrderNo" resultMap="BaseResultMap">SELECT <include refid="cols"/> FROM mall_order WHERE order_no=#{orderNo} AND deleted=0</select>
    <select id="selectByUserId" resultMap="BaseResultMap">SELECT <include refid="cols"/> FROM mall_order WHERE user_id=#{userId} AND deleted=0<if test="status!=null">AND status=#{status}</if>ORDER BY create_time DESC LIMIT #{offset},#{limit}</select>
    <select id="countByUserId" resultType="long">SELECT COUNT(*) FROM mall_order WHERE user_id=#{userId} AND deleted=0<if test="status!=null">AND status=#{status}</if></select>
    <select id="selectList" resultMap="BaseResultMap">SELECT <include refid="cols"/> FROM mall_order WHERE deleted=0<if test="orderNo!=null and orderNo!=''">AND order_no LIKE CONCAT('%',#{orderNo},'%')</if><if test="status!=null">AND status=#{status}</if><if test="userId!=null">AND user_id=#{userId}</if>ORDER BY create_time DESC LIMIT #{offset},#{limit}</select>
    <select id="count" resultType="long">SELECT COUNT(*) FROM mall_order WHERE deleted=0<if test="orderNo!=null and orderNo!=''">AND order_no LIKE CONCAT('%',#{orderNo},'%')</if><if test="status!=null">AND status=#{status}</if><if test="userId!=null">AND user_id=#{userId}</if></select>
    <update id="updateStatus">UPDATE mall_order SET status=#{status} WHERE id=#{id} AND deleted=0</update>
    <update id="updateShip">UPDATE mall_order SET ship_company=#{shipCompany},ship_no=#{shipNo},ship_time=NOW(),status=2 WHERE id=#{id} AND deleted=0</update>
</mapper>
''')

w(f"{XML}/OrderItemMapper.xml", '''<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.mall.module.order.mapper.OrderItemMapper">
    <resultMap id="BaseResultMap" type="com.mall.module.order.entity.OrderItem">
        <id column="id" property="id"/>
        <result column="order_id" property="orderId"/>
        <result column="order_no" property="orderNo"/>
        <result column="product_id" property="productId"/>
        <result column="sku_id" property="skuId"/>
        <result column="product_name" property="productName"/>
        <result column="product_image" property="productImage"/>
        <result column="sku_name" property="skuName"/>
        <result column="price" property="price"/>
        <result column="quantity" property="quantity"/>
        <result column="total_amount" property="totalAmount"/>
        <result column="create_time" property="createTime"/>
    </resultMap>
    <insert id="batchInsert">
        INSERT INTO order_item(order_id,order_no,product_id,sku_id,product_name,product_image,sku_name,price,quantity,total_amount)
        VALUES
        <foreach collection="list" item="item" separator=",">
            (#{item.orderId},#{item.orderNo},#{item.productId},#{item.skuId},#{item.productName},#{item.productImage},#{item.skuName},#{item.price},#{item.quantity},#{item.totalAmount})
        </foreach>
    </insert>
    <select id="selectByOrderId" resultMap="BaseResultMap">SELECT * FROM order_item WHERE order_id=#{orderId}</select>
    <select id="selectByOrderIds" resultMap="BaseResultMap">SELECT * FROM order_item WHERE order_id IN<foreach collection="orderIds" item="id" open="(" separator="," close=")">#{id}</foreach></select>
</mapper>
''')

w(f"{JAVA}/order/service/OrderService.java", '''package com.mall.module.order.service;

import com.mall.common.PageResult;
import com.mall.common.exception.BusinessException;
import com.mall.module.cart.entity.CartItem;
import com.mall.module.cart.mapper.CartItemMapper;
import com.mall.module.order.dto.CreateOrderDTO;
import com.mall.module.order.entity.MallOrder;
import com.mall.module.order.entity.OrderItem;
import com.mall.module.order.mapper.MallOrderMapper;
import com.mall.module.order.mapper.OrderItemMapper;
import com.mall.module.product.entity.Product;
import com.mall.module.product.mapper.ProductMapper;
import com.mall.module.product.mapper.ProductSkuMapper;
import com.mall.module.product.entity.ProductSku;
import com.mall.module.user.entity.UserAddress;
import com.mall.module.user.mapper.UserAddressMapper;
import com.mall.security.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderService {
    @Autowired private MallOrderMapper orderMapper;
    @Autowired private OrderItemMapper itemMapper;
    @Autowired private CartItemMapper cartMapper;
    @Autowired private ProductMapper productMapper;
    @Autowired private ProductSkuMapper skuMapper;
    @Autowired private UserAddressMapper addressMapper;
    @Autowired private com.mall.module.points.service.PointsService pointsService;
    @Autowired private com.mall.module.coupon.service.CouponService couponService;

    @Transactional
    public MallOrder create(CreateOrderDTO dto) {
        Long userId = UserContext.require().getUserId();

        // 1. get cart items
        List<CartItem> allCart = cartMapper.selectByUserId(userId);
        List<CartItem> selected;
        if (dto.getCartItemIds() != null && !dto.getCartItemIds().isEmpty()) {
            Set<Long> idSet = new HashSet<>(dto.getCartItemIds());
            selected = allCart.stream().filter(c -> idSet.contains(c.getId())).collect(Collectors.toList());
        } else {
            selected = allCart.stream().filter(CartItem::getSelected).filter(c -> c.getSelected() == 1).collect(Collectors.toList());
        }
        if (selected.isEmpty()) throw BusinessException.of("购物车中没有选中的商品");

        // 2. validate stock & build order items
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cart : selected) {
            Product product = productMapper.selectById(cart.getProductId());
            if (product == null || product.getStatus() != 1) throw BusinessException.of("商品已下架: " + cart.getProductName());
            int stock = cart.getSkuId() != null ?
                    (skuMapper.selectById(cart.getSkuId()) != null ? skuMapper.selectById(cart.getSkuId()).getStock() : 0) :
                    product.getStock();
            if (stock < cart.getQuantity()) throw BusinessException.of("库存不足: " + cart.getProductName());

            // deduct stock
            if (cart.getSkuId() != null) {
                skuMapper.reduceStock(cart.getSkuId(), cart.getQuantity());
            } else {
                productMapper.reduceStock(cart.getProductId(), cart.getQuantity());
            }
            productMapper.addSales(cart.getProductId(), cart.getQuantity());

            OrderItem item = new OrderItem();
            item.setProductId(cart.getProductId());
            item.setSkuId(cart.getSkuId());
            item.setProductName(cart.getProductName());
            item.setProductImage(cart.getProductImage());
            item.setSkuName(cart.getSkuName());
            item.setPrice(cart.getPrice());
            item.setQuantity(cart.getQuantity());
            item.setTotalAmount(cart.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity())));
            totalAmount = totalAmount.add(item.getTotalAmount());
            orderItems.add(item);
        }

        // 3. address
        UserAddress addr = addressMapper.selectById(dto.getAddressId());
        if (addr == null) throw BusinessException.of("收货地址不存在");

        // 4. coupon discount
        BigDecimal discountAmount = BigDecimal.ZERO;
        Long couponId = dto.getCouponId();
        if (couponId != null) {
            discountAmount = couponService.calculateDiscount(couponId, userId, totalAmount);
            if (discountAmount.compareTo(BigDecimal.ZERO) > 0) {
                couponService.useCoupon(couponId, userId, null); // order id set later
            }
        }

        // 5. points
        BigDecimal pointsAmount = BigDecimal.ZERO;
        int pointsUsed = dto.getPointsUsed() != null ? dto.getPointsUsed() : 0;
        if (pointsUsed > 0) {
            pointsAmount = BigDecimal.valueOf(pointsUsed).divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP);
            if (pointsAmount.compareTo(totalAmount.subtract(discountAmount)) > 0) {
                pointsAmount = totalAmount.subtract(discountAmount);
                pointsUsed = pointsAmount.multiply(BigDecimal.valueOf(100)).intValue();
            }
            pointsService.deductPoints(userId, pointsUsed, "ORDER", null, "下单使用积分");
        }

        // 6. calculate pay amount
        BigDecimal payAmount = totalAmount.subtract(discountAmount).subtract(pointsAmount);
        if (payAmount.compareTo(BigDecimal.ZERO) < 0) payAmount = BigDecimal.ZERO;

        // 7. earned points
        int pointsEarned = payAmount.intValue(); // 1 yuan = 1 point

        // 8. create order
        String orderNo = generateOrderNo(userId);
        MallOrder order = new MallOrder();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setDiscountAmount(discountAmount);
        order.setPointsAmount(pointsAmount);
        order.setPayAmount(payAmount);
        order.setPointsUsed(pointsUsed);
        order.setPointsEarned(pointsEarned);
        order.setCouponId(couponId);
        order.setStatus(0); // pending payment
        order.setPayType(dto.getPayType() != null ? dto.getPayType() : 1);
        order.setReceiver(addr.getReceiver());
        order.setReceiverPhone(addr.getPhone());
        order.setReceiverAddress(addr.getProvince() + addr.getCity() + addr.getDistrict() + addr.getDetail());
        order.setRemark(dto.getRemark());
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, 30);
        order.setExpireTime(cal.getTime());
        orderMapper.insert(order);

        // 9. order items
        for (OrderItem item : orderItems) {
            item.setOrderId(order.getId());
            item.setOrderNo(orderNo);
        }
        itemMapper.batchInsert(orderItems);
        order.setItems(orderItems);

        // 10. clear cart
        for (CartItem c : selected) cartMapper.deleteById(c.getId());

        return order;
    }

    public MallOrder detail(Long id) {
        MallOrder order = orderMapper.selectById(id);
        if (order == null) throw BusinessException.of("订单不存在");
        order.setItems(itemMapper.selectByOrderId(id));
        return order;
    }

    public MallOrder detailByOrderNo(String orderNo) {
        MallOrder order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) throw BusinessException.of("订单不存在");
        order.setItems(itemMapper.selectByOrderId(order.getId()));
        return order;
    }

    public PageResult<MallOrder> myList(Integer status, int pageNum, int pageSize) {
        Long userId = UserContext.require().getUserId();
        List<MallOrder> orders = orderMapper.selectByUserId(userId, status, (pageNum-1)*pageSize, pageSize);
        long total = orderMapper.countByUserId(userId, status);
        for (MallOrder o : orders) o.setItems(itemMapper.selectByOrderId(o.getId()));
        return PageResult.of(orders, total, pageNum, pageSize);
    }

    public PageResult<MallOrder> adminList(String orderNo, Integer status, Long userId, int pageNum, int pageSize) {
        List<MallOrder> orders = orderMapper.selectList(orderNo, status, userId, (pageNum-1)*pageSize, pageSize);
        long total = orderMapper.count(orderNo, status, userId);
        for (MallOrder o : orders) o.setItems(itemMapper.selectByOrderId(o.getId()));
        return PageResult.of(orders, total, pageNum, pageSize);
    }

    @Transactional
    public void pay(Long id, Integer payType) {
        MallOrder order = orderMapper.selectById(id);
        if (order == null) throw BusinessException.of("订单不存在");
        if (order.getStatus() != 0) throw BusinessException.of("订单状态不正确");
        MallOrder update = new MallOrder();
        update.setId(id);
        update.setStatus(1);
        update.setPayType(payType);
        update.setPayTime(new Date());
        orderMapper.updateById(update);
        // award points
        if (order.getPointsEarned() > 0) {
            pointsService.addPoints(order.getUserId(), order.getPointsEarned(), "ORDER", order.getId(), "下单获得积分");
        }
    }

    @Transactional
    public void cancel(Long id) {
        MallOrder order = orderMapper.selectById(id);
        if (order == null) throw BusinessException.of("订单不存在");
        if (order.getStatus() != 0) throw BusinessException.of("只能取消待付款订单");
        // restore stock
        List<OrderItem> items = itemMapper.selectByOrderId(id);
        for (OrderItem item : items) {
            if (item.getSkuId() != null) {
                skuMapper.restoreStock(item.getSkuId(), item.getQuantity());
            } else {
                productMapper.restoreStock(item.getProductId(), item.getQuantity());
            }
        }
        // refund points
        if (order.getPointsUsed() > 0) {
            pointsService.refundPoints(order.getUserId(), order.getPointsUsed(), "ORDER_CANCEL", order.getId(), "订单取消退回积分");
        }
        MallOrder update = new MallOrder();
        update.setId(id);
        update.setStatus(4);
        update.setCloseTime(new Date());
        orderMapper.updateById(update);
    }

    @Transactional
    public void ship(Long id, String company, String no) {
        MallOrder order = orderMapper.selectById(id);
        if (order == null) throw BusinessException.of("订单不存在");
        if (order.getStatus() != 1) throw BusinessException.of("只能发货待发货订单");
        orderMapper.updateShip(id, company, no);
    }

    @Transactional
    public void receive(Long id) {
        MallOrder order = orderMapper.selectById(id);
        if (order == null) throw BusinessException.of("订单不存在");
        if (order.getStatus() != 2) throw BusinessException.of("只能确认待收货订单");
        MallOrder update = new MallOrder();
        update.setId(id);
        update.setStatus(3);
        update.setReceiveTime(new Date());
        orderMapper.updateById(update);
    }

    @Transactional
    public void refund(Long id) {
        MallOrder order = orderMapper.selectById(id);
        if (order == null) throw BusinessException.of("订单不存在");
        // restore stock
        List<OrderItem> items = itemMapper.selectByOrderId(id);
        for (OrderItem item : items) {
            if (item.getSkuId() != null) skuMapper.restoreStock(item.getSkuId(), item.getQuantity());
            else productMapper.restoreStock(item.getProductId(), item.getQuantity());
        }
        // refund points
        if (order.getPointsEarned() > 0) {
            pointsService.deductPoints(order.getUserId(), order.getPointsEarned(), "REFUND", order.getId(), "退款扣除积分");
        }
        if (order.getPointsUsed() > 0) {
            pointsService.refundPoints(order.getUserId(), order.getPointsUsed(), "REFUND", order.getId(), "退款退回使用积分");
        }
        MallOrder update = new MallOrder();
        update.setId(id);
        update.setStatus(5);
        update.setCloseTime(new Date());
        orderMapper.updateById(update);
    }

    private String generateOrderNo(Long userId) {
        return String.format("%d%06d%04d", System.currentTimeMillis() / 1000, userId % 1000000, new Random().nextInt(10000));
    }
}
''')

w(f"{JAVA}/order/controller/OrderController.java", '''package com.mall.module.order.controller;

import com.mall.common.PageResult;
import com.mall.common.Result;
import com.mall.module.order.dto.CreateOrderDTO;
import com.mall.module.order.entity.MallOrder;
import com.mall.module.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired private OrderService orderService;

    @PostMapping
    public Result<MallOrder> create(@RequestBody CreateOrderDTO dto) { return Result.success(orderService.create(dto)); }

    @GetMapping("/{id}")
    public Result<MallOrder> detail(@PathVariable Long id) { return Result.success(orderService.detail(id)); }

    @GetMapping("/no/{orderNo}")
    public Result<MallOrder> detailByNo(@PathVariable String orderNo) { return Result.success(orderService.detailByOrderNo(orderNo)); }

    @GetMapping("/my")
    public Result<PageResult<MallOrder>> myList(@RequestParam(required = false) Integer status,
                                                 @RequestParam(defaultValue = "1") int pageNum,
                                                 @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(orderService.myList(status, pageNum, pageSize));
    }

    @PutMapping("/{id}/pay")
    public Result<Void> pay(@PathVariable Long id, @RequestParam Integer payType) { orderService.pay(id, payType); return Result.success(); }

    @PutMapping("/{id}/cancel")
    public Result<Void> cancel(@PathVariable Long id) { orderService.cancel(id); return Result.success(); }

    @PutMapping("/{id}/receive")
    public Result<Void> receive(@PathVariable Long id) { orderService.receive(id); return Result.success(); }

    // ===== Admin =====
    @GetMapping("/list")
    public Result<PageResult<MallOrder>> adminList(@RequestParam(required = false) String orderNo,
                                                    @RequestParam(required = false) Integer status,
                                                    @RequestParam(required = false) Long userId,
                                                    @RequestParam(defaultValue = "1") int pageNum,
                                                    @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(orderService.adminList(orderNo, status, userId, pageNum, pageSize));
    }

    @PutMapping("/{id}/ship")
    public Result<Void> ship(@PathVariable Long id, @RequestParam String shipCompany, @RequestParam String shipNo) {
        orderService.ship(id, shipCompany, shipNo); return Result.success();
    }

    @PutMapping("/{id}/refund")
    public Result<Void> refund(@PathVariable Long id) { orderService.refund(id); return Result.success(); }
}
''')

# ============================================================
# Points Module
# ============================================================
print("=== Points Module ===")

w(f"{JAVA}/points/entity/PointsAccount.java", '''package com.mall.module.points.entity;

import lombok.Data;
import java.util.Date;

@Data
public class PointsAccount {
    private Long id;
    private Long userId;
    private Integer balance;
    private Integer frozen;
    private Long totalEarned;
    private Long totalUsed;
    private Integer version;
    private Date createTime;
    private Date updateTime;
}
''')

w(f"{JAVA}/points/entity/PointsLog.java", '''package com.mall.module.points.entity;

import lombok.Data;
import java.util.Date;

@Data
public class PointsLog {
    private Long id;
    private Long userId;
    private String changeType; // EARN, USE, FREEZE, UNFREEZE, REFUND
    private Integer points;
    private Integer balanceAfter;
    private String source; // ORDER, SIGN, COUPON, ADMIN
    private Long refId;
    private String remark;
    private Date createTime;
}
''')

w(f"{JAVA}/points/mapper/PointsAccountMapper.java", '''package com.mall.module.points.mapper;

import com.mall.module.points.entity.PointsAccount;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PointsAccountMapper {
    int insert(PointsAccount account);
    int updateById(PointsAccount account);
    PointsAccount selectByUserId(@Param("userId") Long userId);
    int addPoints(@Param("userId") Long userId, @Param("points") int points);
    int deductPoints(@Param("userId") Long userId, @Param("points") int points);
}
''')

w(f"{JAVA}/points/mapper/PointsLogMapper.java", '''package com.mall.module.points.mapper;

import com.mall.module.points.entity.PointsLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface PointsLogMapper {
    int insert(PointsLog log);
    List<PointsLog> selectByUserId(@Param("userId") Long userId, @Param("source") String source, @Param("offset") int offset, @Param("limit") int limit);
    long countByUserId(@Param("userId") Long userId, @Param("source") String source);
}
''')

w(f"{XML}/PointsAccountMapper.xml", '''<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.mall.module.points.mapper.PointsAccountMapper">
    <resultMap id="BaseResultMap" type="com.mall.module.points.entity.PointsAccount">
        <id column="id" property="id"/>
        <result column="user_id" property="userId"/>
        <result column="balance" property="balance"/>
        <result column="frozen" property="frozen"/>
        <result column="total_earned" property="totalEarned"/>
        <result column="total_used" property="totalUsed"/>
        <result column="version" property="version"/>
        <result column="create_time" property="createTime"/>
        <result column="update_time" property="updateTime"/>
    </resultMap>
    <insert id="insert" parameterType="com.mall.module.points.entity.PointsAccount" useGeneratedKeys="true" keyProperty="id">
        INSERT INTO points_account(user_id,balance,frozen,total_earned,total_used,version) VALUES(#{userId},#{balance},#{frozen},#{totalEarned},#{totalUsed},#{version})
    </insert>
    <update id="updateById" parameterType="com.mall.module.points.entity.PointsAccount">
        UPDATE points_account<set>
            <if test="balance!=null">balance=#{balance},</if>
            <if test="frozen!=null">frozen=#{frozen},</if>
            <if test="totalEarned!=null">total_earned=#{totalEarned},</if>
            <if test="totalUsed!=null">total_used=#{totalUsed},</if>
            version=version+1,
        </set>WHERE id=#{id} AND version=#{version}
    </update>
    <select id="selectByUserId" resultMap="BaseResultMap">SELECT * FROM points_account WHERE user_id=#{userId}</select>
    <update id="addPoints">UPDATE points_account SET balance=balance+#{points},total_earned=total_earned+#{points} WHERE user_id=#{userId}</update>
    <update id="deductPoints">UPDATE points_account SET balance=balance-#{points},total_used=total_used+#{points} WHERE user_id=#{userId} AND balance>=#{points}</update>
</mapper>
''')

w(f"{XML}/PointsLogMapper.xml", '''<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.mall.module.points.mapper.PointsLogMapper">
    <resultMap id="BaseResultMap" type="com.mall.module.points.entity.PointsLog">
        <id column="id" property="id"/>
        <result column="user_id" property="userId"/>
        <result column="change_type" property="changeType"/>
        <result column="points" property="points"/>
        <result column="balance_after" property="balanceAfter"/>
        <result column="source" property="source"/>
        <result column="ref_id" property="refId"/>
        <result column="remark" property="remark"/>
        <result column="create_time" property="createTime"/>
    </resultMap>
    <insert id="insert" parameterType="com.mall.module.points.entity.PointsLog" useGeneratedKeys="true" keyProperty="id">
        INSERT INTO points_log(user_id,change_type,points,balance_after,source,ref_id,remark) VALUES(#{userId},#{changeType},#{points},#{balanceAfter},#{source},#{refId},#{remark})
    </insert>
    <select id="selectByUserId" resultMap="BaseResultMap">SELECT * FROM points_log WHERE user_id=#{userId}<if test="source!=null and source!=''">AND source=#{source}</if>ORDER BY create_time DESC LIMIT #{offset},#{limit}</select>
    <select id="countByUserId" resultType="long">SELECT COUNT(*) FROM points_log WHERE user_id=#{userId}<if test="source!=null and source!=''">AND source=#{source}</if></select>
</mapper>
''')

w(f"{JAVA}/points/service/PointsService.java", '''package com.mall.module.points.service;

import com.mall.common.PageResult;
import com.mall.common.exception.BusinessException;
import com.mall.module.points.entity.PointsAccount;
import com.mall.module.points.entity.PointsLog;
import com.mall.module.points.mapper.PointsAccountMapper;
import com.mall.module.points.mapper.PointsLogMapper;
import com.mall.security.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class PointsService {
    @Autowired private PointsAccountMapper accountMapper;
    @Autowired private PointsLogMapper logMapper;

    private PointsAccount getOrCreate(Long userId) {
        PointsAccount acc = accountMapper.selectByUserId(userId);
        if (acc == null) {
            acc = new PointsAccount();
            acc.setUserId(userId);
            acc.setBalance(0);
            acc.setFrozen(0);
            acc.setTotalEarned(0L);
            acc.setTotalUsed(0L);
            acc.setVersion(0);
            accountMapper.insert(acc);
        }
        return acc;
    }

    public PointsAccount getAccount(Long userId) { return getOrCreate(userId); }

    public PointsAccount myAccount() { return getOrCreate(UserContext.require().getUserId()); }

    @Transactional
    public void addPoints(Long userId, int points, String source, Long refId, String remark) {
        if (points <= 0) return;
        getOrCreate(userId);
        int rows = accountMapper.addPoints(userId, points);
        if (rows == 0) throw BusinessException.of("积分增加失败");
        PointsAccount acc = accountMapper.selectByUserId(userId);
        PointsLog log = new PointsLog();
        log.setUserId(userId);
        log.setChangeType("EARN");
        log.setPoints(points);
        log.setBalanceAfter(acc.getBalance());
        log.setSource(source);
        log.setRefId(refId);
        log.setRemark(remark);
        logMapper.insert(log);
    }

    @Transactional
    public void deductPoints(Long userId, int points, String source, Long refId, String remark) {
        if (points <= 0) return;
        getOrCreate(userId);
        int rows = accountMapper.deductPoints(userId, points);
        if (rows == 0) throw BusinessException.of("积分不足");
        PointsAccount acc = accountMapper.selectByUserId(userId);
        PointsLog log = new PointsLog();
        log.setUserId(userId);
        log.setChangeType("USE");
        log.setPoints(points);
        log.setBalanceAfter(acc.getBalance());
        log.setSource(source);
        log.setRefId(refId);
        log.setRemark(remark);
        logMapper.insert(log);
    }

    @Transactional
    public void refundPoints(Long userId, int points, String source, Long refId, String remark) {
        getOrCreate(userId);
        accountMapper.addPoints(userId, points);
        PointsAccount acc = accountMapper.selectByUserId(userId);
        PointsLog log = new PointsLog();
        log.setUserId(userId);
        log.setChangeType("REFUND");
        log.setPoints(points);
        log.setBalanceAfter(acc.getBalance());
        log.setSource(source);
        log.setRefId(refId);
        log.setRemark(remark);
        logMapper.insert(log);
    }

    @Transactional
    public void dailySign() {
        Long userId = UserContext.require().getUserId();
        addPoints(userId, 10, "SIGN", null, "每日签到");
    }

    public PageResult<PointsLog> myLogs(String source, int pageNum, int pageSize) {
        Long userId = UserContext.require().getUserId();
        List<PointsLog> logs = logMapper.selectByUserId(userId, source, (pageNum-1)*pageSize, pageSize);
        long total = logMapper.countByUserId(userId, source);
        return PageResult.of(logs, total, pageNum, pageSize);
    }

    public PageResult<PointsLog> adminLogs(Long userId, String source, int pageNum, int pageSize) {
        List<PointsLog> logs = logMapper.selectByUserId(userId, source, (pageNum-1)*pageSize, pageSize);
        long total = logMapper.countByUserId(userId, source);
        return PageResult.of(logs, total, pageNum, pageSize);
    }
}
''')

w(f"{JAVA}/points/controller/PointsController.java", '''package com.mall.module.points.controller;

import com.mall.common.PageResult;
import com.mall.common.Result;
import com.mall.module.points.entity.PointsAccount;
import com.mall.module.points.entity.PointsLog;
import com.mall.module.points.service.PointsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/points")
public class PointsController {
    @Autowired private PointsService pointsService;

    @GetMapping("/account")
    public Result<PointsAccount> myAccount() { return Result.success(pointsService.myAccount()); }

    @PostMapping("/sign")
    public Result<Void> sign() { pointsService.dailySign(); return Result.success(); }

    @GetMapping("/logs")
    public Result<PageResult<PointsLog>> myLogs(@RequestParam(required = false) String source,
                                                 @RequestParam(defaultValue = "1") int pageNum,
                                                 @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(pointsService.myLogs(source, pageNum, pageSize));
    }

    // Admin
    @GetMapping("/account/{userId}")
    public Result<PointsAccount> account(@PathVariable Long userId) { return Result.success(pointsService.getAccount(userId)); }

    @GetMapping("/logs/{userId}")
    public Result<PageResult<PointsLog>> adminLogs(@PathVariable Long userId,
                                                    @RequestParam(required = false) String source,
                                                    @RequestParam(defaultValue = "1") int pageNum,
                                                    @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(pointsService.adminLogs(userId, source, pageNum, pageSize));
    }
}
''')

# ============================================================
# Coupon Module
# ============================================================
print("=== Coupon Module ===")

w(f"{JAVA}/coupon/entity/CouponTemplate.java", '''package com.mall.module.coupon.entity;

import com.mall.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
public class CouponTemplate extends BaseEntity {
    private Long id;
    private String name;
    private Integer type; // 1-满减, 2-折扣, 3-无门槛
    private BigDecimal faceValue;
    private BigDecimal discount;
    private BigDecimal minSpend;
    private Long categoryLimit;
    private Integer totalCount;
    private Integer issuedCount;
    private Integer perLimit;
    private Integer validType; // 1-固定日期, 2-领取后N天
    private Date validStart;
    private Date validEnd;
    private Integer validDays;
    private Integer status;
}
''')

w(f"{JAVA}/coupon/entity/UserCoupon.java", '''package com.mall.module.coupon.entity;

import lombok.Data;
import java.util.Date;

@Data
public class UserCoupon {
    private Long id;
    private Long userId;
    private Long couponId;
    private Integer status; // 0-未使用, 1-已使用, 2-已过期
    private Long orderId;
    private Date validStart;
    private Date validEnd;
    private Date receiveTime;
    private Date useTime;
    private Date createTime;
    private Date updateTime;
    // transient
    private CouponTemplate coupon;
}
''')

w(f"{JAVA}/coupon/mapper/CouponTemplateMapper.java", '''package com.mall.module.coupon.mapper;

import com.mall.module.coupon.entity.CouponTemplate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface CouponTemplateMapper {
    int insert(CouponTemplate coupon);
    int updateById(CouponTemplate coupon);
    int deleteById(Long id);
    CouponTemplate selectById(Long id);
    List<CouponTemplate> selectList(@Param("name") String name, @Param("status") Integer status, @Param("offset") int offset, @Param("limit") int limit);
    long count(@Param("name") String name, @Param("status") Integer status);
    int updateStatus(@Param("id") Long id, @Param("status") int status);
    int incrementIssued(@Param("id") Long id);
    List<CouponTemplate> selectAvailable();
}
''')

w(f"{JAVA}/coupon/mapper/UserCouponMapper.java", '''package com.mall.module.coupon.mapper;

import com.mall.module.coupon.entity.UserCoupon;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface UserCouponMapper {
    int insert(UserCoupon uc);
    int updateById(UserCoupon uc);
    UserCoupon selectById(Long id);
    List<UserCoupon> selectByUserId(@Param("userId") Long userId, @Param("status") Integer status);
    int countByUserAndCoupon(@Param("userId") Long userId, @Param("couponId") Long couponId);
    UserCoupon selectAvailableByUserAndCoupon(@Param("userId") Long userId, @Param("couponId") Long couponId);
    int updateUsed(@Param("id") Long id, @Param("orderId") Long orderId);
    List<UserCoupon> selectByUserIdWithDetail(@Param("userId") Long userId, @Param("status") Integer status);
}
''')

w(f"{XML}/CouponTemplateMapper.xml", '''<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.mall.module.coupon.mapper.CouponTemplateMapper">
    <resultMap id="BaseResultMap" type="com.mall.module.coupon.entity.CouponTemplate">
        <id column="id" property="id"/>
        <result column="name" property="name"/>
        <result column="type" property="type"/>
        <result column="face_value" property="faceValue"/>
        <result column="discount" property="discount"/>
        <result column="min_spend" property="minSpend"/>
        <result column="category_limit" property="categoryLimit"/>
        <result column="total_count" property="totalCount"/>
        <result column="issued_count" property="issuedCount"/>
        <result column="per_limit" property="perLimit"/>
        <result column="valid_type" property="validType"/>
        <result column="valid_start" property="validStart"/>
        <result column="valid_end" property="validEnd"/>
        <result column="valid_days" property="validDays"/>
        <result column="status" property="status"/>
        <result column="create_time" property="createTime"/>
        <result column="update_time" property="updateTime"/>
        <result column="deleted" property="deleted"/>
    </resultMap>
    <sql id="cols">id,name,type,face_value,discount,min_spend,category_limit,total_count,issued_count,per_limit,valid_type,valid_start,valid_end,valid_days,status,create_time,update_time,deleted</sql>
    <insert id="insert" parameterType="com.mall.module.coupon.entity.CouponTemplate" useGeneratedKeys="true" keyProperty="id">
        INSERT INTO coupon_template(name,type,face_value,discount,min_spend,category_limit,total_count,per_limit,valid_type,valid_start,valid_end,valid_days,status)
        VALUES(#{name},#{type},#{faceValue},#{discount},#{minSpend},#{categoryLimit},#{totalCount},#{perLimit},#{validType},#{validStart},#{validEnd},#{validDays},#{status})
    </insert>
    <update id="updateById" parameterType="com.mall.module.coupon.entity.CouponTemplate">
        UPDATE coupon_template<set>
            <if test="name!=null">name=#{name},</if>
            <if test="type!=null">type=#{type},</if>
            <if test="faceValue!=null">face_value=#{faceValue},</if>
            <if test="discount!=null">discount=#{discount},</if>
            <if test="minSpend!=null">min_spend=#{minSpend},</if>
            <if test="totalCount!=null">total_count=#{totalCount},</if>
            <if test="perLimit!=null">per_limit=#{perLimit},</if>
            <if test="validType!=null">valid_type=#{validType},</if>
            <if test="validStart!=null">valid_start=#{validStart},</if>
            <if test="validEnd!=null">valid_end=#{validEnd},</if>
            <if test="validDays!=null">valid_days=#{validDays},</if>
            <if test="status!=null">status=#{status},</if>
        </set>WHERE id=#{id} AND deleted=0
    </update>
    <update id="deleteById">UPDATE coupon_template SET deleted=1 WHERE id=#{id}</update>
    <select id="selectById" resultMap="BaseResultMap">SELECT <include refid="cols"/> FROM coupon_template WHERE id=#{id} AND deleted=0</select>
    <select id="selectList" resultMap="BaseResultMap">SELECT <include refid="cols"/> FROM coupon_template WHERE deleted=0<if test="name!=null and name!=''">AND name LIKE CONCAT('%',#{name},'%')</if><if test="status!=null">AND status=#{status}</if>ORDER BY create_time DESC LIMIT #{offset},#{limit}</select>
    <select id="count" resultType="long">SELECT COUNT(*) FROM coupon_template WHERE deleted=0<if test="name!=null and name!=''">AND name LIKE CONCAT('%',#{name},'%')</if><if test="status!=null">AND status=#{status}</if></select>
    <update id="updateStatus">UPDATE coupon_template SET status=#{status} WHERE id=#{id} AND deleted=0</update>
    <update id="incrementIssued">UPDATE coupon_template SET issued_count=issued_count+1 WHERE id=#{id} AND(total_count=-1 OR issued_count<total_count)</update>
    <select id="selectAvailable" resultMap="BaseResultMap">SELECT <include refid="cols"/> FROM coupon_template WHERE deleted=0 AND status=1 AND(total_count=-1 OR issued_count<total_count)ORDER BY create_time DESC</select>
</mapper>
''')

w(f"{XML}/UserCouponMapper.xml", '''<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.mall.module.coupon.mapper.UserCouponMapper">
    <resultMap id="BaseResultMap" type="com.mall.module.coupon.entity.UserCoupon">
        <id column="id" property="id"/>
        <result column="user_id" property="userId"/>
        <result column="coupon_id" property="couponId"/>
        <result column="status" property="status"/>
        <result column="order_id" property="orderId"/>
        <result column="valid_start" property="validStart"/>
        <result column="valid_end" property="validEnd"/>
        <result column="receive_time" property="receiveTime"/>
        <result column="use_time" property="useTime"/>
        <result column="create_time" property="createTime"/>
        <result column="update_time" property="updateTime"/>
    </resultMap>
    <resultMap id="DetailResultMap" type="com.mall.module.coupon.entity.UserCoupon" extends="BaseResultMap">
        <association property="coupon" javaType="com.mall.module.coupon.entity.CouponTemplate">
            <id column="c_id" property="id"/>
            <result column="c_name" property="name"/>
            <result column="c_type" property="type"/>
            <result column="c_face_value" property="faceValue"/>
            <result column="c_discount" property="discount"/>
            <result column="c_min_spend" property="minSpend"/>
        </association>
    </resultMap>
    <insert id="insert" parameterType="com.mall.module.coupon.entity.UserCoupon" useGeneratedKeys="true" keyProperty="id">
        INSERT INTO user_coupon(user_id,coupon_id,status,valid_start,valid_end) VALUES(#{userId},#{couponId},#{status},#{validStart},#{validEnd})
    </insert>
    <update id="updateById" parameterType="com.mall.module.coupon.entity.UserCoupon">
        UPDATE user_coupon<set><if test="status!=null">status=#{status},</if><if test="orderId!=null">order_id=#{orderId},</if><if test="useTime!=null">use_time=#{useTime},</if></set>WHERE id=#{id}
    </update>
    <select id="selectById" resultMap="BaseResultMap">SELECT * FROM user_coupon WHERE id=#{id}</select>
    <select id="selectByUserId" resultMap="BaseResultMap">SELECT * FROM user_coupon WHERE user_id=#{userId}<if test="status!=null">AND status=#{status}</if>ORDER BY receive_time DESC</select>
    <select id="countByUserAndCoupon" resultType="int">SELECT COUNT(*) FROM user_coupon WHERE user_id=#{userId} AND coupon_id=#{couponId}</select>
    <select id="selectAvailableByUserAndCoupon" resultMap="BaseResultMap">SELECT * FROM user_coupon WHERE user_id=#{userId} AND coupon_id=#{couponId} AND status=0 AND valid_end>=NOW() LIMIT 1</select>
    <update id="updateUsed">UPDATE user_coupon SET status=1,order_id=#{orderId},use_time=NOW() WHERE id=#{id} AND status=0</update>
    <select id="selectByUserIdWithDetail" resultMap="DetailResultMap">
        SELECT uc.*,c.id AS c_id,c.name AS c_name,c.type AS c_type,c.face_value AS c_face_value,c.discount AS c_discount,c.min_spend AS c_min_spend
        FROM user_coupon uc LEFT JOIN coupon_template c ON uc.coupon_id=c.id
        WHERE uc.user_id=#{userId}<if test="status!=null">AND uc.status=#{status}</if>
        ORDER BY uc.receive_time DESC
    </select>
</mapper>
''')

w(f"{JAVA}/coupon/service/CouponService.java", '''package com.mall.module.coupon.service;

import com.mall.common.PageResult;
import com.mall.common.exception.BusinessException;
import com.mall.module.coupon.entity.CouponTemplate;
import com.mall.module.coupon.entity.UserCoupon;
import com.mall.module.coupon.mapper.CouponTemplateMapper;
import com.mall.module.coupon.mapper.UserCouponMapper;
import com.mall.security.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class CouponService {
    @Autowired private CouponTemplateMapper templateMapper;
    @Autowired private UserCouponMapper userCouponMapper;

    // ===== Template CRUD =====
    public PageResult<CouponTemplate> list(String name, Integer status, int pageNum, int pageSize) {
        List<CouponTemplate> list = templateMapper.selectList(name, status, (pageNum-1)*pageSize, pageSize);
        long total = templateMapper.count(name, status);
        return PageResult.of(list, total, pageNum, pageSize);
    }

    public CouponTemplate getById(Long id) { return templateMapper.selectById(id); }

    @Transactional
    public void save(CouponTemplate coupon) {
        if (coupon.getId() == null) {
            coupon.setIssuedCount(0);
            if (coupon.getStatus() == null) coupon.setStatus(1);
            templateMapper.insert(coupon);
        } else {
            templateMapper.updateById(coupon);
        }
    }

    @Transactional
    public void delete(Long id) { templateMapper.deleteById(id); }

    @Transactional
    public void updateStatus(Long id, int status) { templateMapper.updateStatus(id, status); }

    public List<CouponTemplate> available() { return templateMapper.selectAvailable(); }

    // ===== User Coupon =====
    public List<UserCoupon> myCoupons(Integer status) {
        return userCouponMapper.selectByUserIdWithDetail(UserContext.require().getUserId(), status);
    }

    @Transactional
    public void receive(Long couponId) {
        Long userId = UserContext.require().getUserId();
        CouponTemplate coupon = templateMapper.selectById(couponId);
        if (coupon == null || coupon.getStatus() != 1) throw BusinessException.of("优惠券不存在或已停用");
        int count = userCouponMapper.countByUserAndCoupon(userId, couponId);
        if (count >= coupon.getPerLimit()) throw BusinessException.of("已超过每人限领数量");
        int rows = templateMapper.incrementIssued(couponId);
        if (rows == 0) throw BusinessException.of("优惠券已被抢光");

        UserCoupon uc = new UserCoupon();
        uc.setUserId(userId);
        uc.setCouponId(couponId);
        uc.setStatus(0);
        if (coupon.getValidType() == 1) {
            uc.setValidStart(coupon.getValidStart());
            uc.setValidEnd(coupon.getValidEnd());
        } else {
            Calendar cal = Calendar.getInstance();
            uc.setValidStart(cal.getTime());
            cal.add(Calendar.DAY_OF_MONTH, coupon.getValidDays());
            uc.setValidEnd(cal.getTime());
        }
        userCouponMapper.insert(uc);
    }

    public BigDecimal calculateDiscount(Long couponId, Long userId, BigDecimal totalAmount) {
        UserCoupon uc = userCouponMapper.selectAvailableByUserAndCoupon(userId, couponId);
        if (uc == null) return BigDecimal.ZERO;
        CouponTemplate coupon = templateMapper.selectById(couponId);
        if (coupon == null) return BigDecimal.ZERO;
        if (totalAmount.compareTo(coupon.getMinSpend()) < 0) return BigDecimal.ZERO;
        switch (coupon.getType()) {
            case 1: // 满减
                return coupon.getFaceValue();
            case 2: // 折扣
                return totalAmount.multiply(BigDecimal.ONE.subtract(coupon.getDiscount())).setScale(2, RoundingMode.HALF_UP);
            case 3: // 无门槛
                return coupon.getFaceValue();
            default:
                return BigDecimal.ZERO;
        }
    }

    @Transactional
    public void useCoupon(Long couponId, Long userId, Long orderId) {
        UserCoupon uc = userCouponMapper.selectAvailableByUserAndCoupon(userId, couponId);
        if (uc == null) throw BusinessException.of("优惠券不可用");
        userCouponMapper.updateUsed(uc.getId(), orderId);
    }
}
''')

w(f"{JAVA}/coupon/controller/CouponController.java", '''package com.mall.module.coupon.controller;

import com.mall.common.PageResult;
import com.mall.common.Result;
import com.mall.module.coupon.entity.CouponTemplate;
import com.mall.module.coupon.entity.UserCoupon;
import com.mall.module.coupon.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/coupon")
public class CouponController {
    @Autowired private CouponService couponService;

    // ===== User =====
    @GetMapping("/available")
    public Result<List<CouponTemplate>> available() { return Result.success(couponService.available()); }

    @GetMapping("/mine")
    public Result<List<UserCoupon>> mine(@RequestParam(required = false) Integer status) { return Result.success(couponService.myCoupons(status)); }

    @PostMapping("/receive/{couponId}")
    public Result<Void> receive(@PathVariable Long couponId) { couponService.receive(couponId); return Result.success(); }

    // ===== Admin =====
    @GetMapping("/list")
    public Result<PageResult<CouponTemplate>> list(@RequestParam(required = false) String name,
                                                    @RequestParam(required = false) Integer status,
                                                    @RequestParam(defaultValue = "1") int pageNum,
                                                    @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(couponService.list(name, status, pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public Result<CouponTemplate> detail(@PathVariable Long id) { return Result.success(couponService.getById(id)); }

    @PostMapping
    public Result<Void> save(@RequestBody CouponTemplate coupon) { couponService.save(coupon); return Result.success(); }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) { couponService.delete(id); return Result.success(); }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam int status) { couponService.updateStatus(id, status); return Result.success(); }
}
''')

# ============================================================
# Marketing Module
# ============================================================
print("=== Marketing Module ===")

w(f"{JAVA}/marketing/entity/MarketingActivity.java", '''package com.mall.module.marketing.entity;

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
''')

w(f"{JAVA}/marketing/entity/ActivityProduct.java", '''package com.mall.module.marketing.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class ActivityProduct {
    private Long id;
    private Long activityId;
    private Long productId;
    private Long skuId;
    private BigDecimal activityPrice;
    private Integer activityStock;
    private Integer limitPerUser;
    private Integer sort;
    private Date createTime;
    // transient
    private String productName;
    private String productImage;
}
''')

w(f"{JAVA}/marketing/mapper/MarketingActivityMapper.java", '''package com.mall.module.marketing.mapper;

import com.mall.module.marketing.entity.MarketingActivity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface MarketingActivityMapper {
    int insert(MarketingActivity activity);
    int updateById(MarketingActivity activity);
    int deleteById(Long id);
    MarketingActivity selectById(Long id);
    List<MarketingActivity> selectList(@Param("name") String name, @Param("type") String type, @Param("status") Integer status, @Param("offset") int offset, @Param("limit") int limit);
    long count(@Param("name") String name, @Param("type") String type, @Param("status") Integer status);
    int updateStatus(@Param("id") Long id, @Param("status") int status);
    List<MarketingActivity> selectActive(@Param("type") String type);
}
''')

w(f"{JAVA}/marketing/mapper/ActivityProductMapper.java", '''package com.mall.module.marketing.mapper;

import com.mall.module.marketing.entity.ActivityProduct;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ActivityProductMapper {
    int insert(ActivityProduct ap);
    int batchInsert(@Param("list") List<ActivityProduct> list);
    int deleteByActivityId(@Param("activityId") Long activityId);
    List<ActivityProduct> selectByActivityId(@Param("activityId") Long activityId);
    List<ActivityProduct> selectByProductId(@Param("productId") Long productId);
}
''')

w(f"{XML}/MarketingActivityMapper.xml", '''<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.mall.module.marketing.mapper.MarketingActivityMapper">
    <resultMap id="BaseResultMap" type="com.mall.module.marketing.entity.MarketingActivity">
        <id column="id" property="id"/>
        <result column="name" property="name"/>
        <result column="type" property="type"/>
        <result column="description" property="description"/>
        <result column="start_time" property="startTime"/>
        <result column="end_time" property="endTime"/>
        <result column="status" property="status"/>
        <result column="enabled" property="enabled"/>
        <result column="rules" property="rules"/>
        <result column="create_time" property="createTime"/>
        <result column="update_time" property="updateTime"/>
        <result column="deleted" property="deleted"/>
    </resultMap>
    <sql id="cols">id,name,type,description,start_time,end_time,status,enabled,rules,create_time,update_time,deleted</sql>
    <insert id="insert" parameterType="com.mall.module.marketing.entity.MarketingActivity" useGeneratedKeys="true" keyProperty="id">
        INSERT INTO marketing_activity(name,type,description,start_time,end_time,status,enabled,rules)
        VALUES(#{name},#{type},#{description},#{startTime},#{endTime},#{status},#{enabled},#{rules})
    </insert>
    <update id="updateById" parameterType="com.mall.module.marketing.entity.MarketingActivity">
        UPDATE marketing_activity<set>
            <if test="name!=null">name=#{name},</if>
            <if test="type!=null">type=#{type},</if>
            <if test="description!=null">description=#{description},</if>
            <if test="startTime!=null">start_time=#{startTime},</if>
            <if test="endTime!=null">end_time=#{endTime},</if>
            <if test="status!=null">status=#{status},</if>
            <if test="enabled!=null">enabled=#{enabled},</if>
            <if test="rules!=null">rules=#{rules},</if>
        </set>WHERE id=#{id} AND deleted=0
    </update>
    <update id="deleteById">UPDATE marketing_activity SET deleted=1 WHERE id=#{id}</update>
    <select id="selectById" resultMap="BaseResultMap">SELECT <include refid="cols"/> FROM marketing_activity WHERE id=#{id} AND deleted=0</select>
    <select id="selectList" resultMap="BaseResultMap">SELECT <include refid="cols"/> FROM marketing_activity WHERE deleted=0<if test="name!=null and name!=''">AND name LIKE CONCAT('%',#{name},'%')</if><if test="type!=null and type!=''">AND type=#{type}</if><if test="status!=null">AND status=#{status}</if>ORDER BY create_time DESC LIMIT #{offset},#{limit}</select>
    <select id="count" resultType="long">SELECT COUNT(*) FROM marketing_activity WHERE deleted=0<if test="name!=null and name!=''">AND name LIKE CONCAT('%',#{name},'%')</if><if test="type!=null and type!=''">AND type=#{type}</if><if test="status!=null">AND status=#{status}</if></select>
    <update id="updateStatus">UPDATE marketing_activity SET status=#{status} WHERE id=#{id} AND deleted=0</update>
    <select id="selectActive" resultMap="BaseResultMap">SELECT <include refid="cols"/> FROM marketing_activity WHERE deleted=0 AND enabled=1 AND status=1 AND start_time<=NOW() AND end_time>=NOW()<if test="type!=null and type!=''">AND type=#{type}</if>ORDER BY start_time ASC</select>
</mapper>
''')

w(f"{XML}/ActivityProductMapper.xml", '''<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.mall.module.marketing.mapper.ActivityProductMapper">
    <resultMap id="BaseResultMap" type="com.mall.module.marketing.entity.ActivityProduct">
        <id column="id" property="id"/>
        <result column="activity_id" property="activityId"/>
        <result column="product_id" property="productId"/>
        <result column="sku_id" property="skuId"/>
        <result column="activity_price" property="activityPrice"/>
        <result column="activity_stock" property="activityStock"/>
        <result column="limit_per_user" property="limitPerUser"/>
        <result column="sort" property="sort"/>
        <result column="create_time" property="createTime"/>
        <result column="product_name" property="productName"/>
        <result column="product_image" property="productImage"/>
    </resultMap>
    <insert id="insert" parameterType="com.mall.module.marketing.entity.ActivityProduct" useGeneratedKeys="true" keyProperty="id">
        INSERT INTO activity_product(activity_id,product_id,sku_id,activity_price,activity_stock,limit_per_user,sort)
        VALUES(#{activityId},#{productId},#{skuId},#{activityPrice},#{activityStock},#{limitPerUser},#{sort})
    </insert>
    <insert id="batchInsert">
        INSERT INTO activity_product(activity_id,product_id,sku_id,activity_price,activity_stock,limit_per_user,sort) VALUES
        <foreach collection="list" item="item" separator=",">(#{item.activityId},#{item.productId},#{item.skuId},#{item.activityPrice},#{item.activityStock},#{item.limitPerUser},#{item.sort})</foreach>
    </insert>
    <delete id="deleteByActivityId">DELETE FROM activity_product WHERE activity_id=#{activityId}</delete>
    <select id="selectByActivityId" resultMap="BaseResultMap">
        SELECT ap.*,p.name AS product_name,p.main_image AS product_image
        FROM activity_product ap LEFT JOIN product p ON ap.product_id=p.id
        WHERE ap.activity_id=#{activityId} ORDER BY ap.sort
    </select>
    <select id="selectByProductId" resultMap="BaseResultMap">
        SELECT ap.*,p.name AS product_name,p.main_image AS product_image
        FROM activity_product ap LEFT JOIN product p ON ap.product_id=p.id
        WHERE ap.product_id=#{productId}
    </select>
</mapper>
''')

w(f"{JAVA}/marketing/service/MarketingService.java", '''package com.mall.module.marketing.service;

import com.mall.common.PageResult;
import com.mall.common.exception.BusinessException;
import com.mall.module.marketing.entity.ActivityProduct;
import com.mall.module.marketing.entity.MarketingActivity;
import com.mall.module.marketing.mapper.ActivityProductMapper;
import com.mall.module.marketing.mapper.MarketingActivityMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;
import java.util.List;

@Service
public class MarketingService {
    @Autowired private MarketingActivityMapper activityMapper;
    @Autowired private ActivityProductMapper apMapper;

    public PageResult<MarketingActivity> list(String name, String type, Integer status, int pageNum, int pageSize) {
        List<MarketingActivity> list = activityMapper.selectList(name, type, status, (pageNum-1)*pageSize, pageSize);
        long total = activityMapper.count(name, type, status);
        return PageResult.of(list, total, pageNum, pageSize);
    }

    public MarketingActivity getById(Long id) {
        MarketingActivity activity = activityMapper.selectById(id);
        if (activity != null) activity.setProducts(apMapper.selectByActivityId(id));
        return activity;
    }

    @Transactional
    public void save(MarketingActivity activity) {
        // auto-calc status
        Date now = new Date();
        if (activity.getStartTime().after(now)) activity.setStatus(0);
        else if (activity.getEndTime().before(now)) activity.setStatus(2);
        else activity.setStatus(1);

        if (activity.getEnabled() == null) activity.setEnabled(1);

        if (activity.getId() == null) {
            activityMapper.insert(activity);
        } else {
            activityMapper.updateById(activity);
            apMapper.deleteByActivityId(activity.getId());
        }
        if (activity.getProducts() != null && !activity.getProducts().isEmpty()) {
            for (ActivityProduct ap : activity.getProducts()) {
                ap.setActivityId(activity.getId());
            }
            apMapper.batchInsert(activity.getProducts());
        }
    }

    @Transactional
    public void delete(Long id) { activityMapper.deleteById(id); }

    @Transactional
    public void updateStatus(Long id, int status) { activityMapper.updateStatus(id, status); }

    public List<MarketingActivity> activeList(String type) {
        List<MarketingActivity> list = activityMapper.selectActive(type);
        for (MarketingActivity a : list) a.setProducts(apMapper.selectByActivityId(a.getId()));
        return list;
    }

    public List<ActivityProduct> getActivityProducts(Long activityId) {
        return apMapper.selectByActivityId(activityId);
    }
}
''')

w(f"{JAVA}/marketing/controller/MarketingController.java", '''package com.mall.module.marketing.controller;

import com.mall.common.PageResult;
import com.mall.common.Result;
import com.mall.module.marketing.entity.ActivityProduct;
import com.mall.module.marketing.entity.MarketingActivity;
import com.mall.module.marketing.service.MarketingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/marketing")
public class MarketingController {
    @Autowired private MarketingService marketingService;

    // ===== Public =====
    @GetMapping("/active")
    public Result<List<MarketingActivity>> active(@RequestParam(required = false) String type) {
        return Result.success(marketingService.activeList(type));
    }

    @GetMapping("/activity/{id}/products")
    public Result<List<ActivityProduct>> products(@PathVariable Long id) {
        return Result.success(marketingService.getActivityProducts(id));
    }

    // ===== Admin =====
    @GetMapping("/list")
    public Result<PageResult<MarketingActivity>> list(@RequestParam(required = false) String name,
                                                       @RequestParam(required = false) String type,
                                                       @RequestParam(required = false) Integer status,
                                                       @RequestParam(defaultValue = "1") int pageNum,
                                                       @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(marketingService.list(name, type, status, pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public Result<MarketingActivity> detail(@PathVariable Long id) { return Result.success(marketingService.getById(id)); }

    @PostMapping
    public Result<Void> save(@RequestBody MarketingActivity activity) { marketingService.save(activity); return Result.success(); }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) { marketingService.delete(id); return Result.success(); }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam int status) { marketingService.updateStatus(id, status); return Result.success(); }
}
''')

print("\n=== All business modules generated! ===")
