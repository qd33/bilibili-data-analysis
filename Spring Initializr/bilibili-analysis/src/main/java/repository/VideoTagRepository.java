package com.qd33.bilibili_analysis.repository;

import com.qd33.bilibili_analysis.entity.VideoTag;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface VideoTagRepository extends JpaRepository<VideoTag, Long> {
    List<VideoTag> findByVideoBvId(String bvId);
    List<VideoTag> findByTagName(String tagName);
    void deleteByVideoBvIdAndTagName(String bvId, String tagName);
    boolean existsByVideoBvIdAndTagName(String bvId, String tagName);
}