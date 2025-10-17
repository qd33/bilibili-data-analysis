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

    @GetMapping("/{uid}")
    public Map<String, Object> getUpByUid(@PathVariable String uid) {
        System.out.println("🔍 获取UP主信息: " + uid);
        try {
            Optional<Up> upOptional = upRepository.findByUid(uid);

            if (upOptional.isPresent()) {
                Up up = upOptional.get();
                UpDTO upDTO = DTOConverter.convertToUpDTO(up);

                Map<String, Object> result = new HashMap<>();
                result.put("success", true);
                result.put("up", upDTO);
                System.out.println("✅ 成功返回UP主DTO: " + upDTO.getName());
                return result;
            } else {
                System.out.println("🔄 UP主不存在，自动触发爬取: " + uid);
                Map<String, Object> crawlResult = upService.triggerUpCrawl(uid);

                if (Boolean.TRUE.equals(crawlResult.get("success"))) {
                    upOptional = upRepository.findByUid(uid);
                    if (upOptional.isPresent()) {
                        Up up = upOptional.get();
                        UpDTO upDTO = DTOConverter.convertToUpDTO(up);

                        Map<String, Object> result = new HashMap<>();
                        result.put("success", true);
                        result.put("up", upDTO);
                        result.put("message", "数据已自动爬取并加载");
                        return result;
                    }
                }

                Map<String, Object> result = new HashMap<>();
                result.put("success", false);
                result.put("code", "UP_NOT_EXIST");
                result.put("message", "UP主不存在且自动爬取失败");
                return result;
            }
        } catch (Exception e) {
            System.err.println("❌ 获取UP主信息失败: " + e.getMessage());

            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "获取UP主信息失败: " + e.getMessage());
            return result;
        }
    }

    // 🆕 检查UP主是否存在
    @GetMapping("/{uid}/exists")
    public Map<String, Object> checkUpExists(@PathVariable String uid) {
        Map<String, Object> result = new HashMap<>();
        try {
            boolean exists = upService.upExists(uid);
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

    // 🆕 获取UP主视频列表
    @GetMapping("/{uid}/videos")
    public Map<String, Object> getUpVideos(@PathVariable String uid) {
        System.out.println("🎬 获取UP主视频列表: " + uid);
        return upService.getUpWithVideos(uid);
    }

    // 🆕 获取UP主完整信息（包含视频）
    @GetMapping("/{uid}/detail")
    public Map<String, Object> getUpDetailWithVideos(@PathVariable String uid) {
        System.out.println("📊 获取UP主完整信息: " + uid);
        return upService.getUpWithVideos(uid);
    }

    // 🆕 获取UP主趋势数据
    @GetMapping("/{uid}/trend")
    public Map<String, Object> getUpTrend(@PathVariable String uid) {
        Map<String, Object> result = new HashMap<>();
        try {
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
        Map<String, Object> statusResult = pythonCrawlerService.checkCrawlerStatus();

        // 安全地处理嵌套的 Map
        Object pythonEnvObj = statusResult.get("pythonEnvironment");
        if (pythonEnvObj instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> pythonEnvironment = (Map<String, Object>) pythonEnvObj;
            System.out.println("Python环境状态: " + pythonEnvironment.get("success"));
        }

        Object scriptPathObj = statusResult.get("scriptPath");
        if (scriptPathObj instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> scriptPath = (Map<String, Object>) scriptPathObj;
            System.out.println("脚本路径状态: " + scriptPath.get("success"));
        }

        return statusResult;
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