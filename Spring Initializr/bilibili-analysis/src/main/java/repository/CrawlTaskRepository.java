package com.qd33.bilibili_analysis.repository;

import com.qd33.bilibili_analysis.entity.CrawlTask;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface CrawlTaskRepository extends JpaRepository<CrawlTask, Long> {
    List<CrawlTask> findByExecuteTimeBetweenOrderByExecuteTimeDesc(LocalDateTime start, LocalDateTime end);
    List<CrawlTask> findByStatusOrderByExecuteTimeDesc(String status);
    List<CrawlTask> findByTaskNameOrderByExecuteTimeDesc(String taskName);
}