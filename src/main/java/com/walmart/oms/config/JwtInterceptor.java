package com.walmart.oms.config;

import com.walmart.oms.utils.JwtUtils;
import com.walmart.oms.utils.R;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * JWT认证拦截器
 */
@Slf4j
@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从请求头中获取token
        String token = request.getHeader("Authorization");
        if (token == null || token.isEmpty()) {
            log.warn("JWT认证失败: 未提供认证令牌, URI={}", request.getRequestURI());
            sendErrorResponse(response, "未提供认证令牌");
            return false;
        }

        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        if (!jwtUtils.validateToken(token)) {
            log.warn("JWT认证失败: 令牌无效或已过期, URI={}, tokenPrefix={}",
                    request.getRequestURI(), token.substring(0, Math.min(20, token.length())));
            sendErrorResponse(response, "认证令牌无效或已过期");
            return false;
        }

        // 从令牌中获取用户ID
        Long userId = jwtUtils.getUserIdFromToken(token);
        // 将用户ID存储到请求中，供后续使用
        request.setAttribute("userId", userId);

        // 这里可以添加权限校验逻辑
        // 例如：检查用户是否有权限访问当前接口
        // boolean hasPermission = checkPermission(userId, request.getRequestURI(), request.getMethod());
        // if (!hasPermission) {
        //     sendErrorResponse(response, "权限不足");
        //     return false;
        // }

        return true;
    }

    private void sendErrorResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json; charset=utf-8");
        PrintWriter writer = response.getWriter();
        writer.write(R.error(401, message).toJSONString());
        writer.flush();
        writer.close();
    }

    /**
     * 检查用户权限
     */
    private boolean checkPermission(Long userId, String uri, String method) {
        // 这里可以实现具体的权限检查逻辑
        // 例如：从数据库中查询用户的权限，然后检查是否有权限访问当前接口
        // 暂时返回true，允许所有已认证用户访问
        return true;
    }
}