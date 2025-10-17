package com.qd33.bilibili_analysis.repository;

import com.qd33.bilibili_analysis.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface VideoRepository extends JpaRepository<Video, Long> {
    Optional<Video> findByBvId(String bvId);
    boolean existsByBvId(String bvId);
    List<Video> findByUpUid(String upUid);

    // ✅ 修复分区查询方法
    List<Video> findByVideoPartition(String videoPartition);

    // 🆕 添加新的查询方法
    List<Video> findByVideoPartitionOrderByPublishTimeDesc(String videoPartition);
    List<Video> findByUpUidOrderByPublishTimeDesc(String upUid);
}