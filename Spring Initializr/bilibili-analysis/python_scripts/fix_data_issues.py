#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import pymysql
import requests
import asyncio
from bilibili_api import video
import datetime

async def fix_video_stats():
    """修复视频统计数据"""
    try:
        # 数据库连接
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
            # 获取所有视频的BV号
            cursor.execute("SELECT id, bv_id, title FROM video WHERE play_count = 0 OR like_count = 0")
            videos = cursor.fetchall()

            print(f"📊 找到 {len(videos)} 个需要修复统计数据的视频")

            fixed_count = 0

            for video_id, bv_id, title in videos:
                try:
                    print(f"🔧 修复视频: {title} ({bv_id})")

                    # 从B站API重新获取视频信息
                    v = video.Video(bvid=bv_id)
                    video_info = await v.get_info()

                    # 更新数据库
                    update_sql = """UPDATE video SET 
                                   play_count = %s, like_count = %s, danmaku_count = %s, 
                                   comment_count = %s, coin_count = %s, share_count = %s, 
                                   favorite_count = %s, duration = %s
                                   WHERE id = %s"""

                    cursor.execute(update_sql, (
                        video_info['stat']['view'],
                        video_info['stat']['like'],
                        video_info['stat']['danmaku'],
                        video_info['stat']['reply'],
                        video_info['stat']['coin'],
                        video_info['stat']['share'],
                        video_info['stat']['favorite'],
                        video_info['duration'],
                        video_id
                    ))

                    fixed_count += 1
                    print(f"✅ 修复完成: {title}")
                    print(f"   播放: {video_info['stat']['view']}, 点赞: {video_info['stat']['like']}")

                    # 避免请求过快
                    await asyncio.sleep(1)

                except Exception as e:
                    print(f"❌ 修复视频失败 {bv_id}: {e}")
                    continue

        connection.commit()
        connection.close()

        print(f"\n🎉 数据修复完成！共修复 {fixed_count} 个视频")
        return True

    except Exception as e:
        print(f"❌ 数据修复失败: {e}")
        return False

# 运行修复
if __name__ == "__main__":
    asyncio.run(fix_video_stats())