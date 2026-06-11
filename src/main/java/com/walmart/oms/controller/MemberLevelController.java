package com.walmart.oms.controller;

import com.walmart.oms.entity.MemberLevel;
import com.walmart.oms.service.MemberLevelService;
import com.walmart.oms.utils.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/member/level")
@Tag(name = "会员等级管理")
public class MemberLevelController {

    @Autowired
    private MemberLevelService memberLevelService;

    @GetMapping("/list")
    @Operation(summary = "会员等级列表")
    public R list(@RequestParam Map<String, Object> params) {
        return R.ok().put("page", memberLevelService.queryPage(params));
    }

    @PostMapping("/add")
    @Operation(summary = "添加会员等级")
    public R add(@RequestBody MemberLevel memberLevel) {
        memberLevelService.save(memberLevel);
        return R.ok();
    }

    @PutMapping("/update")
    @Operation(summary = "更新会员等级")
    public R update(@RequestBody MemberLevel memberLevel) {
        memberLevelService.updateById(memberLevel);
        return R.ok();
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "删除会员等级")
    public R delete(@PathVariable Long id) {
        memberLevelService.removeById(id);
        return R.ok();
    }
}