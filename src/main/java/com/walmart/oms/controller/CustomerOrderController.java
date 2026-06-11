package com.walmart.oms.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.walmart.oms.dto.OrderCreateRequest;
import com.walmart.oms.entity.Order;
import com.walmart.oms.entity.OrderItem;
import com.walmart.oms.entity.Product;
import com.walmart.oms.mapper.OrderItemMapper;
import com.walmart.oms.mapper.ProductMapper;
import com.walmart.oms.service.MemberService;
import com.walmart.oms.service.OrderService;
import com.walmart.oms.utils.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/customer/order")
@Tag(name = "顾客订单", description = "顾客端订单接口")
public class CustomerOrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private MemberService memberService;

    @PostMapping("/create")
    @Operation(summary = "创建订单")
    public R<Order> create(HttpServletRequest request, @RequestBody OrderCreateRequest req) {
        Long memberId = (Long) request.getAttribute("userId");
        Order order = orderService.createOrder(memberId, req);
        return R.success(order);
    }

    @GetMapping("/list")
    @Operation(summary = "我的订单列表")
    public R<IPage<Order>> list(HttpServletRequest request,
                                @RequestParam(required = false) Integer status,
                                @RequestParam(defaultValue = "1") Integer pageNum,
                                @RequestParam(defaultValue = "10") Integer pageSize) {
        Long memberId = (Long) request.getAttribute("userId");
        IPage<Order> page = orderService.getMemberOrders(memberId, status, pageNum, pageSize);
        return R.success(page);
    }

    @GetMapping("/detail/{orderNo}")
    @Operation(summary = "订单详情")
    public R<Order> detail(HttpServletRequest request, @PathVariable String orderNo) {
        Long memberId = (Long) request.getAttribute("userId");
        Order order = orderService.getOrderDetail(orderNo, memberId);
        if (order == null) {
            return R.error("订单不存在");
        }
        return R.success(order);
    }

    @Data
    public static class PayRequest {
        private String orderNo;
    }

    @PutMapping("/pay/{orderNo}")
    @Operation(summary = "模拟支付")
    public R<?> pay(HttpServletRequest request, @PathVariable String orderNo) {
        Long memberId = (Long) request.getAttribute("userId");
        Order order = orderService.getOrderDetail(orderNo, memberId);
        if (order == null) {
            return R.error("订单不存在");
        }
        if (order.getStatus() != 0) {
            return R.error("订单状态异常");
        }
        // 更新订单状态为已支付
        orderService.updateStatus(order.getId(), 1);
        
        // 支付成功后，按订单金额的 1% 返积分（至少 1 积分）
        BigDecimal amount = order.getTotalAmount();
        int points = Math.max(1, amount.divide(BigDecimal.valueOf(100), 0, BigDecimal.ROUND_DOWN).intValue());
        memberService.updatePoints(memberId, points, 1);
        
        return R.ok();
    }

    @PutMapping("/cancel/{orderNo}")
    @Operation(summary = "取消订单")
    public R<?> cancel(HttpServletRequest request, @PathVariable String orderNo) {
        Long memberId = (Long) request.getAttribute("userId");
        Order order = orderService.getOrderDetail(orderNo, memberId);
        if (order == null) {
            return R.error("订单不存在");
        }
        if (order.getStatus() != 0) {
            return R.error("只能取消待支付订单");
        }
        
        // 恢复库存
        List<OrderItem> items = orderItemMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<OrderItem>()
                        .eq("order_id", order.getId()));
        for (OrderItem item : items) {
            Product product = productMapper.selectById(item.getProductId());
            if (product != null) {
                product.setStock(product.getStock() + item.getQuantity());
                productMapper.updateById(product);
            }
        }
        
        orderService.updateStatus(order.getId(), 5);
        return R.ok();
    }

    @PutMapping("/confirm/{orderNo}")
    @Operation(summary = "确认收货")
    public R<?> confirm(HttpServletRequest request, @PathVariable String orderNo) {
        Long memberId = (Long) request.getAttribute("userId");
        Order order = orderService.getOrderDetail(orderNo, memberId);
        if (order == null) {
            return R.error("订单不存在");
        }
        if (order.getStatus() != 3) {
            return R.error("只能确认已发货订单");
        }
        orderService.updateStatus(order.getId(), 4);
        return R.ok();
    }
}
