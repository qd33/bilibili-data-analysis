# complete_bilibili_crawler.py
import requests
import json
from datetime import datetime
import time
import random

class CompleteBilibiliCrawler:
    def __init__(self, backend_url="http://localhost:8080/api"):
        self.backend_url = backend_url
        self.headers = {
            "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36",
            "Referer": "https://www.bilibili.com",
        }

    def crawl_video_detail(self, bv_id):
        """爬取视频详细信息"""
        url = f"https://api.bilibili.com/x/web-interface/view?bvid={bv_id}"

        try:
            response = requests.get(url, headers=self.headers, timeout=10)
            print(f"爬取 {bv_id} - HTTP状态码: {response.status_code}")
            response.raise_for_status()
            data = response.json()

            if data['code'] != 0:
                print(f"错误: {data['message']}")
                return None

            data = data['data']
            video_info = {
                "bvId": data['bvid'],
                "title": data['title'],
                "coverUrl": data['pic'],  # 封面URL
                "description": data['desc'][:500] if data['desc'] else "",  # 描述，限制长度
                "publishTime": datetime.fromtimestamp(data['pubdate']).strftime('%Y-%m-%d %H:%M:%S'),
                "videoPartition": self.get_partition_name(data['tid']),
                # UP主信息
                "up": {
                    "uid": str(data['owner']['mid']),
                    "name": data['owner']['name'],
                    "avatar": data['owner']['face']  # UP主头像
                },
                # 当前统计数据（用于创建第一条统计记录）
                "currentStats": {
                    "viewCount": data['stat']['view'],
                    "likeCount": data['stat']['like'],
                    "coinCount": data['stat']['coin'],
                    "favoriteCount": data['stat']['favorite'],
                    "danmakuCount": data['stat']['danmaku'],
                    "replyCount": data['stat']['reply'],
                    "shareCount": data['stat']['share']
                }
            }
            return video_info

        except Exception as e:
            print(f"爬取出错: {e}")
            return None

    def get_partition_name(self, tid):
        """将分区ID转换为分区名称"""
        partitions = {
            1: "动画", 17: "单机游戏", 3: "音乐",
            129: "舞蹈", 4: "游戏", 36: "知识",
            188: "科技", 160: "生活", 119: "鬼畜",
            155: "时尚", 165: "广告", 5: "娱乐"
        }
        return partitions.get(tid, "其他")

    def save_to_backend(self, data, endpoint):
        """保存数据到后端"""
        url = f"{self.backend_url}/{endpoint}"
        try:
            response = requests.post(url, json=data, timeout=10)
            print(f"保存到后端 {endpoint}: {response.status_code}")
            if response.status_code == 200:
                return response.json()
            else:
                print(f"保存失败，状态码: {response.status_code}")
                return None
        except Exception as e:
            print(f"保存到后端失败: {e}")
            return None

    def collect_demo_data(self):
        """采集演示数据"""
        # 热门视频BV号（不同分区）
        demo_bvids = [
            "BV1fx411M7C7",  # 动画区
            "BV1Js411o76u",  # 音乐区
            "BV1b4411E78m",  # 游戏区
            "BV1CJ4m1Y7p9",  # 知识区
            "BV1N4421E7qP",  # 生活区
        ]

        success_count = 0

        for bvid in demo_bvids:
            print(f"\n🎬 开始采集视频: {bvid}")

            # 1. 爬取视频信息
            video_data = self.crawl_video_detail(bvid)
            if not video_data:
                print(f"❌ 视频爬取失败: {bvid}")
                continue

            # 2. 先保存UP主信息
            up_result = self.save_to_backend(video_data['up'], "up")
            if up_result and up_result.get('success'):
                print(f"✅ UP主保存成功: {video_data['up']['name']}")
            else:
                print(f"⚠️ UP主保存可能失败或已存在: {video_data['up']['name']}")

            # 3. 准备视频数据（移除up对象，使用upUid）
            video_save_data = {
                "bvId": video_data['bvId'],
                "title": video_data['title'],
                "coverUrl": video_data['coverUrl'],
                "description": video_data['description'],
                "publishTime": video_data['publishTime'],
                "videoPartition": video_data['videoPartition'],
                "up": {"uid": video_data['up']['uid']}  # 只传递UID，让后端关联
            }

            # 4. 保存视频信息
            video_result = self.save_to_backend(video_save_data, "video")
            if video_result and video_result.get('success'):
                success_count += 1
                print(f"✅ 视频保存成功: {video_data['title']}")

                # 5. 保存初始统计数据
                stat_data = {
                    "bvId": video_data['bvId'],
                    "recordDate": datetime.now().strftime('%Y-%m-%d'),
                    **video_data['currentStats']
                }
                stat_result = self.save_to_backend(stat_data, "video/stats")
                if stat_result and stat_result.get('success'):
                    print(f"📊 统计数据保存成功")
                else:
                    print(f"⚠️ 统计数据保存失败")
            else:
                print(f"❌ 视频保存失败: {video_data['title']}")

            # 添加延迟，避免请求过快
            time.sleep(random.uniform(1, 2))

        print(f"\n🎉 数据采集完成！成功采集 {success_count} 个视频")
        return success_count

# 使用示例
if __name__ == "__main__":
    print("🚀 开始采集B站演示数据...")
    crawler = CompleteBilibiliCrawler()

    # 检查后端是否运行
    try:
        test_response = requests.get("http://localhost:8080/", timeout=5)
        if test_response.status_code == 200:
            print("✅ 后端服务正常运行")
        else:
            print("❌ 后端服务异常")
    except:
        print("❌ 后端服务未启动，请先启动Spring Boot应用")
        exit(1)

    # 开始采集数据
    crawler.collect_demo_data()