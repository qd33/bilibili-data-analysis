package com.qd33.bilibili_analysis.controller;

import com.qd33.bilibili_analysis.service.VideoService;
import com.qd33.bilibili_analysis.service.UpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ServiceTestController {

    @Autowired
    private VideoService videoService;

    @Autowired
    private UpService upService;

    @GetMapping("/api/test/services")
    public String testServices() {
        StringBuilder result = new StringBuilder();
        result.append("=== Service层验证结果 ===<br/>");

        try {
            // 测试Service依赖注入
            boolean videoServiceOk = videoService != null;
            boolean upServiceOk = upService != null;

            result.append("✅ VideoService: ").append(videoServiceOk ? "注入成功" : "注入失败").append("<br/>");
            result.append("✅ UpService: ").append(upServiceOk ? "注入成功" : "注入失败").append("<br/>");

            // 测试基础方法
            if (videoServiceOk) {
                boolean videoExists = videoService.videoExists("BV1GJ4y1Y7p9");
                result.append("✅ VideoService.videoExists(): ").append(videoExists).append("<br/>");
            }

            if (upServiceOk) {
                boolean upExists = upService.upExists("123456");
                result.append("✅ UpService.upExists(): ").append(upExists).append("<br/>");
            }

            result.append("<br/>🎉 Service层验证完成！");

        } catch (Exception e) {
            result.append("❌ Service层验证失败: ").append(e.getMessage());
        }

        return result.toString();
    }
}