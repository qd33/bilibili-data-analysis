package com.qd33.bilibili_analysis.controller;

import com.qd33.bilibili_analysis.entity.Video;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.qd33.bilibili_analysis.service.VideoService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/video")
public class VideoController {

    @Autowired
    private VideoService videoService;

    // 🆕 调试接口：返回完整的视频数据结构
    @GetMapping("/{bvId}/debug")
    public ResponseEntity<?> getVideoDebug(@PathVariable String bvId) {
        Map<String, Object> result = videoService.getVideoByBvId(bvId);

        // 添加调试信息
        if (Boolean.TRUE.equals(result.get("success"))) {
            Video video = (Video) result.get("video");
            System.out.println("🔍 调试视频数据: " + video.toString());

            Map<String, Object> debugInfo = new HashMap<>();
            debugInfo.put("success", true);
            debugInfo.put("video", video);
            debugInfo.put("fieldsCheck", Map.of(
                    "title", video.getTitle() != null,
                    "cover", video.getCoverUrl() != null,
                    "description", video.getDescription() != null,
                    "publishTime", video.getPublishTime() != null
            ));
            debugInfo.put("message", "字段检查完成");

            return ResponseEntity.ok(debugInfo);
        }

        return ResponseEntity.ok(result);
    }

    // 根据BV号查询视频详情
    @GetMapping("/{bvId}")
    public ResponseEntity<?> getVideo(@PathVariable String bvId) {
        Map<String, Object> result = videoService.getVideoByBvId(bvId);

        // 添加日志输出，便于调试
        if (Boolean.TRUE.equals(result.get("success"))) {
            Video video = (Video) result.get("video");
            System.out.println("🎬 返回视频数据 - 标题: " + video.getTitle());
            System.out.println("🖼️ 返回视频数据 - 封面: " + video.getCoverUrl());
        }

        return ResponseEntity.ok(result);
    }

    // 其他方法保持不变...
    @GetMapping("/test")
    public String test() {
        return "VideoController 正常工作！";
    }

    @PostMapping
    public ResponseEntity<?> saveVideo(@RequestBody Video video) {
        return ResponseEntity.ok(videoService.saveVideo(video));
    }

    @GetMapping("/{bvId}/trend")
    public ResponseEntity<?> getVideoTrend(@PathVariable String bvId) {
        return ResponseEntity.ok(videoService.getVideoTrend(bvId));
    }

    @GetMapping("/{bvId}/exists")
    public ResponseEntity<?> videoExists(@PathVariable String bvId) {
        Map<String, Object> result = new HashMap<>();
        result.put("exists", videoService.videoExists(bvId));
        result.put("bvId", bvId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{bvId}/stats")
    public ResponseEntity<?> saveVideoStats(@PathVariable String bvId, @RequestBody Map<String, Object> statData) {
        return ResponseEntity.ok(videoService.saveVideoStat(statData));
    }

    @GetMapping("/batch")
    public ResponseEntity<?> getVideosByPartition(@RequestParam String partition) {
        Map<String, Object> result = new HashMap<>();
        try {
            result.put("success", true);
            result.put("partition", partition);
            result.put("message", "分区查询功能待实现");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "查询失败: " + e.getMessage());
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/hot")
    public ResponseEntity<?> getHotVideos() {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "热门视频功能待实现");
        result.put("data", new Object[]{});
        return ResponseEntity.ok(result);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchVideos(@RequestParam String keyword) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("keyword", keyword);
        result.put("message", "视频搜索功能待实现");
        result.put("results", new Object[]{});
        return ResponseEntity.ok(result);
    }
}