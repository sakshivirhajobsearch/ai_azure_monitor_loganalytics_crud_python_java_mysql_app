CREATE DATABASE IF NOT EXISTS ai_azure_monitor_loganalytics;

USE ai_azure_monitor_loganalytics;

CREATE TABLE IF NOT EXISTS logs (
    id INT AUTO_INCREMENT PRIMARY KEY,
    timestamp DATETIME,
    cpu_usage FLOAT,
    memory_usage FLOAT
);
