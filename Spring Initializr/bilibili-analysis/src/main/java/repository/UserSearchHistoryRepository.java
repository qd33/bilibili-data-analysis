package com.qd33.bilibili_analysis.repository;

import com.qd33.bilibili_analysis.entity.UserSearchHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserSearchHistoryRepository extends JpaRepository<UserSearchHistory, Long> {
    List<UserSearchHistory> findByUserIdOrderBySearchTimeDesc(Long userId);
    List<UserSearchHistory> findByUserIdAndSearchTypeOrderBySearchTimeDesc(Long userId, String searchType);
    void deleteByUserId(Long userId);
}