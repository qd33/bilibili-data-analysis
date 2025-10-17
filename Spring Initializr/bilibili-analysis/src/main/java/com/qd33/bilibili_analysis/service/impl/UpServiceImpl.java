package com.qd33.bilibili_analysis.service.impl;

import com.qd33.bilibili_analysis.repository.UpRepository;
import com.qd33.bilibili_analysis.repository.UpStatRepository;
import com.qd33.bilibili_analysis.service.PythonCrawlerService;
import com.qd33.bilibili_analysis.service.UpService;
import com.qd33.bilibili_analysis.entity.Up;
import com.qd33.bilibili_analysis.entity.UpStat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class UpServiceImpl implements UpService {

    @Autowired
    private UpRepository upRepository;

    @Autowired
    private UpStatRepository upStatRepository;

    @Autowired
    private PythonCrawlerService pythonCrawlerService;

    @Override
    public Map<String, Object> getUpByUid(String uid) {
        Map<String, Object> result = new HashMap<>();

        try {
            boolean exists = upRepository.existsByUid(uid);

            if (!exists) {
                result.put("success", false);
                result.put("message", "UP主不存在");
                return result;
            }

            Up up = upRepository.findByUid(uid).get();
            List<UpStat> stats = upStatRepository.findByUpUidOrderByRecordDateAsc(uid);

            result.put("success", true);
            result.put("up", up);
            result.put("stats", stats);
            result.put("statsCount", stats.size());

            System.out.println("✅ 成功查询UP主: " + uid);
        } catch (Exception e) {
            System.err.println("❌ 查询UP主失败: " + uid + ", 错误: " + e.getMessage());
            result.put("success", false);
            result.put("message", "查询失败: " + e.getMessage());
        }

        return result;
    }

    @Override
    public boolean upExists(String uid) {
        return upRepository.existsByUid(uid);
    }

    @Override
    public Map<String, Object> saveUp(Object upObj) {
        Map<String, Object> result = new HashMap<>();

        try {
            Up up = (Up) upObj;

            if (upRepository.existsByUid(up.getUid())) {
                result.put("success", false);
                result.put("message", "UP主已存在");
                return result;
            }

            Up savedUp = upRepository.save(up);
            result.put("success", true);
            result.put("up", savedUp);
            result.put("message", "UP主保存成功");

            System.out.println("✅ 成功保存UP主: " + up.getUid());
        } catch (Exception e) {
            System.err.println("❌ 保存UP主失败: " + e.getMessage());
            result.put("success", false);
            result.put("message", "保存失败: " + e.getMessage());
        }

        return result;
    }

    @Override
    public Map<String, Object> saveUpStat(Object upStatObj) {
        Map<String, Object> result = new HashMap<>();

        try {
            if (upStatObj instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> statData = (Map<String, Object>) upStatObj;

                // 获取UID并查找UP主
                String uid = (String) statData.get("uid");
                Optional<Up> upOpt = upRepository.findByUid(uid);

                if (!upOpt.isPresent()) {
                    result.put("success", false);
                    result.put("message", "UP主不存在，请先保存UP主基本信息");
                    return result;
                }

                Up up = upOpt.get();
                LocalDate recordDate = LocalDate.parse(statData.get("recordDate").toString());

                // 检查是否已存在相同日期的数据
                List<UpStat> existingStats = upStatRepository
                        .findByUpUidAndRecordDateBetween(uid, recordDate, recordDate);

                UpStat upStat;
                if (!existingStats.isEmpty()) {
                    // 更新现有数据
                    upStat = existingStats.get(0);
                    result.put("message", "统计数据已更新");
                } else {
                    // 创建新数据
                    upStat = new UpStat();
                    upStat.setUp(up);
                    upStat.setRecordDate(recordDate);
                    result.put("message", "统计数据已保存");
                }

                // 设置统计数据
                upStat.setFollowerCount(Long.valueOf(statData.get("followerCount").toString()));
                upStat.setTotalViewCount(Long.valueOf(statData.get("totalViewCount").toString()));

                upStatRepository.save(upStat);
                result.put("success", true);
                result.put("upStat", upStat);

                System.out.println("✅ 成功保存UP主统计: " + uid + " - " + recordDate);
            } else {
                result.put("success", false);
                result.put("message", "数据格式错误");
            }

        } catch (Exception e) {
            System.err.println("❌ 保存UP主统计失败: " + e.getMessage());
            result.put("success", false);
            result.put("message", "保存失败: " + e.getMessage());
        }

        return result;
    }

    @Override
    public Object getUpTrend(String uid) {
        return upStatRepository.findByUpUidOrderByRecordDateAsc(uid);
    }

    // 🆕 触发UP主数据抓取
    @Override
    public Map<String, Object> triggerUpCrawl(String uid) {
        Map<String, Object> result = new HashMap<>();

        try {
            System.out.println("🎯 服务层开始UP主数据抓取: " + uid);

            // 调用Python爬虫服务
            Map<String, Object> crawlResult = pythonCrawlerService.crawlUpData(uid);

            result.putAll(crawlResult);
            result.put("uid", uid);
            result.put("service", "UpService");

            System.out.println("✅ 服务层UP主抓取完成: " + crawlResult.get("success"));

        } catch (Exception e) {
            System.err.println("❌ 服务层UP主抓取失败: " + e.getMessage());
            result.put("success", false);
            result.put("message", "服务层抓取失败: " + e.getMessage());
        }

        return result;
    }

    // 🆕 获取UP主信息包含视频列表
    @Override
    public Map<String, Object> getUpWithVideos(String uid) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 获取UP主基本信息
            Map<String, Object> upResult = getUpByUid(uid);

            if (!(Boolean) upResult.get("success")) {
                return upResult;
            }

            result.put("success", true);
            result.put("up", upResult.get("up"));
            result.put("stats", upResult.get("stats"));

            // 🆕 这里应该从数据库查询该UP主的视频列表
            // 暂时使用模拟数据
            result.put("videos", generateEnhancedMockVideoList(uid));
            result.put("videoCount", 8);
            result.put("message", "成功获取UP主信息及视频列表");

            System.out.println("✅ 获取UP主完整信息: " + uid);

        } catch (Exception e) {
            System.err.println("❌ 获取UP主视频列表失败: " + e.getMessage());
            result.put("success", false);
            result.put("message", "获取视频列表失败: " + e.getMessage());
        }

        return result;
    }

    // 🆕 修复的模拟视频列表生成 - 使用HashMap代替Map.of
    private Object generateEnhancedMockVideoList(String uid) {
        // 创建视频1
        Map<String, Object> video1 = new HashMap<>();
        video1.put("bvid", "BV1A" + (uid.length() >= 6 ? uid.substring(0, 6) : uid));
        video1.put("title", "【生活VLOG】测试视频1 - UP主" + uid);
        video1.put("pic", "https://example.com/cover1.jpg");
        video1.put("play", 15842);
        video1.put("view", 15842);
        video1.put("viewCount", 15842);
        video1.put("video_review", 1250);
        video1.put("danmaku", 1250);
        video1.put("danmakuCount", 1250);
        video1.put("like", 1250);
        video1.put("likeCount", 1250);
        video1.put("duration", 360);
        video1.put("pubdate", System.currentTimeMillis() - 86400000);
        video1.put("publishTime", "2025-10-15T10:00:00");
        video1.put("partition", "生活");
        video1.put("description", "这是一个测试视频描述");

        // 创建视频2
        Map<String, Object> video2 = new HashMap<>();
        video2.put("bvid", "BV1B" + (uid.length() >= 6 ? uid.substring(0, 6) : uid));
        video2.put("title", "【科技测评】测试视频2 - UP主" + uid);
        video2.put("pic", "https://example.com/cover2.jpg");
        video2.put("play", 23467);
        video2.put("view", 23467);
        video2.put("viewCount", 23467);
        video2.put("video_review", 1890);
        video2.put("danmaku", 1890);
        video2.put("danmakuCount", 1890);
        video2.put("like", 1890);
        video2.put("likeCount", 1890);
        video2.put("duration", 420);
        video2.put("pubdate", System.currentTimeMillis() - 172800000);
        video2.put("publishTime", "2025-10-10T14:30:00");
        video2.put("partition", "科技");
        video2.put("description", "科技产品测评视频");

        // 创建视频3
        Map<String, Object> video3 = new HashMap<>();
        video3.put("bvid", "BV1C" + (uid.length() >= 6 ? uid.substring(0, 6) : uid));
        video3.put("title", "【游戏实况】测试视频3 - UP主" + uid);
        video3.put("pic", "https://example.com/cover3.jpg");
        video3.put("play", 18753);
        video3.put("view", 18753);
        video3.put("viewCount", 18753);
        video3.put("video_review", 1420);
        video3.put("danmaku", 1420);
        video3.put("danmakuCount", 1420);
        video3.put("like", 1420);
        video3.put("likeCount", 1420);
        video3.put("duration", 580);
        video3.put("pubdate", System.currentTimeMillis() - 259200000);
        video3.put("publishTime", "2025-10-05T20:15:00");
        video3.put("partition", "游戏");
        video3.put("description", "游戏实况录制");

        return new Object[] { video1, video2, video3 };
    }
}