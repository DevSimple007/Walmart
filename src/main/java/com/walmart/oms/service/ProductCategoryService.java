package com.walmart.oms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.walmart.oms.entity.ProductCategory;

import java.util.List;

/**
 * 商品分类服务接口
 */
public interface ProductCategoryService extends IService<ProductCategory> {
    /**
     * 构建分类树
     * @param categories 分类列表
     * @return 分类树
     */
    List<ProductCategory> buildCategoryTree(List<ProductCategory> categories);

    /**
     * 获取所有分类树
     * @return 分类树
     */
    List<ProductCategory> getAllCategoryTree();
}