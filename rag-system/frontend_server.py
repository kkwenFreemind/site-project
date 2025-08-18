#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
簡單的前端服務器
提供靜態網頁服務
"""

from flask import Flask, send_from_directory, send_file
from flask_cors import CORS
import os

app = Flask(__name__)
CORS(app)

# 前端檔案目錄
FRONTEND_DIR = os.path.join(os.path.dirname(__file__), 'frontend')

@app.route('/')
def index():
    """主頁"""
    return send_file(os.path.join(FRONTEND_DIR, 'index.html'))

@app.route('/static/<path:filename>')
def static_files(filename):
    """靜態檔案"""
    return send_from_directory(FRONTEND_DIR, filename)

@app.route('/health')
def health():
    """健康檢查"""
    return {'status': 'healthy', 'service': 'Frontend Server'}

if __name__ == '__main__':
    print("🌐 前端服務器啟動中...")
    print(f"📁 靜態檔案目錄: {FRONTEND_DIR}")
    print("🔗 前端地址: http://127.0.0.1:3000")
    print("💡 請確保 RAG API (Port 5001) 正在運行")
    
    app.run(host='0.0.0.0', port=3000, debug=True)
