#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import requests
import json
import time

def test_all_apis():
    """测试所有后端API接口"""
    base_url = "http://localhost:8080/api"
    test_uid = "23947287"  # 小约翰可汗

    print("=== 完整后端API测试 ===")

    tests = [
        # 基础服务测试
        ("健康检查", f"{base_url}/proxy/health", "GET"),
        ("爬虫状态", f"{base_url}/up/checkStatus", "GET"),
        ("Python环境", f"{base_url}/up/testPython", "GET"),

        # UP主数据测试
        ("UP主基本信息", f"{base_url}/up/{test_uid}", "GET"),
        ("UP主存在检查", f"{base_url}/up/{test_uid}/exists", "GET"),
        ("UP主视频列表", f"{base_url}/up/{test_uid}/videos", "GET"),
        ("UP主完整信息", f"{base_url}/up/{test_uid}/detail", "GET"),
        ("UP主趋势数据", f"{base_url}/up/{test_uid}/trend", "GET"),
    ]

    all_success = True

    for test_name, url, method in tests:
        try:
            print(f"\n🔍 测试: {test_name}")
            print(f"   请求: {method} {url}")

            if method == "GET":
                response = requests.get(url, timeout=10)
            else:
                response = requests.post(url, timeout=10)

            data = response.json()

            if response.status_code == 200:
                if data.get('success', True):
                    print(f"   ✅ 成功 - 状态码: {response.status_code}")

                    # 显示关键信息
                    if 'up' in data:
                        up_info = data['up']
                        print(f"      UP主: {up_info.get('name')} (UID: {up_info.get('uid')})")

                    if 'videos' in data:
                        videos = data['videos']
                        print(f"      视频数量: {len(videos)}")

                    if 'message' in data:
                        print(f"      消息: {data['message']}")
                else:
                    print(f"   ⚠️ API返回失败 - 消息: {data.get('message')}")
                    all_success = False
            else:
                print(f"   ❌ HTTP错误 - 状态码: {response.status_code}")
                print(f"      响应: {data}")
                all_success = False

        except Exception as e:
            print(f"   ❌ 请求失败: {e}")
            all_success = False

        # 短暂延迟，避免请求过快
        time.sleep(1)

    # 测试图片代理
    print(f"\n🔍 测试: 图片代理服务")
    try:
        # 使用一个已知的B站图片URL进行测试
        test_image_url = "https://i0.hdslb.com/bfs/face/adaad997126a1e379d780806728c77e91de9931d.jpg"
        encoded_url = requests.utils.quote(test_image_url)
        proxy_url = f"{base_url}/proxy/image?url={encoded_url}"

        response = requests.get(proxy_url, timeout=10)
        if response.status_code == 200:
            print("   ✅ 图片代理服务正常")
        else:
            print(f"   ❌ 图片代理失败 - 状态码: {response.status_code}")
            all_success = False
    except Exception as e:
        print(f"   ❌ 图片代理测试失败: {e}")
        all_success = False

    print("\n" + "="*50)
    if all_success:
        print("🎉 所有API测试通过！系统运行正常。")
    else:
        print("⚠️ 部分测试失败，请检查相关问题。")

    return all_success

if __name__ == "__main__":
    test_all_apis()