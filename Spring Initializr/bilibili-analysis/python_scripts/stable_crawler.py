# stable_crawler.py
import asyncio
import requests
from bilibili_api import video
import pymysql
from datetime import datetime

class StableBilibiliCrawler:
    def __init__(self):
        self.db_config = {
            'host': 'localhost',
            'user': 'root',
            'password': '123456789',
            'database': 'bilibili_db',
            'charset': 'utf8mb4'
        }

    def get_connection(self):
        return pymysql.connect(**self.db_config)

    async def crawl_with_api(self, bv_id):
        """使用bilibili-api-python库爬取数据:cite[3]"""
        try:
            # 实例化Video类
            v = video.Video(bvid=bv_id)
            # 获取信息
            info = await v.get_info()
            return info
        except Exception as e:
            print(f"API爬取失败 {bv_id}: {e}")
            return None

    def insert_up(self, up_data):
        """插入UP主数据"""
        connection = self.get_connection()
        try:
            with connection.cursor() as cursor:
                sql = """
                INSERT IGNORE INTO up (uid, name, avatar) 
                VALUES (%s, %s, %s)
                """
                cursor.execute(sql, (
                    up_data['uid'],
                    up_data['name'],
                    up_data['avatar']
                ))
                connection.commit()
                return cursor.lastrowid
        finally:
            connection.close()

    def insert_video(self, video_data, up_id):
        """插入视频数据"""
        connection = self.get_connection()
        try:
            with connection.cursor() as cursor:
                sql = """
                INSERT IGNORE INTO video 
                (bv_id, title, cover_url, description, publish_time, video_partition, up_id) 
                VALUES (%s, %s, %s, %s, %s, %s, %s)
                """
                cursor.execute(sql, (
                    video_data['bv_id'],
                    video_data['title'],
                    video_data['cover_url'],
                    video_data['description'],
                    video_data['publish_time'],
                    video_data['partition'],
                    up_id
                ))
                connection.commit()
                return cursor.lastrowid
        finally:
            connection.close()

    def insert_video_stat(self, video_id, stats):
        """插入视频统计数据"""
        connection = self.get_connection()
        try:
            with connection.cursor() as cursor:
                sql = """
                INSERT IGNORE INTO video_stat 
                (video_id, record_date, view_count, like_count, coin_count, favorite_count, 
                 danmaku_count, reply_count, share_count) 
                VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s)
                """
                cursor.execute(sql, (
                    video_id,
                    datetime.now().date(),
                    stats.get('view', 0),
                    stats.get('like', 0),
                    stats.get('coin', 0),
                    stats.get('favorite', 0),
                    stats.get('danmaku', 0),
                    stats.get('reply', 0),
                    stats.get('share', 0)
                ))
                connection.commit()
        finally:
            connection.close()

    async def process_video(self, bv_id):
        """处理单个视频"""
        print(f"\n🎬 处理视频: {bv_id}")

        # 使用API爬取数据
        info = await self.crawl_with_api(bv_id)
        if not info:
            return False

        print(f"✅ 爬取成功: {info['title']}")

        # 提取UP主数据
        up_data = {
            'uid': str(info['owner']['mid']),
            'name': info['owner']['name'],
            'avatar': info['owner']['face']
        }

        # 插入UP主
        up_id = self.insert_up(up_data)
        print(f"✅ UP主插入完成: {up_data['name']}")

        # 准备视频数据
        video_data = {
            'bv_id': info['bvid'],
            'title': info['title'],
            'cover_url': info['pic'],
            'description': info['desc'][:500] if info['desc'] else "",
            'publish_time': datetime.fromtimestamp(info['pubdate']),
            'partition': self.get_partition_name(info['tid'])
        }

        # 插入视频
        video_id = self.insert_video(video_data, up_id)
        if video_id:
            print(f"✅ 视频插入成功: {video_data['title']}")

            # 插入统计数据
            stats = {
                'view': info['stat']['view'],
                'like': info['stat']['like'],
                'coin': info['stat']['coin'],
                'favorite': info['stat']['favorite'],
                'danmaku': info['stat']['danmaku'],
                'reply': info['stat']['reply'],
                'share': info['stat']['share']
            }
            self.insert_video_stat(video_id, stats)
            print(f"📊 统计数据插入成功")
            return True
        else:
            print(f"⚠️ 视频可能已存在")
            return False

    def get_partition_name(self, tid):
        """获取分区名称:cite[3]"""
        partitions = {
            1: "动画", 17: "单机游戏", 3: "音乐",
            129: "舞蹈", 4: "游戏", 36: "知识",
            188: "科技", 160: "生活", 119: "鬼畜",
            155: "时尚", 165: "广告", 5: "娱乐"
        }
        return partitions.get(tid, "其他")

    def check_database(self):
        """检查数据库状态"""
        connection = self.get_connection()
        try:
            with connection.cursor() as cursor:
                cursor.execute("SELECT COUNT(*) FROM video")
                video_count = cursor.fetchone()[0]
                cursor.execute("SELECT COUNT(*) FROM up")
                up_count = cursor.fetchone()[0]
                print(f"\n📊 当前数据库状态: {video_count} 个视频, {up_count} 个UP主")
        finally:
            connection.close()

async def main():
    crawler = StableBilibiliCrawler()

    # 检查当前状态
    crawler.check_database()

    # 处理视频列表
    bv_ids = [
        "BV1LiHWzaEzy",  # 之前成功的
        "BV1u3xTzJEku",  # 之前成功的
        "BV1xx411c79H",  # 备用的
    ]

    success_count = 0
    for bv_id in bv_ids:
        if await crawler.process_video(bv_id):
            success_count += 1

    # 最终状态
    print(f"\n🎉 完成! 成功处理 {success_count} 个视频")
    crawler.check_database()

if __name__ == "__main__":
    # 运行异步主函数
    asyncio.run(main())