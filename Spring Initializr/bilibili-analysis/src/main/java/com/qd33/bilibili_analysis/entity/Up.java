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
    @JsonProperty("uid")  // 确保UID字段正确序列化
    private String uid;

    @Column(name = "name")
    @JsonProperty("name")  // 确保名称字段正确序列化
    private String name;

    @Column(name = "avatar")
    @JsonProperty("avatar")  // 确保头像字段正确序列化
    private String avatar;

    @OneToMany(mappedBy = "up", cascade = CascadeType.ALL)
    @JsonProperty("videos")  // 确保视频列表正确序列化
    private List<Video> videos;



    // Getter and Setter methods
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }

    public List<Video> getVideos() { return videos; }
    public void setVideos(List<Video> videos) { this.videos = videos; }

    @Override
    public String toString() {
        return "Up{" +
                "id=" + id +
                ", uid='" + uid + '\'' +
                ", name='" + name + '\'' +
                ", avatar='" + avatar + '\'' +
                '}';
    }
}