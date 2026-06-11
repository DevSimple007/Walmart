package com.walmart.oms.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderCreateRequest {
    private List<OrderItemRequest> items;
    private String remark;

    /**
     * 配送方式: pickup-到店自提, delivery-送货上门
     */
    private String deliveryType;

    /**
     * 收货地址
     */
    private String address;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 期望送达时间
     */
    private String deliveryTime;

    /**
     * 使用的优惠券ID（member_coupon表的id）
     */
    private Long couponMemberId;

    @Data
    public static class OrderItemRequest {
        private Long productId;
        private Integer quantity;
    }
}
