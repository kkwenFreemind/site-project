# 🤖 NLP Query System

一個基於 Spring Boot + Google Gemini AI 的自然語言轉 SQL 查詢系統，可以將中文自然語言轉換為 PostgreSQL 查詢語句並安全執行。

## ✨ 功能特點

- 🔄 **自然語言轉SQL**：使用 Google Gemini API 將中文查詢轉換為 PostgreSQL 語句
- 🔒 **安全性保障**：SQL 語法驗證、危險關鍵字檢查、權限過濾
- 📊 **查詢記錄**：完整的查詢日誌和用戶反饋機制
- 📈 **圖表生成**：根據查詢結果自動生成圖表數據
- 🌐 **Web 界面**：簡潔易用的 Web 前端界面

## 🏗️ 技術架構

- **後端**：Spring Boot 3.x + JPA + PostgreSQL
- **AI 服務**：Google Gemini 1.5 Flash API
- **安全性**：Spring Security + 基本認證
- **前端**：HTML + JavaScript（原生）
- **資料庫**：PostgreSQL（使用 mydb schema）

## 📋 系統要求

- Java 17+
- Maven 3.6+
- PostgreSQL 12+
- Google Gemini API Key

## 🚀 快速開始

### 1. 資料庫設置

您的 PostgreSQL 資料庫已配置：
- 主機：localhost:15432
- 資料庫：tsdb
- 用戶：postgres
- Schema：mydb

運行數據庫初始化腳本：
```sql
-- 執行 database/init_mydb.sql 中的腳本
-- 這將創建必要的表和範例數據
```

### 2. 配置文件

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
    api-key: AIzaSyDsAoBpxEHD6vKwx3adxdNZRPofaGNgwWw
    model: gemini-1.5-flash
```

### 3. 運行後端

```bash
cd backend
mvn spring-boot:run
```

後端將在 http://localhost:8080 啟動

### 4. 使用前端

直接在瀏覽器中打開 `frontend/index.html`，或使用 Live Server 擴展。

### 5. 測試系統

1. 在前端頁面中使用默認認證資訊：
   - 用戶名：admin
   - 密碼：admin123

2. 嘗試範例查詢：
   - "顯示所有活躍用戶"
   - "查詢最近10筆訂單"
   - "統計各類別產品數量"

## 📡 API 接口

### 主要端點

1. **執行 AI 查詢**
   ```
   POST /api/v1/ai/query
   Content-Type: application/json
   Authorization: Basic <base64(username:password)>
   
   {
     "query": "顯示所有活躍用戶",
     "needChart": true,
     "limit": 100
   }
   ```

2. **獲取資料庫結構**
   ```
   GET /api/v1/ai/schema
   Authorization: Basic <base64(username:password)>
   ```

3. **查詢歷史**
   ```
   GET /api/v1/ai/history
   Authorization: Basic <base64(username:password)>
   ```

4. **統計資訊**
   ```
   GET /api/v1/ai/statistics
   Authorization: Basic <base64(username:password)>
   ```

### Swagger 文檔

啟動後端後，可以訪問：
- Swagger UI：http://localhost:8080/swagger-ui.html
- API 文檔：http://localhost:8080/v3/api-docs

## 🗄️ 資料庫結構

系統使用 `mydb` schema，包含以下表：

- `users` - 用戶資料
- `products` - 產品資料  
- `orders` - 訂單資料
- `ai_query_log` - AI 查詢日誌

## 🔧 開發指南

### 添加新的查詢功能

1. 修改 `DataDictionaryService` 以包含新表的描述
2. 更新 `AiQueryService` 中的安全檢查規則
3. 在前端添加新的查詢範例

### 自定義權限過濾

在 `AiQueryService.addPermissionFilter()` 方法中添加：

```java
// 例如：按用戶過濾
if (sql.toUpperCase().contains("FROM USERS")) {
    filteredSql = sql + " AND username = '" + username + "'";
}
```

## 🔒 安全機制

1. **SQL 安全檢查**
   - 只允許 SELECT 查詢
   - 禁止 DDL/DML 語句
   - 強制添加 LIMIT 限制

2. **權限控制**
   - 基本認證（Basic Auth）
   - 查詢日誌記錄
   - 用戶權限過濾

3. **輸入驗證**
   - SQL 語法解析
   - 危險關鍵字檢查
   - 參數長度限制

## 📝 查詢範例

| 自然語言 | 用途 |
|---------|------|
| 顯示所有活躍用戶 | 查詢 users 表中 status='ACTIVE' 的記錄 |
| 查詢最近10筆訂單 | 按時間排序的訂單查詢 |
| 統計各類別產品數量 | 產品分類統計 |
| 顯示狀態為完成的訂單 | 條件過濾查詢 |
| 查詢價格大於100的產品 | 數值比較查詢 |

## 🔍 故障排除

### 常見問題

1. **連接資料庫失敗**
   - 檢查 PostgreSQL 是否在 15432 端口運行
   - 確認 tsdb 資料庫存在
   - 檢查 mydb schema 是否已創建

2. **Gemini API 錯誤**
   - 確認 API Key 是否有效
   - 檢查網路連接
   - 查看日誌中的詳細錯誤資訊

3. **前端無法連接後端**
   - 確認後端已啟動在 8080 端口
   - 檢查認證資訊是否正確
   - 查看瀏覽器 Console 中的錯誤

### 日誌查看

```bash
# 查看應用日誌
tail -f logs/application.log

# 查看 SQL 執行
# 應用會在 DEBUG 級別記錄生成的 SQL
```

## 🚀 部署

### Docker 部署（可選）

創建 `Dockerfile`：

```dockerfile
FROM openjdk:17-jdk-slim
COPY target/nlp-query-system-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

構建和運行：

```bash
mvn clean package
docker build -t nlp-query-system .
docker run -p 8080:8080 nlp-query-system
```

## 📄 授權

本項目僅供學習和開發使用。

## 🤝 貢獻

歡迎提交 Issue 和 Pull Request！

---

**注意**：請確保在生產環境中保護好您的 Gemini API Key，不要將其提交到版本控制系統中。
