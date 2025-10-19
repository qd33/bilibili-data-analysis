#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import pymysql
import requests
import json

def diagnose_data_issue():
    """诊断数据问题：对比数据库原始数据和API返回数据"""

    print("=== 数据问题诊断 ===")

    # 1. 检查数据库原始数据
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
            # 检查表结构
            cursor.execute("DESC video")
            columns = cursor.fetchall()
            print("\n📊 video表结构:")
            stat_columns = []
            for column in columns:
                column_name = column[0]
                column_type = column[1]
                print(f"  {column_name} - {column_type}")
                if 'count' in column_name.lower() or column_name in ['duration']:
                    stat_columns.append(column_name)

            # 检查实际数据
            print(f"\n🎬 视频数据检查:")
            cursor.execute("""
                SELECT 
                    bv_id, title, 
                    play_count, like_count, danmaku_count,
                    comment_count, coin_count, share_count, favorite_count,
                    duration
                FROM video 
                LIMIT 3
            """)
            videos = cursor.fetchall()

            for video in videos:
                print(f"\n📺 视频: {video[1]}")
                print(f"  BV号: {video[0]}")
                print(f"  数据库原始数据:")
                print(f"    播放: {video[2]}")
                print(f"    点赞: {video[3]}")
                print(f"    弹幕: {video[4]}")
                print(f"    评论: {video[5]}")
                print(f"    投币: {video[6]}")
                print(f"    分享: {video[7]}")
                print(f"    收藏: {video[8]}")
                print(f"    时长: {video[9]}")

        connection.close()

    except Exception as e:
        print(f"❌ 数据库检查失败: {e}")
        return False

    # 2. 检查API返回数据
    print(f"\n🌐 API数据检查:")
    try:
        base_url = "http://localhost:8080/api"
        test_uid = "23947287"

        # 获取API数据
        response = requests.get(f"{base_url}/up/{test_uid}/videos")
        api_data = response.json()

        if api_data.get('success'):
            videos = api_data.get('videos', [])
            print(f"  API返回视频数量: {len(videos)}")

            if videos:
                first_video = videos[0]
                print(f"  📺 第一个视频API数据:")
                print(f"    标题: {first_video.get('title')}")
                print(f"    播放: {first_video.get('play')}")
                print(f"    点赞: {first_video.get('like')}")
                print(f"    弹幕: {first_video.get('danmaku')}")
                print(f"    评论: {first_video.get('comment')}")
                print(f"    投币: {first_video.get('coin')}")
                print(f"    分享: {first_video.get('share')}")
                print(f"    收藏: {first_video.get('favorite')}")
                print(f"    时长: {first_video.get('duration')}")

                # 对比数据库和API数据
                print(f"\n🔍 数据对比:")
                db_video = videos[0] if videos else None
                if db_video and videos:
                    print(f"    播放 - 数据库: {db_video[2]}, API: {first_video.get('play')}")
                    print(f"    点赞 - 数据库: {db_video[3]}, API: {first_video.get('like')}")
        else:
            print(f"  ❌ API请求失败: {api_data.get('message')}")

    except Exception as e:
        print(f"  ❌ API检查失败: {e}")
        return False

    print("\n🎉 数据诊断完成！")
    return True

if __name__ == "__main__":
    diagnose_data_issue()