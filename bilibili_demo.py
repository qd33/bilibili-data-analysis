# -*- coding: utf-8 -*-
import requests
import json
from datetime import datetime

def crawl_bilibili_video(bv_id):
    """
    根据BV号爬取B站视频数据
    """
    url = f"https://api.bilibili.com/x/web-interface/view?bvid={bv_id}"
    headers = {
        "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36",
        "Referer": f"https://www.bilibili.com/video/{bv_id}",
        "Origin": "https://www.bilibili.com",
        "Accept": "application/json, text/plain, */*",
        "Accept-Language": "zh-CN,zh;q=0.9,en;q=0.8",
        "Accept-Encoding": "gzip, deflate, br",
        "Connection": "keep-alive",
    }

    try:
        response = requests.get(url, headers=headers, timeout=10)
        print(f"HTTP状态码: {response.status_code}")  # 添加这行来查看HTTP状态码
        response.raise_for_status()
        data = response.json()
        print(f"API返回数据: {data}")  # 添加这行来查看原始API响应
        
        if data['code'] != 0:
            print(f"错误: {data['message']}")
            return None

        data = data['data']
        video_info = {
            "BV号": data['bvid'],
            "视频标题": data['title'],
            "UP主": data['owner']['name'],
            "UP主UID": data['owner']['mid'],
            "发布时间": datetime.fromtimestamp(data['pubdate']).strftime('%Y-%m-%d'),
            "播放量": data['stat']['view'],
            "弹幕数": data['stat']['danmaku'],
            "点赞数": data['stat']['like'],
            "投币数": data['stat']['coin'],
            "收藏数": data['stat']['favorite'],
            "评论数": data['stat']['reply'],
            "分享数": data['stat']['share'],
            "抓取时间": datetime.now().strftime('%Y-%m-%d %H:%M:%S')
        }
        return video_info

    except Exception as e:
        print(f"出错了: {e}")
        return None

if __name__ == "__main__":
    # 尝试两个不同的BV号
    for bv_id in ["BV1hRHzzvEvo", "BV1GJ4y1Y7p9"]:
        print(f"\n尝试爬取视频: {bv_id}")
        video_data = crawl_bilibili_video(bv_id)
        
        if video_data:
            print("="*50)
            print("✅ B站视频数据爬取成功！")
            print("="*50)
            for key, value in video_data.items():
                print(f"{key}: {value}")
            print("="*50)
            break
        else:
            print(f"视频 {bv_id} 爬取失败，尝试下一个...")
    
    if not video_data:
        print("所有视频爬取均失败，请检查网络连接或稍后再试。")
