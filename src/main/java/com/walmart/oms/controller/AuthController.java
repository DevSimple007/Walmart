package com.walmart.oms.controller;

import com.walmart.oms.entity.SysUser;
import com.walmart.oms.service.SysUserService;
import com.walmart.oms.utils.JwtUtils;
import com.walmart.oms.utils.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.walmart.oms.utils.CaptchaUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@Tag(name = "认证管理", description = "登录认证相关接口")
public class AuthController {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * 登录请求参数
     */
    public static class LoginRequest {
        private String username;
        private String password;
        private Boolean rememberMe;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public Boolean getRememberMe() {
            return rememberMe;
        }

        public void setRememberMe(Boolean rememberMe) {
            this.rememberMe = rememberMe;
        }
    }

    /**
     * 登录响应数据
     */
    public static class LoginResponse {
        private String token;
        private String refreshToken;
        private SysUser userInfo;

        public LoginResponse(String token, String refreshToken, SysUser userInfo) {
            this.token = token;
            this.refreshToken = refreshToken;
            this.userInfo = userInfo;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getRefreshToken() {
            return refreshToken;
        }

        public void setRefreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
        }

        public SysUser getUserInfo() {
            return userInfo;
        }

        public void setUserInfo(SysUser userInfo) {
            this.userInfo = userInfo;
        }
    }

    /**
     * 刷新令牌请求参数
     */
    public static class RefreshTokenRequest {
        private String refreshToken;

        public String getRefreshToken() {
            return refreshToken;
        }

        public void setRefreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
        }
    }

    @PostMapping("/login")
    @Operation(summary = "登录", description = "用户登录获取令牌")
    public R<LoginResponse> login(@RequestBody LoginRequest request) {
        // 登录认证
        SysUser user = sysUserService.login(request.getUsername(), request.getPassword());
        user.setPassword(null);

        // 生成JWT令牌和刷新令牌
        String token = jwtUtils.generateToken(user.getId());
        String refreshToken = jwtUtils.generateRefreshToken(user.getId());

        // 构造响应数据
        LoginResponse response = new LoginResponse(token, refreshToken, user);
        return R.success(response);
    }

    @PostMapping("/logout")
    @Operation(summary = "退出登录", description = "用户退出登录")
    public R<?> logout(HttpServletRequest request) {
        // 从请求头中获取token
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            // 将token加入黑名单
            jwtUtils.addTokenToBlacklist(token);
            log.info("User logged out, token added to blacklist");
        }
        return R.success();
    }

    @PostMapping("/refresh")
    @Operation(summary = "刷新令牌", description = "刷新JWT令牌")
    public R<Map<String, String>> refreshToken(@RequestBody RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();
        if (refreshToken == null || refreshToken.isEmpty()) {
            return R.error(400, "刷新令牌不能为空");
        }

        try {
            // 验证刷新令牌
            if (!jwtUtils.validateToken(refreshToken)) {
                return R.error(401, "刷新令牌无效或已过期");
            }

            // 从刷新令牌中获取用户ID
            Long userId = jwtUtils.getUserIdFromToken(refreshToken);
            // 生成新的访问令牌和刷新令牌
            String newToken = jwtUtils.generateToken(userId);
            String newRefreshToken = jwtUtils.generateRefreshToken(userId);

            Map<String, String> result = new HashMap<>();
            result.put("token", newToken);
            result.put("refreshToken", newRefreshToken);
            return R.success(result);
        } catch (Exception e) {
            log.error("Token refresh failed: {}", e.getMessage());
            return R.error(401, "刷新令牌无效或已过期");
        }
    }

    @GetMapping("/captcha")
    @Operation(summary = "获取图形验证码", description = "生成并返回图形验证码")
    public R<CaptchaUtils.CaptchaResult> getCaptcha() {
        CaptchaUtils.CaptchaResult captchaResult = CaptchaUtils.generateCaptcha();
        // 实际应用中应该将验证码存储到Redis中，设置过期时间
        return R.success(captchaResult);
    }
}