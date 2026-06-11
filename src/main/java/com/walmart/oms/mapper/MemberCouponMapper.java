package com.walmart.oms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.walmart.oms.entity.MemberCoupon;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MemberCouponMapper extends BaseMapper<MemberCoupon> {
    List<MemberCoupon> selectMemberCoupons(@Param("memberId") Long memberId);
}
