package com.walmart.oms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("home_category")
public class HomeCategory {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String iconUrl;
    private Long linkCategoryId;
    private String color;
    private Integer sort;
    private Integer status;
    private java.time.LocalDateTime createTime;
    private java.time.LocalDateTime updateTime;
}
