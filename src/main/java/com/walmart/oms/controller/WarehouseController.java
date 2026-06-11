package com.walmart.oms.controller;

import com.walmart.oms.entity.Warehouse;
import com.walmart.oms.service.WarehouseService;
import com.walmart.oms.utils.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/inventory/warehouse")
@Tag(name = "仓库管理")
public class WarehouseController {

    @Autowired
    private WarehouseService warehouseService;

    @GetMapping("/list")
    @Operation(summary = "仓库列表")
    public R list(@RequestParam Map<String, Object> params) {
        return R.ok().put("page", warehouseService.queryPage(params));
    }

    @PostMapping("/add")
    @Operation(summary = "添加仓库")
    public R add(@RequestBody Warehouse warehouse) {
        warehouseService.save(warehouse);
        return R.ok();
    }

    @PutMapping("/update")
    @Operation(summary = "更新仓库")
    public R update(@RequestBody Warehouse warehouse) {
        warehouseService.updateById(warehouse);
        return R.ok();
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "删除仓库")
    public R delete(@PathVariable Long id) {
        warehouseService.removeById(id);
        return R.ok();
    }
}