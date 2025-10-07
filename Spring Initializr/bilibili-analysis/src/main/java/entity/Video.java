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

    @Column(name = "video_partition")  // 修改字段名，避免使用MySQL关键字
    private String videoPartition;

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
}