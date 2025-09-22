package com.qd33.bilibili_analysis.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "video_stat",
        uniqueConstraints = @UniqueConstraint(columnNames = {"video_id", "record_date"}))
public class VideoStat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "video_id")
    private Video video;

    private LocalDate recordDate;
    private Long viewCount;
    private Long likeCount;
    private Long coinCount;
    private Long favoriteCount;
    private Long danmakuCount;
    private Long replyCount;
    private Long shareCount;

    // Getter and Setter methods
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Video getVideo() { return video; }
    public void setVideo(Video video) { this.video = video; }

    public LocalDate getRecordDate() { return recordDate; }
    public void setRecordDate(LocalDate recordDate) { this.recordDate = recordDate; }

    public Long getViewCount() { return viewCount; }
    public void setViewCount(Long viewCount) { this.viewCount = viewCount; }

    public Long getLikeCount() { return likeCount; }
    public void setLikeCount(Long likeCount) { this.likeCount = likeCount; }

    public Long getCoinCount() { return coinCount; }
    public void setCoinCount(Long coinCount) { this.coinCount = coinCount; }

    public Long getFavoriteCount() { return favoriteCount; }
    public void setFavoriteCount(Long favoriteCount) { this.favoriteCount = favoriteCount; }

    public Long getDanmakuCount() { return danmakuCount; }
    public void setDanmakuCount(Long danmakuCount) { this.danmakuCount = danmakuCount; }

    public Long getReplyCount() { return replyCount; }
    public void setReplyCount(Long replyCount) { this.replyCount = replyCount; }

    public Long getShareCount() { return shareCount; }
    public void setShareCount(Long shareCount) { this.shareCount = shareCount; }
}