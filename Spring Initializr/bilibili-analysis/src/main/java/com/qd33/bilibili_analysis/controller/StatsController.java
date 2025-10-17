// StatsController.java
package com.qd33.bilibili_analysis.controller;

import com.qd33.bilibili_analysis.repository.VideoRepository;
import com.qd33.bilibili_analysis.repository.UpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/stats")
public class StatsController {

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private UpRepository upRepository;

    @GetMapping("/overview")
    public Map<String, Object> getOverviewStats() {
        Map<String, Object> result = new HashMap<>();

        try {
            // 获取统计信息
            long videoCount = videoRepository.count();
            long upCount = upRepository.count();

            // 模拟播放量和点赞数（实际项目中需要从统计表查询）
            long totalViews = videoCount * 8000; // 模拟数据
            long totalLikes = videoCount * 500;  // 模拟数据

            result.put("success", true);
            result.put("data", Map.of(
                    "videoCount", videoCount,
                    "upCount", upCount,
                    "totalViews", totalViews,
                    "totalLikes", totalLikes
            ));

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取统计信息失败: " + e.getMessage());
        }

        return result;
    }

    @GetMapping("/trend")
    public Map<String, Object> getVideoTrend() {
        Map<String, Object> result = new HashMap<>();

        try {
            // 模拟趋势数据
            Object[] trendData = {
                    Map.of("date", "2024-01", "views", 120000),
                    Map.of("date", "2024-02", "views", 150000),
                    Map.of("date", "2024-03", "views", 180000),
                    Map.of("date", "2024-04", "views", 165000),
                    Map.of("date", "2024-05", "views", 195000)
            };

            result.put("success", true);
            result.put("data", trendData);

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取趋势数据失败: " + e.getMessage());
        }

        return result;
    }

    @GetMapping("/partitions")
    public Map<String, Object> getPartitionStats() {
        Map<String, Object> result = new HashMap<>();

        try {
            // 模拟分区数据
            Object[] partitionData = {
                    Map.of("name", "生活", "value", 35),
                    Map.of("name", "游戏", "value", 25),
                    Map.of("name", "科技", "value", 20),
                    Map.of("name", "音乐", "value", 15),
                    Map.of("name", "舞蹈", "value", 5)
            };

            result.put("success", true);
            result.put("data", partitionData);

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取分区数据失败: " + e.getMessage());
        }

        return result;
    }
}