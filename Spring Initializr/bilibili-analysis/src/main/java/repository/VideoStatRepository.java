package com.qd33.bilibili_analysis.repository;

import com.qd33.bilibili_analysis.entity.VideoStat;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface VideoStatRepository extends JpaRepository<VideoStat, Long> {
    List<VideoStat> findByVideoBvIdOrderByRecordDateAsc(String bvId);
    List<VideoStat> findByVideoBvIdAndRecordDateBetween(String bvId, LocalDate startDate, LocalDate endDate);
    void deleteByVideoBvId(String bvId);

    // 🆕 需要添加的方法
    List<VideoStat> findByVideoBvId(String bvId);
}