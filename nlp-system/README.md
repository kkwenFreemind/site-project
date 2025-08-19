# 🤖 智慧查詢系統 - NLP Query System

<div align="center">

## ⭐ 支持專案 | Support the Project

✨ 如果覺得這個專案對你有幫助，請幫我點個 Star ⭐ 支持一下！  
這不只是鼓勵我，還能讓更多人看到這個專案，一起交流 NLP 到 SQL 的應用 🙌

✨ If this project helps you, please give us a Star ⭐ to support!
This not only encourages me, but also helps more people discover this project and exchange NLP to SQL applications together 🙌

**🌍 多語言版本 | Multi-language**

[![中文](https://img.shields.io/badge/語言-中文-red.svg)](README.md)
[![English](https://img.shields.io/badge/Language-English-blue.svg)](README_EN.md)

</div>

---

## 🖼️ 使用者介面截圖 | User Interface Screenshot

![NLP Query System](NLP%20Query%20System.png)

---

> 本系統基於 Spring Boot + Google Gemini AI 的自然語言轉 SQL 查詢系統，可以將中文自然語言轉換為 PostgreSQL 查詢語句並安全執行。

> This system is based on Spring Boot + Google Gemini AI for natural language to SQL query conversion, transforming Chinese natural language into PostgreSQL queries and executing them safely.

## 🎯 專案目標 | Project Objectives

- 建立企業級智慧資料查詢助手，將自然語言轉換為安全的SQL查詢，並提供圖表化數據展示，支援「查詢記錄可追溯」功能。
- Build an enterprise-level intelligent data query assistant that transforms natural language into secure SQL queries, provides chart visualization, and supports query history traceability.

## 📊 核心成果 | Core Achievements

- ⚡ SQL 查詢生成平均回應 < 2 秒
- 🎯 智慧中文語意理解
- 📚 支援多表聯合查詢，完整資料庫操作
- 🔍 每個查詢均提供完整SQL語句與執行結果
- 📈 自動生成圖表數據格式

- ⚡ SQL query generation average response < 2 seconds
- 🎯 Intelligent Chinese semantic understanding
- 📚 Support multi-table joins and complete database operations
- 🔍 Every query provides complete SQL statements and execution results
- 📈 Automatic chart data format generation

## 🛠️ 技術架構 | Technical Architecture

- **後端框架**：Spring Boot 3.x + JPA + PostgreSQL
- **AI 服務**：Google Gemini 1.5 Flash API
- **安全性**：Spring Security + Basic Authentication
- **前端技術**：HTML5 + JavaScript + CSS3
- **資料庫**：PostgreSQL (mydb schema)
- **查詢系統**：自然語言處理 + SQL生成 + 安全驗證

- **Backend Framework**: Spring Boot 3.x + JPA + PostgreSQL
- **AI Service**: Google Gemini 1.5 Flash API
- **Security**: Spring Security + Basic Authentication
- **Frontend**: HTML5 + JavaScript + CSS3
- **Database**: PostgreSQL (mydb schema)
- **Query System**: NLP + SQL Generation + Security Validation

## 📋 系統要求 | System Requirements

- Java 17+
- Maven 3.6+
- PostgreSQL 12+
- Google Gemini API Key

## 🚀 快速開始 | Quick Start

### 環境建置 | Environment Setup
```bash
# 下載專案 | Clone the project
git clone <your-repo-url>
cd nlp-system

# 進入後端目錄 | Navigate to backend directory
cd backend
```

### 資料庫設置 | Database Setup

### 資料庫設置 | Database Setup

您的 PostgreSQL 資料庫已配置：
- 主機：localhost:15432
- 資料庫：tsdb  
- 用戶：postgres
- Schema：mydb

運行數據庫初始化腳本：
```sql
-- 執行 database/init_mydb.sql 中的腳本
-- 這將創建必要的表和範例數據
-- Execute database/init_mydb.sql script
-- This will create necessary tables and sample data
```

### 配置文件 | Configuration
`application.yml` 已配置您的資料庫和 Gemini API：

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:15432/tsdb
    username: postgres
    password: 
    hikari:
      schema: mydb

ai:
  gemini:
    api-key: your-gemini-api-key-here
    model: gemini-1.5-flash
```

### 啟動服務 | Start Services
```bash
# 運行後端 | Run backend
cd backend
mvn spring-boot:run
# 後端將在 http://localhost:8080 啟動
# Backend will start at http://localhost:8080

# 使用前端 | Use frontend  
# 直接在瀏覽器中打開 frontend/index.html，或使用 Live Server 擴展
# Open frontend/index.html in browser directly or use Live Server extension
```

### 測試系統 | System Testing

### 測試系統 | System Testing

1. 在前端頁面中使用默認認證資訊：
   - 用戶名：admin  
   - 密碼：admin123

2. 嘗試範例查詢：
   - "顯示所有活躍用戶" | "Show all active users"
   - "查詢最近10筆訂單" | "Query the latest 10 orders"  
   - "統計各類別產品數量" | "Count products by category"

## � API 範例 | API Usage Examples

### 自然語言查詢 API | Natural Language Query API
```bash
curl -X POST http://localhost:8080/api/v1/ai/query \
  -H "Content-Type: application/json" \
  -H "Authorization: Basic <base64(username:password)>" \
  -d '{
    "query": "顯示所有活躍用戶",
    "needChart": true,
    "limit": 100
  }'
```

**回應範例 | Response Example**:
```json
{
  "success": true,
  "data": {
    "query": "顯示所有活躍用戶",
    "sql": "SELECT * FROM users WHERE status = 'ACTIVE' LIMIT 100",
    "results": [
      {
        "id": 1,
        "username": "john_doe",
        "email": "john@example.com",
        "status": "ACTIVE"
      }
    ],
    "chartData": {
      "type": "table",
      "columns": ["id", "username", "email", "status"]
    },
    "executionTime": "0.8s"
  }
}
```

### 其他 API 端點 | Other API Endpoints

**獲取資料庫結構 | Get Database Schema**
```bash
curl -X GET http://localhost:8080/api/v1/ai/schema \
  -H "Authorization: Basic <base64(username:password)>"
```

**查詢歷史 | Query History**  
```bash
curl -X GET http://localhost:8080/api/v1/ai/history \
  -H "Authorization: Basic <base64(username:password)>"
```

**統計資訊 | Statistics**
```bash
curl -X GET http://localhost:8080/api/v1/ai/statistics \
  -H "Authorization: Basic <base64(username:password)>"
```

### Swagger 文檔 | API Documentation

啟動後端後，可以訪問：
- Swagger UI：http://localhost:8080/swagger-ui.html
- API 文檔：http://localhost:8080/v3/api-docs

## 📊 系統性能指標 | System Performance Metrics

| 指標 | 數值 | 說明 |
|------|------|------|
| SQL 生成時間 | < 2 秒 | 單次查詢回應時間 |
| 安全檢查層數 | 3 層 | SQL注入防護機制 |
| 支援表格數量 | 4 張 | users, products, orders, ai_query_log |
| 查詢類型 | 8 種 | SELECT, JOIN, COUNT, GROUP BY 等 |

| Metric | Value | Description |
|--------|-------|-------------|
| SQL Generation Time | < 2 seconds | Single query response time |
| Security Check Layers | 3 layers | SQL injection protection |
| Supported Tables | 4 tables | users, products, orders, ai_query_log |
| Query Types | 8 types | SELECT, JOIN, COUNT, GROUP BY etc |

## 🗄️ 資料庫結構 | Database Structure

系統使用 `mydb` schema，包含以下表：

- `users` - 用戶資料 | User data
- `products` - 產品資料 | Product data  
- `orders` - 訂單資料 | Order data
- `ai_query_log` - AI 查詢日誌 | AI query logs

## 🧪 測試與驗證 | Testing & Validation

### 自動化測試 | Automated Testing
```bash
# API 功能測試 | API Function Testing
mvn test

# 資料庫連接測試 | Database Connection Testing  
mvn test -Dtest=DatabaseConnectionTest

# AI 服務測試 | AI Service Testing
mvn test -Dtest=AiServiceTest
```

### 測試案例 | Test Cases
- ✅ **基礎查詢**：簡單 SELECT 語句測試 | Basic queries: Simple SELECT statement testing
- ✅ **複雜查詢**：JOIN、GROUP BY、聚合函數 | Complex queries: JOIN, GROUP BY, aggregate functions
- ✅ **安全檢查**：SQL 注入防護測試 | Security checks: SQL injection protection testing
- ✅ **錯誤處理**：異常情況處理 | Error handling: Exception handling
- ✅ **性能測試**：高併發請求測試 | Performance testing: High concurrency request testing

## 📝 查詢範例 | Query Examples

| 自然語言 | 用途 | Natural Language | Purpose |
|---------|------|------------------|---------|
| 顯示所有活躍用戶 | 查詢 users 表中 status='ACTIVE' 的記錄 | Show all active users | Query records with status='ACTIVE' in users table |
| 查詢最近10筆訂單 | 按時間排序的訂單查詢 | Query latest 10 orders | Time-sorted order query |
| 統計各類別產品數量 | 產品分類統計 | Count products by category | Product category statistics |
| 顯示狀態為完成的訂單 | 條件過濾查詢 | Show completed orders | Conditional filtering query |
| 查詢價格大於100的產品 | 數值比較查詢 | Query products with price > 100 | Numerical comparison query |

## � 開發指南 | Development Guide

### 添加新的查詢功能 | Adding New Query Features

1. 修改 `DataDictionaryService` 以包含新表的描述
   Modify `DataDictionaryService` to include new table descriptions
2. 更新 `AiQueryService` 中的安全檢查規則  
   Update security check rules in `AiQueryService`
3. 在前端添加新的查詢範例
   Add new query examples in frontend

### 自定義權限過濾 | Custom Permission Filtering

在 `AiQueryService.addPermissionFilter()` 方法中添加：

```java
// 例如：按用戶過濾 | Example: Filter by user
if (sql.toUpperCase().contains("FROM USERS")) {
    filteredSql = sql + " AND username = '" + username + "'";
}
```

## 🔒 安全機制 | Security Mechanisms

### SQL 安全檢查 | SQL Security Checks
- 只允許 SELECT 查詢 | Only allow SELECT queries
- 禁止 DDL/DML 語句 | Prohibit DDL/DML statements  
- 強制添加 LIMIT 限制 | Force LIMIT restrictions

### 權限控制 | Access Control
- 基本認證（Basic Auth） | Basic Authentication
- 查詢日誌記錄 | Query logging
- 用戶權限過濾 | User permission filtering

### 輸入驗證 | Input Validation
- SQL 語法解析 | SQL syntax parsing
- 危險關鍵字檢查 | Dangerous keyword checking
- 參數長度限制 | Parameter length limits

## �🔍 故障排除 | Troubleshooting

### 常見問題 | Common Issues

**連接資料庫失敗 | Database Connection Failed**
- 檢查 PostgreSQL 是否在 15432 端口運行
  Check if PostgreSQL is running on port 15432
- 確認 tsdb 資料庫存在  
  Confirm tsdb database exists
- 檢查 mydb schema 是否已創建
  Check if mydb schema is created

**Gemini API 錯誤 | Gemini API Error**  
- 確認 API Key 是否有效
  Confirm if API Key is valid
- 檢查網路連接
  Check network connection
- 查看日誌中的詳細錯誤資訊
  Check detailed error information in logs

**前端無法連接後端 | Frontend Cannot Connect to Backend**
- 確認後端已啟動在 8080 端口
  Confirm backend is running on port 8080
- 檢查認證資訊是否正確  
  Check if authentication credentials are correct
- 查看瀏覽器 Console 中的錯誤
  Check errors in browser Console

### 日誌查看 | Log Monitoring

```bash
# 查看應用日誌 | View application logs
tail -f logs/application.log

# 查看 SQL 執行 | View SQL execution
# 應用會在 DEBUG 級別記錄生成的 SQL
# Application logs generated SQL at DEBUG level
```

## 🚀 部署與擴展 | Deployment & Extension

### Docker 部署 | Docker Deployment

創建 `Dockerfile`：

```dockerfile
FROM openjdk:17-jdk-slim

WORKDIR /app
COPY target/nlp-query-system-1.0.0.jar app.jar
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app.jar"]
```

構建和運行 | Build and Run：

```bash
# 構建專案 | Build project
mvn clean package

# 構建 Docker 鏡像 | Build Docker image  
docker build -t nlp-query-system .

# 運行容器 | Run container
docker run -p 8080:8080 nlp-query-system
```

### 擴展建議 | Extension Recommendations
- 🔧 **多模型支援**：整合不同的 LLM 模型 | Multi-model support: Integrate different LLM models
- 🌐 **微服務架構**：查詢與生成服務分離 | Microservice architecture: Separate query and generation services
- 📊 **監控系統**：加入 Prometheus + Grafana | Monitoring system: Add Prometheus + Grafana
- 🔄 **持續學習**：使用者回饋自動優化 | Continuous learning: User feedback auto-optimization

## 📈 專案亮點 | Project Highlights

### 技術創新 | Technical Innovation
- 🏗️ **模組化設計**：高內聚、低耦合的系統架構 | Modular design: High cohesion, low coupling system architecture
- ⚡ **智慧轉換**：自然語言到SQL的智慧轉換 | Smart conversion: Intelligent NL to SQL transformation
- 🎯 **安全優先**：多層次的SQL安全防護 | Security-first: Multi-layer SQL security protection
- 📊 **數據可視**：自動圖表生成機制 | Data visualization: Automatic chart generation mechanism

### 商業價值 | Business Value
- 💰 **效率提升**：自動化SQL查詢降低技術門檻 | Efficiency improvement: Automated SQL queries lower technical barriers
- 🎯 **精準查詢**：智慧語意理解提供準確結果 | Precise queries: Intelligent semantic understanding provides accurate results
- 📚 **知識管理**：結構化數據查詢體系建立 | Knowledge management: Structured data query system establishment
- 🔍 **操作透明**：提供完整SQL語句追溯機制 | Operation transparency: Complete SQL statement traceability mechanism

## � 學習資源與參考文獻 | Learning Resources & References

### 🎓 核心技術知識點 | Core Technical Knowledge

#### 自然語言處理相關 | NLP Related
- **Text-to-SQL**：自然語言轉SQL的核心技術
- **大型語言模型**：GPT、Gemini、Claude 等的使用與比較
- **Prompt Engineering**：提示詞工程與優化技巧
- **語意理解**：中文自然語言處理技術

#### 後端開發技術 | Backend Development
- **Spring Boot**：現代化Java應用開發框架
- **Spring Security**：企業級安全框架
- **JPA/Hibernate**：Java持久化技術
- **PostgreSQL**：進階資料庫操作與優化

### 🌐 學習網站與資源 | Learning Websites & Resources

#### 官方文檔與教學 | Official Documentation
- **Spring Boot 官方文檔**：[https://spring.io/projects/spring-boot](https://spring.io/projects/spring-boot)
- **Google Gemini API**：[https://ai.google.dev/](https://ai.google.dev/)
- **PostgreSQL 官方文檔**：[https://www.postgresql.org/docs/](https://www.postgresql.org/docs/)

#### Text-to-SQL 專題 | Text-to-SQL Topics
- **Spider Dataset**：[https://yale-lily.github.io/spider](https://yale-lily.github.io/spider)
- **WikiSQL Dataset**：[https://github.com/salesforce/WikiSQL](https://github.com/salesforce/WikiSQL)

## �📄 授權條款 | License

本專案採用 CC-BY-NC 授權，歡迎學習與非商業使用。

This project is licensed under CC-BY-NC, welcome for learning and non-commercial use.

[![License: CC BY-NC 4.0](https://img.shields.io/badge/License-CC%20BY--NC%204.0-lightgrey.svg)](https://creativecommons.org/licenses/by-nc/4.0/)



---

**注意 | Note**：請確保在生產環境中保護好您的 Gemini API Key，不要將其提交到版本控制系統中。
Please ensure to protect your Gemini API Key in production environment and do not commit it to version control systems.

---

如需詳細說明，請參閱本目錄下的相關文件。
For more details, please refer to the related files in this directory.

---
*最後更新 Last Updated: August 2025*
