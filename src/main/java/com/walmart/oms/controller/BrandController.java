package com.walmart.oms.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.walmart.oms.entity.Brand;
import com.walmart.oms.service.BrandService;
import com.walmart.oms.utils.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 品牌管理控制器
 */
@RestController
@RequestMapping("/product/brand")
@Tag(name = "品牌管理", description = "品牌相关接口")
public class BrandController {

    @Autowired
    private BrandService brandService;

    @GetMapping("/list")
    @Operation(summary = "获取品牌列表", description = "分页获取品牌列表")
    public R<IPage<Brand>> list(@RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<Brand> page = new Page<>(pageNum, pageSize);
        IPage<Brand> result = brandService.page(page);
        return R.success(result);
    }

    @PostMapping("/add")
    @Operation(summary = "添加品牌", description = "添加新品牌")
    public R<?> add(@RequestBody Brand brand) {
        brandService.save(brand);
        return R.success();
    }

    @PutMapping("/update")
    @Operation(summary = "更新品牌", description = "更新品牌信息")
    public R<?> update(@RequestBody Brand brand) {
        brandService.updateById(brand);
        return R.success();
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "删除品牌", description = "根据ID删除品牌")
    public R<?> delete(@PathVariable Long id) {
        brandService.removeById(id);
        return R.success();
    }

    @GetMapping("/get/{id}")
    @Operation(summary = "获取品牌详情", description = "根据ID获取品牌详情")
    public R<Brand> get(@PathVariable Long id) {
        Brand brand = brandService.getById(id);
        return R.success(brand);
    }
}