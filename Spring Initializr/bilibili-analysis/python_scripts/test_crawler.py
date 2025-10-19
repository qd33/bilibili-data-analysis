#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import sys
import asyncio
import json

print("=== B站爬虫功能测试 ===")

try:
    from bilibili_api import user, video
    import pymysql

    async def test_user_info():
        """测试获取用户信息"""
        try:
            print("\n1. 测试获取UP主信息...")
            # 测试一个已知的UP主（老番茄）
            u = user.User(uid=23947287)
            user_info = await u.get_user_info()
            print(f"✅ UP主信息获取成功:")
            print(f"   名称: {user_info['name']}")
            print(f"   粉丝数: {user_info['follower']}")
            print(f"   头像: {user_info['face']}")
            return user_info
        except Exception as e:
            print(f"❌ 获取UP主信息失败: {e}")
            return None

    async def test_video_info():
        """测试获取视频信息"""
        try:
            print("\n2. 测试获取视频信息...")
            # 测试一个已知的视频
            v = video.Video(bvid="BV1vKpVzKEmC")
            video_info = await v.get_info()
            print(f"✅ 视频信息获取成功:")
            print(f"   标题: {video_info['title']}")
            print(f"   播放量: {video_info['stat']['view']}")
            print(f"   分区: {video_info['tname']}")
            return video_info
        except Exception as e:
            print(f"❌ 获取视频信息失败: {e}")
            return None

    async def test_user_videos():
        """测试获取用户视频列表"""
        try:
            print("\n3. 测试获取UP主视频列表...")
            u = user.User(uid=23947287)
            videos = await u.get_videos()
            video_list = videos['list']['vlist']
            print(f"✅ 视频列表获取成功，共 {len(video_list)} 个视频")
            if video_list:
                print(f"   最新视频: {video_list[0]['title']}")
                print(f"   BV号: {video_list[0]['bvid']}")
            return video_list
        except Exception as e:
            print(f"❌ 获取视频列表失败: {e}")
            return None

    async def main():
        """主测试函数"""
        print("开始测试B站API功能...")

        # 测试各项功能
        user_info = await test_user_info()
        video_info = await test_video_info()
        video_list = await test_user_videos()

        # 汇总结果
        print("\n" + "="*50)
        print("测试结果汇总:")
        print(f"✅ UP主信息测试: {'成功' if user_info else '失败'}")
        print(f"✅ 视频信息测试: {'成功' if video_info else '失败'}")
        print(f"✅ 视频列表测试: {'成功' if video_list else '失败'}")

        if user_info and video_info and video_list:
            print("\n🎉 所有功能测试通过！爬虫可以正常工作。")
            return True
        else:
            print("\n⚠️ 部分功能测试失败，请检查网络连接或API限制。")
            return False

    # 运行测试
    if __name__ == "__main__":
        result = asyncio.run(main())
        if result:
            print("\n现在可以运行完整的爬虫脚本了！")
        else:
            print("\n请检查问题后重试。")

except ImportError as e:
    print(f"❌ 导入依赖包失败: {e}")
    print("请确保已安装所有依赖: pip install -r requirements.txt")
except Exception as e:
    print(f"❌ 测试过程中出现错误: {e}")