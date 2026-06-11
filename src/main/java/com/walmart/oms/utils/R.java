package com.walmart.oms.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 统一返回结果封装
 */
@Data
@Slf4j
public class R<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * 状态码
     */
    private int code;

    /**
     * 消息
     */
    private String message;

    /**
     * 数据
     */
    private T data;

    /**
     * 成功
     */
    public static <T> R<T> success() {
        return success(null);
    }

    /**
     * 成功
     */
    public static <T> R<T> success(T data) {
        R<T> r = new R<>();
        r.setCode(200);
        r.setMessage("成功");
        r.setData(data);
        return r;
    }

    /**
     * ok方法 - success的别名
     */
    @SuppressWarnings("unchecked")
    public static R<Map<String, Object>> ok() {
        R<Map<String, Object>> r = new R<>();
        r.setCode(200);
        r.setMessage("成功");
        r.setData(new HashMap<>());
        return r;
    }

    /**
     * ok方法带数据
     */
    public static <T> R<T> ok(T data) {
        return success(data);
    }

    /**
     * 失败
     */
    public static <T> R<T> fail(int code, String message) {
        R<T> r = new R<>();
        r.setCode(code);
        r.setMessage(message);
        return r;
    }

    /**
     * 失败
     */
    public static <T> R<T> fail(String message) {
        return fail(500, message);
    }

    /**
     * 错误（fail的别名）
     */
    public static <T> R<T> error(int code, String message) {
        return fail(code, message);
    }

    /**
     * 错误（fail的别名）
     */
    public static <T> R<T> error(String message) {
        return fail(message);
    }

    /**
     * 添加数据到data中（仅当data为Map时有效）
     */
    @SuppressWarnings("unchecked")
    public R<T> put(String key, Object value) {
        if (this.data == null) {
            this.data = (T) new HashMap<String, Object>();
        }
        if (this.data instanceof Map) {
            ((Map<String, Object>) this.data).put(key, value);
        }
        return this;
    }

    /**
     * 转换为JSON字符串
     */
    public String toJSONString() {
        try {
            return OBJECT_MAPPER.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            log.error("JSON序列化失败", e);
            return "{\"code\":500,\"message\":\"JSON序列化失败\"}";
        }
    }
}