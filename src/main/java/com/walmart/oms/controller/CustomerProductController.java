package com.walmart.oms.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.walmart.oms.entity.Product;
import com.walmart.oms.entity.ProductCategory;
import com.walmart.oms.service.ProductCategoryService;
import com.walmart.oms.service.ProductService;
import com.walmart.oms.utils.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/customer/product")
@Tag(name = "顾客商品", description = "顾客端商品查询接口")
public class CustomerProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductCategoryService productCategoryService;

    @GetMapping("/list")
    @Operation(summary = "搜索商品列表")
    public R<IPage<Product>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Boolean discountOnly,
            @RequestParam(required = false) Boolean internationalOnly,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        IPage<Product> page = productService.searchProducts(keyword, categoryId, pageNum, pageSize,
                sort, minPrice, maxPrice, discountOnly, internationalOnly);
        return R.success(page);
    }

    @GetMapping("/{id:\\d+}")
    @Operation(summary = "商品详情")
    public R<Product> detail(@PathVariable Long id) {
        Product product = productService.getById(id);
        if (product == null) {
            return R.error("商品不存在");
        }
        // 补充分类名称（详情页需要按商品分类动态展示内容）
        if (product.getCategoryId() != null) {
            ProductCategory category = productCategoryService.getById(product.getCategoryId());
            if (category != null) {
                product.setCategoryName(category.getCategoryName());
            }
        }
        return R.success(product);
    }

    @GetMapping("/category/list")
    @Operation(summary = "商品分类列表")
    public R<List<ProductCategory>> categoryList() {
        List<ProductCategory> tree = productCategoryService.getAllCategoryTree();
        return R.success(tree);
    }

    @GetMapping("/hot")
    @Operation(summary = "热门商品")
    public R<List<Product>> hot() {
        return R.success(productService.getHotProducts());
    }

    @GetMapping("/latest")
    @Operation(summary = "最新商品")
    public R<List<Product>> latest() {
        return R.success(productService.getLatestProducts());
    }

    @GetMapping("/discount")
    @Operation(summary = "折扣商品（大牌低价）")
    public R<List<Product>> discount(@RequestParam(defaultValue = "10") Integer limit) {
        return R.success(productService.getDiscountProducts(limit));
    }

    @GetMapping("/best-sellers")
    @Operation(summary = "热销榜单（按真实成交量）")
    public R<List<Product>> bestSellers(@RequestParam(defaultValue = "10") Integer limit) {
        return R.success(productService.getBestSellers(limit));
    }
}
