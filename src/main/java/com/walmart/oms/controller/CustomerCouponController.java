package com.walmart.oms.controller;

import com.walmart.oms.entity.MemberCoupon;
import com.walmart.oms.service.MemberCouponService;
import com.walmart.oms.utils.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customer/coupon")
@Tag(name = "顾客优惠券", description = "顾客端优惠券接口")
public class CustomerCouponController {

    @Autowired
    private MemberCouponService memberCouponService;

    @GetMapping("/list")
    @Operation(summary = "我的优惠券列表")
    public R<List<MemberCoupon>> list(HttpServletRequest request) {
        Long memberId = (Long) request.getAttribute("userId");
        List<MemberCoupon> coupons = memberCouponService.getMemberCoupons(memberId);
        return R.success(coupons);
    }

    @PostMapping("/claim/{couponId}")
    @Operation(summary = "领取优惠券")
    public R<?> claim(HttpServletRequest request, @PathVariable Long couponId) {
        Long memberId = (Long) request.getAttribute("userId");
        memberCouponService.claimCoupon(memberId, couponId);
        return R.ok();
    }
}
