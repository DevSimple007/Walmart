package com.walmart.oms.controller;

import com.walmart.oms.entity.Member;
import com.walmart.oms.entity.MemberLevel;
import com.walmart.oms.service.MemberService;
import com.walmart.oms.service.MemberLevelService;
import com.walmart.oms.utils.JwtUtils;
import com.walmart.oms.utils.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customer/auth")
@Tag(name = "顾客认证", description = "顾客注册登录接口")
public class CustomerAuthController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberLevelService memberLevelService;

    @Autowired
    private JwtUtils jwtUtils;

    @Data
    public static class LoginRequest {
        private String phone;
        private String password;
    }

    @Data
    public static class RegisterRequest {
        private String phone;
        private String password;
        private String memberName;
    }

    @Data
    public static class AuthResponse {
        private String token;
        private Member userInfo;
    }

    @PostMapping("/register")
    @Operation(summary = "顾客注册")
    public R<AuthResponse> register(@RequestBody RegisterRequest request) {
        Member member = memberService.register(request.getPhone(), request.getPassword(), request.getMemberName());
        String token = jwtUtils.generateToken(member.getId());
        AuthResponse response = new AuthResponse();
        response.setToken(token);
        response.setUserInfo(member);
        return R.success(response);
    }

    @PostMapping("/login")
    @Operation(summary = "顾客登录")
    public R<AuthResponse> login(@RequestBody LoginRequest request) {
        Member member = memberService.login(request.getPhone(), request.getPassword());
        String token = jwtUtils.generateToken(member.getId());
        AuthResponse response = new AuthResponse();
        response.setToken(token);
        response.setUserInfo(member);
        return R.success(response);
    }

    @GetMapping("/info")
    @Operation(summary = "获取当前顾客信息")
    public R<Member> info(HttpServletRequest request) {
        Long memberId = (Long) request.getAttribute("userId");
        Member member = memberService.getById(memberId);
        if (member == null) {
            return R.error(401, "用户不存在");
        }
        return R.success(member);
    }

    @GetMapping("/levels")
    @Operation(summary = "获取会员等级列表")
    public R levels() {
        return R.ok().put("list", memberLevelService.list());
    }
}
