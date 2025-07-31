package com.zshyyds.backend.common;

import java.util.HashMap;
import java.util.Map;

/**
 * 统一API响应工具类
 */
public class ApiResponse {
    
    /**
     * 成功响应
     * @param data 数据
     * @return 响应Map
     */
    public static Map<String, Object> success(Object data) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "操作成功");
        result.put("data", data);
        return result;
    }
    
    /**
     * 成功响应（自定义消息）
     * @param message 消息
     * @param data 数据
     * @return 响应Map
     */
    public static Map<String, Object> success(String message, Object data) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", message);
        result.put("data", data);
        return result;
    }
    
    /**
     * 成功响应（仅消息）
     * @param message 消息
     * @return 响应Map
     */
    public static Map<String, Object> success(String message) {
        return success(message, null);
    }
    
    /**
     * 失败响应
     * @param message 错误消息
     * @return 响应Map
     */
    public static Map<String, Object> error(String message) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 400);
        result.put("message", message);
        result.put("data", null);
        return result;
    }
    
    /**
     * 失败响应（自定义错误码）
     * @param code 错误码
     * @param message 错误消息
     * @return 响应Map
     */
    public static Map<String, Object> error(Integer code, String message) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", code);
        result.put("message", message);
        result.put("data", null);
        return result;
    }
    
    /**
     * 服务器错误响应
     * @return 响应Map
     */
    public static Map<String, Object> serverError() {
        return error(500, "服务器内部错误");
    }
    
    /**
     * 未授权响应
     * @return 响应Map
     */
    public static Map<String, Object> unauthorized() {
        return error(401, "未授权访问");
    }
    
    /**
     * 禁止访问响应
     * @return 响应Map
     */
    public static Map<String, Object> forbidden() {
        return error(403, "禁止访问");
    }
    
    /**
     * 资源不存在响应
     * @return 响应Map
     */
    public static Map<String, Object> notFound() {
        return error(404, "资源不存在");
    }
    
    /**
     * 分页响应
     * @param data 数据
     * @param total 总数
     * @param page 当前页
     * @param size 每页大小
     * @return 响应Map
     */
    public static Map<String, Object> page(Object data, Long total, Integer page, Integer size) {
        Map<String, Object> pageInfo = new HashMap<>();
        pageInfo.put("list", data);
        pageInfo.put("total", total);
        pageInfo.put("page", page);
        pageInfo.put("size", size);
        pageInfo.put("pages", (total + size - 1) / size); // 总页数
        
        return success("查询成功", pageInfo);
    }
} 