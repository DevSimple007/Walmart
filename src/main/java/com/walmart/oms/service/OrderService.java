package com.walmart.oms.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.walmart.oms.dto.OrderCreateRequest;
import com.walmart.oms.entity.Order;
import com.walmart.oms.utils.PageUtils;

import java.util.List;
import java.util.Map;

public interface OrderService extends IService<Order> {
    PageUtils queryPage(Map<String, Object> params);
    void updateStatus(Long orderId, Integer status);
    void handleRefund(Long orderId);
    Order createOrder(Long memberId, OrderCreateRequest request);
    IPage<Order> getMemberOrders(Long memberId, Integer status, Integer pageNum, Integer pageSize);
    Order getOrderDetail(String orderNo, Long memberId);
}