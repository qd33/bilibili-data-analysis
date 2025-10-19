#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import sys
import asyncio
import json
from bilibili_api import video, user
import pymysql
import datetime
import os

# 🆕 设置系统编码为UTF-8
sys.stdout.reconfigure(encoding='utf-8')
sys.stderr.reconfigure(encoding='utf-8')

# 🆕 自定义JSON序列化器，处理datetime对象
class DateTimeEncoder(json.JSONEncoder):
    def default(self, obj):
        if isinstance(obj, (datetime.datetime, datetime.date)):
            return obj.isoformat()
        return super().default(obj)

class StableBilibiliCrawler:
    def __init__(self):
        # 数据库连接配置
        self.db_config = {
            'host': 'localhost',
            'user': 'root',
            'password': '123456789',
            'database': 'bilibili_db',
            'charset': 'utf8mb4'
        }
        self.connection = None
        self.connect_db()

    def connect_db(self):
        """连接数据库"""
        try:
            self.connection = pymysql.connect(**self.db_config)
            print("[INFO] 数据库连接成功", file=sys.stderr)
        except Exception as e:
            print(f"[ERROR] 数据库连接失败: {e}", file=sys.stderr)
            sys.exit(1)

    def insert_up(self, up_data):
        """插入UP主数据"""
        try:
            with self.connection.cursor() as cursor:
                # 检查是否已存在
                sql = "SELECT id FROM up WHERE uid = %s"
                cursor.execute(sql, (up_data['uid'],))
                result = cursor.fetchone()

                if result:
                    print(f"[INFO] UP主已存在: {up_data['name']}", file=sys.stderr)
                    return result[0]

                # 插入新UP主
                sql = """INSERT INTO up (uid, name, avatar, follower_count) 
                         VALUES (%s, %s, %s, %s)"""
                cursor.execute(sql, (
                    up_data['uid'],
                    up_data['name'],
                    up_data['avatar'],
                    up_data.get('follower_count', 0)
                ))
                self.connection.commit()
                print(f"[SUCCESS] UP主数据插入完成: {up_data['name']}", file=sys.stderr)
                return cursor.lastrowid
        except Exception as e:
            print(f"[ERROR] 插入UP主数据失败: {e}", file=sys.stderr)
            return None

    def insert_video(self, video_data, up_id):
        """插入视频数据"""
        try:
            with self.connection.cursor() as cursor:
                # 🆕 修复：正确处理 bvid 字段
                bvid = video_data.get('bvid') or video_data.get('bv_id')
                if not bvid:
                    print(f"[ERROR] 视频数据缺少bvid字段: {video_data}", file=sys.stderr)
                    return None

                # 检查是否已存在
                sql = "SELECT id FROM video WHERE bv_id = %s"
                cursor.execute(sql, (bvid,))
                result = cursor.fetchone()

                if result:
                    print(f"[INFO] 视频已存在: {video_data.get('title', '未知标题')}", file=sys.stderr)
                    return result[0]

                # 🆕 修复：使用正确的数据库字段名
                sql = """INSERT INTO video (
                         bv_id, title, cover_url, description, 
                         up_id, publish_time, video_partition, 
                         play_count, like_count, danmaku_count, comment_count,
                         coin_count, share_count, favorite_count, duration) 
                         VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)"""

                # 🆕 修复：确保publish_time是字符串格式
                publish_time = video_data.get('publish_time')
                if isinstance(publish_time, datetime.datetime):
                    publish_time = publish_time.isoformat()

                cursor.execute(sql, (
                    bvid,
                    video_data.get('title', ''),
                    video_data.get('cover_url', ''),
                    video_data.get('description', ''),
                    up_id,
                    publish_time,  # 🆕 现在确保是字符串
                    video_data.get('video_partition', '未知分区'),
                    video_data.get('play', 0),
                    video_data.get('like', 0),
                    video_data.get('danmaku', 0),
                    video_data.get('comment', 0),
                    video_data.get('coin', 0),
                    video_data.get('share', 0),
                    video_data.get('favorite', 0),
                    video_data.get('duration', 0)
                ))
                self.connection.commit()
                print(f"[SUCCESS] 视频数据插入完成: {video_data.get('title', '未知标题')}", file=sys.stderr)
                return cursor.lastrowid
        except Exception as e:
            print(f"[ERROR] 插入视频数据失败: {e}", file=sys.stderr)
            return None

    async def process_video(self, bv_id):
        """处理单个视频"""
        try:
            print(f"[PROCESS] 处理视频: {bv_id}", file=sys.stderr)

            # 获取视频信息
            v = video.Video(bvid=bv_id)
            video_info = await v.get_info()

            # 获取UP主ID
            up_id = self.insert_up({
                'uid': str(video_info['owner']['mid']),
                'name': video_info['owner']['name'],
                'avatar': video_info['owner']['face']
            })

            if not up_id:
                return None

            # 🆕 修复：确保所有datetime对象都转换为字符串
            publish_time = datetime.datetime.fromtimestamp(video_info['pubdate'])

            # 插入视频数据
            video_id = self.insert_video({
                'bvid': bv_id,
                'title': video_info['title'],
                'cover_url': video_info['pic'],
                'description': video_info['desc'],
                'publish_time': publish_time.isoformat(),  # 🆕 转换为字符串
                'video_partition': video_info['tname'],
                'play': video_info['stat']['view'],
                'like': video_info['stat']['like'],
                'danmaku': video_info['stat']['danmaku'],
                'comment': video_info['stat']['reply'],
                'coin': video_info['stat']['coin'],
                'share': video_info['stat']['share'],
                'favorite': video_info['stat']['favorite'],
                'duration': video_info['duration']
            }, up_id)

            return video_id

        except Exception as e:
            print(f"[ERROR] 处理视频失败 {bv_id}: {e}", file=sys.stderr)
            return None

    async def crawl_up_videos(self, uid):
        """通过UP主UID抓取该UP主的所有视频"""
        try:
            print(f"[START] 开始抓取UP主 {uid} 的视频数据", file=sys.stderr)

            # 创建User对象
            u = user.User(uid=int(uid))

            # 获取UP主基本信息
            up_info = await u.get_user_info()
            print(f"[SUCCESS] 获取UP主信息成功: {up_info['name']}", file=sys.stderr)

            # 🆕 获取关系信息（包含粉丝数）
            relation_info = await u.get_relation_info()
            follower_count = relation_info['follower']
            print(f"[INFO] 粉丝数: {follower_count}", file=sys.stderr)

            # 插入UP主数据到数据库
            up_data = {
                'uid': str(up_info['mid']),
                'name': up_info['name'],
                'avatar': up_info['face'],
                'follower_count': follower_count
            }
            up_id = self.insert_up(up_data)

            # 获取UP主的视频列表
            page = 1
            success_count = 0
            max_videos = 5  # 🆕 减少抓取数量，避免超时
            video_list = []

            while success_count < max_videos:
                try:
                    videos = await u.get_videos(pn=page)
                    video_list_page = videos['list']['vlist']

                    if not video_list_page:
                        print("[INFO] 没有更多视频了", file=sys.stderr)
                        break

                    print(f"[PAGE] 第{page}页，获取到 {len(video_list_page)} 个视频", file=sys.stderr)

                    # 处理每个视频
                    for video_info in video_list_page:
                        if success_count >= max_videos:
                            break

                        bv_id = video_info['bvid']
                        print(f"[VIDEO] 处理视频 {success_count + 1}: {bv_id} - {video_info['title']}", file=sys.stderr)

                        try:
                            # 获取详细视频信息
                            v = video.Video(bvid=bv_id)
                            detail_info = await v.get_info()

                            # 🆕 修复：确保时间字段是字符串
                            publish_time = datetime.datetime.fromtimestamp(video_info['created'])

                            # 处理视频并收集数据
                            video_data = {
                                'bvid': bv_id,
                                'title': video_info['title'],
                                'cover_url': video_info['pic'],
                                'description': video_info['description'],
                                'publish_time': publish_time.isoformat(),  # 🆕 转换为字符串
                                'video_partition': detail_info.get('tname', '未知分区'),
                                'play': detail_info['stat']['view'],
                                'like': detail_info['stat']['like'],
                                'danmaku': detail_info['stat']['danmaku'],
                                'comment': detail_info['stat']['reply'],
                                'coin': detail_info['stat']['coin'],
                                'share': detail_info['stat']['share'],
                                'favorite': detail_info['stat']['favorite'],
                                'duration': detail_info['duration']
                            }
                            video_list.append(video_data)

                            # 插入数据库
                            video_id = self.insert_video(video_data, up_id)
                            if video_id:
                                success_count += 1

                        except Exception as video_error:
                            print(f"[ERROR] 处理单个视频失败 {bv_id}: {video_error}", file=sys.stderr)
                            continue

                        # 添加延迟，避免请求过快
                        await asyncio.sleep(2)

                    page += 1

                except Exception as e:
                    print(f"[ERROR] 获取第{page}页视频失败: {e}", file=sys.stderr)
                    break

            # 🆕 修复：使用自定义的JSON编码器
            result = {
                'success': True,
                'uid': uid,
                'up_data': up_data,
                'videos': video_list,
                'video_count': success_count,
                'message': f'成功抓取 {success_count} 个视频'
            }

            print(f"[COMPLETE] UP主 {up_info['name']} 数据抓取完成", file=sys.stderr)
            return result

        except Exception as e:
            print(f"[ERROR] 抓取UP主 {uid} 失败: {e}", file=sys.stderr)
            return {
                'success': False,
                'uid': uid,
                'message': f'抓取失败: {str(e)}'
            }

# 主函数
async def crawl_up_main(uid):
    """UP主数据抓取主函数"""
    crawler = StableBilibiliCrawler()
    result = await crawler.crawl_up_videos(uid)

    # 🆕 修复：使用自定义编码器确保datetime正确序列化
    json_output = json.dumps(result, ensure_ascii=False, indent=2, cls=DateTimeEncoder)
    print(json_output)

if __name__ == "__main__":
    # 🆕 设置环境编码
    if sys.platform.startswith('win'):
        os.system('chcp 65001 > nul')

    if len(sys.argv) > 1 and sys.argv[1] == "--uid":
        uid = sys.argv[2]
        asyncio.run(crawl_up_main(uid))
    else:
        error_result = {
            "success": False,
            "message": "请使用 --uid 参数指定UP主UID"
        }
        print(json.dumps(error_result, ensure_ascii=False))