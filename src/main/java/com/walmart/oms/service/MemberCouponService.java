package com.walmart.oms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.walmart.oms.entity.MemberCoupon;

import java.util.List;

public interface MemberCouponService extends IService<MemberCoupon> {
    List<MemberCoupon> getMemberCoupons(Long memberId);
    void claimCoupon(Long memberId, Long couponId);
    void useCoupon(Long memberId, Long couponId, String orderNo);
}
