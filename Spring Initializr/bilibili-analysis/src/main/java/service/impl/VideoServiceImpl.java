package com.qd33.bilibili_analysis.service.impl;

import com.qd33.bilibili_analysis.entity.Video;
import com.qd33.bilibili_analysis.entity.VideoStat;
import com.qd33.bilibili_analysis.repository.VideoRepository;
import com.qd33.bilibili_analysis.repository.VideoStatRepository;
import com.qd33.bilibili_analysis.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class VideoServiceImpl implements VideoService {

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private VideoStatRepository videoStatRepository;

    @Override
    public Map<String, Object> getVideoByBvId(String bvId) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 使用Repository查询视频
            boolean exists = videoRepository.existsByBvId(bvId);

            if (!exists) {
                result.put("success", false);
                result.put("message", "视频不存在");
                return result;
            }

            // 获取视频详情
            Video video = videoRepository.findByBvId(bvId).get();
            List<VideoStat> stats = videoStatRepository.findByVideoBvIdOrderByRecordDateAsc(bvId);

            result.put("success", true);
            result.put("video", video);
            result.put("stats", stats);
            result.put("statsCount", stats.size());

            System.out.println("成功查询视频: " + bvId);
        } catch (Exception e) {
            System.err.println("查询视频失败: " + bvId + ", 错误: " + e.getMessage());
            result.put("success", false);
            result.put("message", "查询失败: " + e.getMessage());
        }

        return result;
    }

    @Override
    public boolean videoExists(String bvId) {
        return videoRepository.existsByBvId(bvId);
    }

    @Override
    public Map<String, Object> saveVideo(Object videoObj) {
        Map<String, Object> result = new HashMap<>();

        try {
            Video video = (Video) videoObj;

            if (videoRepository.existsByBvId(video.getBvId())) {
                result.put("success", false);
                result.put("message", "视频已存在");
                return result;
            }

            Video savedVideo = videoRepository.save(video);
            result.put("success", true);
            result.put("video", savedVideo);
            result.put("message", "视频保存成功");

            System.out.println("成功保存视频: " + video.getBvId());
        } catch (Exception e) {
            System.err.println("保存视频失败: " + e.getMessage());
            result.put("success", false);
            result.put("message", "保存失败: " + e.getMessage());
        }

        return result;
    }

    @Override
    public Map<String, Object> saveVideoStat(Object videoStatObj) {
        Map<String, Object> result = new HashMap<>();

        try {
            VideoStat videoStat = (VideoStat) videoStatObj;
            String bvId = videoStat.getVideo().getBvId();
            LocalDate recordDate = videoStat.getRecordDate();

            // 检查是否已存在相同日期的数据
            List<VideoStat> existingStats = videoStatRepository
                    .findByVideoBvIdAndRecordDateBetween(bvId, recordDate, recordDate);

            if (!existingStats.isEmpty()) {
                // 更新现有数据
                VideoStat existing = existingStats.get(0);
                existing.setViewCount(videoStat.getViewCount());
                existing.setLikeCount(videoStat.getLikeCount());
                existing.setCoinCount(videoStat.getCoinCount());
                existing.setFavoriteCount(videoStat.getFavoriteCount());
                existing.setDanmakuCount(videoStat.getDanmakuCount());
                existing.setReplyCount(videoStat.getReplyCount());
                existing.setShareCount(videoStat.getShareCount());

                videoStatRepository.save(existing);
                result.put("message", "统计数据已更新");
            } else {
                // 新增数据
                videoStatRepository.save(videoStat);
                result.put("message", "统计数据已保存");
            }

            result.put("success", true);
            System.out.println("成功保存视频统计: " + bvId);
        } catch (Exception e) {
            System.err.println("保存视频统计失败: " + e.getMessage());
            result.put("success", false);
            result.put("message", "保存失败: " + e.getMessage());
        }

        return result;
    }

    @Override
    public Object getVideoTrend(String bvId) {
        return videoStatRepository.findByVideoBvIdOrderByRecordDateAsc(bvId);
    }
}