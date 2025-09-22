package com.qd33.bilibili_analysis.repository;

import com.qd33.bilibili_analysis.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface VideoRepository extends JpaRepository<Video, Long> {
    Optional<Video> findByBvId(String bvId);
    boolean existsByBvId(String bvId);
    List<Video> findByUpUid(String upUid);
    List<Video> findByPartition(String partition);
}