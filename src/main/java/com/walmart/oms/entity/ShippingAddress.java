package com.walmart.oms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("shipping_address")
public class ShippingAddress {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long memberId;
    private String name;
    private String phone;
    private String province;
    private String city;
    private String district;
    private String address;
    private Integer isDefault;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
