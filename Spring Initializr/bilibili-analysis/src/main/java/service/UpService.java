package com.qd33.bilibili_analysis.service;

import java.util.Map;

public interface UpService {
    Map<String, Object> getUpByUid(String uid);
    boolean upExists(String uid);
    Map<String, Object> saveUp(Object up);
    Map<String, Object> saveUpStat(Object upStat);
    Object getUpTrend(String uid);
}