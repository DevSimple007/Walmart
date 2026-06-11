package com.walmart.oms.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.walmart.oms.entity.ProductCategory;
import com.walmart.oms.mapper.ProductCategoryMapper;
import com.walmart.oms.service.ProductCategoryService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 商品分类服务实现类
 */
@Service
public class ProductCategoryServiceImpl extends ServiceImpl<ProductCategoryMapper, ProductCategory> implements ProductCategoryService {

    @Override
    public List<ProductCategory> buildCategoryTree(List<ProductCategory> categories) {
        // 构建分类树
        List<ProductCategory> categoryTree = new ArrayList<>();
        // 先找出所有的根分类（parentId为0）
        List<ProductCategory> rootCategories = categories.stream()
                .filter(category -> category.getParentId() == 0)
                .collect(Collectors.toList());

        // 为每个根分类递归构建子分类
        for (ProductCategory rootCategory : rootCategories) {
            buildChildren(rootCategory, categories);
            categoryTree.add(rootCategory);
        }

        return categoryTree;
    }

    @Override
    public List<ProductCategory> getAllCategoryTree() {
        List<ProductCategory> categories = list();
        return buildCategoryTree(categories);
    }

    /**
     * 递归构建子分类
     * @param parentCategory 父分类
     * @param categories 所有分类列表
     */
    private void buildChildren(ProductCategory parentCategory, List<ProductCategory> categories) {
        List<ProductCategory> children = categories.stream()
                .filter(category -> category.getParentId().equals(parentCategory.getId()))
                .collect(Collectors.toList());

        if (!children.isEmpty()) {
            parentCategory.setChildren(children);
            for (ProductCategory child : children) {
                buildChildren(child, categories);
            }
        }
    }
}