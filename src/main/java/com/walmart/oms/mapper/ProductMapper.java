package com.walmart.oms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.walmart.oms.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * 商品Mapper接口
 */
@Mapper
public interface ProductMapper extends BaseMapper<Product> {
    /**
     * 分页查询商品列表，包含分类名称（支持名称/分类/国际品牌/折扣价筛选）
     * @param page 分页参数
     * @return 商品列表
     */
    IPage<Product> selectProductPage(Page<Product> page,
                                     @Param("productName") String productName,
                                     @Param("categoryId") Long categoryId,
                                     @Param("isInternational") Integer isInternational,
                                     @Param("minOriginalPrice") BigDecimal minOriginalPrice,
                                     @Param("maxOriginalPrice") BigDecimal maxOriginalPrice);

    /**
     * 搜索商品（按关键词和分类筛选，支持排序/价格区间/折扣/国际品牌）
     */
    IPage<Product> selectProductPageWithFilter(Page<Product> page,
                                               @Param("keyword") String keyword,
                                               @Param("categoryId") Long categoryId,
                                               @Param("sort") String sort,
                                               @Param("minPrice") BigDecimal minPrice,
                                               @Param("maxPrice") BigDecimal maxPrice,
                                               @Param("discountOnly") Boolean discountOnly,
                                               @Param("internationalOnly") Boolean internationalOnly);

    /**
     * 查询热门商品
     */
    List<Product> selectHotProducts();

    /**
     * 查询最新商品
     */
    List<Product> selectLatestProducts();

    /**
     * 查询折扣商品（原价大于现价，按折扣力度排序）
     * @param limit 返回条数
     * @return 折扣商品列表
     */
    List<Product> selectDiscountProducts(@Param("limit") Integer limit);

    /**
     * 查询热销榜（按真实成交量排序）
     * @param limit 返回条数
     * @return 热销商品列表
     */
    List<Product> selectBestSellers(@Param("limit") Integer limit);
}