import mysql.connector
import pandas as pd
from sklearn.ensemble import IsolationForest
from config import DB_CONFIG

def detect_anomalies():
    conn = mysql.connector.connect(**DB_CONFIG)
    df = pd.read_sql("SELECT id, cpu_usage, memory_usage FROM logs", conn)
    conn.close()

    if df.empty:
        return []

    model = IsolationForest(contamination=0.1, random_state=42)
    df['anomaly'] = model.fit_predict(df[['cpu_usage', 'memory_usage']])
    anomalies = df[df['anomaly'] == -1][['id', 'cpu_usage', 'memory_usage']]
    return anomalies.to_dict(orient='records')

if __name__ == '__main__':
    anomalies = detect_anomalies()
    if not anomalies:
        print("⚠️ No anomalies detected or no data in the logs table.")
    else:
        print("✅ Detected Anomalies:")
        for anomaly in anomalies:
            print(anomaly)
