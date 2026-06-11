package com.walmart.oms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("member_level")
public class MemberLevel {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String levelName;
    private Integer minPoints;
    private Integer maxPoints;
    private BigDecimal discount;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}