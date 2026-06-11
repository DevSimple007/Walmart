package com.walmart.oms.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.walmart.oms.entity.Coupon;
import com.walmart.oms.entity.Member;
import com.walmart.oms.entity.MemberCoupon;
import com.walmart.oms.service.CouponService;
import com.walmart.oms.service.MemberCouponService;
import com.walmart.oms.service.MemberService;
import com.walmart.oms.utils.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/coupon")
@Tag(name = "优惠券管理")
public class CouponController {

    @Autowired
    private CouponService couponService;

    @Autowired
    private MemberCouponService memberCouponService;

    @Autowired
    private MemberService memberService;

    @GetMapping("/list")
    @Operation(summary = "优惠券列表")
    public R list(@RequestParam Map<String, Object> params) {
        int pageNum = Integer.parseInt(params.getOrDefault("pageNum", "1").toString());
        int pageSize = Integer.parseInt(params.getOrDefault("pageSize", "10").toString());
        IPage<Coupon> page = couponService.page(new Page<>(pageNum, pageSize), new QueryWrapper<Coupon>());
        return R.ok().put("records", page.getRecords()).put("total", page.getTotal());
    }

    @PostMapping("/add")
    @Operation(summary = "添加优惠券")
    public R add(@RequestBody Coupon coupon) {
        couponService.save(coupon);
        return R.ok();
    }

    @PutMapping("/update")
    @Operation(summary = "更新优惠券")
    public R update(@RequestBody Coupon coupon) {
        couponService.updateById(coupon);
        return R.ok();
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "删除优惠券")
    public R delete(@PathVariable Long id) {
        couponService.removeById(id);
        return R.ok();
    }

    @PostMapping("/distribute")
    @Operation(summary = "发放优惠券给指定用户")
    public R distribute(@RequestBody Map<String, Object> params) {
        Long memberId = Long.parseLong(params.get("memberId").toString());
        Long couponId = Long.parseLong(params.get("couponId").toString());
        memberCouponService.claimCoupon(memberId, couponId);
        return R.ok();
    }

    @PostMapping("/distributeAll")
    @Operation(summary = "发放优惠券给所有用户")
    public R distributeAll(@RequestBody Map<String, Object> params) {
        Long couponId = Long.parseLong(params.get("couponId").toString());
        List<Member> members = memberService.list();
        int count = 0;
        for (Member member : members) {
            try {
                memberCouponService.claimCoupon(member.getId(), couponId);
                count++;
            } catch (Exception ignored) {}
        }
        return R.ok().put("count", count);
    }
}
