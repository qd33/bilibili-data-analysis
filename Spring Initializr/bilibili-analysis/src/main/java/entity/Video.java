package com.qd33.bilibili_analysis.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "video")
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bv_id", unique = true, nullable = false)
    private String bvId;

    @Column(name = "title")
    private String title;

    @ManyToOne
    @JoinColumn(name = "up_id")
    private Up up;

    @Column(name = "publish_time")
    private LocalDateTime publishTime;

    @Column(name = "video_partition")
    private String videoPartition;

    // 新增字段：封面URL
    @Column(name = "cover_url", length = 500)
    private String coverUrl;

    // 新增字段：视频描述
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

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

    // 新增getter/setter
    public String getCoverUrl() { return coverUrl; }
    public void setCoverUrl(String coverUrl) { this.coverUrl = coverUrl; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}