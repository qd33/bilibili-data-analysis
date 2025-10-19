#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import pymysql

def test_database():
    """测试数据库连接和表结构"""
    try:
        # 数据库连接配置
        db_config = {
            'host': 'localhost',
            'user': 'root',
            'password': '123456789',
            'database': 'bilibili_db',
            'charset': 'utf8mb4'
        }

        connection = pymysql.connect(**db_config)
        print("✅ 数据库连接成功")

        # 检查video表结构
        with connection.cursor() as cursor:
            cursor.execute("DESC video")
            columns = cursor.fetchall()
            print("\n📊 video表结构:")
            for column in columns:
                print(f"  {column[0]} - {column[1]}")

            # 检查是否有play_count字段
            has_play_count = any('play_count' in column for column in columns)
            if has_play_count:
                print("✅ play_count字段存在")
            else:
                print("❌ play_count字段缺失")

        connection.close()
        return True

    except Exception as e:
        print(f"❌ 数据库测试失败: {e}")
        return False

if __name__ == "__main__":
    test_database()