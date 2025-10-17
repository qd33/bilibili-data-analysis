package com.qd33.bilibili_analysis.dto;

import com.qd33.bilibili_analysis.entity.Up;
import com.qd33.bilibili_analysis.entity.Video;
import java.util.List;
import java.util.ArrayList;

public class DTOConverter {

    public static UpDTO convertToUpDTO(Up up) {
        if (up == null) {
            return null;
        }

        UpDTO dto = new UpDTO();
        dto.setId(up.getId());
        dto.setUid(up.getUid());
        dto.setName(up.getName());
        dto.setAvatar(up.getAvatar());

        // 转换视频列表
        if (up.getVideos() != null && !up.getVideos().isEmpty()) {
            List<VideoSimpleDTO> videoDTOs = new ArrayList<>();
            for (Video video : up.getVideos()) {
                VideoSimpleDTO videoDTO = convertToVideoSimpleDTO(video);
                if (videoDTO != null) {
                    videoDTOs.add(videoDTO);
                }
            }
            dto.setVideos(videoDTOs);
        }

        return dto;
    }

    public static VideoSimpleDTO convertToVideoSimpleDTO(Video video) {
        if (video == null) {
            return null;
        }

        VideoSimpleDTO dto = new VideoSimpleDTO();
        dto.setId(video.getId());
        dto.setBvid(video.getBvId());
        dto.setTitle(video.getTitle());
        dto.setCoverUrl(video.getCoverUrl());
        dto.setDescription(video.getDescription());
        dto.setPublishTime(video.getPublishTime() != null ? video.getPublishTime().toString() : null);
        dto.setVideoPartition(video.getVideoPartition());

        // 注意：统计数据需要从VideoStat获取，这里先设为0
        dto.setPlay(0);
        dto.setLike(0);
        dto.setDanmaku(0);

        return dto;
    }
}