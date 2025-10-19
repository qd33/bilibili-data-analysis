package com.qd33.bilibili_analysis.controller;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URLDecoder;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/proxy")
public class ImageProxyController {

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 图片代理接口
     * @param imgUrl 原图片URL，需要URL编码
     */
    @GetMapping("/image")
    public ResponseEntity<byte[]> proxyImage(@RequestParam("url") String imgUrl) {
        try {
            System.out.println("🖼️ 图片代理请求: " + imgUrl);

            // 解码URL参数
            String decodedUrl = URLDecoder.decode(imgUrl, "UTF-8");

            // 创建请求头，设置Referer为B站域名以绕过防盗链
            HttpHeaders headers = new HttpHeaders();
            headers.set("Referer", "https://www.bilibili.com");
            headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
            headers.set("Accept", "image/webp,image/apng,image/*,*/*;q=0.8");

            HttpEntity<String> entity = new HttpEntity<>(headers);

            // 发送请求获取图片
            ResponseEntity<byte[]> response = restTemplate.exchange(
                    decodedUrl, HttpMethod.GET, entity, byte[].class);

            // 根据原图Content-Type设置响应类型
            HttpHeaders responseHeaders = new HttpHeaders();
            MediaType contentType = response.getHeaders().getContentType();
            if (contentType != null) {
                responseHeaders.setContentType(contentType);
            } else {
                responseHeaders.setContentType(MediaType.IMAGE_JPEG);
            }

            // 设置缓存策略
            responseHeaders.setCacheControl("public, max-age=86400"); // 缓存1天
            responseHeaders.set("X-Proxy-Source", "Bilibili Image Proxy");

            System.out.println("✅ 图片代理成功: " + decodedUrl);

            return new ResponseEntity<>(response.getBody(), responseHeaders, HttpStatus.OK);

        } catch (Exception e) {
            System.err.println("❌ 图片代理失败: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("图片代理失败: " + e.getMessage()).getBytes());
        }
    }

    /**
     * 健康检查接口
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("service", "Image Proxy Service");
        result.put("status", "running");
        result.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(result);
    }

    /**
     * 批量图片代理接口（可选）
     */
    @PostMapping("/batch-images")
    public ResponseEntity<Map<String, Object>> batchProxyImages(@RequestBody List<String> imageUrls) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Map<String, String>> processedUrls = new ArrayList<>();

            for (String imageUrl : imageUrls) {
                Map<String, String> urlInfo = new HashMap<>();
                String proxyUrl = "/api/proxy/image?url=" + java.net.URLEncoder.encode(imageUrl, "UTF-8");
                urlInfo.put("original", imageUrl);
                urlInfo.put("proxy", proxyUrl);
                processedUrls.add(urlInfo);
            }

            result.put("success", true);
            result.put("processedCount", processedUrls.size());
            result.put("urls", processedUrls);

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "批量处理失败: " + e.getMessage());
        }

        return ResponseEntity.ok(result);
    }
}