package com.qd33.bilibili_analysis.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "up_stat",
        uniqueConstraints = @UniqueConstraint(columnNames = {"up_id", "record_date"}))
@Data
public class UpStat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "up_id")
    private Up up;

    private LocalDate recordDate;
    private Long followerCount;
    private Long totalViewCount;
}