package com.qd33.bilibili_analysis.controller;

import com.qd33.bilibili_analysis.service.PythonCrawlerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/up")
public class UpController {

    @Autowired
    private PythonCrawlerService pythonCrawlerService;

    @GetMapping("/checkStatus")
    public Map<String, Object> checkCrawlerStatus() {
        Map<String, Object> statusResult = pythonCrawlerService.checkCrawlerStatus();

        // 安全地处理嵌套的 Map
        Object pythonEnvObj = statusResult.get("pythonEnvironment");
        if (pythonEnvObj instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> pythonEnvironment = (Map<String, Object>) pythonEnvObj;
            // 使用 pythonEnvironment
            System.out.println("Python环境状态: " + pythonEnvironment.get("success"));
        }

        Object scriptPathObj = statusResult.get("scriptPath");
        if (scriptPathObj instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> scriptPath = (Map<String, Object>) scriptPathObj;
            // 使用 scriptPath
            System.out.println("脚本路径状态: " + scriptPath.get("success"));
        }

        return statusResult;
    }

    @PostMapping("/crawl")
    public Map<String, Object> crawlUpData(@RequestParam String uid) {
        return pythonCrawlerService.crawlUpData(uid);
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