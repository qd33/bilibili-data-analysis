package com.qd33.bilibili_analysis.service;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Service
public class PythonCrawlerService {

    // ... 保持原有方法不变，只需修改包声明
    // 所有方法内容与之前相同
    // 这里省略具体方法实现以节省空间

    /**
     * 触发UP主数据抓取
     */
    public Map<String, Object> crawlUpData(String uid) {
        Map<String, Object> result = new HashMap<>();

        try {
            System.out.println("🎯 开始执行UP主数据抓取，UID: " + uid);

            // 修正Python脚本路径
            String projectRoot = "C:/Users/Administrator/IdeaProjects/bilibili-data-analysis";
            String pythonScriptPath = projectRoot + "/Spring Initializr/bilibili-analysis/python_scripts/stable_crawler.py";

            // 检查文件是否存在
            File file = new File(pythonScriptPath);
            if (!file.exists()) {
                System.err.println("❌ Python脚本文件不存在: " + pythonScriptPath);
                result.put("success", false);
                result.put("message", "Python脚本文件不存在: " + pythonScriptPath);
                return result;
            }

            System.out.println("✅ Python脚本文件存在: " + pythonScriptPath);

            // 构建Python命令 - 使用完整的Python路径
            String pythonExecutable = "python"; // 或者使用完整路径如 "C:/Python313/python.exe"
            String command = String.format("%s \"%s\" --uid %s", pythonExecutable, pythonScriptPath, uid);

            System.out.println("执行命令: " + command);

            // 执行Python脚本
            Process process = Runtime.getRuntime().exec(command);

            // 读取输出流
            BufferedReader inputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = inputReader.readLine()) != null) {
                output.append(line).append("\n");
                System.out.println("Python输出: " + line);
            }

            // 读取错误流
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            StringBuilder errorOutput = new StringBuilder();
            while ((line = errorReader.readLine()) != null) {
                errorOutput.append(line).append("\n");
                System.err.println("Python错误: " + line);
            }

            // 等待进程完成
            int exitCode = process.waitFor();
            System.out.println("Python进程退出码: " + exitCode);

            if (exitCode == 0) {
                result.put("success", true);
                result.put("message", "UP主数据抓取完成");
                result.put("output", output.toString());
                result.put("uid", uid);
            } else {
                result.put("success", false);
                result.put("message", "Python脚本执行失败，退出码: " + exitCode);
                result.put("error", errorOutput.toString());
            }

        } catch (Exception e) {
            System.err.println("❌ 执行Python爬虫失败: " + e.getMessage());
            result.put("success", false);
            result.put("message", "爬虫执行失败: " + e.getMessage());
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

            // 修正Python脚本路径
            String projectRoot = "C:/Users/Administrator/IdeaProjects/bilibili-data-analysis";
            String pythonScriptPath = projectRoot + "/Spring Initializr/bilibili-analysis/python_scripts/stable_crawler.py";

            // 检查文件是否存在
            File file = new File(pythonScriptPath);
            if (!file.exists()) {
                System.err.println("❌ Python脚本文件不存在: " + pythonScriptPath);
                result.put("success", false);
                result.put("message", "Python脚本文件不存在: " + pythonScriptPath);
                return result;
            }

            System.out.println("✅ Python脚本文件存在: " + pythonScriptPath);

            // 构建Python命令 - 假设支持 --bvid 参数
            String pythonExecutable = "python";
            String command = String.format("%s \"%s\" --bvid %s", pythonExecutable, pythonScriptPath, bvId);

            System.out.println("执行命令: " + command);

            // 执行Python脚本
            Process process = Runtime.getRuntime().exec(command);

            // 读取输出流
            BufferedReader inputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = inputReader.readLine()) != null) {
                output.append(line).append("\n");
                System.out.println("Python输出: " + line);
            }

            // 读取错误流
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            StringBuilder errorOutput = new StringBuilder();
            while ((line = errorReader.readLine()) != null) {
                errorOutput.append(line).append("\n");
                System.err.println("Python错误: " + line);
            }

            // 等待进程完成
            int exitCode = process.waitFor();
            System.out.println("Python进程退出码: " + exitCode);

            if (exitCode == 0) {
                result.put("success", true);
                result.put("message", "视频数据抓取完成");
                result.put("output", output.toString());
                result.put("bvId", bvId);
            } else {
                result.put("success", false);
                result.put("message", "Python脚本执行失败，退出码: " + exitCode);
                result.put("error", errorOutput.toString());
            }

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
            Process process = Runtime.getRuntime().exec("python --version");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String version = reader.readLine();

            result.put("success", true);
            result.put("pythonVersion", version);
            result.put("message", "Python环境正常");

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Python环境检查失败: " + e.getMessage());
        }

        return result;
    }

    /**
     * 测试Python脚本路径 - 修复UpController中调用的方法
     */
    public Map<String, Object> testPythonScriptPath() {
        Map<String, Object> result = new HashMap<>();

        try {
            String projectRoot = "C:/Users/Administrator/IdeaProjects/bilibili-data-analysis";
            String pythonScriptPath = projectRoot + "/Spring Initializr/bilibili-analysis/python_scripts/stable_crawler.py";

            File file = new File(pythonScriptPath);
            boolean exists = file.exists();

            result.put("success", exists);
            result.put("scriptPath", pythonScriptPath);
            result.put("exists", exists);
            result.put("message", exists ? "脚本文件存在" : "脚本文件不存在");

            System.out.println("📁 Python脚本路径检查: " + pythonScriptPath);
            System.out.println("✅ 文件存在: " + exists);

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
            // 检查Python环境
            Map<String, Object> pythonCheck = checkPythonEnvironment();
            result.put("pythonEnvironment", pythonCheck);

            // 检查脚本路径
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
}