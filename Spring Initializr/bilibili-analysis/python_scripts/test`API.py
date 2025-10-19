import asyncio
from bilibili_api import user

async def debug_user_info():
    try:
        u = user.User(uid=23947287)  # 这里替换你实际查询的UID
        user_info = await u.get_user_info()
        print("=== 完整的用户信息 ===")
        print(user_info)  # 查看整个字典里到底有什么字段

        # 特别查看统计信息部分（如果存在）
        if 'data' in user_info and 'follower' not in user_info:
            print("可能粉丝数字段在 data 里，或者字段名已更新")
    except Exception as e:
        print(f"获取信息失败: {e}")

asyncio.run(debug_user_info())
