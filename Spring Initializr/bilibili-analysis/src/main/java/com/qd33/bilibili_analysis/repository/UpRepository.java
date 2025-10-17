package com.qd33.bilibili_analysis.repository;

import com.qd33.bilibili_analysis.entity.Up;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UpRepository extends JpaRepository<Up, Long> {
    Optional<Up> findByUid(String uid);
    boolean existsByUid(String uid);
    Optional<Up> findByNameContaining(String name);
}