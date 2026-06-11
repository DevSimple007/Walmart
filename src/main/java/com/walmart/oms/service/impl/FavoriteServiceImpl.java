package com.walmart.oms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.walmart.oms.entity.Favorite;
import com.walmart.oms.entity.Product;
import com.walmart.oms.exception.BusinessException;
import com.walmart.oms.mapper.FavoriteMapper;
import com.walmart.oms.mapper.ProductMapper;
import com.walmart.oms.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FavoriteServiceImpl extends ServiceImpl<FavoriteMapper, Favorite> implements FavoriteService {

    @Autowired
    private ProductMapper productMapper;

    @Override
    public List<Favorite> getFavorites(Long memberId) {
        return baseMapper.selectFavoritesWithProduct(memberId);
    }

    @Override
    public void addFavorite(Long memberId, Long productId) {
        Product product = productMapper.selectById(productId);
        if (product == null || product.getStatus() != 1) {
            throw new BusinessException("商品不存在或已下架");
        }
        
        // 检查是否已收藏
        Favorite existing = this.getOne(new QueryWrapper<Favorite>()
                .eq("member_id", memberId).eq("product_id", productId));
        if (existing != null) {
            throw new BusinessException("您已收藏该商品");
        }
        
        Favorite favorite = new Favorite();
        favorite.setMemberId(memberId);
        favorite.setProductId(productId);
        favorite.setCreateTime(LocalDateTime.now());
        this.save(favorite);
    }

    @Override
    public void removeFavorite(Long memberId, Long productId) {
        this.remove(new QueryWrapper<Favorite>()
                .eq("member_id", memberId).eq("product_id", productId));
    }

    @Override
    public boolean isFavorite(Long memberId, Long productId) {
        return this.count(new QueryWrapper<Favorite>()
                .eq("member_id", memberId).eq("product_id", productId)) > 0;
    }
}
