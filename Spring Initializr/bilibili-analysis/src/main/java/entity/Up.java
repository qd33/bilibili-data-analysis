package com.qd33.bilibili_analysis.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "up")
@Data
public class Up {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String uid;

    private String name;
    private String avatar;
}