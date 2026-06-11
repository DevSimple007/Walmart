package com.walmart.oms.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.walmart.oms.entity.Product;

import java.util.List;

/**
 * 商品服务接口
 */
public interface ProductService extends IService<Product> {
    /**
     * 分页查询商品列表
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 商品列表
     */
    IPage<Product> getProductPage(Integer pageNum, Integer pageSize);

    /**
     * 商品上下架
     * @param id 商品ID
     * @param status 状态：1-在售，0-下架
     */
    void updateStatus(Long id, Integer status);

    /**
     * 搜索商品（按关键词和分类筛选）
     */
    IPage<Product> searchProducts(String keyword, Long categoryId, Integer pageNum, Integer pageSize);

    /**
     * 获取热门商品
     */
    List<Product> getHotProducts();

    /**
     * 获取最新商品
     */
    List<Product> getLatestProducts();
}