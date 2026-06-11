package com.walmart.oms.controller;

import com.walmart.oms.entity.Order;
import com.walmart.oms.entity.OrderItem;
import com.walmart.oms.entity.Product;
import com.walmart.oms.mapper.OrderItemMapper;
import com.walmart.oms.mapper.ProductMapper;
import com.walmart.oms.service.OrderService;
import com.walmart.oms.utils.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/order/list")
@Tag(name = "订单管理")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private ProductMapper productMapper;

    @GetMapping("/list")
    @Operation(summary = "订单列表")
    public R list(@RequestParam Map<String, Object> params) {
        return R.ok().put("page", orderService.queryPage(params));
    }

    @PostMapping("/add")
    @Operation(summary = "添加订单")
    public R add(@RequestBody Order order) {
        orderService.save(order);
        return R.ok();
    }

    @PutMapping("/update")
    @Operation(summary = "更新订单")
    public R update(@RequestBody Order order) {
        orderService.updateById(order);
        return R.ok();
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "删除订单")
    public R delete(@PathVariable Long id) {
        orderService.removeById(id);
        return R.ok();
    }

    @PutMapping("/updateStatus/{orderId}")
    @Operation(summary = "更新订单状态")
    public R updateStatus(@PathVariable Long orderId, @RequestParam Integer status) {
        orderService.updateStatus(orderId, status);
        return R.ok();
    }

    @PutMapping("/handleRefund/{orderId}")
    @Operation(summary = "处理退款")
    public R handleRefund(@PathVariable Long orderId) {
        orderService.handleRefund(orderId);
        return R.ok();
    }

    @GetMapping("/get/{id}")
    @Operation(summary = "获取订单详情")
    public R get(@PathVariable Long id) {
        Order order = orderService.getById(id);
        if (order == null) {
            return R.error(400, "订单不存在");
        }
        return R.ok().put("data", order);
    }

    @GetMapping("/item/list/{orderId}")
    @Operation(summary = "获取订单商品列表")
    public R getOrderItems(@PathVariable Long orderId) {
        List<OrderItem> items = orderItemMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<OrderItem>()
                .eq("order_id", orderId)
        );
        for (OrderItem item : items) {
            Product product = productMapper.selectById(item.getProductId());
            if (product != null) {
                item.setProductName(product.getProductName());
            }
        }
        return R.ok().put("data", items);
    }
}