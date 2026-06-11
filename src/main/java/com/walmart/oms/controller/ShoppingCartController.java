package com.walmart.oms.controller;

import com.walmart.oms.entity.ShoppingCart;
import com.walmart.oms.service.ShoppingCartService;
import com.walmart.oms.utils.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customer/cart")
@Tag(name = "购物车", description = "顾客购物车接口")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Data
    public static class CartRequest {
        private Long productId;
        private Integer quantity;
    }

    @GetMapping("/list")
    @Operation(summary = "获取购物车列表")
    public R<List<ShoppingCart>> list(HttpServletRequest request) {
        Long memberId = (Long) request.getAttribute("userId");
        List<ShoppingCart> items = shoppingCartService.getCartItems(memberId);
        return R.success(items);
    }

    @PostMapping("/add")
    @Operation(summary = "添加到购物车")
    public R<?> add(HttpServletRequest request, @RequestBody CartRequest cartRequest) {
        Long memberId = (Long) request.getAttribute("userId");
        shoppingCartService.addToCart(memberId, cartRequest.getProductId(), cartRequest.getQuantity());
        return R.ok();
    }

    @PutMapping("/update")
    @Operation(summary = "更新购物车数量")
    public R<?> update(HttpServletRequest request, @RequestBody CartRequest cartRequest) {
        Long memberId = (Long) request.getAttribute("userId");
        shoppingCartService.updateQuantity(memberId, cartRequest.getProductId(), cartRequest.getQuantity());
        return R.ok();
    }

    @DeleteMapping("/remove/{productId}")
    @Operation(summary = "移除购物车商品")
    public R<?> remove(HttpServletRequest request, @PathVariable Long productId) {
        Long memberId = (Long) request.getAttribute("userId");
        shoppingCartService.removeFromCart(memberId, productId);
        return R.ok();
    }

    @DeleteMapping("/clear")
    @Operation(summary = "清空购物车")
    public R<?> clear(HttpServletRequest request) {
        Long memberId = (Long) request.getAttribute("userId");
        shoppingCartService.clearCart(memberId);
        return R.ok();
    }
}
