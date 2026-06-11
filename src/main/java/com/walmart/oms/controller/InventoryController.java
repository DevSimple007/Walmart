package com.walmart.oms.controller;

import com.walmart.oms.entity.Inventory;
import com.walmart.oms.service.InventoryService;
import com.walmart.oms.utils.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/inventory/list")
@Tag(name = "库存管理")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @GetMapping("/list")
    @Operation(summary = "库存列表")
    public R list(@RequestParam Map<String, Object> params) {
        return R.ok().put("page", inventoryService.queryPage(params));
    }

    @PostMapping("/add")
    @Operation(summary = "添加库存")
    public R add(@RequestBody Inventory inventory) {
        inventoryService.save(inventory);
        return R.ok();
    }

    @PutMapping("/update")
    @Operation(summary = "更新库存")
    public R update(@RequestBody Inventory inventory) {
        inventoryService.updateById(inventory);
        return R.ok();
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "删除库存")
    public R delete(@PathVariable Long id) {
        inventoryService.removeById(id);
        return R.ok();
    }

    @PutMapping("/updateStock")
    @Operation(summary = "更新库存数量")
    public R updateStock(@RequestParam Long productId, @RequestParam Integer quantity, @RequestParam Integer type) {
        inventoryService.updateStock(productId, quantity, type);
        return R.ok();
    }

    @PutMapping("/updateMinMaxStock/{id}")
    @Operation(summary = "更新库存预警值")
    public R updateMinMaxStock(@PathVariable Long id, @RequestParam Integer minStock, @RequestParam Integer maxStock) {
        inventoryService.updateMinMaxStock(id, minStock, maxStock);
        return R.ok();
    }

    @PostMapping("/checkAlert")
    @Operation(summary = "检查库存预警")
    public R checkAlert() {
        inventoryService.checkStockAlert();
        return R.ok();
    }
}