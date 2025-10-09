# check_database.py
import pymysql

def check_database_status():
    try:
        # 配置你的数据库连接参数
        connection = pymysql.connect(
            host='localhost',
            user='root',
            password='123456789',  # 请替换为你的MySQL密码
            database='bilibili_db',
            charset='utf8mb4'
        )

        with connection.cursor() as cursor:
            # 检查视频数量
            cursor.execute("SELECT COUNT(*) AS video_count FROM video")
            video_result = cursor.fetchone()
            print(f"📊 视频表记录数量: {video_result[0]}")

            # 检查UP主数量
            cursor.execute("SELECT COUNT(*) AS up_count FROM up")
            up_result = cursor.fetchone()
            print(f"👤 UP主表记录数量: {up_result[0]}")

            # 检查视频统计数据
            cursor.execute("SELECT COUNT(*) AS stat_count FROM video_stat")
            stat_result = cursor.fetchone()
            print(f"📈 视频统计记录数量: {stat_result[0]}")

            # (可选) 列出最近几个视频
            print("\n最近添加的视频:")
            cursor.execute("SELECT bv_id, title FROM video ORDER BY id DESC LIMIT 3")
            recent_videos = cursor.fetchall()
            for video in recent_videos:
                print(f" - BV号: {video[0]}, 标题: {video[1]}")

    except Exception as e:
        print(f"❌ 检查数据库时出错: {e}")
    finally:
        if connection:
            connection.close()

if __name__ == "__main__":
    check_database_status()