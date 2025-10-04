from flask import Flask, jsonify, request
from flask_cors import CORS
from azure_monitor import get_all_logs, insert_log, update_log, delete_log
from ai_anomaly import detect_anomalies
from db import init_db

app = Flask(__name__)
CORS(app)

init_db()

@app.route('/')
def index():
    return jsonify({
        'message': 'AI + Azure Monitor & Log Analytics Backend Running',
        'endpoints': ['/logs', '/logs/<id>', '/logs/anomalies']
    })

@app.route('/logs', methods=['GET'])
def get_logs():
    return jsonify(get_all_logs())

@app.route('/logs', methods=['POST'])
def post_log():
    data = request.json
    result = insert_log(data)
    return jsonify({'success': result})

@app.route('/logs/<int:log_id>', methods=['PUT'])
def put_log(log_id):
    data = request.json
    result = update_log(log_id, data)
    return jsonify({'success': result})

@app.route('/logs/<int:log_id>', methods=['DELETE'])
def remove_log(log_id):
    result = delete_log(log_id)
    return jsonify({'success': result})

@app.route('/logs/anomalies', methods=['GET'])
def anomalies():
    return jsonify(detect_anomalies())

if __name__ == '__main__':
    app.run(debug=True)