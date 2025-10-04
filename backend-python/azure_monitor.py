import mysql.connector
from config import DB_CONFIG

def get_all_logs():
    conn = mysql.connector.connect(**DB_CONFIG)
    cursor = conn.cursor(dictionary=True)
    cursor.execute("SELECT * FROM logs")
    results = cursor.fetchall()
    conn.close()
    return results

def insert_log(data):
    conn = mysql.connector.connect(**DB_CONFIG)
    cursor = conn.cursor()
    sql = "INSERT INTO logs (timestamp, cpu_usage, memory_usage) VALUES (%s, %s, %s)"
    values = (data['timestamp'], data['cpu_usage'], data['memory_usage'])
    cursor.execute(sql, values)
    conn.commit()
    conn.close()
    return True

def update_log(log_id, data):
    conn = mysql.connector.connect(**DB_CONFIG)
    cursor = conn.cursor()
    sql = "UPDATE logs SET cpu_usage=%s, memory_usage=%s WHERE id=%s"
    values = (data['cpu_usage'], data['memory_usage'], log_id)
    cursor.execute(sql, values)
    conn.commit()
    conn.close()
    return True

def delete_log(log_id):
    conn = mysql.connector.connect(**DB_CONFIG)
    cursor = conn.cursor()
    sql = "DELETE FROM logs WHERE id=%s"
    cursor.execute(sql, (log_id,))
    conn.commit()
    conn.close()
    return True

if __name__ == '__main__':
    print("✅ Fetching all logs from database...")
    logs = get_all_logs()
    if not logs:
        print("⚠️ No logs found in the database.")
    else:
        for log in logs:
            print(log)
