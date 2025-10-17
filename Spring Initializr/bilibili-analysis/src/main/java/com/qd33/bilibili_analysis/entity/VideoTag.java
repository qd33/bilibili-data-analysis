package com.qd33.bilibili_analysis.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "video_tag",
        uniqueConstraints = @UniqueConstraint(columnNames = {"video_id", "tag_id"}))
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

    // Getter and Setter methods
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Video getVideo() { return video; }
    public void setVideo(Video video) { this.video = video; }

    public Tag getTag() { return tag; }
    public void setTag(Tag tag) { this.tag = tag; }
}