package com.qd33.bilibili_analysis.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;

@Entity
@Table(name = "video")
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bv_id", unique = true, nullable = false)
    @JsonProperty("bvid")
    private String bvId;

    @Column(name = "title")
    @JsonProperty("title")
    private String title;

    @ManyToOne
    @JoinColumn(name = "up_id")
    @JsonIgnore
    private Up up;

    @Column(name = "publish_time")
    @JsonProperty("publishTime")
    private LocalDateTime publishTime;

    @Column(name = "video_partition")
    @JsonProperty("partition")
    private String videoPartition;

    @Column(name = "cover_url", length = 500)
    @JsonProperty("cover")
    private String coverUrl;

    @Column(name = "description", columnDefinition = "TEXT")
    @JsonProperty("description")
    private String description;

    // 🆕 修复：确保字段名与数据库完全匹配
    @Column(name = "play_count")
    @JsonProperty("play")
    private Integer playCount = 0;

    @Column(name = "like_count")
    @JsonProperty("like")
    private Integer likeCount = 0;

    @Column(name = "danmaku_count")
    @JsonProperty("danmaku")
    private Integer danmakuCount = 0;

    @Column(name = "comment_count")
    @JsonProperty("comment")
    private Integer commentCount = 0;

    @Column(name = "coin_count")
    @JsonProperty("coin")
    private Integer coinCount = 0;

    @Column(name = "share_count")
    @JsonProperty("share")
    private Integer shareCount = 0;

    @Column(name = "favorite_count")
    @JsonProperty("favorite")
    private Integer favoriteCount = 0;

    @Column(name = "duration")
    @JsonProperty("duration")
    private Integer duration = 0;

    // Getter and Setter methods
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getBvId() { return bvId; }
    public void setBvId(String bvId) { this.bvId = bvId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Up getUp() { return up; }
    public void setUp(Up up) { this.up = up; }

    public LocalDateTime getPublishTime() { return publishTime; }
    public void setPublishTime(LocalDateTime publishTime) { this.publishTime = publishTime; }

    public String getVideoPartition() { return videoPartition; }
    public void setVideoPartition(String videoPartition) { this.videoPartition = videoPartition; }

    public String getCoverUrl() { return coverUrl; }
    public void setCoverUrl(String coverUrl) { this.coverUrl = coverUrl; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    // 🆕 修复：确保Getter方法正确映射到JSON字段
    public Integer getPlayCount() {
        return playCount != null ? playCount : 0;
    }
    public void setPlayCount(Integer playCount) {
        this.playCount = playCount != null ? playCount : 0;
    }

    public Integer getLikeCount() {
        return likeCount != null ? likeCount : 0;
    }
    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount != null ? likeCount : 0;
    }

    public Integer getDanmakuCount() {
        return danmakuCount != null ? danmakuCount : 0;
    }
    public void setDanmakuCount(Integer danmakuCount) {
        this.danmakuCount = danmakuCount != null ? danmakuCount : 0;
    }

    public Integer getCommentCount() {
        return commentCount != null ? commentCount : 0;
    }
    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount != null ? commentCount : 0;
    }

    public Integer getCoinCount() {
        return coinCount != null ? coinCount : 0;
    }
    public void setCoinCount(Integer coinCount) {
        this.coinCount = coinCount != null ? coinCount : 0;
    }

    public Integer getShareCount() {
        return shareCount != null ? shareCount : 0;
    }
    public void setShareCount(Integer shareCount) {
        this.shareCount = shareCount != null ? shareCount : 0;
    }

    public Integer getFavoriteCount() {
        return favoriteCount != null ? favoriteCount : 0;
    }
    public void setFavoriteCount(Integer favoriteCount) {
        this.favoriteCount = favoriteCount != null ? favoriteCount : 0;
    }

    public Integer getDuration() {
        return duration != null ? duration : 0;
    }
    public void setDuration(Integer duration) {
        this.duration = duration != null ? duration : 0;
    }

    @Override
    public String toString() {
        return "Video{" +
                "id=" + id +
                ", bvId='" + bvId + '\'' +
                ", title='" + title + '\'' +
                ", coverUrl='" + coverUrl + '\'' +
                ", description='" + description + '\'' +
                ", publishTime=" + publishTime +
                ", videoPartition='" + videoPartition + '\'' +
                ", playCount=" + playCount +
                ", likeCount=" + likeCount +
                ", danmakuCount=" + danmakuCount +
                ", commentCount=" + commentCount +
                ", coinCount=" + coinCount +
                ", shareCount=" + shareCount +
                ", favoriteCount=" + favoriteCount +
                ", duration=" + duration +
                '}';
    }
}