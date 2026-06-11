package com.walmart.oms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("inventory_out")
public class InventoryOut {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String outNo;
    private Long warehouseId;
    private String warehouseName;
    private Integer totalQuantity;
    private String operator;
    private String remark;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}