package com.walmart.oms.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.walmart.oms.entity.ProductCategory;
import com.walmart.oms.service.ProductCategoryService;
import com.walmart.oms.utils.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品分类管理控制器
 */
@RestController
@RequestMapping("/product/category")
@Tag(name = "商品分类管理", description = "商品分类相关接口")
public class ProductCategoryController {

    @Autowired
    private ProductCategoryService productCategoryService;

    @GetMapping("/list")
    @Operation(summary = "获取商品分类列表", description = "分页获取商品分类列表")
    public R<IPage<ProductCategory>> list(@RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<ProductCategory> page = new Page<>(pageNum, pageSize);
        IPage<ProductCategory> result = productCategoryService.page(page);
        return R.success(result);
    }

    @GetMapping("/tree")
    @Operation(summary = "获取商品分类树", description = "获取商品分类树结构")
    public R<List<ProductCategory>> tree() {
        List<ProductCategory> categoryTree = productCategoryService.getAllCategoryTree();
        return R.success(categoryTree);
    }

    @PostMapping("/add")
    @Operation(summary = "添加商品分类", description = "添加新商品分类")
    public R<?> add(@RequestBody ProductCategory category) {
        productCategoryService.save(category);
        return R.success();
    }

    @PutMapping("/update")
    @Operation(summary = "更新商品分类", description = "更新商品分类信息")
    public R<?> update(@RequestBody ProductCategory category) {
        productCategoryService.updateById(category);
        return R.success();
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "删除商品分类", description = "根据ID删除商品分类")
    public R<?> delete(@PathVariable Long id) {
        productCategoryService.removeById(id);
        return R.success();
    }

    @GetMapping("/get/{id}")
    @Operation(summary = "获取商品分类详情", description = "根据ID获取商品分类详情")
    public R<ProductCategory> get(@PathVariable Long id) {
        ProductCategory category = productCategoryService.getById(id);
        return R.success(category);
    }
}