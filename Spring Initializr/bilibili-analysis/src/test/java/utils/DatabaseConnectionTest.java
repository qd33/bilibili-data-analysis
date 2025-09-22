package com.qd33.bilibili_analysis.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

@Component
public class DatabaseConnectionTest implements CommandLineRunner {

    @Autowired
    private DataSource dataSource;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("🔍 开始数据库连接测试...");

        try {
            // 测试数据库连接
            Connection connection = dataSource.getConnection();
            System.out.println("✅ 数据库连接成功！");

            // 测试数据库是否存在
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet resultSet = metaData.getCatalogs();

            boolean dbExists = false;
            while (resultSet.next()) {
                String databaseName = resultSet.getString(1);
                if ("bilibili_db".equals(databaseName)) {
                    dbExists = true;
                    break;
                }
            }

            if (dbExists) {
                System.out.println("✅ 数据库 'bilibili_db' 存在");
            } else {
                System.out.println("❌ 数据库 'bilibili_db' 不存在，需要创建");
                System.out.println("💡 请手动创建数据库：");
                System.out.println("   CREATE DATABASE bilibili_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;");
            }

            connection.close();

        } catch (Exception e) {
            System.out.println("❌ 数据库连接失败！");
            System.out.println("错误信息: " + e.getMessage());
            System.out.println("💡 可能的原因：");
            System.out.println("   - MySQL 服务未启动");
            System.out.println("   - 用户名/密码错误");
            System.out.println("   - 端口被占用");
        }
    }
}
