package com.walmart.oms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.walmart.oms.entity.Warehouse;
import com.walmart.oms.mapper.WarehouseMapper;
import com.walmart.oms.service.WarehouseService;
import com.walmart.oms.utils.PageUtils;
import com.walmart.oms.utils.Query;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class WarehouseServiceImpl extends ServiceImpl<WarehouseMapper, Warehouse> implements WarehouseService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<Warehouse> page = this.page(
                new Query<Warehouse>().getPage(params),
                new QueryWrapper<Warehouse>()
        );
        return new PageUtils(page);
    }
}