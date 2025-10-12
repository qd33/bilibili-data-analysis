#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import sys
import asyncio
from bilibili_api import video, user
import pymysql
import datetime

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
            print("[INFO] 数据库连接成功")
        except Exception as e:
            print(f"[ERROR] 数据库连接失败: {e}")
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
                    print(f"[INFO] UP主已存在: {up_data['name']}")
                    return result[0]

                # 插入新UP主
                sql = """INSERT INTO up (uid, name, avatar) 
                         VALUES (%s, %s, %s)"""
                cursor.execute(sql, (up_data['uid'], up_data['name'], up_data['avatar']))
                self.connection.commit()
                print(f"[SUCCESS] UP主数据插入完成: {up_data['name']}")
                return cursor.lastrowid
        except Exception as e:
            print(f"[ERROR] 插入UP主数据失败: {e}")
            return None

    def insert_video(self, video_data, up_id):
        """插入视频数据"""
        try:
            with self.connection.cursor() as cursor:
                # 检查是否已存在
                sql = "SELECT id FROM video WHERE bv_id = %s"
                cursor.execute(sql, (video_data['bv_id'],))
                result = cursor.fetchone()

                if result:
                    print(f"[INFO] 视频已存在: {video_data['title']}")
                    return result[0]

                # 插入新视频
                sql = """INSERT INTO video (bv_id, title, cover_url, description, 
                         up_id, publish_time, video_partition) 
                         VALUES (%s, %s, %s, %s, %s, %s, %s)"""
                cursor.execute(sql, (
                    video_data['bv_id'], video_data['title'],
                    video_data['cover_url'], video_data['description'],
                    up_id, video_data['publish_time'], video_data['video_partition']
                ))
                self.connection.commit()
                print(f"[SUCCESS] 视频数据插入完成: {video_data['title']}")
                return cursor.lastrowid
        except Exception as e:
            print(f"[ERROR] 插入视频数据失败: {e}")
            return None

    def insert_video_stat(self, video_id, stats):
        """插入视频统计数据"""
        try:
            with self.connection.cursor() as cursor:
                # 检查今天是否已有数据
                today = datetime.date.today()
                sql = """SELECT id FROM video_stat 
                         WHERE video_id = %s AND record_date = %s"""
                cursor.execute(sql, (video_id, today))
                result = cursor.fetchone()

                if result:
                    # 更新现有数据
                    sql = """UPDATE video_stat 
                             SET view_count = %s, like_count = %s, coin_count = %s,
                                 favorite_count = %s, danmaku_count = %s, reply_count = %s,
                                 share_count = %s
                             WHERE id = %s"""
                    cursor.execute(sql, (
                        stats['view'], stats['like'], stats['coin'],
                        stats['favorite'], stats['danmaku'], stats['reply'],
                        stats['share'], result[0]
                    ))
                    print(f"[INFO] 视频统计数据已更新")
                else:
                    # 插入新数据
                    sql = """INSERT INTO video_stat 
                             (video_id, record_date, view_count, like_count, 
                              coin_count, favorite_count, danmaku_count, 
                              reply_count, share_count) 
                             VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s)"""
                    cursor.execute(sql, (
                        video_id, today, stats['view'], stats['like'],
                        stats['coin'], stats['favorite'], stats['danmaku'],
                        stats['reply'], stats['share']
                    ))
                    print(f"[SUCCESS] 视频统计数据已保存")

                self.connection.commit()
                return True
        except Exception as e:
            print(f"[ERROR] 插入视频统计数据失败: {e}")
            return False

    async def process_video(self, bv_id):
        """处理单个视频"""
        try:
            print(f"[PROCESS] 处理视频: {bv_id}")

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
                return False

            # 插入视频数据
            video_id = self.insert_video({
                'bv_id': bv_id,
                'title': video_info['title'],
                'cover_url': video_info['pic'],
                'description': video_info['desc'],
                'publish_time': datetime.datetime.fromtimestamp(video_info['pubdate']),
                'video_partition': video_info['tname']
            }, up_id)

            if not video_id:
                return False

            # 插入统计数据
            stats = {
                'view': video_info['stat']['view'],
                'like': video_info['stat']['like'],
                'coin': video_info['stat']['coin'],
                'favorite': video_info['stat']['favorite'],
                'danmaku': video_info['stat']['danmaku'],
                'reply': video_info['stat']['reply'],
                'share': video_info['stat']['share']
            }

            success = self.insert_video_stat(video_id, stats)
            if success:
                print(f"[SUCCESS] 视频处理完成: {bv_id}")
            return success

        except Exception as e:
            print(f"[ERROR] 处理视频失败 {bv_id}: {e}")
            return False

    def check_database(self):
        """检查数据库状态"""
        try:
            with self.connection.cursor() as cursor:
                # 统计各表数据量
                tables = ['up', 'video', 'video_stat']
                for table in tables:
                    cursor.execute(f"SELECT COUNT(*) FROM {table}")
                    count = cursor.fetchone()[0]
                    print(f"[DATABASE] {table} 表记录数: {count}")
            return True
        except Exception as e:
            print(f"[ERROR] 检查数据库失败: {e}")
            return False

    async def crawl_up_videos(self, uid):
        """通过UP主UID抓取该UP主的所有视频"""
        try:
            print(f"[START] 开始抓取UP主 {uid} 的视频数据")

            # 创建User对象
            u = user.User(uid=int(uid))

            # 获取UP主基本信息
            up_info = await u.get_user_info()
            print(f"[SUCCESS] 获取UP主信息成功: {up_info['name']}")

            # 插入UP主数据到数据库
            up_data = {
                'uid': str(up_info['mid']),
                'name': up_info['name'],
                'avatar': up_info['face']
            }
            up_id = self.insert_up(up_data)
            print(f"[SUCCESS] UP主数据插入完成: {up_info['name']}")

            # 获取UP主的视频列表（前20个）
            page = 1
            success_count = 0
            max_videos = 10  # 限制抓取数量，避免请求过多

            while success_count < max_videos:
                try:
                    videos = await u.get_videos(pn=page)
                    video_list = videos['list']['vlist']

                    if not video_list:
                        print("[INFO] 没有更多视频了")
                        break

                    print(f"[PAGE] 第{page}页，获取到 {len(video_list)} 个视频")

                    # 处理每个视频
                    for video_info in video_list:
                        if success_count >= max_videos:
                            break

                        bv_id = video_info['bvid']
                        print(f"[VIDEO] 处理视频 {success_count + 1}: {bv_id} - {video_info['title']}")

                        # 使用现有的视频处理逻辑
                        if await self.process_video(bv_id):
                            success_count += 1

                        # 添加延迟，避免请求过快
                        await asyncio.sleep(1)

                    page += 1

                except Exception as e:
                    print(f"[ERROR] 获取第{page}页视频失败: {e}")
                    break

            print(f"[COMPLETE] UP主 {up_info['name']} 数据抓取完成，成功处理 {success_count} 个视频")
            return success_count

        except Exception as e:
            print(f"[ERROR] 抓取UP主 {uid} 失败: {e}")
            return 0

# 添加UP主抓取的主函数
async def crawl_up_main(uid):
    """UP主数据抓取主函数"""
    crawler = StableBilibiliCrawler()

    # 检查当前状态
    crawler.check_database()

    print(f"[LAUNCH] 开始抓取UP主 {uid} 的数据...")

    # 抓取UP主数据
    success_count = await crawler.crawl_up_videos(uid)

    # 最终状态
    print(f"[FINAL] UP主数据抓取完成! 成功处理 {success_count} 个视频")
    crawler.check_database()
    return success_count

# 修改主函数以支持命令行参数
if __name__ == "__main__":
    # 设置默认编码，避免Windows控制台编码问题
    if sys.platform == "win32":
        import io
        sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8', errors='ignore')
        sys.stderr = io.TextIOWrapper(sys.stderr.buffer, encoding='utf-8', errors='ignore')

    if len(sys.argv) > 1 and sys.argv[1] == "--uid":
        uid = sys.argv[2]
        print(f"[LAUNCH] 开始抓取UP主 {uid} 的数据")
        result = asyncio.run(crawl_up_main(uid))
        print(f"[RESULT] 最终结果: 成功抓取 {result} 个视频")
    else:
        # 原有的视频抓取逻辑
        print("[INFO] 执行默认视频抓取任务")
        # 这里可以添加默认的视频抓取逻辑
        print("[INFO] 请使用 --uid 参数指定UP主UID")