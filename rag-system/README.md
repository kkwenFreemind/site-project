# 🏛️ 法規問答系統 - RAG 知識庫專案

<div align="center">

## ⭐ 支持專案 | Support the Project

✨ 如果覺得這個專案對你有幫助，請幫我點個 Star ⭐ 支持一下！  
這不只是鼓勵我，還能讓更多人看到這個專案，一起交流 RAG 的應用 🙌

✨ If this project helps you, please give us a Star ⭐ to support!
This not only encourages me, but also helps more people discover this project and exchange RAG applications together 🙌

**🌍 多語言版本 | Multi-language**

[![中文](https://img.shields.io/badge/語言-中文-red.svg)](README.md)
[![English](https://img.shields.io/badge/Language-English-blue.svg)](README_EN.md)

</div>

---

## 🖼️ 使用者介面截圖 | User Interface Screenshot

![RAG 知識庫專案使用者介面](RAG%20%E7%9F%A5%E8%AD%98%E5%BA%AB%E5%B0%88%E6%A1%88.png)

---

> 本系統基於 RAG (檢索增強生成) 架構，結合向量資料庫檢索技術與大型語言模型整合，提供精確法規檢索與智慧問答服務。

> This system is based on RAG (Retrieval-Augmented Generation) architecture, integrating vector database retrieval technology and large language model support, providing precise legal regulation search and intelligent Q&A services.

## 🎯 專案目標 | Project Objectives

- 建立企業級智慧知識庫助手，將複雜法規轉為可檢索向量資料庫，並保留大型語言模型整合介面，支援「法條來源可追溯」功能。
- Build an enterprise-level intelligent knowledge base assistant that transforms complex legal regulations into searchable vector databases, with reserved LLM integration interfaces and document source traceability.

## 📊 核心成果 | Core Achievements

- ⚡ 向量檢索平均回應 < 1 秒
- 🎯 智慧法條語意比對
- 📚 覆蓋 21 部法規，1974 條法條
- 🔍 每個答案均提供完整法條來源

- ⚡ Vector retrieval average response < 1 second
- 🎯 Intelligent legal article semantic matching
- 📚 Covers 21 regulations, 1974 articles
- 🔍 Every answer provides complete legal article source citations

## 🛠️ 技術架構 | Technical Architecture

- 向量資料庫：FAISS
- 文本向量化：SentenceTransformers
- 網頁框架：Flask + CORS
- 前端技術：HTML5 + JavaScript + CSS3
- 資料處理：BeautifulSoup, JSON, Pandas
- 問答系統：檢索+模板+LLM介面

- Vector DB: FAISS
- Text Vectorization: SentenceTransformers
- Web Framework: Flask + CORS
- Frontend: HTML5 + JavaScript + CSS3
- Data Processing: BeautifulSoup, JSON, Pandas
- Q&A System: Retrieval + Template + LLM interface

## 🚀 快速開始 | Quick Start

### 環境建置 | Environment Setup
```bash
# 下載專案 | Clone the project
git clone <your-repo-url>
cd rag-system

# 安裝依賴 | Install dependencies
pip install -r requirements.txt
```

### 建立知識庫 | Build Knowledge Base
```bash
python law_parser.py      # 解析法規原始資料
python law_vectorizer.py  # 建立向量資料庫
python data_quality_check.py # 資料品質檢查
```

### 啟動服務 | Start Services
```bash
python rag_api.py         # 啟動 RAG API 服務
python frontend_server.py # 啟動前端服務 (選用)
```

## 📋 API 範例 | API Usage Examples

### 向量檢索 API | Vector Retrieval API
```bash
curl -X POST http://127.0.0.1:5001/api/rag/search \
  -H "Content-Type: application/json" \
  -d '{"query": "警棍使用規定", "top_k": 5}'
```

**回應範例**:
```json
{
  "success": true,
  "query": "警棍使用規定",
  "results": [
    {
      "law_name": "警械使用條例",
      "article_number": "第4條",
      "content": "警察人員應於必要時始得使用警棍...",
      "similarity_score": 0.892,
      "rank": 1
    }
  ],
  "total": 5,
  "method": "vector_search"
}
```

### 智慧問答 API | Intelligent Q&A API
```bash
curl -X POST http://127.0.0.1:5001/api/rag/ask \
  -H "Content-Type: application/json" \
  -d '{"query": "警察在什麼情況下可以使用警棍？", "use_ai": true}'
```

**回應範例**:
```json
{
  "success": true,
  "data": {
    "answer": "根據警械使用條例，警察得於下列情形使用警棍：1. 制止犯罪行為時 2. 自衛必要時 3. 協助逮捕嫌犯時...",
    "sources": [
      {
        "law_name": "警械使用條例",
        "article_number": "第4條",
        "content": "警察人員應於必要時始得使用警棍...",
        "relevance": 0.892
      }
    ],
    "method": "template_based",
    "response_time": "1.2s"
  }
}
```

## 📊 系統性能指標 | System Performance Metrics

| 指標 | 數值 | 說明 |
|------|------|------|
| 向量檢索時間 | < 1 秒 | 單次查詢回應時間 |
| 向量維度 | 384 | 多語言模型維度 |
| 資料庫大小 | 1974 條法條 | 21 部完整法規 |
| 法規類型 | 8 大類 | 刑法、民法、警察法規等 |

## 🧪 測試與驗證 | Testing & Validation

### 自動化測試
```bash
# API 功能測試
python test_rag_api.py

# 資料品質檢查
python data_quality_check.py

# 模型性能比較
python model_comparison.py
```

### 測試案例
- ✅ **基礎檢索**：關鍵字匹配測試
- ✅ **語意理解**：同義詞、近義詞測試
- ✅ **多條文整合**：跨條文問答
- ✅ **邊界案例**：模糊問題處理
- ✅ **性能測試**：高併發請求測試

## 🚀 部署與擴展 | Deployment & Extension

### Docker 部署
```dockerfile
FROM python:3.9-slim

WORKDIR /app
COPY requirements.txt .
RUN pip install -r requirements.txt

COPY . .
EXPOSE 5001

CMD ["python", "rag_api.py"]
```

### 擴展建議
- 🔧 **多模型支援**：整合不同向量模型
- 🌐 **微服務架構**：檢索與生成服務分離
- 📊 **監控系統**：加入 Prometheus + Grafana
- 🔄 **持續學習**：使用者回饋自動優化

## 🤖 LLM 整合說明 | LLM Integration

### 📋 快速整合指南
系統已預留完整 LLM 整合介面，只需實作 `_generate_ai_answer` 函數：

#### 1. OpenAI GPT 整合
```bash
# 安裝依賴
pip install openai

# 設定環境變數
export OPENAI_API_KEY="your-api-key-here"
```

```python
# 修改 rag_api.py 中的 _generate_ai_answer 函數
import openai

def _generate_ai_answer(self, query: str, relevant_laws: List[Dict[str, Any]]) -> str:
    """使用 OpenAI GPT 生成答案"""
    try:
        # 建立上下文
        context = "\n\n".join([
            f"【{law['law_name']} {law['article_number']}】\n{law['content']}"
            for law in relevant_laws[:3]
        ])
        
        # 呼叫 OpenAI API
        response = openai.ChatCompletion.create(
            model="gpt-4",
            messages=[
                {"role": "system", "content": "你是專業的法律助手，請根據提供的法條回答問題。"},
                {"role": "user", "content": f"法條內容：\n{context}\n\n問題：{query}"}
            ],
            max_tokens=500,
            temperature=0.7
        )
        
        return response.choices[0].message.content
    except Exception as e:
        logger.error(f"OpenAI API 呼叫失敗：{str(e)}")
        return self._generate_template_answer(query, relevant_laws)
```

#### 2. 其他 LLM 選項
- **Anthropic Claude**：`pip install anthropic`
- **Google PaLM**：`pip install google-generativeai`
- **本地 Ollama**：`pip install ollama`
- **Azure OpenAI**：修改 OpenAI 端點設定

### 📊 性能考量
- **回應時間**：LLM 呼叫增加 2-5 秒延遲
- **成本控制**：設定 max_tokens 限制
- **錯誤處理**：API 失敗時回退到模板答案
- **快取機制**：考慮實作答案快取

## 📈 專案亮點 | Project Highlights

### 技術創新
- 🏗️ **模組化設計**：高內聚、低耦合的系統架構
- ⚡ **高速檢索**：FAISS 向量檢索毫秒級回應
- 🎯 **智慧問答**：RAG 架構結合大型語言模型
- 📊 **品質保證**：完整的測試與監控體系

### 商業價值
- 💰 **效率提升**：自動化法規檢索降低人工查詢時間
- 🎯 **精準匹配**：語意理解技術提供相關法條檢索
- 📚 **知識整合**：結構化法規知識體系建立
- 🔍 **完整追溯**：提供完整法條來源追溯機制

## 🤝 貢獻指南 | Contributing

歡迎提交 Issues 和 Pull Requests！

### 開發流程
1. Fork 專案
2. 建立功能分支
3. 提交變更
4. 建立 Pull Request

## 📄 授權條款 | License

本專案採用 CC-BY-NC 授權，歡迎學習與非商業使用。

This project is licensed under CC-BY-NC, welcome for learning and non-commercial use.

[![License: CC BY-NC 4.0](https://img.shields.io/badge/License-CC%20BY--NC%204.0-lightgrey.svg)](https://creativecommons.org/licenses/by-nc/4.0/)



---

如需詳細說明，請參閱本目錄下的 README_EN.md (English) 或 README.md (中文)。
For more details, see README_EN.md (English) or README.md (中文) in this directory.

---
*最後更新 Last Updated: August 2025*
