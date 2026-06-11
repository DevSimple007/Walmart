package com.walmart.oms.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.walmart.oms.entity.Product;
import com.walmart.oms.mapper.ProductMapper;
import com.walmart.oms.service.ProductService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 商品服务实现类
 */
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    @Override
    public IPage<Product> getProductPage(Integer pageNum, Integer pageSize) {
        Page<Product> page = new Page<>(pageNum, pageSize);
        return baseMapper.selectProductPage(page);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        Product product = new Product();
        product.setId(id);
        product.setStatus(status);
        updateById(product);
    }

    @Override
    public IPage<Product> searchProducts(String keyword, Long categoryId, Integer pageNum, Integer pageSize) {
        Page<Product> page = new Page<>(pageNum, pageSize);
        return baseMapper.selectProductPageWithFilter(page, keyword, categoryId);
    }

    @Override
    public List<Product> getHotProducts() {
        return baseMapper.selectHotProducts();
    }

    @Override
    public List<Product> getLatestProducts() {
        return baseMapper.selectLatestProducts();
    }
}