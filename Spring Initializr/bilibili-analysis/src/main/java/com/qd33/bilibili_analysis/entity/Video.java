package com.qd33.bilibili_analysis.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

@Entity
@Table(name = "video")
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bv_id", unique = true, nullable = false)
    @JsonProperty("bvid")  // 统一前端字段名
    private String bvId;

    @Column(name = "title")
    @JsonProperty("title")  // 明确指定序列化字段名
    private String title;

    @ManyToOne
    @JoinColumn(name = "up_id")
    @JsonProperty("up")  // 确保UP主信息正确序列化
    private Up up;

    @Column(name = "publish_time")
    @JsonProperty("publishTime")  // 统一时间字段名
    private LocalDateTime publishTime;

    @Column(name = "video_partition")
    @JsonProperty("partition")  // 简化字段名
    private String videoPartition;

    // 新增字段：封面URL - 重点修改
    @Column(name = "cover_url", length = 500)
    @JsonProperty("cover")  // 统一前端字段名为cover
    private String coverUrl;

    // 新增字段：视频描述
    @Column(name = "description", columnDefinition = "TEXT")
    @JsonProperty("description")  // 确保描述字段序列化
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

    public String getCoverUrl() { return coverUrl; }
    public void setCoverUrl(String coverUrl) { this.coverUrl = coverUrl; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    // 添加toString方法便于调试
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
                '}';
    }
}