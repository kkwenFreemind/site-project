# 🏛️ Legal Regulation Q&A System - RAG Knowledge Base Project

<div align="center">

## ⭐ Support the Project

✨ 如果覺得這個專案對你有幫助，請幫我點個 Star ⭐ 支持一下！  
這不只是鼓勵我，還能讓更多人看到這個專案，一起交流 RAG 的應用 🙌

✨ If this project helps you, please give us a Star ⭐ to support!  
This not only encourages me, but also helps more people discover this project and exchange RAG applications together 🙌

**🌍 Multi-language | 多語言版本**

[![中文](https://img.shields.io/badge/語言-中文-red.svg)](README.md)
[![English](https://img.shields.io/badge/Language-English-blue.svg)](README_EN.md)

</div>

---

## 🖼️ User Interface Screenshot

![RAG Knowledge Base Project UI](RAG%20%E7%9F%A5%E8%AD%98%E5%BA%AB%E5%B0%88%E6%A1%88.png)

---

[![Python](https://img.shields.io/badge/Python-3.8+-blue.svg)](https://python.org)
[![Flask](https://img.shields.io/badge/Flask-2.0+-green.svg)](https://flask.palletsprojects.com)
[![FAISS](https://img.shields.io/badge/FAISS-Vector_Search-orange.svg)](https://github.com/facebookresearch/faiss)
[![SentenceTransformers](https://img.shields.io/badge/SentenceTransformers-Embeddings-red.svg)](https://www.sbert.net)

> An intelligent legal regulation Q&A system based on RAG (Retrieval-Augmented Generation) architecture, integrating vector database retrieval technology with large language model integration support, providing precise legal regulation search and intelligent Q&A services.

## 🎯 Project Objectives

Building an enterprise-level intelligent knowledge base assistant that transforms complex legal regulations into searchable vector databases, with reserved large language model integration interfaces, supporting **document source traceability** functionality.

## 📊 Core Achievements

- ⚡ **Fast Response**: Vector retrieval average response time < 1 second
- 🎯 **Precise Retrieval**: Intelligent legal article matching based on semantic similarity
- 📚 **Complete Coverage**: Covers 21 regulations with 1974 legal articles
- 🔍 **Source Traceability**: Every answer provides complete legal article source citations

## 🛠️ Technical Architecture

### RAG System Architecture
```
📝 Raw Documents (HTML Regulations)
    ↓ Parsing & Cleaning
📊 Structured Data (JSON)
    ↓ Text Segmentation & Vectorization
🧠 Vector Database (FAISS)
    ↓ Semantic Retrieval
🔍 Relevant Document Fragments
    ↓ Prompt Engineering
🤖 Large Language Model Response
    ↓ Post-processing & Validation
✅ Intelligent Answer + Source Traceability
```

### Technology Stack
- **Vector Database**: FAISS (Facebook AI Similarity Search)
- **Text Vectorization**: SentenceTransformers (Multilingual Model)
- **Web Framework**: Flask + CORS
- **Frontend Technology**: HTML5 + JavaScript + CSS3
- **Data Processing**: BeautifulSoup, JSON, Pandas
- **Q&A System**: Template-based responses with retrieval + LLM integration interface (supports OpenAI/local models)

## 🚀 Core Features

### 1. Intelligent Vector Retrieval
- 🔍 **Semantic Search**: Supports natural language queries, understanding semantics rather than keyword matching
- 📊 **Similarity Calculation**: Precise matching based on cosine similarity
- ⚡ **High-speed Retrieval**: FAISS indexing supports millisecond-level retrieval

### 2. RAG Q&A System
- 🤖 **Intelligent Answers**: Generates structured answers combining retrieval results (supports LLM integration)
- 📖 **Source Traceability**: Every answer annotates specific legal articles cited
- 🎯 **Context Understanding**: Provides relevant legal information based on semantic retrieval

### 3. Knowledge Base Management
- 📚 **Automated Parsing**: HTML legal documents automatically parsed into structured data
- 🔄 **Incremental Updates**: Supports dynamic addition and updates of new regulations
- 📊 **Quality Monitoring**: Complete data quality checking mechanisms

## 🔧 Quick Start

### Environment Setup
```bash
# Clone the project
git clone <your-repo-url>
cd rag-system

# Install dependencies
pip install -r requirements.txt
# Or manual installation
pip install flask flask-cors sentence-transformers faiss-cpu beautifulsoup4 numpy pandas
```

### Build Knowledge Base
```bash
# 1. Parse legal documents
python law_parser.py

# 2. Build vector database
python law_vectorizer.py

# 3. Verify data quality
python data_quality_check.py
```

### Start Services
```bash
# Start RAG API service
python rag_api.py
# 🔗 API Address: http://127.0.0.1:5001

# Start frontend service (optional)
python frontend_server.py
# 🌐 Frontend Address: http://127.0.0.1:3000
```

## 📋 API Usage Examples

### Vector Retrieval API
```bash
curl -X POST http://127.0.0.1:5001/api/rag/search \
  -H "Content-Type: application/json" \
  -d '{
    "query": "Police baton usage regulations",
    "top_k": 5
  }'
```

**Response Example**:
```json
{
  "success": true,
  "query": "Police baton usage regulations",
  "results": [
    {
      "law_name": "Police Equipment Usage Regulations",
      "article_number": "Article 4",
      "content": "Police officers shall use batons only when necessary...",
      "similarity_score": 0.892,
      "rank": 1
    }
  ],
  "total": 5,
  "method": "vector_search"
}
```

### Intelligent Q&A API
```bash
curl -X POST http://127.0.0.1:5001/api/rag/ask \
  -H "Content-Type: application/json" \
  -d '{
    "query": "Under what circumstances can police use batons?",
    "use_ai": true
  }'
```

**Response Example**:
```json
{
  "success": true,
  "data": {
    "answer": "According to Police Equipment Usage Regulations, police may use batons in the following situations: 1. When stopping criminal behavior 2. When self-defense is necessary 3. When assisting in suspect arrest...",
    "sources": [
      {
        "law_name": "Police Equipment Usage Regulations",
        "article_number": "Article 4",
        "content": "Police officers shall use batons only when necessary...",
        "relevance": 0.892
      }
    ],
    "method": "template_based",
    "response_time": "1.2s"
  }
}
```

## 📊 System Performance Metrics

| Metric | Value | Description |
|--------|-------|-------------|
| Vector Retrieval Time | < 1 second | Single query response time |
| Vector Dimensions | 384 | Multilingual model dimensions |
| Database Size | 1974 articles | 21 complete regulations |
| Regulation Types | 8 categories | Criminal law, civil law, police regulations, etc. |

## 🎯 RAG System Optimization

### Vector Retrieval Optimization
- **Text Preprocessing**: Remove invalid characters, standardize format
- **Chunking Strategy**: Natural segmentation by articles, maintaining semantic integrity
- **Index Optimization**: FAISS IVF indexing improves retrieval speed

### Prompt Engineering
```python
# Example prompt template
PROMPT_TEMPLATE = """
Based on the following legal articles, answer user questions. Please ensure:
1. Answers are accurate and based on provided articles
2. Clearly cite relevant legal articles
3. Use plain language

Legal Articles:
{context}

User Question: {question}

Answer:
"""
```

## 📁 Project Structure

```
rag-system/
├── 🔧 Core Modules
│   ├── rag_api.py              # Main RAG API service
│   ├── law_vectorizer.py       # Vectorization engine
│   └── law_parser.py          # Document parser
├── 🌐 User Interface
│   ├── frontend_server.py      # Frontend server
│   └── frontend/index.html     # Web interface
├── 🔍 Quality Assurance
│   ├── data_quality_check.py   # Data quality checking
│   ├── test_rag_api.py        # API automated testing
│   └── model_comparison.py     # Model performance comparison
├── 📊 Data Directories
│   ├── law-source/            # Original HTML documents
│   ├── law-target/            # Parsed JSON data
│   └── vector_db/             # Vector database
└── 📋 Configuration
    ├── requirements.txt        # Dependencies list
    └── README.md              # Project documentation
```

## 🧪 Testing & Validation

### Automated Testing
```bash
# API functionality testing
python test_rag_api.py

# Data quality checking
python data_quality_check.py

# Model performance comparison
python model_comparison.py
```

### Test Cases
- ✅ **Basic Retrieval**: Keyword matching tests
- ✅ **Semantic Understanding**: Synonym, near-synonym tests
- ✅ **Multi-article Integration**: Cross-article question answering
- ✅ **Edge Cases**: Ambiguous question handling
- ✅ **Performance Testing**: High concurrency request testing

## 🚀 Deployment & Extension

### Docker Deployment
```dockerfile
FROM python:3.9-slim

WORKDIR /app
COPY requirements.txt .
RUN pip install -r requirements.txt

COPY . .
EXPOSE 5001

CMD ["python", "rag_api.py"]
```

### Extension Suggestions
- 🔧 **Multi-model Support**: Integrate different vector models
- 🌐 **Microservice Architecture**: Separate retrieval and generation services
- 📊 **Monitoring System**: Add Prometheus + Grafana
- 🔄 **Continuous Learning**: User feedback automatic optimization

## 🤖 LLM Integration TODO

### 📋 Quick Integration Guide
The system has reserved complete LLM integration interfaces, only need to implement the `_generate_ai_answer` function:

#### 1. OpenAI GPT Integration
```bash
# Install dependencies
pip install openai

# Set environment variables
export OPENAI_API_KEY="your-api-key-here"
```

```python
# Modify _generate_ai_answer function in rag_api.py
import openai

def _generate_ai_answer(self, query: str, relevant_laws: List[Dict[str, Any]]) -> str:
    """Generate answers using OpenAI GPT"""
    try:
        # Build context
        context = "\n\n".join([
            f"【{law['law_name']} {law['article_number']}】\n{law['content']}"
            for law in relevant_laws[:3]
        ])
        
        # Call OpenAI API
        response = openai.ChatCompletion.create(
            model="gpt-4",
            messages=[
                {"role": "system", "content": "You are a professional legal assistant. Please answer questions based on provided legal articles."},
                {"role": "user", "content": f"Legal Articles:\n{context}\n\nQuestion: {query}"}
            ],
            max_tokens=500,
            temperature=0.7
        )
        
        return response.choices[0].message.content
    except Exception as e:
        logger.error(f"OpenAI API call failed: {str(e)}")
        return self._generate_template_answer(query, relevant_laws)
```

#### 2. Local LLM Integration (Hugging Face)
```bash
# Install dependencies
pip install transformers torch
```

```python
# Local model integration example
from transformers import AutoTokenizer, AutoModelForCausalLM

class LocalLLM:
    def __init__(self):
        self.tokenizer = AutoTokenizer.from_pretrained("microsoft/DialoGPT-medium")
        self.model = AutoModelForCausalLM.from_pretrained("microsoft/DialoGPT-medium")
    
    def generate_answer(self, prompt: str) -> str:
        inputs = self.tokenizer.encode(prompt, return_tensors='pt')
        outputs = self.model.generate(inputs, max_length=500, pad_token_id=self.tokenizer.eos_token_id)
        return self.tokenizer.decode(outputs[0], skip_special_tokens=True)
```

#### 3. Other LLM Options
- **Anthropic Claude**: `pip install anthropic`
- **Google PaLM**: `pip install google-generativeai`
- **Local Ollama**: `pip install ollama`
- **Azure OpenAI**: Modify OpenAI endpoint settings

### 🔧 Implementation Steps
1. **Choose LLM Provider**: OpenAI, Anthropic, local models, etc.
2. **Install Corresponding Packages**: `pip install openai` or others
3. **Set API Keys**: Environment variables or config files
4. **Modify `_generate_ai_answer`**: Implement actual LLM calls
5. **Test Integration**: `python test_rag_api.py`
6. **Adjust Prompts**: Optimize answer quality

### 🎯 Prompt Optimization Suggestions
```python
SYSTEM_PROMPT = """
You are a professional Taiwan legal assistant. Please answer questions based on provided legal articles, requirements:
1. Answers must be based on provided articles, do not fabricate content
2. Clearly cite relevant legal article names and numbers
3. Use plain language to explain legal provisions
4. If articles are insufficient to answer questions, clearly state so
5. Add disclaimer at the end of answers
"""
```

### 📊 Performance Considerations
- **Response Time**: LLM calls add 2-5 seconds delay
- **Cost Control**: Set max_tokens limits
- **Error Handling**: Fallback to template answers when API fails
- **Caching Mechanism**: Consider implementing answer caching

## 📈 Project Highlights

### Technical Innovation
- 🏗️ **Modular Design**: High cohesion, low coupling system architecture
- ⚡ **High-speed Retrieval**: FAISS vector retrieval with millisecond response
- 🎯 **Intelligent Q&A**: RAG architecture combined with large language models
- 📊 **Quality Assurance**: Complete testing and monitoring system

### Business Value
- 💰 **Efficiency Improvement**: Automated legal retrieval reduces manual query time
- 🎯 **Precise Matching**: Semantic understanding technology provides relevant legal article retrieval
- 📚 **Knowledge Integration**: Structured legal knowledge system establishment
- 🔍 **Complete Traceability**: Provides complete legal article source traceability mechanisms

## 📄 License

本專案採用 CC-BY-NC 授權，歡迎學習與非商業使用。

This project is licensed under CC-BY-NC, welcome for learning and non-commercial use.

[![License: CC BY-NC 4.0](https://img.shields.io/badge/License-CC%20BY--NC%204.0-lightgrey.svg)](https://creativecommons.org/licenses/by-nc/4.0/)

## ⭐ Support the Project

✨ 如果覺得這個專案對你有幫助，請幫我點個 Star ⭐ 支持一下！
這不只是鼓勵我，還能讓更多人看到這個專案，一起交流 RAG 的應用 🙌

✨ If this project helps you, please give us a Star ⭐ to support!
This not only encourages me, but also helps more people discover this project and exchange RAG applications together 🙌



---

**⭐ If this project helps you, please give it a Star!**

---
*Last Updated: August 2025*
