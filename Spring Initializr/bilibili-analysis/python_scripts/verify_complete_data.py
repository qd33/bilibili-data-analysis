#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import pymysql
import requests
import json

def verify_complete_data():
    """验证数据库和API数据的完整性"""

    print("=== 数据完整性验证 ===")

    # 数据库验证
    try:
        db_config = {
            'host': 'localhost',
            'user': 'root',
            'password': '123456789',
            'database': 'bilibili_db',
            'charset': 'utf8mb4'
        }

        connection = pymysql.connect(**db_config)
        print("✅ 数据库连接成功")

        with connection.cursor() as cursor:
            # UP主数据
            cursor.execute("SELECT COUNT(*) as up_count FROM up")
            up_count = cursor.fetchone()[0]
            print(f"📊 数据库UP主数量: {up_count}")

            # 视频数据
            cursor.execute("SELECT COUNT(*) as video_count FROM video")
            video_count = cursor.fetchone()[0]
            print(f"🎬 数据库视频数量: {video_count}")

            # 显示UP主列表
            cursor.execute("SELECT uid, name, follower_count FROM up")
            up_list = cursor.fetchall()
            print("\n📋 UP主列表:")
            for up in up_list:
                print(f"  UID: {up[0]}, 名称: {up[1]}, 粉丝: {up[2]}")

            # 显示视频统计
            if video_count > 0:
                cursor.execute("""
                    SELECT 
                        COUNT(*) as total,
                        SUM(play_count) as total_plays,
                        AVG(play_count) as avg_plays,
                        MAX(play_count) as max_plays
                    FROM video
                """)
                stats = cursor.fetchone()
                print(f"\n📈 视频统计:")
                print(f"  总播放量: {stats[1] or 0}")
                print(f"  平均播放: {int(stats[2] or 0)}")
                print(f"  最高播放: {stats[3] or 0}")

                # 显示分区分布
                cursor.execute("""
                    SELECT video_partition, COUNT(*) as count 
                    FROM video 
                    GROUP BY video_partition 
                    ORDER BY count DESC
                """)
                partitions = cursor.fetchall()
                print(f"\n🎯 分区分布:")
                for partition in partitions:
                    print(f"  {partition[0]}: {partition[1]} 个视频")

        connection.close()

    except Exception as e:
        print(f"❌ 数据库验证失败: {e}")
        return False

    # API数据验证
    print(f"\n🌐 API数据验证:")
    try:
        base_url = "http://localhost:8080/api"
        test_uid = "23947287"

        # 测试UP主信息API
        response = requests.get(f"{base_url}/up/{test_uid}")
        api_data = response.json()

        if api_data.get('success'):
            up_info = api_data.get('up', {})
            print(f"  ✅ API UP主信息: {up_info.get('name')}")

            # 测试视频列表API
            response = requests.get(f"{base_url}/up/{test_uid}/videos")
            videos_data = response.json()

            if videos_data.get('success'):
                videos = videos_data.get('videos', [])
                print(f"  ✅ API 视频数量: {len(videos)}")

                if videos:
                    first_video = videos[0]
                    print(f"  ✅ 第一个视频: {first_video.get('title')}")
                    print(f"     播放: {first_video.get('play')}, 点赞: {first_video.get('like')}")
            else:
                print(f"  ❌ API视频列表失败: {videos_data.get('message')}")
        else:
            print(f"  ❌ API UP主信息失败: {api_data.get('message')}")

    except Exception as e:
        print(f"  ❌ API验证失败: {e}")
        return False

    print("\n🎉 数据完整性验证完成！")
    return True

if __name__ == "__main__":
    verify_complete_data()