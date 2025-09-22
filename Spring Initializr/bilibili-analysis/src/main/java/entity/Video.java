package com.qd33.bilibili_analysis.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "video")
@Data
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
}