package com.qd33.bilibili_analysis.controller;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class TestController {

    @GetMapping("/")
    public Map<String, Object> home() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "哔哩哔哩数据分析网站后端服务正常运行");
        response.put("timestamp", System.currentTimeMillis());
        response.put("version", "1.0.0");
        return response;
    }

    @GetMapping("/api/test/hello")
    public Map<String, Object> hello() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "后端服务已启动！");
        response.put("data", Map.of(
                "com/qd33/bilibili_analysis/service", "B站数据分析平台",
                "status", "运行中",
                "timestamp", System.currentTimeMillis()
        ));
        return response;
    }

    @GetMapping("/api/test/connection")
    public Map<String, Object> testConnection() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "前后端连接测试成功");
        response.put("data", Map.of(
                "backend", "Spring Boot 3.5.6",
                "frontend", "Vue 3 + TypeScript",
                "database", "MySQL 8.0",
                "timestamp", System.currentTimeMillis()
        ));
        return response;
    }

    @PostMapping("/api/test/echo")
    public Map<String, Object> echo(@RequestBody Map<String, Object> data) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "数据接收成功");
        response.put("receivedData", data);
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }
}