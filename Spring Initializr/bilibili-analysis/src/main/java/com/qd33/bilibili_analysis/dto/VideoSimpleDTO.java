package com.qd33.bilibili_analysis.dto;

public class VideoSimpleDTO {
    private Long id;
    private String bvid;
    private String title;
    private String coverUrl;
    private String description;
    private Integer play;
    private Integer like;
    private Integer danmaku;
    private Integer comment; // 🆕 评论数
    private Integer coin;    // 🆕 投币数
    private Integer share;   // 🆕 分享数
    private Integer favorite; // 🆕 收藏数
    private Integer duration; // 🆕 时长
    private String publishTime;
    private String videoPartition;

    // 构造方法
    public VideoSimpleDTO() {}

    // Getter和Setter方法
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getBvid() { return bvid; }
    public void setBvid(String bvid) { this.bvid = bvid; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCoverUrl() { return coverUrl; }
    public void setCoverUrl(String coverUrl) { this.coverUrl = coverUrl; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getPlay() { return play; }
    public void setPlay(Integer play) { this.play = play; }

    public Integer getLike() { return like; }
    public void setLike(Integer like) { this.like = like; }

    public Integer getDanmaku() { return danmaku; }
    public void setDanmaku(Integer danmaku) { this.danmaku = danmaku; }

    public Integer getComment() { return comment; }
    public void setComment(Integer comment) { this.comment = comment; }

    public Integer getCoin() { return coin; }
    public void setCoin(Integer coin) { this.coin = coin; }

    public Integer getShare() { return share; }
    public void setShare(Integer share) { this.share = share; }

    public Integer getFavorite() { return favorite; }
    public void setFavorite(Integer favorite) { this.favorite = favorite; }

    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }

    public String getPublishTime() { return publishTime; }
    public void setPublishTime(String publishTime) { this.publishTime = publishTime; }

    public String getVideoPartition() { return videoPartition; }
    public void setVideoPartition(String videoPartition) { this.videoPartition = videoPartition; }
}