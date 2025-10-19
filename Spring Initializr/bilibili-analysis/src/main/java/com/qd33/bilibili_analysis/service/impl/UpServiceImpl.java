package com.qd33.bilibili_analysis.service.impl;

import com.qd33.bilibili_analysis.repository.UpRepository;
import com.qd33.bilibili_analysis.repository.UpStatRepository;
import com.qd33.bilibili_analysis.repository.VideoRepository;
import com.qd33.bilibili_analysis.service.PythonCrawlerService;
import com.qd33.bilibili_analysis.service.UpService;
import com.qd33.bilibili_analysis.entity.Up;
import com.qd33.bilibili_analysis.entity.Video;
import com.qd33.bilibili_analysis.entity.UpStat;
import com.qd33.bilibili_analysis.dto.UpDTO;
import com.qd33.bilibili_analysis.dto.VideoSimpleDTO;
import com.qd33.bilibili_analysis.dto.DTOConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Service("upService")
@Transactional
public class UpServiceImpl implements UpService {

    @Autowired
    private UpRepository upRepository;

    @Autowired
    private UpStatRepository upStatRepository;

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    @Lazy
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
            List<Video> videos = videoRepository.findByUpUid(uid);
            List<UpStat> stats = upStatRepository.findByUpUidOrderByRecordDateAsc(uid);

            result.put("success", true);
            result.put("up", up);
            result.put("videos", videos);
            result.put("stats", stats);
            result.put("statsCount", stats.size());
            result.put("videoCount", videos.size());

            System.out.println("✅ 成功查询UP主: " + uid + ", 视频数量: " + videos.size());
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
            Up up;

            // 🆕 修复：支持 HashMap 到 Up 实体的转换
            if (upObj instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> upMap = (Map<String, Object>) upObj;
                up = new Up();
                up.setUid((String) upMap.get("uid"));
                up.setName((String) upMap.get("name"));
                up.setAvatar((String) upMap.get("avatar"));
            } else if (upObj instanceof Up) {
                up = (Up) upObj;
            } else {
                result.put("success", false);
                result.put("message", "不支持的数据类型: " + upObj.getClass().getSimpleName());
                return result;
            }

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

    // 🆕 获取UP主信息包含视频列表 - 优先使用真实数据，回退到模拟数据
    @Override
    public Map<String, Object> getUpWithVideos(String uid) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 获取UP主基本信息
            Map<String, Object> upResult = getUpByUid(uid);

            if (!(Boolean) upResult.get("success")) {
                return upResult;
            }

            // 将Up实体转换为UpDTO
            Up up = (Up) upResult.get("up");
            UpDTO upDTO = DTOConverter.convertToUpDTO(up);

            // 🆕 优先使用数据库中的真实视频数据
            List<Video> realVideos = videoRepository.findByUpUid(uid);
            List<VideoSimpleDTO> videoDTOs;

            if (realVideos != null && !realVideos.isEmpty()) {
                // 使用真实数据
                videoDTOs = realVideos.stream()
                        .map(DTOConverter::convertToVideoSimpleDTO)
                        .collect(Collectors.toList());
                System.out.println("✅ 使用真实视频数据，数量: " + videoDTOs.size());
            } else {
                // 回退到模拟数据
                videoDTOs = generateLaFanQieMockVideos();
                System.out.println("⚠️ 使用模拟视频数据，数量: " + videoDTOs.size());
            }

            result.put("success", true);
            result.put("up", upDTO);
            result.put("stats", upResult.get("stats"));
            result.put("videos", videoDTOs);
            result.put("videoCount", videoDTOs.size());
            result.put("message", "成功获取UP主信息及视频列表");

            System.out.println("✅ 获取UP主完整信息: " + uid + ", 视频数量: " + videoDTOs.size());

        } catch (Exception e) {
            System.err.println("❌ 获取UP主视频列表失败: " + e.getMessage());
            result.put("success", false);
            result.put("message", "获取视频列表失败: " + e.getMessage());
        }

        return result;
    }

    // 🆕 使用你提供的模拟数据
    private List<VideoSimpleDTO> generateLaFanQieMockVideos() {
        List<VideoSimpleDTO> videos = new ArrayList<>();

        // 视频1 - 完全按照你提供的格式
        VideoSimpleDTO video1 = new VideoSimpleDTO();
        video1.setId(11L);
        video1.setBvid("BV1jxHJzXE1V");
        video1.setTitle("我复活啦！！！");
        video1.setCoverUrl("http://i1.hdslb.com/bfs/archive/22b3e283294fa98b68968f267b29a75555b48b43.jpg");
        video1.setDescription("游戏：The Drifter\n祝大家假期快乐，吃好喝好~！\n本来以为这期视频时长在10分钟左右，做着做着就快半小时了！\n游戏二创视频制作不易，如果喜欢就一键三连一下吧！！！万分感谢啦！！！");
        video1.setPlay(0);
        video1.setLike(0);
        video1.setDanmaku(0);
        video1.setComment(0);
        video1.setCoin(0);
        video1.setShare(0);
        video1.setFavorite(0);
        video1.setPublishTime("2025-10-02T11:20");
        video1.setVideoPartition("单机游戏");
        videos.add(video1);

        // 视频2
        VideoSimpleDTO video2 = new VideoSimpleDTO();
        video2.setId(12L);
        video2.setBvid("BV1KXpDz8Ehe");
        video2.setTitle("在从小生活的地方当导游！！感觉我是假的本地人……");
        video2.setCoverUrl("http://i1.hdslb.com/bfs/archive/9c9fc735ce8be2560c7ebe6e27be0ffba78547c6.jpg");
        video2.setDescription("感谢元宝老师的指导！");
        video2.setPlay(0);
        video2.setLike(0);
        video2.setDanmaku(0);
        video2.setComment(0);
        video2.setCoin(0);
        video2.setShare(0);
        video2.setFavorite(0);
        video2.setPublishTime("2025-09-20T17:00");
        video2.setVideoPartition("出行");
        videos.add(video2);

        // 视频3
        VideoSimpleDTO video3 = new VideoSimpleDTO();
        video3.setId(13L);
        video3.setBvid("BV1vKpVzKEmC");
        video3.setTitle("史上最难越狱");
        video3.setCoverUrl("http://i2.hdslb.com/bfs/archive/72563e83994532f21c796e2302406b624c436c70.jpg");
        video3.setDescription("游戏：越狱模拟器\n喜欢的话能给我一个三连吗？谢谢你！！！！");
        video3.setPlay(0);
        video3.setLike(0);
        video3.setDanmaku(0);
        video3.setComment(0);
        video3.setCoin(0);
        video3.setShare(0);
        video3.setFavorite(0);
        video3.setPublishTime("2025-09-13T11:20");
        video3.setVideoPartition("单机游戏");
        videos.add(video3);

        return videos;
    }

    // 🆕 保存视频数据的方法（供爬虫调用）
    public Map<String, Object> saveVideoData(String uid, List<Map<String, Object>> videoDataList) {
        Map<String, Object> result = new HashMap<>();
        try {
            Optional<Up> upOpt = upRepository.findByUid(uid);
            if (!upOpt.isPresent()) {
                result.put("success", false);
                result.put("message", "UP主不存在");
                return result;
            }

            Up up = upOpt.get();
            List<Video> savedVideos = new ArrayList<>();

            for (Map<String, Object> videoData : videoDataList) {
                // 🆕 修复：正确处理 bvid 字段映射
                String bvId = (String) videoData.get("bvid");
                if (bvId == null) {
                    bvId = (String) videoData.get("bv_id"); // 尝试其他可能的字段名
                }

                if (bvId == null) {
                    System.err.println("❌ 视频数据缺少bvid字段: " + videoData);
                    continue;
                }

                // 检查视频是否已存在
                Optional<Video> existingVideoOpt = videoRepository.findByBvId(bvId);
                Video video;

                if (existingVideoOpt.isPresent()) {
                    // 更新现有视频
                    video = existingVideoOpt.get();
                } else {
                    // 创建新视频
                    video = new Video();
                    video.setBvId(bvId);
                    video.setUp(up);
                }

                // 设置/更新视频信息
                video.setTitle((String) videoData.get("title"));
                video.setCoverUrl((String) videoData.get("cover_url"));
                video.setDescription((String) videoData.get("description"));

                // 处理发布时间
                if (videoData.get("publish_time") != null) {
                    try {
                        String timeStr = videoData.get("publish_time").toString();
                        LocalDateTime publishTime = LocalDateTime.parse(timeStr.replace(" ", "T"));
                        video.setPublishTime(publishTime);
                    } catch (Exception e) {
                        System.err.println("❌ 解析发布时间失败: " + e.getMessage());
                        video.setPublishTime(LocalDateTime.now());
                    }
                } else {
                    video.setPublishTime(LocalDateTime.now());
                }

                video.setVideoPartition((String) videoData.get("video_partition"));

                // 设置时长
                if (videoData.get("duration") != null) {
                    video.setDuration(getIntegerValue(videoData.get("duration")));
                }

                // 🆕 设置统计信息
                video.setPlayCount(getIntegerValue(videoData.get("play")));
                video.setLikeCount(getIntegerValue(videoData.get("like")));
                video.setDanmakuCount(getIntegerValue(videoData.get("danmaku")));
                video.setCommentCount(getIntegerValue(videoData.get("comment")));
                video.setCoinCount(getIntegerValue(videoData.get("coin")));
                video.setShareCount(getIntegerValue(videoData.get("share")));
                video.setFavoriteCount(getIntegerValue(videoData.get("favorite")));

                Video savedVideo = videoRepository.save(video);
                savedVideos.add(savedVideo);
            }

            result.put("success", true);
            result.put("message", "成功保存 " + savedVideos.size() + " 个视频");
            result.put("videos", savedVideos);

        } catch (Exception e) {
            System.err.println("❌ 保存视频数据失败: " + e.getMessage());
            result.put("success", false);
            result.put("message", "保存视频数据失败: " + e.getMessage());
        }
        return result;
    }

    // 🆕 辅助方法：安全地获取整数值
    private Integer getIntegerValue(Object value) {
        if (value == null) {
            return 0;
        }
        try {
            if (value instanceof Integer) {
                return (Integer) value;
            } else {
                return Integer.parseInt(value.toString());
            }
        } catch (NumberFormatException e) {
            System.err.println("❌ 转换整数值失败: " + value);
            return 0;
        }
    }
}