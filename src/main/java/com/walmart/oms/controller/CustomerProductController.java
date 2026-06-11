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
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        IPage<Product> page = productService.searchProducts(keyword, categoryId, pageNum, pageSize);
        return R.success(page);
    }

    @GetMapping("/{id:\\d+}")
    @Operation(summary = "商品详情")
    public R<Product> detail(@PathVariable Long id) {
        Product product = productService.getById(id);
        if (product == null) {
            return R.error("商品不存在");
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
}
