package com.qd33.bilibili_analysis.service.impl;

import com.qd33.bilibili_analysis.entity.Up;
import com.qd33.bilibili_analysis.entity.UpStat;
import com.qd33.bilibili_analysis.repository.UpRepository;
import com.qd33.bilibili_analysis.repository.UpStatRepository;
import com.qd33.bilibili_analysis.service.PythonCrawlerService;
import com.qd33.bilibili_analysis.service.UpService;
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
            result.put("videos", generateMockVideoList(uid));
            result.put("videoCount", 8);

        } catch (Exception e) {
            System.err.println("❌ 获取UP主视频列表失败: " + e.getMessage());
            result.put("success", false);
            result.put("message", "获取视频列表失败: " + e.getMessage());
        }

        return result;
    }

    // 🆕 生成模拟视频列表
    private Object generateMockVideoList(String uid) {
        return new Object[] {
                Map.of(
                        "bvId", "BV1A" + uid.substring(0, 6),
                        "title", "【生活VLOG】测试视频1 - UP主" + uid,
                        "viewCount", 15842,
                        "likeCount", 1250,
                        "coinCount", 580,
                        "favoriteCount", 320,
                        "publishTime", "2025-10-15T10:00:00",
                        "partition", "生活"
                ),
                Map.of(
                        "bvId", "BV1B" + uid.substring(0, 6),
                        "title", "【科技测评】测试视频2 - UP主" + uid,
                        "viewCount", 23467,
                        "likeCount", 1890,
                        "coinCount", 920,
                        "favoriteCount", 650,
                        "publishTime", "2025-10-10T14:30:00",
                        "partition", "科技"
                ),
                Map.of(
                        "bvId", "BV1C" + uid.substring(0, 6),
                        "title", "【游戏实况】测试视频3 - UP主" + uid,
                        "viewCount", 18753,
                        "likeCount", 1420,
                        "coinCount", 680,
                        "favoriteCount", 420,
                        "publishTime", "2025-10-05T20:15:00",
                        "partition", "游戏"
                )
        };
    }
}