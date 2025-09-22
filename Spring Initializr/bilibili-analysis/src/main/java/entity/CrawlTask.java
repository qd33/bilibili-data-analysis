package com.qd33.bilibili_analysis.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "crawl_task")
@Data
public class CrawlTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String taskName;
    private LocalDateTime executeTime = LocalDateTime.now();
    private String status;      // SUCCESS/FAILED
    private Integer dataCount;
    private String errorMessage;
}