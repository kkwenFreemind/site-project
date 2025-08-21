# API 響應格式規範

## 統一響應格式

為了確保前後端數據交互的一致性，所有 API 響應都應該遵循統一的格式。

### 響應結構

```json
{
  "code": 200,           // 業務狀態碼：200-成功，其他-失敗
  "message": "操作成功",  // 響應信息
  "data": {},            // 響應數據，可以是任何類型
  "timestamp": 1693478400000  // 時間戳（毫秒）
}
```

### 狀態碼定義

- **200**: 成功
- **400**: 請求參數錯誤
- **401**: 未授權
- **403**: 無權限
- **404**: 資源不存在
- **500**: 服務器內部錯誤

### 成功響應示例

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "username": "admin",
    "email": "admin@example.com"
  },
  "timestamp": 1693478400000
}
```

### 錯誤響應示例

```json
{
  "code": 400,
  "message": "用戶名不能為空",
  "data": null,
  "timestamp": 1693478400000
}
```

### 分頁響應示例

```json
{
  "code": 200,
  "message": "查詢成功",
  "data": {
    "content": [...],      // 數據列表
    "totalElements": 100,  // 總記錄數
    "totalPages": 10,      // 總頁數
    "number": 0,          // 當前頁碼（從0開始）
    "size": 10,           // 每頁大小
    "numberOfElements": 10, // 當前頁記錄數
    "first": true,        // 是否第一頁
    "last": false         // 是否最後一頁
  },
  "timestamp": 1693478400000
}
```

### 登入響應示例

```json
{
  "code": 200,
  "message": "登入成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "refresh_token_here",
    "userInfo": {
      "id": 1,
      "username": "admin",
      "email": "admin@example.com",
      "roles": ["ADMIN"]
    }
  },
  "timestamp": 1693478400000
}
```

## 實現規範

### 後端實現

1. **統一使用 `com.alertsystem.dto.ApiResponse` 類**
2. **所有 Controller 方法都返回 `ApiResponse<T>` 類型**
3. **使用 `code` 字段表示業務狀態碼**
4. **使用 `timestamp` 字段表示時間戳（毫秒）**

### 前端處理

1. **響應攔截器統一處理 `ApiResponse` 格式**
2. **檢查 `code` 字段判斷請求是否成功**
3. **`code === 200` 時返回 `data` 字段的內容**
4. **`code !== 200` 時拋出錯誤並顯示 `message`**

### 遷移步驟

1. 統一所有 Controller 使用相同的 ApiResponse 類
2. 修改前端響應攔截器適配統一格式
3. 測試所有 API 端點確保格式正確
4. 更新 API 文檔和示例
