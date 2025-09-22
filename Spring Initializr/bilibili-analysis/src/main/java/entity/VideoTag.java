package com.qd33.bilibili_analysis.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "video_tag",
        uniqueConstraints = @UniqueConstraint(columnNames = {"video_id", "tag_id"}))
@Data
public class VideoTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "video_id")
    private Video video;

    @ManyToOne
    @JoinColumn(name = "tag_id")
    private Tag tag;
}