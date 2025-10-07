package com.qd33.bilibili_analysis.controller;

import com.qd33.bilibili_analysis.entity.Video;
import com.qd33.bilibili_analysis.entity.VideoStat;
import com.qd33.bilibili_analysis.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/video")
public class VideoController {

    @Autowired
    private VideoService videoService;

    // 🆕 测试接口
    @GetMapping("/test")
    public String test() {
        return "VideoController 正常工作！";
    }

    // 根据BV号查询视频详情
    @GetMapping("/{bvId}")
    public ResponseEntity<?> getVideo(@PathVariable String bvId) {
        return ResponseEntity.ok(videoService.getVideoByBvId(bvId));
    }

    // 保存视频基本信息
    @PostMapping
    public ResponseEntity<?> saveVideo(@RequestBody Video video) {
        return ResponseEntity.ok(videoService.saveVideo(video));
    }

    // 获取视频数据趋势
    @GetMapping("/{bvId}/trend")
    public ResponseEntity<?> getVideoTrend(@PathVariable String bvId) {
        return ResponseEntity.ok(videoService.getVideoTrend(bvId));
    }

    // 检查视频是否存在
    @GetMapping("/{bvId}/exists")
    public ResponseEntity<?> videoExists(@PathVariable String bvId) {
        Map<String, Object> result = new HashMap<>();
        result.put("exists", videoService.videoExists(bvId));
        result.put("bvId", bvId);
        return ResponseEntity.ok(result);
    }

    // 🆕 保存视频统计数据
    @PostMapping("/{bvId}/stats")
    public ResponseEntity<?> saveVideoStats(@PathVariable String bvId, @RequestBody Map<String, Object> statData) {
        // 这里需要先根据bvId获取视频，然后创建VideoStat对象
        // 暂时简化处理，直接调用service
        return ResponseEntity.ok(videoService.saveVideoStat(statData));
    }

    // 🆕 批量获取视频信息
    @GetMapping("/batch")
    public ResponseEntity<?> getVideosByPartition(@RequestParam String partition) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 这里需要调用Repository的findByVideoPartition方法
            result.put("success", true);
            result.put("partition", partition);
            result.put("message", "分区查询功能待实现");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "查询失败: " + e.getMessage());
        }
        return ResponseEntity.ok(result);
    }

    // 🆕 获取热门视频
    @GetMapping("/hot")
    public ResponseEntity<?> getHotVideos() {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "热门视频功能待实现");
        result.put("data", new Object[]{});
        return ResponseEntity.ok(result);
    }

    // 🆕 搜索视频
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