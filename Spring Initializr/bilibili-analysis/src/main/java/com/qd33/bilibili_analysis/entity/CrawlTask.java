package com.qd33.bilibili_analysis.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "crawl_task")
public class CrawlTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String taskName;
    private LocalDateTime executeTime = LocalDateTime.now();
    private String status;
    private Integer dataCount;
    private String errorMessage;

    // Getter and Setter methods
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTaskName() { return taskName; }
    public void setTaskName(String taskName) { this.taskName = taskName; }

    public LocalDateTime getExecuteTime() { return executeTime; }
    public void setExecuteTime(LocalDateTime executeTime) { this.executeTime = executeTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getDataCount() { return dataCount; }
    public void setDataCount(Integer dataCount) { this.dataCount = dataCount; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
}