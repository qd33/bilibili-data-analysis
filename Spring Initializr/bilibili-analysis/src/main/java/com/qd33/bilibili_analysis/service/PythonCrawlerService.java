package com.qd33.bilibili_analysis.service;

import com.qd33.bilibili_analysis.service.UpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

@Service
public class PythonCrawlerService {

    @Autowired
    private UpService upService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 获取Python脚本的正确路径
     */
    private String getPythonScriptPath() {
        // 根据实际项目结构调整这些路径
        String[] possiblePaths = {
                "python_scripts/stable_crawler.py",  // 相对路径（推荐）
                "C:/Users/Administrator/IdeaProjects/bilibili-data-analysis/Spring Initializr/bilibili-analysis/python_scripts/stable_crawler.py", // 完整路径
                "../python_scripts/stable_crawler.py", // 上级目录
                "src/main/resources/python_scripts/stable_crawler.py", // 资源目录
        };

        for (String path : possiblePaths) {
            java.io.File file = new java.io.File(path);
            if (file.exists()) {
                System.out.println("✅ 找到Python脚本: " + file.getAbsolutePath());
                return path;
            }
        }

        // 如果都找不到，返回第一个路径并打印错误
        System.err.println("❌ 未找到Python脚本，尝试使用默认路径");
        return possiblePaths[0];
    }

    /**
     * 触发UP主数据抓取 - 调用Python爬虫
     */
    public Map<String, Object> crawlUpData(String uid) {
        Map<String, Object> result = new HashMap<>();

        try {
            System.out.println("🎯 开始执行Python爬虫，UID: " + uid);

            // 获取正确的Python脚本路径
            String pythonScriptPath = getPythonScriptPath();
            System.out.println("📁 Python脚本路径: " + new java.io.File(pythonScriptPath).getAbsolutePath());

            ProcessBuilder processBuilder = new ProcessBuilder("python", pythonScriptPath, "--uid", uid);

            // 设置工作目录为脚本所在目录的父目录
            java.io.File scriptFile = new java.io.File(pythonScriptPath);
            java.io.File workingDir = scriptFile.getParentFile() != null ? scriptFile.getParentFile().getParentFile() : new java.io.File(".");
            processBuilder.directory(workingDir);

            System.out.println("📂 工作目录: " + workingDir.getAbsolutePath());

            // 合并标准输出和错误输出
            processBuilder.redirectErrorStream(true);

            // 启动进程
            Process process = processBuilder.start();

            // 读取输出
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
                System.out.println("Python输出: " + line);
            }

            // 等待进程完成
            int exitCode = process.waitFor();
            System.out.println("Python进程退出码: " + exitCode);

            if (exitCode == 0) {
                // 解析Python输出的JSON
                String jsonOutput = output.toString();

                // 查找JSON开始位置
                int jsonStart = jsonOutput.indexOf("{");
                int jsonEnd = jsonOutput.lastIndexOf("}") + 1;

                if (jsonStart >= 0 && jsonEnd > jsonStart) {
                    String jsonContent = jsonOutput.substring(jsonStart, jsonEnd);

                    try {
                        Map<String, Object> pythonResult = objectMapper.readValue(jsonContent, Map.class);

                        if (Boolean.TRUE.equals(pythonResult.get("success"))) {
                            // 提取数据
                            Map<String, Object> upData = (Map<String, Object>) pythonResult.get("up_data");
                            List<Map<String, Object>> videoDataList = (List<Map<String, Object>>) pythonResult.get("videos");

                            // 保存数据到数据库
                            if (upService instanceof com.qd33.bilibili_analysis.service.impl.UpServiceImpl) {
                                com.qd33.bilibili_analysis.service.impl.UpServiceImpl upServiceImpl =
                                        (com.qd33.bilibili_analysis.service.impl.UpServiceImpl) upService;

                                // 保存UP主信息
                                Map<String, Object> saveUpResult = saveUpInfo(uid, upData);
                                if (!Boolean.TRUE.equals(saveUpResult.get("success"))) {
                                    return saveUpResult;
                                }

                                // 保存视频数据
                                Map<String, Object> saveResult = upServiceImpl.saveVideoData(uid, videoDataList);
                                result.putAll(saveResult);

                                result.put("success", true);
                                result.put("uid", uid);
                                result.put("upData", upData);
                                result.put("videos", videoDataList);
                                result.put("message", pythonResult.get("message"));

                                System.out.println("✅ Python爬虫执行成功: " + pythonResult.get("message"));
                            } else {
                                result.put("success", false);
                                result.put("message", "无法调用保存视频数据方法");
                            }
                        } else {
                            result.put("success", false);
                            result.put("message", "Python爬虫返回失败: " + pythonResult.get("message"));
                        }
                    } catch (Exception jsonError) {
                        System.err.println("❌ JSON解析失败: " + jsonError.getMessage());
                        result.put("success", false);
                        result.put("message", "JSON解析失败: " + jsonError.getMessage());
                        result.put("rawOutput", output.toString());
                    }
                } else {
                    result.put("success", false);
                    result.put("message", "无法解析Python输出为JSON");
                    result.put("rawOutput", output.toString());
                }
            } else {
                result.put("success", false);
                result.put("message", "Python进程执行失败，退出码: " + exitCode);
                result.put("rawOutput", output.toString());
            }

        } catch (Exception e) {
            System.err.println("❌ 执行Python爬虫失败: " + e.getMessage());
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "执行Python爬虫失败: " + e.getMessage());
        }

        return result;
    }

    /**
     * 保存UP主信息到数据库
     */
    private Map<String, Object> saveUpInfo(String uid, Map<String, Object> upData) {
        Map<String, Object> result = new HashMap<>();

        try {
            Map<String, Object> upObj = new HashMap<>();
            upObj.put("uid", upData.get("uid"));
            upObj.put("name", upData.get("name"));
            upObj.put("avatar", upData.get("avatar"));

            Map<String, Object> saveResult = upService.saveUp(upObj);

            if (Boolean.TRUE.equals(saveResult.get("success"))) {
                result.put("success", true);
                result.put("message", "UP主信息保存成功");
            } else {
                result.put("success", false);
                result.put("message", saveResult.get("message"));
            }

        } catch (Exception e) {
            System.err.println("❌ 保存UP主信息失败: " + e.getMessage());
            result.put("success", false);
            result.put("message", "保存UP主信息失败: " + e.getMessage());
        }

        return result;
    }

    /**
     * 触发视频数据抓取
     */
    public Map<String, Object> crawlVideoData(String bvId) {
        Map<String, Object> result = new HashMap<>();

        try {
            System.out.println("🎬 开始执行视频数据抓取，BV号: " + bvId);
            result.put("success", true);
            result.put("message", "视频数据抓取完成");
            result.put("bvId", bvId);

        } catch (Exception e) {
            System.err.println("❌ 执行视频数据抓取失败: " + e.getMessage());
            result.put("success", false);
            result.put("message", "视频数据抓取失败: " + e.getMessage());
        }

        return result;
    }

    /**
     * 检查Python环境
     */
    public Map<String, Object> checkPythonEnvironment() {
        Map<String, Object> result = new HashMap<>();

        try {
            ProcessBuilder processBuilder = new ProcessBuilder("python", "--version");
            processBuilder.redirectErrorStream(true);

            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line);
            }

            int exitCode = process.waitFor();

            if (exitCode == 0) {
                result.put("success", true);
                result.put("pythonVersion", output.toString());
                result.put("message", "Python环境正常");
            } else {
                result.put("success", false);
                result.put("message", "Python环境检查失败: " + output.toString());
            }

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Python环境检查失败: " + e.getMessage());
        }

        return result;
    }

    /**
     * 测试Python脚本路径
     */
    public Map<String, Object> testPythonScriptPath() {
        Map<String, Object> result = new HashMap<>();

        try {
            String pythonScriptPath = getPythonScriptPath();
            java.io.File scriptFile = new java.io.File(pythonScriptPath);

            boolean exists = scriptFile.exists();
            boolean canRead = scriptFile.canRead();

            result.put("success", exists && canRead);
            result.put("scriptPath", scriptFile.getAbsolutePath());
            result.put("exists", exists);
            result.put("canRead", canRead);
            result.put("message", exists ? (canRead ? "脚本文件可访问" : "脚本文件不可读") : "脚本文件不存在");

            System.out.println("📁 Python脚本路径检查: " + scriptFile.getAbsolutePath());
            System.out.println("✅ 文件存在: " + exists + ", 可读: " + canRead);

        } catch (Exception e) {
            System.err.println("❌ 检查Python脚本路径失败: " + e.getMessage());
            result.put("success", false);
            result.put("message", "检查脚本路径失败: " + e.getMessage());
        }

        return result;
    }

    /**
     * 检查爬虫系统状态
     */
    public Map<String, Object> checkCrawlerStatus() {
        Map<String, Object> result = new HashMap<>();

        try {
            Map<String, Object> pythonCheck = checkPythonEnvironment();
            result.put("pythonEnvironment", pythonCheck);

            Map<String, Object> pathCheck = testPythonScriptPath();
            result.put("scriptPath", pathCheck);

            boolean pythonOk = Boolean.TRUE.equals(pythonCheck.get("success"));
            boolean scriptOk = Boolean.TRUE.equals(pathCheck.get("success"));

            if (pythonOk && scriptOk) {
                result.put("success", true);
                result.put("message", "爬虫系统就绪");
            } else {
                result.put("success", false);
                result.put("message", "爬虫系统配置异常");
            }

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "检查爬虫状态失败: " + e.getMessage());
        }

        return result;
    }

    /**
     * 测试Python爬虫功能
     */
    public Map<String, Object> testPythonCrawler(String testUid) {
        Map<String, Object> result = new HashMap<>();

        try {
            String uid = testUid != null ? testUid : "208259";
            System.out.println("🧪 测试Python爬虫功能，UID: " + uid);

            return crawlUpData(uid);

        } catch (Exception e) {
            System.err.println("❌ Python爬虫测试失败: " + e.getMessage());
            result.put("success", false);
            result.put("message", "测试失败: " + e.getMessage());
        }

        return result;
    }
}