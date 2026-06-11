package com.walmart.oms.controller;

import com.walmart.oms.entity.Favorite;
import com.walmart.oms.service.FavoriteService;
import com.walmart.oms.utils.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customer/favorite")
@Tag(name = "顾客收藏", description = "顾客端收藏接口")
public class CustomerFavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    @GetMapping("/list")
    @Operation(summary = "我的收藏列表")
    public R<List<Favorite>> list(HttpServletRequest request) {
        Long memberId = (Long) request.getAttribute("userId");
        List<Favorite> favorites = favoriteService.getFavorites(memberId);
        return R.success(favorites);
    }

    @PostMapping("/add/{productId}")
    @Operation(summary = "添加收藏")
    public R<?> add(HttpServletRequest request, @PathVariable Long productId) {
        Long memberId = (Long) request.getAttribute("userId");
        favoriteService.addFavorite(memberId, productId);
        return R.ok();
    }

    @DeleteMapping("/remove/{productId}")
    @Operation(summary = "取消收藏")
    public R<?> remove(HttpServletRequest request, @PathVariable Long productId) {
        Long memberId = (Long) request.getAttribute("userId");
        favoriteService.removeFavorite(memberId, productId);
        return R.ok();
    }

    @GetMapping("/check/{productId}")
    @Operation(summary = "检查是否已收藏")
    public R<Boolean> check(HttpServletRequest request, @PathVariable Long productId) {
        Long memberId = (Long) request.getAttribute("userId");
        boolean isFavorite = favoriteService.isFavorite(memberId, productId);
        return R.success(isFavorite);
    }
}
