package com.qd33.bilibili_analysis.repository;

import com.qd33.bilibili_analysis.entity.UserFavorite;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserFavoriteRepository extends JpaRepository<UserFavorite, Long> {
    List<UserFavorite> findByUserIdOrderByFavoriteTimeDesc(Long userId);
    List<UserFavorite> findByUserIdAndFavoriteTypeOrderByFavoriteTimeDesc(Long userId, String favoriteType);
    Optional<UserFavorite> findByUserIdAndFavoriteTypeAndTargetId(Long userId, String favoriteType, String targetId);
    boolean existsByUserIdAndFavoriteTypeAndTargetId(Long userId, String favoriteType, String targetId);
    void deleteByUserIdAndFavoriteTypeAndTargetId(Long userId, String favoriteType, String targetId);
}