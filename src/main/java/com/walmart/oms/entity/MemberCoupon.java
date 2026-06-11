package com.walmart.oms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 会员优惠券关联表
 */
@Data
@TableName("member_coupon")
public class MemberCoupon {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long memberId;
    private Long couponId;
    
    /**
     * 使用状态: 0-未使用, 1-已使用, 2-已过期
     */
    private Integer status;
    
    /**
     * 使用时间
     */
    private LocalDateTime usedTime;
    
    /**
     * 订单号
     */
    private String orderNo;
    
    private LocalDateTime createTime;
    
    @TableField(exist = false)
    private String couponName;
    
    @TableField(exist = false)
    private Integer couponType;
    
    @TableField(exist = false)
    private BigDecimal couponValue;
    
    @TableField(exist = false)
    private BigDecimal minAmount;
    
    @TableField(exist = false)
    private LocalDateTime startTime;
    
    @TableField(exist = false)
    private LocalDateTime endTime;
}
