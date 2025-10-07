package com.qd33.bilibili_analysis.controller;

import com.qd33.bilibili_analysis.entity.Up;
import com.qd33.bilibili_analysis.service.UpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/up")
public class UpController {

    @Autowired
    private UpService upService;

    // 🆕 添加测试方法
    @GetMapping("/test")
    public String test() {
        return "UpController 正常工作！";
    }

    // 根据UID查询UP主详情
    @GetMapping("/{uid}")
    public ResponseEntity<?> getUp(@PathVariable String uid) {
        return ResponseEntity.ok(upService.getUpByUid(uid));
    }

    // 保存UP主信息
    @PostMapping
    public ResponseEntity<?> saveUp(@RequestBody Up up) {
        return ResponseEntity.ok(upService.saveUp(up));
    }

    // 获取UP主粉丝增长趋势
    @GetMapping("/{uid}/trend")
    public ResponseEntity<?> getUpTrend(@PathVariable String uid) {
        return ResponseEntity.ok(upService.getUpTrend(uid));
    }

    // 检查UP主是否存在
    @GetMapping("/{uid}/exists")
    public ResponseEntity<?> upExists(@PathVariable String uid) {
        return ResponseEntity.ok(upService.upExists(uid));
    }
}