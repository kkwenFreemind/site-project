#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
RAG API 測試腳本
測試向量搜尋和智能問答功能
"""

import requests
import json
import time

def test_rag_api():
    """測試 RAG API 功能"""
    base_url = "http://127.0.0.1:5001"
    
    print("🧪 RAG API 功能測試")
    print("=" * 50)
    
    # 測試健康檢查
    print("\n1. 健康檢查測試")
    try:
        response = requests.get(f"{base_url}/health")
        print(f"   狀態碼: {response.status_code}")
        print(f"   回應: {response.json()}")
    except Exception as e:
        print(f"   ❌ 錯誤: {e}")
        return
    
    # 測試向量搜尋
    print("\n2. 向量搜尋測試")
    search_queries = [
        "警棍的使用規定",
        "竊盜罪的刑責",
        "毒品持有的處罰",
        "交通違規罰款",
        "警察執法權限"
    ]
    
    for query in search_queries:
        print(f"\n   查詢: {query}")
        try:
            response = requests.post(f"{base_url}/api/rag/search", 
                                   json={"query": query, "top_k": 3})
            if response.status_code == 200:
                data = response.json()
                results = data.get('results', [])
                print(f"   ✅ 找到 {len(results)} 個相關法條")
                for i, result in enumerate(results, 1):
                    print(f"     {i}. {result['law_name']} {result['article_number']}")
                    print(f"        相似度: {result['similarity_score']:.3f}")
                    print(f"        內容: {result['content'][:80]}...")
            else:
                print(f"   ❌ 錯誤: {response.status_code}")
        except Exception as e:
            print(f"   ❌ 錯誤: {e}")
        
        time.sleep(0.5)  # 避免請求過快
    
    # 測試智能問答
    print("\n3. 智能問答測試")
    qa_queries = [
        "警察什麼情況下可以使用警棍？",
        "竊盜罪會被判幾年？",
        "酒駕會被罰多少錢？"
    ]
    
    for query in qa_queries:
        print(f"\n   問題: {query}")
        try:
            response = requests.post(f"{base_url}/api/rag/ask", 
                                   json={"query": query, "use_ai": False})
            if response.status_code == 200:
                data = response.json()
                result = data.get('data', {})
                answer = result.get('answer', '')
                sources = result.get('sources', [])
                
                print(f"   ✅ 回答生成成功")
                print(f"   📝 回答長度: {len(answer)} 字元")
                print(f"   📚 引用法條: {len(sources)} 條")
                print(f"   🔍 生成方式: {result.get('method', '未知')}")
                
                # 顯示回答的前200字元
                print(f"   💬 回答預覽: {answer[:200]}...")
                
                # 顯示引用的法條
                if sources:
                    print(f"   📖 引用法條:")
                    for i, source in enumerate(sources[:2], 1):
                        print(f"     {i}. {source['law_name']} {source['article_number']}")
            else:
                print(f"   ❌ 錯誤: {response.status_code}")
        except Exception as e:
            print(f"   ❌ 錯誤: {e}")
        
        time.sleep(0.5)
    
    # 測試統計資訊
    print("\n4. 系統統計測試")
    try:
        response = requests.get(f"{base_url}/api/rag/stats")
        if response.status_code == 200:
            data = response.json()
            stats = data.get('stats', {})
            print(f"   ✅ 統計資訊獲取成功")
            print(f"   📊 總片段數: {stats.get('total_chunks', 0)}")
            print(f"   🔢 向量維度: {stats.get('embedding_dimension', 0)}")
            print(f"   📚 法規類型數: {len(stats.get('law_types', {}))}")
        else:
            print(f"   ❌ 錯誤: {response.status_code}")
    except Exception as e:
        print(f"   ❌ 錯誤: {e}")
    
    print("\n" + "=" * 50)
    print("🎉 RAG API 測試完成！")

def compare_apis():
    """比較 Mock API 和 RAG API 的搜尋結果"""
    print("\n🔄 API 搜尋結果比較")
    print("=" * 50)
    
    mock_url = "http://127.0.0.1:5000"
    rag_url = "http://127.0.0.1:5001"
    
    test_query = "警棍使用規定"
    
    print(f"查詢: {test_query}")
    
    # Mock API 搜尋
    print(f"\n📝 Mock API 結果:")
    try:
        response = requests.post(f"{mock_url}/api/law/search", 
                               json={"query": test_query, "top_k": 3})
        if response.status_code == 200:
            data = response.json()
            results = data.get('results', [])
            print(f"   找到 {len(results)} 個結果")
            for i, result in enumerate(results, 1):
                print(f"   {i}. {result['law_name']} {result['article_number']} (分數: {result['similarity_score']:.3f})")
        else:
            print(f"   ❌ 錯誤: {response.status_code}")
    except Exception as e:
        print(f"   ❌ Mock API 無法連接: {e}")
    
    # RAG API 搜尋
    print(f"\n🔍 RAG API 結果:")
    try:
        response = requests.post(f"{rag_url}/api/rag/search", 
                               json={"query": test_query, "top_k": 3})
        if response.status_code == 200:
            data = response.json()
            results = data.get('results', [])
            print(f"   找到 {len(results)} 個結果")
            for i, result in enumerate(results, 1):
                print(f"   {i}. {result['law_name']} {result['article_number']} (相似度: {result['similarity_score']:.3f})")
        else:
            print(f"   ❌ 錯誤: {response.status_code}")
    except Exception as e:
        print(f"   ❌ RAG API 錯誤: {e}")

if __name__ == "__main__":
    test_rag_api()
    compare_apis()
