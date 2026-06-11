package com.walmart.oms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("`order`")
public class Order {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String orderNo;
    private Long userId;

    @TableField(exist = false)
    private String memberName;

    private BigDecimal totalAmount;
    private String remark;
    private Integer status;
    
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
     * 使用的优惠券ID
     */
    private Long couponId;

    /**
     * 优惠券优惠金额
     */
    private BigDecimal couponDiscount;

    /**
     * 实付金额
     */
    private BigDecimal actualAmount;

    @TableField(exist = false)
    private String paymentMethod;

    @TableField(exist = false)
    private List<OrderItem> items;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
