package com.walmart.oms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.walmart.oms.entity.Warehouse;
import com.walmart.oms.utils.PageUtils;

import java.util.Map;

public interface WarehouseService extends IService<Warehouse> {
    PageUtils queryPage(Map<String, Object> params);
}