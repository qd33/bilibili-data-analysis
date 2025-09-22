package com.qd33.bilibili_analysis.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "video_stat",
        uniqueConstraints = @UniqueConstraint(columnNames = {"video_id", "record_date"}))
@Data
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
}