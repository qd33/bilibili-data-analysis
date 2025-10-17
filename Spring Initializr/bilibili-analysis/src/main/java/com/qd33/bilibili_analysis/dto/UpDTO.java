package com.qd33.bilibili_analysis.dto;

import java.util.List;

public class UpDTO {
    private Long id;
    private String uid;
    private String name;
    private String avatar;
    private List<VideoSimpleDTO> videos;

    // 构造方法
    public UpDTO() {}

    public UpDTO(Long id, String uid, String name, String avatar) {
        this.id = id;
        this.uid = uid;
        this.name = name;
        this.avatar = avatar;
    }

    // Getter和Setter方法
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }

    public List<VideoSimpleDTO> getVideos() { return videos; }
    public void setVideos(List<VideoSimpleDTO> videos) { this.videos = videos; }
}