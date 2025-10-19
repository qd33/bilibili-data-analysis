package com.qd33.bilibili_analysis.repository;

import com.qd33.bilibili_analysis.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {

    // 🆕 根据UP主UID查询视频列表
    @Query("SELECT v FROM Video v WHERE v.up.uid = :uid ORDER BY v.publishTime DESC")
    List<Video> findByUpUid(@Param("uid") String uid);

    // 🆕 根据BVID查询视频
    Optional<Video> findByBvId(String bvId);

    // 🆕 检查视频是否存在
    boolean existsByBvId(String bvId);
}