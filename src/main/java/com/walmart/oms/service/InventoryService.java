package com.walmart.oms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.walmart.oms.entity.Inventory;
import com.walmart.oms.utils.PageUtils;

import java.util.Map;

public interface InventoryService extends IService<Inventory> {
    PageUtils queryPage(Map<String, Object> params);
    void updateStock(Long productId, Integer quantity, Integer type);
    void updateMinMaxStock(Long id, Integer minStock, Integer maxStock);
    void checkStockAlert();
}