package com.walmart.oms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("member_card")
public class MemberCard {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long memberId;
    private String memberName;
    private String cardNo;
    private Double balance;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}