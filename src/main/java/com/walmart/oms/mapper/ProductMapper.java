package com.walmart.oms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.walmart.oms.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品Mapper接口
 */
@Mapper
public interface ProductMapper extends BaseMapper<Product> {
    /**
     * 分页查询商品列表，包含分类名称
     * @param page 分页参数
     * @return 商品列表
     */
    IPage<Product> selectProductPage(Page<Product> page);

    /**
     * 搜索商品（按关键词和分类筛选）
     */
    IPage<Product> selectProductPageWithFilter(Page<Product> page, @Param("keyword") String keyword, @Param("categoryId") Long categoryId);

    /**
     * 查询热门商品
     */
    List<Product> selectHotProducts();

    /**
     * 查询最新商品
     */
    List<Product> selectLatestProducts();
}