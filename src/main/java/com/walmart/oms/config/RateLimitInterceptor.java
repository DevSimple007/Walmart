package com.walmart.oms.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    // 存储每个IP的请求计数和窗口起始时间
    private static final Map<String, AtomicInteger> ipRequestCount = new ConcurrentHashMap<>();
    private static final Map<String, Long> ipWindowStart = new ConcurrentHashMap<>();
    // 时间窗口（毫秒）
    private static final long TIME_WINDOW = 60000;
    // 时间窗口内最大请求次数
    private static final int MAX_REQUESTS = 300;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String clientIP = getClientIP(request);
        long currentTime = System.currentTimeMillis();

        // 检查并重置过期窗口
        Long windowStart = ipWindowStart.get(clientIP);
        AtomicInteger count = ipRequestCount.get(clientIP);

        if (windowStart == null || currentTime - windowStart >= TIME_WINDOW) {
            // 新窗口，重置计数
            ipWindowStart.put(clientIP, currentTime);
            ipRequestCount.put(clientIP, new AtomicInteger(1));
            return true;
        }

        // 当前窗口内，检查是否超限
        int currentCount = count.incrementAndGet();
        if (currentCount > MAX_REQUESTS) {
            log.warn("Rate limit exceeded for IP: {}, count: {}", clientIP, currentCount);
            sendErrorResponse(response, "请求过于频繁，请稍后再试");
            return false;
        }

        return true;
    }

    /**
     * 获取客户端IP
     */
    private String getClientIP(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    /**
     * 发送错误响应
     */
    private void sendErrorResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(429);
        response.setContentType("application/json; charset=utf-8");
        PrintWriter writer = response.getWriter();
        writer.write("{\"code\":429,\"message\":\"" + message + "\"}");
        writer.flush();
        writer.close();
    }
}
