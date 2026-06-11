package com.walmart.oms.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.walmart.oms.entity.SysMenu;
import com.walmart.oms.service.SysMenuService;
import com.walmart.oms.utils.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜单管理控制器
 */
@RestController
@RequestMapping("/system/menu")
@Tag(name = "菜单管理", description = "菜单相关接口")
public class SysMenuController {

    @Autowired
    private SysMenuService sysMenuService;

    @GetMapping("/list")
    @Operation(summary = "获取菜单列表", description = "分页获取菜单列表")
    public R<IPage<SysMenu>> list(@RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<SysMenu> page = new Page<>(pageNum, pageSize);
        IPage<SysMenu> result = sysMenuService.page(page);
        return R.success(result);
    }

    @GetMapping("/tree")
    @Operation(summary = "获取菜单树", description = "获取菜单树结构")
    public R<List<SysMenu>> tree() {
        List<SysMenu> menus = sysMenuService.list();
        List<SysMenu> menuTree = sysMenuService.buildMenuTree(menus);
        return R.success(menuTree);
    }

    @PostMapping("/add")
    @Operation(summary = "添加菜单", description = "添加新菜单")
    public R<?> add(@RequestBody SysMenu menu) {
        sysMenuService.save(menu);
        return R.success();
    }

    @PutMapping("/update")
    @Operation(summary = "更新菜单", description = "更新菜单信息")
    public R<?> update(@RequestBody SysMenu menu) {
        sysMenuService.updateById(menu);
        return R.success();
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "删除菜单", description = "根据ID删除菜单")
    public R<?> delete(@PathVariable Long id) {
        sysMenuService.removeById(id);
        return R.success();
    }

    @GetMapping("/get/{id}")
    @Operation(summary = "获取菜单详情", description = "根据ID获取菜单详情")
    public R<SysMenu> get(@PathVariable Long id) {
        SysMenu menu = sysMenuService.getById(id);
        return R.success(menu);
    }
}