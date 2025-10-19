package com.qd33.bilibili_analysis.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@Entity
@Table(name = "up")
public class Up {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "uid", unique = true, nullable = false)
    @JsonProperty("uid")
    private String uid;

    @Column(name = "name")
    @JsonProperty("name")
    private String name;

    @Column(name = "avatar")
    @JsonProperty("avatar")
    private String avatar;

    // 🆕 添加粉丝数字段
    @Column(name = "follower_count")
    @JsonProperty("followerCount")
    private Long followerCount;

    @OneToMany(mappedBy = "up", cascade = CascadeType.ALL)
    @JsonProperty("videos")
    private List<Video> videos;

    // Getter和Setter方法
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }

    // 🆕 粉丝数Getter和Setter
    public Long getFollowerCount() { return followerCount; }
    public void setFollowerCount(Long followerCount) { this.followerCount = followerCount; }

    public List<Video> getVideos() { return videos; }
    public void setVideos(List<Video> videos) { this.videos = videos; }

    @Override
    public String toString() {
        return "Up{" +
                "id=" + id +
                ", uid='" + uid + '\'' +
                ", name='" + name + '\'' +
                ", avatar='" + avatar + '\'' +
                ", followerCount=" + followerCount +
                '}';
    }
}