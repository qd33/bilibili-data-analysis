#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import sys
import os

print("=== Python环境测试 ===")
print(f"Python版本: {sys.version}")
print(f"当前工作目录: {os.getcwd()}")
print(f"脚本所在目录: {os.path.dirname(os.path.abspath(__file__))}")

try:
    from bilibili_api import user, video
    import pymysql
    import asyncio
    import aiohttp
    print("✅ 所有依赖包导入成功！")

    # 测试异步环境
    async def test_async():
        print("✅ 异步环境正常")

    asyncio.run(test_async())

    # 测试bilibili-api版本
    try:
        import bilibili_api
        print(f"✅ bilibili-api版本: {bilibili_api.__version__}")
    except AttributeError:
        print("✅ bilibili-api已安装，但无法获取版本号")

except ImportError as e:
    print(f"❌ 依赖包导入失败: {e}")
    print("请运行: pip install -r requirements.txt")

print("\n=== 环境测试完成 ===")