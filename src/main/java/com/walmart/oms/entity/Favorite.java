package com.walmart.oms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 收藏实体
 */
@Data
@TableName("favorite")
public class Favorite {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long memberId;
    private Long productId;
    private LocalDateTime createTime;
    
    @TableField(exist = false)
    private String productName;
    
    @TableField(exist = false)
    private BigDecimal price;
    
    @TableField(exist = false)
    private Integer stock;
    
    @TableField(exist = false)
    private String imageUrl;
}
