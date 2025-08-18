#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
完整的 RAG API 服務
整合向量搜尋、法規檢索和 AI 回答功能
"""

from flask import Flask, request, jsonify
from flask_cors import CORS
import json
import logging
from typing import List, Dict, Any
from law_vectorizer import LawVectorizer

# 設置日誌
logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')
logger = logging.getLogger(__name__)

app = Flask(__name__)
CORS(app)

class RAGService:
    def __init__(self):
        """初始化 RAG 服務"""
        logger.info("初始化 RAG 服務...")
        
        # 載入向量化器
        self.vectorizer = LawVectorizer()
        
        # 嘗試載入現有的向量資料庫
        if not self.vectorizer.load_vector_database():
            logger.error("無法載入向量資料庫")
            raise Exception("向量資料庫載入失敗")
        
        logger.info("RAG 服務初始化完成")
    
    def search_laws(self, query: str, top_k: int = 5) -> List[Dict[str, Any]]:
        """搜尋相關法條"""
        try:
            results = self.vectorizer.search(query, top_k)
            
            # 格式化結果
            formatted_results = []
            for result in results:
                formatted_result = {
                    "law_name": result['law_title'],
                    "law_type": result['law_type'], 
                    "article_number": f"第{result['article_number']}條",
                    "content": result['article_content'],
                    "full_text": result['full_text'],
                    "similarity_score": result['similarity_score'],
                    "rank": result['rank']
                }
                formatted_results.append(formatted_result)
            
            return formatted_results
            
        except Exception as e:
            logger.error(f"搜尋法條時發生錯誤: {str(e)}")
            return []
    
    def generate_answer_with_context(self, query: str, use_ai: bool = False) -> Dict[str, Any]:
        """基於檢索到的法條生成回答"""
        try:
            # 檢索相關法條
            relevant_laws = self.search_laws(query, top_k=5)
            
            if not relevant_laws:
                return {
                    "answer": "抱歉，找不到相關的法規條文。請嘗試使用其他關鍵字進行搜尋。",
                    "sources": [],
                    "query": query,
                    "method": "no_results"
                }
            
            # 構建回答
            if use_ai:
                # TODO: 整合 OpenAI 或其他 LLM API
                answer = self._generate_ai_answer(query, relevant_laws)
                method = "ai_generated"
            else:
                answer = self._generate_template_answer(query, relevant_laws)
                method = "template_based"
            
            return {
                "answer": answer,
                "sources": relevant_laws,
                "query": query,
                "method": method,
                "total_sources": len(relevant_laws)
            }
            
        except Exception as e:
            logger.error(f"生成回答時發生錯誤: {str(e)}")
            return {
                "answer": "系統發生錯誤，請稍後再試。",
                "sources": [],
                "query": query,
                "method": "error"
            }
    
    def _generate_template_answer(self, query: str, relevant_laws: List[Dict[str, Any]]) -> str:
        """使用模板生成回答"""
        query_lower = query.lower()
        
        # 建構法條內容
        context = "根據相關法規條文：\n\n"
        for i, law in enumerate(relevant_laws, 1):
            context += f"{i}. **{law['law_name']} {law['article_number']}**\n"
            context += f"   {law['content']}\n\n"
        
        # 根據查詢類型生成不同的回答模板
        if any(keyword in query_lower for keyword in ['警棍', '警械', '警刀', '手銬', '電擊器']):
            answer = f"""**關於警械使用的法規規定**

{context}

**重點說明：**
1. **警械種類**：警棍、警刀、手銬、腳鐐、警繩、警網、警盾、警車、警犬、電擊器、瓦斯噴射器等
2. **使用時機**：執行職務遇危害需排除、現行犯抗拒逮捕、合理確信攜帶兇器、受強暴脅迫等情形
3. **使用原則**：基於急迫需要，不得逾越必要程度
4. **事後處理**：應詳實記錄並報告長官

以上資訊僅供參考，實際執法請依相關法規和作業程序辦理。"""

        elif any(keyword in query_lower for keyword in ['竊盜', '強盜', '偷竊', '搶奪']):
            answer = f"""**關於竊盜、強盜罪的法規規定**

{context}

**重點說明：**
1. **竊盜罪構成要件**：意圖為自己或第三人不法所有，竊取他人動產
2. **基本刑責**：五年以下有期徒刑、拘役或五十萬元以下罰金
3. **加重情形**：夜間侵入住宅、攜帶兇器、結夥三人以上等
4. **強盜罪**：意圖為自己或第三人不法所有，以強暴、脅迫等方法取得他人財物

請注意：以上資訊僅供參考，具體案件請諮詢專業法律人士。"""

        elif any(keyword in query_lower for keyword in ['毒品', '藥物', '吸毒', '販毒']):
            answer = f"""**關於毒品犯罪的法規規定**

{context}

**重點說明：**
1. **毒品分級**：第一級至第四級毒品，刑責依等級遞減
2. **製造、販賣**：刑責最重，可處死刑、無期徒刑或十年以上有期徒刑
3. **持有、使用**：依毒品等級不同，處不同程度刑責
4. **戒治處分**：初犯或情節輕微者可施以觀察勒戒或強制戒治

※ 毒品危害身心健康，如需協助請洽相關戒毒機構。"""

        elif any(keyword in query_lower for keyword in ['交通', '道路', '違規', '罰款', '駕駛']):
            answer = f"""**關於道路交通違規的法規規定**

{context}

**重點說明：**
1. **處罰依據**：道路交通管理處罰條例
2. **違規類型**：超速、闖紅燈、酒駕、違規停車、未戴安全帽等
3. **處罰方式**：罰鍰、記點、吊扣/吊銷駕照、禁止其駕駛等
4. **救濟途徑**：對處罰不服可依法申訴或行政救濟

請遵守交通規則，確保行車安全。"""

        else:
            answer = f"""**相關法規規定**

{context}

根據以上法條，針對您的查詢「{query}」，請參考相關法規條文的具體規定。

※ 以上資訊僅供參考，如需專業法律建議，請諮詢合格律師或相關機關。"""
        
        return answer
    
    def _generate_ai_answer(self, query: str, relevant_laws: List[Dict[str, Any]]) -> str:
        """使用 AI 生成回答（預留接口）"""
        # TODO: 整合 OpenAI API 或其他 LLM
        # 這裡暫時使用模板回答
        return self._generate_template_answer(query, relevant_laws)
    
    def get_statistics(self) -> Dict[str, Any]:
        """獲取系統統計資訊"""
        try:
            stats = self.vectorizer.get_statistics()
            return {
                "success": True,
                "stats": stats
            }
        except Exception as e:
            logger.error(f"獲取統計資訊時發生錯誤: {str(e)}")
            return {
                "success": False,
                "error": str(e)
            }

# 初始化 RAG 服務
try:
    rag_service = RAGService()
    service_ready = True
except Exception as e:
    logger.error(f"RAG 服務初始化失敗: {str(e)}")
    rag_service = None
    service_ready = False

@app.route('/api/rag/search', methods=['POST'])
def search_laws():
    """向量搜尋相關法條"""
    if not service_ready:
        return jsonify({"error": "RAG 服務未就緒"}), 503
    
    try:
        data = request.get_json()
        query = data.get('query', '')
        top_k = data.get('top_k', 5)
        
        if not query:
            return jsonify({"error": "查詢內容不能為空"}), 400
        
        results = rag_service.search_laws(query, top_k)
        
        return jsonify({
            "success": True,
            "query": query,
            "results": results,
            "total": len(results),
            "method": "vector_search"
        })
        
    except Exception as e:
        logger.error(f"搜尋請求處理失敗: {str(e)}")
        return jsonify({"error": str(e)}), 500

@app.route('/api/rag/ask', methods=['POST'])
def ask_question():
    """RAG 問答接口"""
    if not service_ready:
        return jsonify({"error": "RAG 服務未就緒"}), 503
    
    try:
        data = request.get_json()
        query = data.get('query', '')
        use_ai = data.get('use_ai', False)
        
        if not query:
            return jsonify({"error": "問題不能為空"}), 400
        
        result = rag_service.generate_answer_with_context(query, use_ai)
        
        return jsonify({
            "success": True,
            "data": result
        })
        
    except Exception as e:
        logger.error(f"問答請求處理失敗: {str(e)}")
        return jsonify({"error": str(e)}), 500

@app.route('/api/rag/stats', methods=['GET'])
def get_stats():
    """取得系統統計資訊"""
    if not service_ready:
        return jsonify({"error": "RAG 服務未就緒"}), 503
    
    try:
        return jsonify(rag_service.get_statistics())
    except Exception as e:
        logger.error(f"統計請求處理失敗: {str(e)}")
        return jsonify({"error": str(e)}), 500

@app.route('/health', methods=['GET'])
def health_check():
    """健康檢查"""
    return jsonify({
        "status": "healthy" if service_ready else "unhealthy",
        "service": "RAG API",
        "vector_db_ready": service_ready
    })

if __name__ == '__main__':
    if service_ready:
        print("🚀 RAG API 服務啟動成功！")
        print("📊 向量搜尋功能已就緒")
        print("🔍 支援法規智能檢索和問答")
        app.run(host='0.0.0.0', port=5001, debug=True)
    else:
        print("❌ RAG API 服務啟動失敗")
        print("請檢查向量資料庫是否正確建立")
