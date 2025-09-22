package com.qd33.bilibili_analysis.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_favorite",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "favorite_type", "target_id"}))
@Data
public class UserFavorite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String favoriteType; // video/up
    private String targetId;     // BV号/UID
    private LocalDateTime favoriteTime = LocalDateTime.now();
}