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
            UpStat upStat = (UpStat) upStatObj;
            String uid = upStat.getUp().getUid();
            LocalDate recordDate = upStat.getRecordDate();

            List<UpStat> existingStats = upStatRepository
                    .findByUpUidAndRecordDateBetween(uid, recordDate, recordDate);

            if (!existingStats.isEmpty()) {
                UpStat existing = existingStats.get(0);
                existing.setFollowerCount(upStat.getFollowerCount());
                existing.setTotalViewCount(upStat.getTotalViewCount());

                upStatRepository.save(existing);
                result.put("message", "统计数据已更新");
            } else {
                upStatRepository.save(upStat);
                result.put("message", "统计数据已保存");
            }

            result.put("success", true);
            System.out.println("成功保存UP主统计: " + uid);
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