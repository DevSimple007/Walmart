package com.walmart.oms.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.walmart.oms.entity.Product;
import com.walmart.oms.service.ProductService;
import com.walmart.oms.utils.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * 商品管理控制器
 */
@RestController
@RequestMapping("/product/list")
@Tag(name = "商品管理", description = "商品相关接口")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/list")
    @Operation(summary = "获取商品列表", description = "分页获取商品列表，支持名称/分类/国际品牌/折扣价筛选")
    public R<IPage<Product>> list(
            @RequestParam(required = false) String productName,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Integer isInternational,
            @RequestParam(required = false) BigDecimal minOriginalPrice,
            @RequestParam(required = false) BigDecimal maxOriginalPrice,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        IPage<Product> result = productService.getProductPage(
                productName, categoryId, isInternational, minOriginalPrice, maxOriginalPrice, pageNum, pageSize);
        return R.success(result);
    }

    @PostMapping("/add")
    @Operation(summary = "添加商品", description = "添加新商品")
    public R<?> add(@RequestBody Product product) {
        productService.save(product);
        return R.success();
    }

    @PutMapping("/update")
    @Operation(summary = "更新商品", description = "更新商品信息")
    public R<?> update(@RequestBody Product product) {
        productService.updateById(product);
        return R.success();
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "删除商品", description = "根据ID删除商品")
    public R<?> delete(@PathVariable Long id) {
        productService.removeById(id);
        return R.success();
    }

    @GetMapping("/get/{id}")
    @Operation(summary = "获取商品详情", description = "根据ID获取商品详情")
    public R<Product> get(@PathVariable Long id) {
        Product product = productService.getById(id);
        return R.success(product);
    }

    @PutMapping("/updateStatus/{id}")
    @Operation(summary = "商品上下架", description = "更新商品状态")
    public R<?> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        productService.updateStatus(id, status);
        return R.success();
    }
}