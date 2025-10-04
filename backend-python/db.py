import mysql.connector
from config import DB_CONFIG

def init_db():
    conn = mysql.connector.connect(**DB_CONFIG)
    cursor = conn.cursor()
    cursor.execute("""
    CREATE TABLE IF NOT EXISTS logs (
        id INT AUTO_INCREMENT PRIMARY KEY,
        timestamp DATETIME,
        cpu_usage FLOAT,
        memory_usage FLOAT
    )
    """)
    conn.commit()
    conn.close()

if __name__ == '__main__':
    try:
        init_db()
        print("✅ Database initialized successfully. 'logs' table is ready.")
    except Exception as e:
        print(f"❌ Error initializing database: {e}")
