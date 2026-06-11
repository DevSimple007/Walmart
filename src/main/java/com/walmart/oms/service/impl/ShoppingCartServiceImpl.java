package com.walmart.oms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.walmart.oms.entity.Product;
import com.walmart.oms.entity.ShoppingCart;
import com.walmart.oms.exception.BusinessException;
import com.walmart.oms.mapper.ProductMapper;
import com.walmart.oms.mapper.ShoppingCartMapper;
import com.walmart.oms.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {

    @Autowired
    private ProductMapper productMapper;

    @Override
    public List<ShoppingCart> getCartItems(Long memberId) {
        return baseMapper.selectCartWithProduct(memberId);
    }

    @Override
    public void addToCart(Long memberId, Long productId, Integer quantity) {
        Product product = productMapper.selectById(productId);
        if (product == null || product.getStatus() != 1) {
            throw new BusinessException("商品不存在或已下架");
        }

        ShoppingCart existing = this.getOne(new QueryWrapper<ShoppingCart>()
                .eq("member_id", memberId).eq("product_id", productId));

        if (existing != null) {
            int newQty = existing.getQuantity() + quantity;
            if (newQty > product.getStock()) {
                throw new BusinessException("库存不足，当前库存：" + product.getStock());
            }
            existing.setQuantity(newQty);
            this.updateById(existing);
        } else {
            if (quantity > product.getStock()) {
                throw new BusinessException("库存不足，当前库存：" + product.getStock());
            }
            ShoppingCart cart = new ShoppingCart();
            cart.setMemberId(memberId);
            cart.setProductId(productId);
            cart.setQuantity(quantity);
            this.save(cart);
        }
    }

    @Override
    public void updateQuantity(Long memberId, Long productId, Integer quantity) {
        if (quantity <= 0) {
            removeFromCart(memberId, productId);
            return;
        }
        Product product = productMapper.selectById(productId);
        if (product != null && quantity > product.getStock()) {
            throw new BusinessException("库存不足，当前库存：" + product.getStock());
        }
        ShoppingCart cart = this.getOne(new QueryWrapper<ShoppingCart>()
                .eq("member_id", memberId).eq("product_id", productId));
        if (cart != null) {
            cart.setQuantity(quantity);
            this.updateById(cart);
        }
    }

    @Override
    public void removeFromCart(Long memberId, Long productId) {
        this.remove(new QueryWrapper<ShoppingCart>()
                .eq("member_id", memberId).eq("product_id", productId));
    }

    @Override
    public void clearCart(Long memberId) {
        this.remove(new QueryWrapper<ShoppingCart>().eq("member_id", memberId));
    }
}
