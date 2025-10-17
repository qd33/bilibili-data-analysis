package com.qd33.bilibili_analysis.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/stats")
public class StatsController {

    @GetMapping("/overview")
    public Map<String, Object> getOverviewStats() {
        Map<String, Object> result = new HashMap<>();
        result.put("videoCount", 156);
        result.put("upCount", 42);
        result.put("totalViews", 1258473);
        result.put("totalLikes", 89234);
        return result;
    }

    @GetMapping("/partitions")
    public Map<String, Object> getPartitionStats() {
        Map<String, Object> result = new HashMap<>();
        result.put("partitions", new Object[]{
                Map.of("name", "生活", "value", 40),
                Map.of("name", "科技", "value", 30),
                Map.of("name", "游戏", "value", 20),
                Map.of("name", "音乐", "value", 10)
        });
        return result;
    }

    @GetMapping("/system")
    public Map<String, Object> getSystemStatus() {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "running");
        result.put("database", "connected");
        result.put("crawler", "active");
        return result;
    }
}