#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import asyncio
from bilibili_api import user

async def test_fixed_crawler():
    """测试修复后的爬虫功能"""
    try:
        print("=== 测试修复后的爬虫功能 ===")

        # 测试UP主
        u = user.User(uid=23947287)

        # 获取基本信息
        user_info = await u.get_user_info()
        print(f"✅ UP主基本信息: {user_info['name']}")

        # 获取关系信息（包含粉丝数）
        relation_info = await u.get_relation_info()
        print(f"✅ 粉丝数: {relation_info['follower']}")
        print(f"✅ 关注数: {relation_info['following']}")

        # 获取视频列表
        videos = await u.get_videos()
        video_list = videos['list']['vlist']
        print(f"✅ 视频数量: {len(video_list)}")

        if video_list:
            print(f"✅ 最新视频: {video_list[0]['title']}")
            print(f"✅ BV号: {video_list[0]['bvid']}")

        return True

    except Exception as e:
        print(f"❌ 测试失败: {e}")
        return False

# 运行测试
if __name__ == "__main__":
    result = asyncio.run(test_fixed_crawler())
    if result:
        print("\n🎉 修复成功！现在可以运行完整爬虫了。")
    else:
        print("\n⚠️ 修复失败，请检查问题。")