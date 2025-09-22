package com.qd33.bilibili_analysis.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "tag")
@Data
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;
}