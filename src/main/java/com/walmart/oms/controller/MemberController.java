package com.walmart.oms.controller;

import com.walmart.oms.entity.Member;
import com.walmart.oms.service.MemberService;
import com.walmart.oms.utils.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/member/list")
@Tag(name = "会员管理")
public class MemberController {

    @Autowired
    private MemberService memberService;

    @GetMapping("/list")
    @Operation(summary = "会员列表")
    public R list(@RequestParam Map<String, Object> params) {
        return R.ok().put("page", memberService.queryPage(params));
    }

    @PostMapping("/add")
    @Operation(summary = "添加会员")
    public R add(@RequestBody Member member) {
        memberService.save(member);
        return R.ok();
    }

    @PutMapping("/update")
    @Operation(summary = "更新会员")
    public R update(@RequestBody Member member) {
        memberService.updateById(member);
        // 根据积分自动更新等级
        if (member.getId() != null) {
            memberService.updateLevel(member.getId());
        }
        return R.ok();
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "删除会员")
    public R delete(@PathVariable Long id) {
        memberService.removeById(id);
        return R.ok();
    }

    @PutMapping("/updatePoints")
    @Operation(summary = "更新会员积分")
    public R updatePoints(@RequestParam Long memberId, @RequestParam Integer points, @RequestParam Integer type) {
        memberService.updatePoints(memberId, points, type);
        return R.ok();
    }

    @PutMapping("/updateBalance")
    @Operation(summary = "更新会员余额")
    public R updateBalance(@RequestParam Long memberId, @RequestParam Double amount, @RequestParam Integer type) {
        memberService.updateBalance(memberId, amount, type);
        return R.ok();
    }

    @PutMapping("/updateLevel/{memberId}")
    @Operation(summary = "更新会员等级")
    public R updateLevel(@PathVariable Long memberId) {
        memberService.updateLevel(memberId);
        return R.ok();
    }
}