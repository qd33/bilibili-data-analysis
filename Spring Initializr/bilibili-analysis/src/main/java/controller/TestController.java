package com.qd33.bilibili_analysis.controller; // 注意：你的包名是bilibili_analysis

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/")
    public String home() {
        return "哔哩哔哩数据分析网站主页！";
    }

    @GetMapping("/api/test/hello")
    public String hello() {
        return "哔哩哔哩数据分析后端服务已启动！";
    }
}