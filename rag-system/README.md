# 🏛️ 法規問答系統 - RAG 知識庫專案

<div align="center">

**🌍 多語言版本 | Multi-language**

[![中文](https://img.shields.io/badge/語言-中文-red.svg)](README.md)
[![English](https://img.shields.io/badge/Language-English-blue.svg)](README_EN.md)

</div>

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

### 智慧問答 API | Intelligent Q&A API
```bash
curl -X POST http://127.0.0.1:5001/api/rag/ask \
  -H "Content-Type: application/json" \
  -d '{"query": "警察在什麼情況下可以使用警棍？", "use_ai": true}'
```

## 📁 專案結構 | Project Structure

```
rag-system/
├── rag_api.py              # RAG API 主服務 | Main RAG API service
├── law_vectorizer.py       # 向量化引擎 | Vectorization engine
├── law_parser.py           # 法規解析 | Document parser
├── frontend_server.py      # 前端伺服器 | Frontend server
├── frontend/index.html     # 前端網頁 | Web interface
├── data_quality_check.py   # 資料品質檢查 | Data quality checking
├── test_rag_api.py         # API 測試 | API testing
├── law-source/             # 法規原始資料 | Raw documents
├── law-target/             # 解析後 JSON | Parsed JSON data
├── vector_db/              # 向量資料庫 | Vector database
└── requirements.txt        # 依賴列表 | Dependencies
```

---

如需詳細說明，請參閱本目錄下的 README_EN.md (English) 或 README.md (中文)。
For more details, see README_EN.md (English) or README.md (中文) in this directory.

---
*最後更新 Last Updated: August 2025*
