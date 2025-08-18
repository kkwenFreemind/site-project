#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
依賴檢查腳本
檢查所有必要的Python套件是否正確安裝
"""

import sys
import importlib

def check_package(package_name):
    """檢查套件是否安裝"""
    try:
        module = importlib.import_module(package_name)
        if hasattr(module, '__version__'):
            current_version = module.__version__
            print(f"✅ {package_name}: {current_version}")
        else:
            print(f"✅ {package_name}: 已安裝")
        return True
    except ImportError:
        print(f"❌ {package_name}: 未安裝")
        return False

def main():
    """檢查所有依賴"""
    print("🔍 檢查 RAG 系統依賴套件...")
    print("=" * 50)
    
    # 定義必要套件
    core_packages = [
        'flask',
        'flask_cors', 
        'sentence_transformers',
        'faiss',  # faiss-cpu
        'numpy',
        'bs4',    # beautifulsoup4
        'requests',
        'pandas',
    ]
    
    # 可選套件
    optional_packages = [
        'openai',
        'transformers', 
        'torch',
        'anthropic',
    ]
    
    all_good = True
    
    print("\n📦 核心依賴:")
    for package in core_packages:
        if not check_package(package):
            all_good = False
    
    print("\n🔧 可選依賴 (LLM 整合):")
    for package in optional_packages:
        check_package(package)
    
    print("\n" + "=" * 50)
    if all_good:
        print("🎉 所有核心依賴都已正確安裝！")
        print("💡 提示：如需 LLM 功能，請安裝可選依賴")
        
        # 額外測試
        print("\n🧪 功能測試:")
        try:
            import sentence_transformers
            model = sentence_transformers.SentenceTransformer('paraphrase-multilingual-MiniLM-L12-v2')
            print("✅ SentenceTransformer 模型載入成功")
        except Exception as e:
            print(f"⚠️  SentenceTransformer 測試失敗: {e}")
        
        try:
            import faiss
            index = faiss.IndexFlatIP(384)
            print("✅ FAISS 索引建立成功")
        except Exception as e:
            print(f"⚠️  FAISS 測試失敗: {e}")
            
    else:
        print("⚠️  有依賴未滿足，請執行：")
        print("   pip install -r requirements.txt")
        sys.exit(1)

if __name__ == "__main__":
    main()
