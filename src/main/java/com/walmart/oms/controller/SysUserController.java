package com.walmart.oms.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.walmart.oms.entity.SysUser;
import com.walmart.oms.service.SysUserService;
import com.walmart.oms.utils.JwtUtils;
import com.walmart.oms.utils.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户管理控制器
 */
@RestController
@RequestMapping("/system/user")
@Tag(name = "用户管理", description = "用户相关接口")
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private JwtUtils jwtUtils;

    public static class ProfileUpdateRequest {
        private String nickname;
        private String email;
        private String phone;

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }
    }

    public static class PasswordUpdateRequest {
        private String oldPassword;
        private String newPassword;

        public String getOldPassword() {
            return oldPassword;
        }

        public void setOldPassword(String oldPassword) {
            this.oldPassword = oldPassword;
        }

        public String getNewPassword() {
            return newPassword;
        }

        public void setNewPassword(String newPassword) {
            this.newPassword = newPassword;
        }
    }

    public static class ThemeUpdateRequest {
        private String theme;

        public String getTheme() {
            return theme;
        }

        public void setTheme(String theme) {
            this.theme = theme;
        }
    }

    private SysUser resolveCurrentUser(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return null;
        }
        String token = authorization.substring(7);
        if (!jwtUtils.validateToken(token)) {
            return null;
        }
        Long userId = jwtUtils.getUserIdFromToken(token);
        return sysUserService.getById(userId);
    }

    private boolean isAdmin(SysUser user) {
        return user != null && "admin".equals(user.getUsername());
    }

    @GetMapping("/list")
    @Operation(summary = "获取用户列表", description = "分页获取用户列表")
    public R<IPage<SysUser>> list(HttpServletRequest request,
                                  @RequestParam(defaultValue = "1") Integer pageNum,
                                  @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<SysUser> page = new Page<>(pageNum, pageSize);
        IPage<SysUser> result = sysUserService.page(page);
        SysUser currentUser = resolveCurrentUser(request);
        if (!isAdmin(currentUser)) {
            result.getRecords().forEach(item -> item.setPassword(null));
        }
        return R.success(result);
    }

    @PostMapping("/add")
    @Operation(summary = "添加用户", description = "添加新用户")
    public R<?> add(@RequestBody SysUser user) {
        sysUserService.save(user);
        return R.success();
    }

    @PutMapping("/update")
    @Operation(summary = "更新用户", description = "更新用户信息")
    public R<?> update(@RequestBody SysUser user) {
        sysUserService.updateById(user);
        return R.success();
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "删除用户", description = "根据ID删除用户")
    public R<?> delete(@PathVariable Long id) {
        sysUserService.removeById(id);
        return R.success();
    }

    @GetMapping("/get/{id}")
    @Operation(summary = "获取用户详情", description = "根据ID获取用户详情")
    public R<SysUser> get(HttpServletRequest request, @PathVariable Long id) {
        SysUser user = sysUserService.getById(id);
        if (user == null) {
            return R.error("用户不存在");
        }
        if (!isAdmin(resolveCurrentUser(request))) {
            user.setPassword(null);
        }
        return R.success(user);
    }

    @GetMapping("/profile")
    @Operation(summary = "获取个人信息", description = "获取当前登录用户信息")
    public R<SysUser> profile(HttpServletRequest request) {
        SysUser currentUser = resolveCurrentUser(request);
        if (currentUser == null) {
            return R.error(401, "未登录或登录已过期");
        }
        currentUser.setPassword(null);
        return R.success(currentUser);
    }

    @PutMapping("/profile")
    @Operation(summary = "更新个人信息", description = "更新当前登录用户昵称、邮箱、电话")
    public R<?> updateProfile(HttpServletRequest request, @RequestBody ProfileUpdateRequest profile) {
        SysUser currentUser = resolveCurrentUser(request);
        if (currentUser == null) {
            return R.error(401, "未登录或登录已过期");
        }
        currentUser.setNickname(profile.getNickname());
        currentUser.setEmail(profile.getEmail());
        currentUser.setPhone(profile.getPhone());
        sysUserService.updateById(currentUser);
        return R.success();
    }

    @PutMapping("/password")
    @Operation(summary = "修改密码", description = "更新当前登录用户密码")
    public R<?> updatePassword(HttpServletRequest request, @RequestBody PasswordUpdateRequest password) {
        SysUser currentUser = resolveCurrentUser(request);
        if (currentUser == null) {
            return R.error(401, "未登录或登录已过期");
        }
        if (password.getOldPassword() == null || !password.getOldPassword().equals(currentUser.getPassword())) {
            return R.error("原密码错误");
        }
        if (password.getNewPassword() == null || password.getNewPassword().isEmpty()) {
            return R.error("新密码不能为空");
        }
        currentUser.setPassword(password.getNewPassword());
        sysUserService.updateById(currentUser);
        return R.success();
    }

    @GetMapping("/settings")
    @Operation(summary = "获取个人设置", description = "获取当前登录用户主题设置")
    public R<ThemeUpdateRequest> getSettings(HttpServletRequest request) {
        SysUser currentUser = resolveCurrentUser(request);
        if (currentUser == null) {
            return R.error(401, "未登录或登录已过期");
        }
        ThemeUpdateRequest response = new ThemeUpdateRequest();
        response.setTheme(currentUser.getTheme() == null ? "default" : currentUser.getTheme());
        return R.success(response);
    }

    @PutMapping("/settings")
    @Operation(summary = "保存个人设置", description = "保存当前登录用户主题设置")
    public R<?> updateSettings(HttpServletRequest request, @RequestBody ThemeUpdateRequest settings) {
        SysUser currentUser = resolveCurrentUser(request);
        if (currentUser == null) {
            return R.error(401, "未登录或登录已过期");
        }
        currentUser.setTheme(settings.getTheme() == null ? "default" : settings.getTheme());
        sysUserService.updateById(currentUser);
        return R.success();
    }
}