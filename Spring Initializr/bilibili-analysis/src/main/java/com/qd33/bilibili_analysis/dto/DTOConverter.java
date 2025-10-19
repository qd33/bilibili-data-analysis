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
        dto.setDuration(video.getDuration());

        // 🆕 关键修复：确保使用正确的Getter方法获取统计信息
        dto.setPlay(video.getPlayCount() != null ? video.getPlayCount() : 0);
        dto.setLike(video.getLikeCount() != null ? video.getLikeCount() : 0);
        dto.setDanmaku(video.getDanmakuCount() != null ? video.getDanmakuCount() : 0);
        dto.setComment(video.getCommentCount() != null ? video.getCommentCount() : 0);
        dto.setCoin(video.getCoinCount() != null ? video.getCoinCount() : 0);
        dto.setShare(video.getShareCount() != null ? video.getShareCount() : 0);
        dto.setFavorite(video.getFavoriteCount() != null ? video.getFavoriteCount() : 0);

        return dto;
    }
}