package com.walmart.oms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.walmart.oms.entity.Coupon;
import com.walmart.oms.entity.MemberCoupon;
import com.walmart.oms.exception.BusinessException;
import com.walmart.oms.mapper.CouponMapper;
import com.walmart.oms.mapper.MemberCouponMapper;
import com.walmart.oms.service.MemberCouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MemberCouponServiceImpl extends ServiceImpl<MemberCouponMapper, MemberCoupon> implements MemberCouponService {

    @Autowired
    private CouponMapper couponMapper;

    @Override
    public List<MemberCoupon> getMemberCoupons(Long memberId) {
        return baseMapper.selectMemberCoupons(memberId);
    }

    @Override
    public void claimCoupon(Long memberId, Long couponId) {
        Coupon coupon = couponMapper.selectById(couponId);
        if (coupon == null || coupon.getStatus() != 1) {
            throw new BusinessException("优惠券不存在或已下架");
        }
        if (coupon.getUsedCount() >= coupon.getTotalCount()) {
            throw new BusinessException("优惠券已被领完");
        }
        
        // 检查是否已领取
        MemberCoupon existing = this.getOne(new QueryWrapper<MemberCoupon>()
                .eq("member_id", memberId).eq("coupon_id", couponId));
        if (existing != null) {
            throw new BusinessException("您已领取过该优惠券");
        }
        
        // 领取优惠券
        MemberCoupon memberCoupon = new MemberCoupon();
        memberCoupon.setMemberId(memberId);
        memberCoupon.setCouponId(couponId);
        memberCoupon.setStatus(0); // 未使用
        memberCoupon.setCreateTime(LocalDateTime.now());
        this.save(memberCoupon);
        
        // 更新已领取数量
        coupon.setUsedCount(coupon.getUsedCount() + 1);
        couponMapper.updateById(coupon);
    }

    @Override
    public void useCoupon(Long memberId, Long couponId, String orderNo) {
        MemberCoupon memberCoupon = this.getOne(new QueryWrapper<MemberCoupon>()
                .eq("member_id", memberId).eq("coupon_id", couponId).eq("status", 0));
        if (memberCoupon == null) {
            throw new BusinessException("优惠券不可用");
        }
        memberCoupon.setStatus(1); // 已使用
        memberCoupon.setUsedTime(LocalDateTime.now());
        memberCoupon.setOrderNo(orderNo);
        this.updateById(memberCoupon);
    }
}
