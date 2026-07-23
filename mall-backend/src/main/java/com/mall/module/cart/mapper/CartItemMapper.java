package com.mall.module.cart.mapper;

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
