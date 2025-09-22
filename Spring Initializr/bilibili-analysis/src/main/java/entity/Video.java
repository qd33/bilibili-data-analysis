package com.qd33.bilibili_analysis.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "video")
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String bvId;

    private String title;

    @ManyToOne
    @JoinColumn(name = "up_id")
    private Up up;

    private LocalDateTime publishTime;
    private String partition;

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

    public String getPartition() { return partition; }
    public void setPartition(String partition) { this.partition = partition; }
}