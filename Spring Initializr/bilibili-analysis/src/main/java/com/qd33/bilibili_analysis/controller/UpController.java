package com.qd33.bilibili_analysis.controller;

import com.qd33.bilibili_analysis.dto.UpDTO;
import com.qd33.bilibili_analysis.dto.DTOConverter;
import com.qd33.bilibili_analysis.entity.Up;
import com.qd33.bilibili_analysis.repository.UpRepository;
import com.qd33.bilibili_analysis.service.PythonCrawlerService;
import com.qd33.bilibili_analysis.service.UpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/up")
public class UpController {

    @Autowired
    private PythonCrawlerService pythonCrawlerService;

    @Autowired
    private UpService upService;

    @Autowired
    private UpRepository upRepository;

    // 🆕 修复：当UP主不存在时自动触发爬取
    @GetMapping("/{uid}")
    public Map<String, Object> getUpByUid(@PathVariable String uid) {
        System.out.println("🔍 获取UP主信息: " + uid);
        Map<String, Object> result = new HashMap<>();
        try {
            Optional<Up> upOptional = upRepository.findByUid(uid);

            if (upOptional.isPresent()) {
                // UP主存在，直接返回
                Up up = upOptional.get();
                UpDTO upDTO = DTOConverter.convertToUpDTO(up);
                result.put("success", true);
                result.put("up", upDTO);
                System.out.println("✅ 成功返回UP主DTO: " + upDTO.getName());
            } else {
                System.out.println("🔄 UP主不存在，尝试自动爬取: " + uid);

                // 自动触发爬取
                Map<String, Object> crawlResult = triggerUpCrawl(uid);

                if (Boolean.TRUE.equals(crawlResult.get("success"))) {
                    // 爬取成功，重新查询
                    System.out.println("🔄 爬取成功，重新查询数据库...");
                    upOptional = upRepository.findByUid(uid);
                    if (upOptional.isPresent()) {
                        Up up = upOptional.get();
                        UpDTO upDTO = DTOConverter.convertToUpDTO(up);
                        result.put("success", true);
                        result.put("up", upDTO);
                        result.put("message", "UP主数据已自动爬取并返回");
                        result.put("autoCrawled", true);
                        System.out.println("✅ 自动爬取成功，返回UP主: " + upDTO.getName());
                    } else {
                        result.put("success", false);
                        result.put("message", "UP主不存在且爬取后仍未找到");
                        System.out.println("❌ 自动爬取后仍未找到UP主: " + uid);
                    }
                } else {
                    result.put("success", false);
                    result.put("message", "UP主不存在且自动爬取失败: " + crawlResult.get("message"));
                    System.out.println("❌ 自动爬取失败: " + crawlResult.get("message"));
                }
            }
        } catch (Exception e) {
            System.err.println("❌ 获取UP主信息失败: " + e.getMessage());
            result.put("success", false);
            result.put("message", "获取UP主信息失败: " + e.getMessage());
        }
        return result;
    }

    // 🆕 检查UP主是否存在 - 也支持自动爬取
    @GetMapping("/{uid}/exists")
    public Map<String, Object> checkUpExists(@PathVariable String uid) {
        Map<String, Object> result = new HashMap<>();
        try {
            boolean exists = upService.upExists(uid);

            if (!exists) {
                System.out.println("🔄 UP主不存在，尝试自动爬取: " + uid);
                Map<String, Object> crawlResult = triggerUpCrawl(uid);
                exists = Boolean.TRUE.equals(crawlResult.get("success")) && upService.upExists(uid);
                result.put("autoCrawled", true);
            }

            result.put("success", true);
            result.put("exists", exists);
            result.put("uid", uid);
            System.out.println("✅ 检查UP主存在: " + uid + " -> " + exists);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "检查失败: " + e.getMessage());
            System.err.println("❌ 检查UP主存在失败: " + e.getMessage());
        }
        return result;
    }

    // 🆕 获取UP主视频列表 - 支持自动爬取
    @GetMapping("/{uid}/videos")
    public Map<String, Object> getUpVideos(@PathVariable String uid) {
        System.out.println("🎬 获取UP主视频列表: " + uid);

        // 首先检查UP主是否存在，不存在则自动爬取
        if (!upService.upExists(uid)) {
            System.out.println("🔄 UP主不存在，先自动爬取: " + uid);
            Map<String, Object> crawlResult = triggerUpCrawl(uid);
            if (!Boolean.TRUE.equals(crawlResult.get("success"))) {
                return crawlResult; // 返回爬取失败的信息
            }
        }

        return upService.getUpWithVideos(uid);
    }

    // 🆕 获取UP主完整信息（包含视频）- 支持自动爬取
    @GetMapping("/{uid}/detail")
    public Map<String, Object> getUpDetailWithVideos(@PathVariable String uid) {
        System.out.println("📊 获取UP主完整信息: " + uid);

        // 首先检查UP主是否存在，不存在则自动爬取
        if (!upService.upExists(uid)) {
            System.out.println("🔄 UP主不存在，先自动爬取: " + uid);
            Map<String, Object> crawlResult = triggerUpCrawl(uid);
            if (!Boolean.TRUE.equals(crawlResult.get("success"))) {
                return crawlResult; // 返回爬取失败的信息
            }
        }

        return upService.getUpWithVideos(uid);
    }

    // 🆕 获取UP主趋势数据
    @GetMapping("/{uid}/trend")
    public Map<String, Object> getUpTrend(@PathVariable String uid) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 首先检查UP主是否存在，不存在则自动爬取
            if (!upService.upExists(uid)) {
                System.out.println("🔄 UP主不存在，先自动爬取: " + uid);
                Map<String, Object> crawlResult = triggerUpCrawl(uid);
                if (!Boolean.TRUE.equals(crawlResult.get("success"))) {
                    return crawlResult; // 返回爬取失败的信息
                }
            }

            Object trendData = upService.getUpTrend(uid);
            result.put("success", true);
            result.put("trend", trendData);
            result.put("uid", uid);
            System.out.println("📈 获取UP主趋势数据: " + uid);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取趋势数据失败: " + e.getMessage());
            System.err.println("❌ 获取UP主趋势数据失败: " + e.getMessage());
        }
        return result;
    }

    @GetMapping("/checkStatus")
    public Map<String, Object> checkCrawlerStatus() {
        return pythonCrawlerService.checkCrawlerStatus();
    }

    @PostMapping("/crawl")
    public Map<String, Object> crawlUpData(@RequestParam String uid) {
        System.out.println("🚀 触发UP主数据爬取: " + uid);
        return pythonCrawlerService.crawlUpData(uid);
    }

    // 🆕 使用服务层的爬取方法
    @PostMapping("/{uid}/crawl")
    public Map<String, Object> triggerUpCrawl(@PathVariable String uid) {
        System.out.println("🎯 服务层UP主数据爬取: " + uid);
        return upService.triggerUpCrawl(uid);
    }

    @GetMapping("/testPython")
    public Map<String, Object> testPythonEnvironment() {
        return pythonCrawlerService.checkPythonEnvironment();
    }

    @GetMapping("/testScriptPath")
    public Map<String, Object> testScriptPath() {
        return pythonCrawlerService.testPythonScriptPath();
    }
}