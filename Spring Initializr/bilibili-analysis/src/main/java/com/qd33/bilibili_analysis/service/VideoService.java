package com.qd33.bilibili_analysis.service;

import java.util.Map;

public interface VideoService {
    Map<String, Object> getVideoByBvId(String bvId);
    boolean videoExists(String bvId);
    Map<String, Object> saveVideo(Object video);
    Map<String, Object> saveVideoStat(Object videoStat);
    Object getVideoTrend(String bvId);
}