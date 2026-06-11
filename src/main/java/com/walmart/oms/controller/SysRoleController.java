package com.walmart.oms.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.walmart.oms.entity.SysRole;
import com.walmart.oms.service.SysRoleService;
import com.walmart.oms.utils.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色管理控制器
 */
@RestController
@RequestMapping("/system/role")
@Tag(name = "角色管理", description = "角色相关接口")
public class SysRoleController {

    @Autowired
    private SysRoleService sysRoleService;

    @GetMapping("/list")
    @Operation(summary = "获取角色列表", description = "分页获取角色列表")
    public R<IPage<SysRole>> list(@RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<SysRole> page = new Page<>(pageNum, pageSize);
        IPage<SysRole> result = sysRoleService.page(page);
        return R.success(result);
    }

    @PostMapping("/add")
    @Operation(summary = "添加角色", description = "添加新角色")
    public R<?> add(@RequestBody SysRole role) {
        sysRoleService.save(role);
        return R.success();
    }

    @PutMapping("/update")
    @Operation(summary = "更新角色", description = "更新角色信息")
    public R<?> update(@RequestBody SysRole role) {
        sysRoleService.updateById(role);
        return R.success();
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "删除角色", description = "根据ID删除角色")
    public R<?> delete(@PathVariable Long id) {
        sysRoleService.removeById(id);
        return R.success();
    }

    @GetMapping("/get/{id}")
    @Operation(summary = "获取角色详情", description = "根据ID获取角色详情")
    public R<SysRole> get(@PathVariable Long id) {
        SysRole role = sysRoleService.getById(id);
        return R.success(role);
    }

    @PostMapping("/saveRoleMenus")
    @Operation(summary = "保存角色菜单关联", description = "保存角色与菜单的关联关系")
    public R<?> saveRoleMenus(@RequestParam Long roleId, @RequestParam List<Long> menuIds) {
        sysRoleService.saveRoleMenus(roleId, menuIds);
        return R.success();
    }
}