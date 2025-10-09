package com.qd33.bilibili_analysis.service.impl;

import com.qd33.bilibili_analysis.entity.Up;
import com.qd33.bilibili_analysis.entity.UpStat;
import com.qd33.bilibili_analysis.repository.UpRepository;
import com.qd33.bilibili_analysis.repository.UpStatRepository;
import com.qd33.bilibili_analysis.service.UpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class UpServiceImpl implements UpService {

    @Autowired
    private UpRepository upRepository;

    @Autowired
    private UpStatRepository upStatRepository;

    @Override
    public Map<String, Object> getUpByUid(String uid) {
        Map<String, Object> result = new HashMap<>();

        try {
            boolean exists = upRepository.existsByUid(uid);

            if (!exists) {
                result.put("success", false);
                result.put("message", "UP主不存在");
                return result;
            }

            Up up = upRepository.findByUid(uid).get();
            List<UpStat> stats = upStatRepository.findByUpUidOrderByRecordDateAsc(uid);

            result.put("success", true);
            result.put("up", up);
            result.put("stats", stats);
            result.put("statsCount", stats.size());

            System.out.println("成功查询UP主: " + uid);
        } catch (Exception e) {
            System.err.println("查询UP主失败: " + uid + ", 错误: " + e.getMessage());
            result.put("success", false);
            result.put("message", "查询失败: " + e.getMessage());
        }

        return result;
    }

    @Override
    public boolean upExists(String uid) {
        return upRepository.existsByUid(uid);
    }

    @Override
    public Map<String, Object> saveUp(Object upObj) {
        Map<String, Object> result = new HashMap<>();

        try {
            Up up = (Up) upObj;

            if (upRepository.existsByUid(up.getUid())) {
                result.put("success", false);
                result.put("message", "UP主已存在");
                return result;
            }

            Up savedUp = upRepository.save(up);
            result.put("success", true);
            result.put("up", savedUp);
            result.put("message", "UP主保存成功");

            System.out.println("成功保存UP主: " + up.getUid());
        } catch (Exception e) {
            System.err.println("保存UP主失败: " + e.getMessage());
            result.put("success", false);
            result.put("message", "保存失败: " + e.getMessage());
        }

        return result;
    }

    @Override
    public Map<String, Object> saveUpStat(Object upStatObj) {
        Map<String, Object> result = new HashMap<>();

        try {
            if (upStatObj instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> statData = (Map<String, Object>) upStatObj;

                // 获取UID并查找UP主
                String uid = (String) statData.get("uid");
                Optional<Up> upOpt = upRepository.findByUid(uid);

                if (!upOpt.isPresent()) {
                    result.put("success", false);
                    result.put("message", "UP主不存在，请先保存UP主基本信息");
                    return result;
                }

                Up up = upOpt.get();
                LocalDate recordDate = LocalDate.parse(statData.get("recordDate").toString());

                // 检查是否已存在相同日期的数据
                List<UpStat> existingStats = upStatRepository
                        .findByUpUidAndRecordDateBetween(uid, recordDate, recordDate);

                UpStat upStat;
                if (!existingStats.isEmpty()) {
                    // 更新现有数据
                    upStat = existingStats.get(0);
                    result.put("message", "统计数据已更新");
                } else {
                    // 创建新数据
                    upStat = new UpStat();
                    upStat.setUp(up);
                    upStat.setRecordDate(recordDate);
                    result.put("message", "统计数据已保存");
                }

                // 设置统计数据
                upStat.setFollowerCount(Long.valueOf(statData.get("followerCount").toString()));
                upStat.setTotalViewCount(Long.valueOf(statData.get("totalViewCount").toString()));

                upStatRepository.save(upStat);
                result.put("success", true);
                result.put("upStat", upStat);

                System.out.println("成功保存UP主统计: " + uid + " - " + recordDate);
            } else {
                result.put("success", false);
                result.put("message", "数据格式错误");
            }

        } catch (Exception e) {
            System.err.println("保存UP主统计失败: " + e.getMessage());
            result.put("success", false);
            result.put("message", "保存失败: " + e.getMessage());
        }

        return result;
    }

    @Override
    public Object getUpTrend(String uid) {
        return upStatRepository.findByUpUidOrderByRecordDateAsc(uid);
    }
}