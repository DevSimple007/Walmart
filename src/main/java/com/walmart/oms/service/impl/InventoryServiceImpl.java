package com.walmart.oms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.walmart.oms.entity.Inventory;
import com.walmart.oms.mapper.InventoryMapper;
import com.walmart.oms.service.InventoryService;
import com.walmart.oms.utils.PageUtils;
import com.walmart.oms.utils.Query;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class InventoryServiceImpl extends ServiceImpl<InventoryMapper, Inventory> implements InventoryService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<Inventory> page = this.page(
                new Query<Inventory>().getPage(params),
                new QueryWrapper<Inventory>()
        );
        return new PageUtils(page);
    }

    @Override
    public void updateStock(Long productId, Integer quantity, Integer type) {
        Inventory inventory = this.getOne(new QueryWrapper<Inventory>().eq("product_id", productId));
        if (inventory != null) {
            if (type == 1) { // 入库
                inventory.setQuantity(inventory.getQuantity() + quantity);
            } else if (type == 2) { // 出库
                inventory.setQuantity(inventory.getQuantity() - quantity);
            }
            this.updateById(inventory);
        }
    }

    @Override
    public void updateMinMaxStock(Long id, Integer minStock, Integer maxStock) {
        // 该方法已不再支持，数据库表中没有 min_stock 和 max_stock 字段
        // 可以考虑在 product 表或其他地方实现
    }

    @Override
    public void checkStockAlert() {
        // 这里可以实现库存预警逻辑，例如发送通知等
        // 暂时实现为空
    }
}