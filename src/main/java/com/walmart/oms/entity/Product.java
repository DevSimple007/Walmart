package com.walmart.oms.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import java.math.BigDecimal;

/**
 * 商品实体类
 */
@Data
@TableName("product")
public class Product {
    /**
     * 主键
     */
    private Long id;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 原价（用于折扣展示，null 表示无折扣）
     */
    private BigDecimal originalPrice;

    /**
     * 是否国际品牌：1-是，0-否
     */
    private Integer isInternational;

    /**
     * 库存
     */
    private Integer stock;

    /**
     * 状态：1-在售，0-下架
     */
    private Integer status;

    /**
     * 描述
     */
    private String description;

    /**
     * 商品图片URL
     */
    private String imageUrl;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 分类名称（非数据库字段，用于前端显示）
     */
    @TableField(exist = false)
    private String categoryName;

    /**
     * 累计销量（非数据库字段，用于热销榜展示）
     */
    @TableField(exist = false)
    private Integer soldCount;
}