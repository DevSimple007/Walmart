package com.walmart.oms.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.walmart.oms.entity.HomeCategory;
import com.walmart.oms.service.HomeCategoryService;
import com.walmart.oms.utils.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/homeCategory")
@Tag(name = "首页分类图标管理")
public class HomeCategoryController {

    @Autowired
    private HomeCategoryService homeCategoryService;

    @GetMapping("/list")
    @Operation(summary = "首页分类图标列表")
    public R list() {
        List<HomeCategory> list = homeCategoryService.list(new QueryWrapper<HomeCategory>().orderByAsc("sort"));
        return R.ok().put("data", list);
    }

    @PostMapping("/add")
    @Operation(summary = "添加首页分类图标")
    public R add(@RequestBody HomeCategory homeCategory) {
        homeCategoryService.save(homeCategory);
        return R.ok();
    }

    @PutMapping("/update")
    @Operation(summary = "更新首页分类图标")
    public R update(@RequestBody HomeCategory homeCategory) {
        homeCategoryService.updateById(homeCategory);
        return R.ok();
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "删除首页分类图标")
    public R delete(@PathVariable Long id) {
        homeCategoryService.removeById(id);
        return R.ok();
    }

    @GetMapping("/customer/list")
    @Operation(summary = "顾客端首页分类列表")
    public R customerList() {
        List<HomeCategory> list = homeCategoryService.list(
            new QueryWrapper<HomeCategory>().eq("status", 1).orderByAsc("sort")
        );
        return R.success(list);
    }
}
