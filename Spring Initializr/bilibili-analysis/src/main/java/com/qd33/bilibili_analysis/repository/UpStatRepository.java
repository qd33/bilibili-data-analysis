package com.qd33.bilibili_analysis.repository;

import com.qd33.bilibili_analysis.entity.UpStat;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface UpStatRepository extends JpaRepository<UpStat, Long> {
    List<UpStat> findByUpUidOrderByRecordDateAsc(String uid);
    List<UpStat> findByUpUidAndRecordDateBetween(String uid, LocalDate startDate, LocalDate endDate);
    List<UpStat> findByUpUid(String uid);
}