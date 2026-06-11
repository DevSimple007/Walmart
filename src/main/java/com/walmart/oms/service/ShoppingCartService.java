package com.walmart.oms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.walmart.oms.entity.ShoppingCart;

import java.util.List;

public interface ShoppingCartService extends IService<ShoppingCart> {
    List<ShoppingCart> getCartItems(Long memberId);
    void addToCart(Long memberId, Long productId, Integer quantity);
    void updateQuantity(Long memberId, Long productId, Integer quantity);
    void removeFromCart(Long memberId, Long productId);
    void clearCart(Long memberId);
}
