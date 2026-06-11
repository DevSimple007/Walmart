package com.walmart.oms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.walmart.oms.entity.Favorite;

import java.util.List;

public interface FavoriteService extends IService<Favorite> {
    List<Favorite> getFavorites(Long memberId);
    void addFavorite(Long memberId, Long productId);
    void removeFavorite(Long memberId, Long productId);
    boolean isFavorite(Long memberId, Long productId);
}
