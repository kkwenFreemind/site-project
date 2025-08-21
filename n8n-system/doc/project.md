# 智能告警分發系統 - MVP開發計劃

## 項目概述

基於現有的n8n工作流引擎和EMQX消息代理，構建一個智能告警分發系統。采用分階段開發方式，從核心功能開始逐步擴展。

## 開發原則

- **最小可行產品(MVP)優先**：每個階段都要產出可驗證的功能
- **技術風險控制**：先解決核心技術難點
- **用戶價值導向**：每個里程碑都要有明確的用戶價值
- **漸進式增強**：後續階段建立在前一階段的穩固基礎上

---

## 階段一：用戶認證與基礎框架 (MVP-1)

### 目標
建立系統的基礎架構和用戶認證功能，為後續開發奠定基礎。

### 核心功能
1. **用戶認證系統**
   - 用戶註冊/登入/登出
   - JWT Token 管理
   - 基礎角色權限(Admin/User)

2. **基礎架構**
   - Spring Boot 3 項目架構
   - PostgreSQL 數據庫連接
   - Redis 緩存配置
   - 統一API響應格式

### 技術實現

#### 數據庫設計
```sql
-- 用戶表
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100),
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 角色表
CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 用戶角色關聯表
CREATE TABLE user_roles (
    user_id BIGINT REFERENCES users(id),
    role_id BIGINT REFERENCES roles(id),
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, role_id)
);

-- 登入記錄表
CREATE TABLE login_logs (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    login_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ip_address INET,
    user_agent TEXT,
    status VARCHAR(20) DEFAULT 'SUCCESS'
);
```

#### 核心API端點
- `POST /api/auth/register` - 用戶註冊
- `POST /api/auth/login` - 用戶登入
- `POST /api/auth/logout` - 用戶登出
- `GET /api/auth/profile` - 獲取用戶資料
- `PUT /api/auth/profile` - 更新用戶資料

### 驗收標準
- [x] 用戶可以成功註冊帳號
- [x] 用戶可以使用帳號密碼登入
- [x] JWT Token 正確生成和驗證
- [x] 受保護的API需要有效Token
- [x] 登入記錄正確儲存
- [x] 基礎角色權限控制生效
- [ ] Vue3前端登入/登出功能完成
- [ ] 用戶管理界面完成
- [ ] 登入記錄查詢界面完成

### 交付成果
- 可運行的Spring Boot應用
- 完整的數據庫Schema
- 基礎前端登入頁面
- API文檔
- 單元測試覆蓋率 > 80%

**預估工期：2-3週**

---

## 階段二：告警數據接收與存儲 (MVP-2)

### 目標
建立告警數據的接收、解析和存儲能力，為後續處理奠定基礎。

### 核心功能
1. **MQTT消息接收**
   - 連接EMQX代理
   - 訂閱告警主題
   - 消息格式驗證

2. **告警數據管理**
   - 告警數據存儲
   - 基礎查詢功能
   - 數據統計

### 技術實現

#### 擴展數據庫設計
```sql
-- 系統表
CREATE TABLE systems (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    mqtt_topic VARCHAR(255),
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 告警記錄表
CREATE TABLE alert_history (
    id BIGSERIAL PRIMARY KEY,
    system_id BIGINT REFERENCES systems(id),
    alert_type VARCHAR(50) NOT NULL,
    severity VARCHAR(20) NOT NULL,
    title VARCHAR(255) NOT NULL,
    message TEXT,
    source_data JSONB,
    received_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    resolved_at TIMESTAMP,
    status VARCHAR(20) DEFAULT 'NEW'
);

-- 告警統計表
CREATE TABLE alert_statistics (
    id BIGSERIAL PRIMARY KEY,
    date DATE,
    system_id BIGINT REFERENCES systems(id),
    alert_type VARCHAR(50),
    severity VARCHAR(20),
    count INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

#### 新增API端點
- `GET /api/alerts` - 查詢告警列表
- `GET /api/alerts/{id}` - 獲取告警詳情
- `PUT /api/alerts/{id}/resolve` - 標記告警為已解決
- `GET /api/systems` - 獲取系統列表
- `POST /api/systems` - 添加新系統
- `GET /api/dashboard/stats` - 獲取統計數據

### 驗收標準
- [ ] MQTT消息能正確接收和解析
- [ ] 告警數據正確存儲到數據庫
- [ ] 用戶可以查看告警列表
- [ ] 告警詳情頁面正確顯示
- [ ] 基礎統計功能正常
- [ ] 系統管理功能可用

### 交付成果
- MQTT消息處理服務
- 告警管理Web界面
- 系統管理界面
- 實時統計儀表板
- 消息處理性能測試報告

**預估工期：3-4週**

---

## 階段三：規則引擎與智能處理 (MVP-3)

### 目標
實現基礎的規則引擎，能夠根據預設規則對告警進行自動處理和分類。

### 核心功能
1. **規則引擎**
   - 規則定義和管理
   - 條件匹配
   - 動作執行

2. **智能處理**
   - 告警去重
   - 嚴重級別調整
   - 自動分組

### 技術實現

#### 擴展數據庫設計
```sql
-- 規則表
CREATE TABLE alert_rules (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    conditions JSONB NOT NULL,
    actions JSONB NOT NULL,
    priority INTEGER DEFAULT 0,
    enabled BOOLEAN DEFAULT true,
    created_by BIGINT REFERENCES users(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 規則執行記錄
CREATE TABLE rule_executions (
    id BIGSERIAL PRIMARY KEY,
    rule_id BIGINT REFERENCES alert_rules(id),
    alert_id BIGINT REFERENCES alert_history(id),
    executed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    success BOOLEAN,
    result JSONB,
    error_message TEXT
);

-- 告警處理記錄
CREATE TABLE alert_processing (
    id BIGSERIAL PRIMARY KEY,
    alert_id BIGINT REFERENCES alert_history(id),
    processing_type VARCHAR(50),
    original_severity VARCHAR(20),
    new_severity VARCHAR(20),
    action_taken VARCHAR(100),
    processed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    processed_by VARCHAR(50)
);
```

#### 新增API端點
- `GET /api/rules` - 獲取規則列表
- `POST /api/rules` - 創建新規則
- `PUT /api/rules/{id}` - 更新規則
- `DELETE /api/rules/{id}` - 刪除規則
- `POST /api/rules/{id}/test` - 測試規則
- `GET /api/alerts/processing-log` - 查看處理記錄

### 驗收標準
- [ ] 規則可以正確定義和保存
- [ ] 規則引擎能夠匹配告警
- [ ] 自動處理動作正確執行
- [ ] 告警去重功能有效
- [ ] 處理記錄完整追踪
- [ ] 規則測試功能可用

### 交付成果
- 規則管理界面
- 規則引擎核心邏輯
- 處理記錄查看界面
- 規則測試工具
- 性能基準測試報告

**預估工期：4-5週**

---

## 階段四：通知渠道與分發 (MVP-4)

### 目標
實現多渠道通知功能，包括郵件、短信、即時通訊等方式。

### 核心功能
1. **通知渠道管理**
   - 郵件通知
   - 企業微信/釘釘
   - Webhook通知

2. **分發策略**
   - 用戶組管理
   - 通知模板
   - 發送策略

### 技術實現

#### 擴展數據庫設計
```sql
-- 通知渠道表
CREATE TABLE notification_channels (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(50) NOT NULL,
    config JSONB NOT NULL,
    enabled BOOLEAN DEFAULT true,
    created_by BIGINT REFERENCES users(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 用戶組表
CREATE TABLE user_groups (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    created_by BIGINT REFERENCES users(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 用戶組成員表
CREATE TABLE user_group_members (
    group_id BIGINT REFERENCES user_groups(id),
    user_id BIGINT REFERENCES users(id),
    added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (group_id, user_id)
);

-- 通知記錄表
CREATE TABLE notification_history (
    id BIGSERIAL PRIMARY KEY,
    alert_id BIGINT REFERENCES alert_history(id),
    channel_id BIGINT REFERENCES notification_channels(id),
    recipient VARCHAR(255),
    title VARCHAR(255),
    content TEXT,
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20),
    error_message TEXT
);
```

#### 新增API端點
- `GET /api/channels` - 獲取通知渠道
- `POST /api/channels` - 創建通知渠道
- `POST /api/channels/{id}/test` - 測試通知渠道
- `GET /api/groups` - 獲取用戶組
- `POST /api/groups` - 創建用戶組
- `GET /api/notifications/history` - 查看通知歷史

### 驗收標準
- [ ] 郵件通知正確發送
- [ ] 企業微信/釘釘集成正常
- [ ] 用戶組管理功能完整
- [ ] 通知模板系統可用
- [ ] 發送狀態正確追踪
- [ ] 失敗重試機制有效

### 交付成果
- 通知渠道管理界面
- 用戶組管理界面
- 通知模板編輯器
- 通知歷史查看界面
- 渠道測試工具

**預估工期：3-4週**

---

## 階段五：可視化界面與報表 (MVP-5)

### 目標
完善用戶界面，提供豐富的數據可視化和報表功能。

### 核心功能
1. **儀表板**
   - 實時告警監控
   - 系統狀態概覽
   - 統計圖表

2. **報表系統**
   - 告警趨勢分析
   - 系統可靠性報告
   - 自定義報表

### 技術實現

#### Vue 3 前端組件
- `AlertDashboard.vue` - 主儀表板
- `AlertChart.vue` - 圖表組件
- `ReportBuilder.vue` - 報表構建器
- `SystemOverview.vue` - 系統概覽

#### 新增API端點
- `GET /api/dashboard/metrics` - 獲取儀表板指標
- `GET /api/reports/trends` - 獲取趨勢數據
- `POST /api/reports/custom` - 生成自定義報表
- `GET /api/reports/export/{id}` - 導出報表

### 驗收標準
- [ ] 儀表板實時更新
- [ ] 圖表正確顯示數據
- [ ] 報表生成功能正常
- [ ] 數據導出功能可用
- [ ] 移動端適配良好
- [ ] 響應時間 < 2秒

### 交付成果
- 完整的前端界面
- 實時數據更新
- 報表導出功能
- 移動端適配
- 用戶體驗測試報告

**預估工期：4-5週**

---

## 階段六：高級功能與優化 (MVP-6)

### 目標
添加高級功能，優化系統性能和用戶體驗。

### 核心功能
1. **高級特性**
   - AI輔助分析
   - 自動化運維
   - 預測性告警

2. **系統優化**
   - 性能調優
   - 安全加固
   - 高可用性

### 技術實現

#### 集成n8n工作流
- 自動化響應流程
- 複雜業務邏輯處理
- 第三方系統集成

#### 性能優化
- 數據庫索引優化
- 緩存策略改進
- 消息隊列優化

### 驗收標準
- [ ] AI分析功能正常
- [ ] 自動化流程運行穩定
- [ ] 系統性能達標
- [ ] 安全測試通過
- [ ] 高可用性驗證完成

### 交付成果
- AI分析模塊
- 自動化工作流
- 性能測試報告
- 安全評估報告
- 運維手冊

**預估工期：5-6週**

---

## 項目時間線

```
階段一 ├──────────┤ (2-3週)
階段二     ├──────────────┤ (3-4週)  
階段三           ├────────────────┤ (4-5週)
階段四                 ├──────────────┤ (3-4週)
階段五                       ├────────────────┤ (4-5週)
階段六                             ├──────────────────┤ (5-6週)

總計：21-27週 (約5-7個月)
```

## 技術債務管理

### 已知技術債務
1. **階段一**：基礎架構可能需要重構
2. **階段二**：MQTT消息處理性能優化
3. **階段三**：規則引擎複雜度管理
4. **階段四**：通知渠道錯誤處理
5. **階段五**：前端性能優化
6. **階段六**：系統整體架構優化

### 債務償還計劃
- 每個階段預留20%時間用於重構
- 定期代碼審查和技術債務評估
- 關鍵路徑優先償還

## 風險評估與緩解

### 高風險項目
1. **MQTT集成複雜度** - 緩解：提前原型驗證
2. **規則引擎性能** - 緩解：分階段壓力測試
3. **通知渠道穩定性** - 緩解：多渠道備份策略
4. **前端複雜度** - 緩解：組件化開發

### 依賴風險
1. **PostgreSQL性能** - 緩解：分庫分表預案
2. **Redis可用性** - 緩解：集群部署
3. **EMQX穩定性** - 緩解：監控和自動重啟

## 交付清單

每個階段交付：
- [ ] 可運行的代碼
- [ ] 功能測試報告
- [ ] API文檔
- [ ] 用戶手冊
- [ ] 部署指南
- [ ] 下階段需求確認

## 成功標準

### 技術指標
- 系統可用性 > 99.5%
- API響應時間 < 500ms
- 消息處理延遲 < 1s
- 數據庫查詢響應 < 200ms

### 業務指標
- 告警處理準確率 > 95%
- 用戶滿意度 > 4.5/5
- 系統學習成本 < 2小時
- 故障恢復時間 < 5分鐘

---

*此文檔會根據開發進度和需求變化持續更新*
