package com.qd33.bilibili_analysis.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_search_history")
public class UserSearchHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String searchType;
    private String targetId;
    private LocalDateTime searchTime = LocalDateTime.now();

    // Getter and Setter methods
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getSearchType() { return searchType; }
    public void setSearchType(String searchType) { this.searchType = searchType; }

    public String getTargetId() { return targetId; }
    public void setTargetId(String targetId) { this.targetId = targetId; }

    public LocalDateTime getSearchTime() { return searchTime; }
    public void setSearchTime(LocalDateTime searchTime) { this.searchTime = searchTime; }
}