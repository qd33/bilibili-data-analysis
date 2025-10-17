package com.qd33.bilibili_analysis.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "up_stat")
public class UpStat {
    // ... 原有内容不变

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "up_id")
    private Up up;

    private LocalDate recordDate;
    private Long followerCount;
    private Long totalViewCount;

    // Getter and Setter methods
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Up getUp() { return up; }
    public void setUp(Up up) { this.up = up; }

    public LocalDate getRecordDate() { return recordDate; }
    public void setRecordDate(LocalDate recordDate) { this.recordDate = recordDate; }

    public Long getFollowerCount() { return followerCount; }
    public void setFollowerCount(Long followerCount) { this.followerCount = followerCount; }

    public Long getTotalViewCount() { return totalViewCount; }
    public void setTotalViewCount(Long totalViewCount) { this.totalViewCount = totalViewCount; }
}