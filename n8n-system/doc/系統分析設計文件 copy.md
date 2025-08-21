# 智能告警分發系統 - 系統分析設計文件

## 專案概覽

基於 Spring Boot 3 與 n8n 整合的企業級智能告警分發系統，實現多系統事件驅動的自動化通知分流。

---

## 1. 系統需求分析

### 1.1 功能需求

#### 核心功能
- **多系統告警整合**：支援不同後端系統的事件告警統一處理
- **智能分流機制**：基於系統類型、告警等級、部門歸屬進行智能分流
- **多通道通知**：支援 Email、Slack、LINE、Teams 等多種通知方式
- **動態規則管理**：支援規則的動態配置與即時生效
- **AI 輔助分析**：整合 AI 進行告警分類、優先級判斷、內容摘要

#### 前端管理功能
- **使用者認證**：登入/登出介面、密碼修改、使用者資訊管理
- **權限控制**：基於角色的頁面存取控制、功能按鈕權限管理
- **告警監控台**：即時告警展示、狀態監控、快速響應
- **規則管理**：視覺化規則配置、條件設定、通知目標管理
- **系統配置**：系統註冊、Topic 管理、連接設定
- **歷史查詢**：告警歷史查詢、統計分析、報表生成
- **工作流程管理**：n8n workflow 可視化、執行狀態監控
- **監控儀表板**：系統效能指標、告警統計圖表、健康狀態
- **使用者管理**：使用者建立、角色分配、權限設定 (管理員功能)

#### 擴展功能
- **歷史記錄查詢**：告警處理歷史與統計分析
- **預測性告警**：基於歷史數據的異常預測
- **工作流程可視化**：n8n 提供的流程圖界面管理
- **API 自動配置**：透過 AI Agent 自動生成 n8n workflow

### 1.2 非功能需求

#### 效能需求
- **高可用性**：系統可用性 ≥ 99.5%
- **低延遲**：告警處理延遲 ≤ 3 秒
- **高併發**：支援每秒 1000+ 條告警處理
- **可擴展性**：支援水平擴展

#### 安全需求
- **使用者認證**：JWT Token 認證機制，支援使用者登入/登出
- **權限管理**：基於角色的存取控制 (RBAC)，支援管理員、操作員、觀察員等角色
- **資料加密**：敏感資料傳輸與存儲加密
- **密碼安全**：BCrypt 雜湊加密，支援密碼強度驗證
- **會話管理**：JWT Token 有效期控制，支援 Refresh Token
- **審計日誌**：完整的登入記錄與操作追蹤

---

## 2. 系統架構設計

### 2.1 整體架構

```
┌─────────────────────────────────────────────────────────────┐
│                    智能告警分發系統                           │
├─────────────────────────────────────────────────────────────┤
│  ┌─────────────────────────────────────────────────────────┐ │
│  │                   前端管理後台                           │ │
│  │  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐      │ │
│  │  │ 告警監控台  │ │ 規則管理    │ │ 系統配置    │      │ │
│  │  └─────────────┘ └─────────────┘ └─────────────┘      │ │
│  │  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐      │ │
│  │  │ 歷史查詢    │ │ 工作流程    │ │ 監控儀表板  │      │ │
│  │  └─────────────┘ └─────────────┘ └─────────────┘      │ │
│  │         Vue 3 + TypeScript + Element Plus            │ │
│  └─────────────────────┬───────────────────────────────────┘ │
│                        │ HTTP/WebSocket API                   │
├─────────────────────────────────────────────────────────────┤
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │   系統 A     │  │   系統 B     │  │   系統 C     │      │
│  │ (後端服務)    │  │ (資料庫)     │  │ (第三方API)   │      │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘      │
│         │                 │                 │              │
│         └─────────────────┼─────────────────┘              │
│                           │                                │
│  ┌─────────────────────────▼─────────────────────────┐      │
│  │           Spring Boot 3 應用程式                   │      │
│  │  ┌─────────────────────────────────────────────┐  │      │
│  │  │           告警收集與預處理模組              │  │      │
│  │  └─────────────────┬───────────────────────────┘  │      │
│  │                    │                              │      │
│  │  ┌─────────────────▼───────────────────────────┐  │      │
│  │  │              AI 分析模組                    │  │      │
│  │  │  • 分類與優先級判斷                         │  │      │
│  │  │  • 告警摘要與重組                           │  │      │
│  │  │  • 智能預測分析                             │  │      │
│  │  └─────────────────┬───────────────────────────┘  │      │
│  │                    │                              │      │
│  │  ┌─────────────────▼───────────────────────────┐  │      │
│  │  │            規則引擎模組                     │  │      │
│  │  │  • 動態規則配置                             │  │      │
│  │  │  • 條件判斷邏輯                             │  │      │
│  │  │  • 分流決策                                 │  │      │
│  │  └─────────────────┬───────────────────────────┘  │      │
│  │                    │                              │      │
│  │  ┌─────────────────▼───────────────────────────┐  │      │
│  │  │           n8n API 整合模組                  │  │      │
│  │  │  • Workflow 動態生成                        │  │      │
│  │  │  • API 調用管理                             │  │      │
│  │  │  • 狀態監控                                 │  │      │
│  │  └─────────────────┬───────────────────────────┘  │      │
│  └──────────────────────────────────────────────────┘      │
│                       │                                     │
│  ┌─────────────────────▼─────────────────────────┐          │
│  │                  n8n 工作流程引擎              │          │
│  │  ┌─────────────────────────────────────────┐  │          │
│  │  │  Webhook/MQTT Trigger               │  │          │
│  │  └─────────────┬───────────────────────────┘  │          │
│  │                │                              │          │
│  │  ┌─────────────▼───────────────────────────┐  │          │
│  │  │     條件分流節點 (Switch/IF Node)      │  │          │
│  │  └─────────────┬───────────────────────────┘  │          │
│  │                │                              │          │
│  │  ┌─────────────▼───────────────────────────┐  │          │
│  │  │          通知執行節點                   │  │          │
│  │  │  ┌─────────┐ ┌─────────┐ ┌─────────┐  │  │          │
│  │  │  │ Email   │ │ Slack   │ │  LINE   │  │  │          │
│  │  │  └─────────┘ └─────────┘ └─────────┘  │  │          │
│  │  └─────────────────────────────────────────┘  │          │
│  └─────────────────────────────────────────────────┘          │
├─────────────────────────────────────────────────────────────┤
│                    資料持久層                                │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │  PostgreSQL  │  │    Redis     │  │   EMQX       │      │
│  │ (主要資料庫)  │  │   (快取)     │  │ (訊息佇列)    │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
└─────────────────────────────────────────────────────────────┘
```

### 2.2 技術架構選型

#### 後端框架
- **Spring Boot 3.2.x**：主要應用框架
- **Spring Security**：安全認證與授權
- **Spring Data JPA**：資料存取層
- **Spring WebFlux**：響應式程式設計支援

#### 前端框架
- **Vue 3.4.x**：前端 UI 框架
- **TypeScript 5.x**：類型安全的 JavaScript
- **Vite 5.x**：現代化建構工具
- **Element Plus**：企業級 UI 組件庫
- **Pinia**：狀態管理
- **Vue Router 4.x**：路由管理
- **Axios**：HTTP 客戶端

#### 資料庫與中介軟體
- **PostgreSQL 15+**：主要關聯式資料庫 (port: 15432)
- **Redis 7.x**：快取與會話存儲 (port: 16379)
- **EMQX 5.0**：MQTT 訊息代理 (TCP: 1883, WebSocket: 8083, Dashboard: 18083)

#### 外部整合
- **n8n**：工作流程自動化引擎
- **OpenAI API**：AI 分析服務
- **各種通知服務 API**

---

## 3. 詳細設計

### 3.1 資料庫設計

#### 核心資料表

```sql
-- 使用者表
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100),
    avatar_url VARCHAR(500),
    status VARCHAR(20) DEFAULT 'ACTIVE', -- ACTIVE, INACTIVE, LOCKED
    last_login_at TIMESTAMP,
    password_updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    failed_login_attempts INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 角色表
CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    permissions JSONB, -- 權限列表
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 使用者角色關聯表
CREATE TABLE user_roles (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    role_id BIGINT REFERENCES roles(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, role_id)
);

-- 登入記錄表
CREATE TABLE login_logs (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    login_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ip_address INET,
    user_agent TEXT,
    login_status VARCHAR(20), -- SUCCESS, FAILED, LOCKED
    failure_reason VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 系統配置表
CREATE TABLE systems (
    id BIGSERIAL PRIMARY KEY,
    system_name VARCHAR(100) NOT NULL UNIQUE,
    system_topic VARCHAR(200) NOT NULL,
    description TEXT,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_by BIGINT REFERENCES users(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 告警規則表
CREATE TABLE alert_rules (
    id BIGSERIAL PRIMARY KEY,
    system_id BIGINT REFERENCES systems(id),
    alert_type VARCHAR(100) NOT NULL,
    priority_level VARCHAR(20) NOT NULL, -- HIGH, MEDIUM, LOW
    notification_channels JSONB, -- 通知管道配置
    notification_targets JSONB, -- 通知對象
    conditions JSONB, -- 觸發條件
    ai_analysis_enabled BOOLEAN DEFAULT false,
    workflow_id VARCHAR(100), -- n8n workflow ID
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_by BIGINT REFERENCES users(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 告警歷史表
CREATE TABLE alert_history (
    id BIGSERIAL PRIMARY KEY,
    system_id BIGINT REFERENCES systems(id),
    alert_type VARCHAR(100) NOT NULL,
    original_content JSONB NOT NULL,
    processed_content JSONB,
    ai_analysis_result JSONB,
    priority_level VARCHAR(20),
    notification_sent JSONB, -- 發送記錄
    processing_time_ms INTEGER,
    status VARCHAR(20) DEFAULT 'PROCESSED',
    handled_by BIGINT REFERENCES users(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- n8n Workflow 配置表
CREATE TABLE n8n_workflows (
    id BIGSERIAL PRIMARY KEY,
    workflow_name VARCHAR(200) NOT NULL,
    workflow_id VARCHAR(100) NOT NULL UNIQUE,
    workflow_definition JSONB NOT NULL,
    system_mappings JSONB, -- 關聯的系統
    status VARCHAR(20) DEFAULT 'ACTIVE',
    last_deployed_at TIMESTAMP,
    created_by BIGINT REFERENCES users(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 初始化預設角色資料
INSERT INTO roles (role_name, description, permissions) VALUES 
('ADMIN', '系統管理員', '["USER_MANAGE", "SYSTEM_CONFIG", "ALERT_MANAGE", "RULE_MANAGE", "WORKFLOW_MANAGE", "REPORT_VIEW"]'),
('OPERATOR', '系統操作員', '["ALERT_MANAGE", "RULE_MANAGE", "WORKFLOW_MANAGE", "REPORT_VIEW"]'),
('VIEWER', '觀察員', '["ALERT_VIEW", "REPORT_VIEW"]');

-- 建立預設管理員帳號 (密碼: Admin123456)
INSERT INTO users (username, email, password_hash, full_name, status) VALUES 
('admin', 'admin@company.com', '$2a$10$rqiU9CjAE.mGl7ZQGLwQRO2nQ8YVJ7JQVjV5QFJ4YVJl4JQVjV5QF', '系統管理員', 'ACTIVE');

-- 分配管理員角色
INSERT INTO user_roles (user_id, role_id) 
SELECT u.id, r.id FROM users u, roles r WHERE u.username = 'admin' AND r.role_name = 'ADMIN';

-- 建立索引提升查詢效能
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_status ON users(status);
CREATE INDEX idx_login_logs_user_id ON login_logs(user_id);
CREATE INDEX idx_login_logs_login_time ON login_logs(login_time);
CREATE INDEX idx_alert_history_system_id ON alert_history(system_id);
CREATE INDEX idx_alert_history_created_at ON alert_history(created_at);
CREATE INDEX idx_alert_rules_system_id ON alert_rules(system_id);
```

### 3.2 API 設計

#### 統一響應格式與錯誤處理

```java
// 統一 API 響應格式
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private int code;
    private String message;
    private T data;
    private long timestamp = System.currentTimeMillis();
    
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .code(200)
                .message("成功")
                .data(data)
                .build();
    }
    
    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .code(200)
                .message(message)
                .data(data)
                .build();
    }
    
    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .code(500)
                .message(message)
                .build();
    }
    
    public static <T> ApiResponse<T> error(int code, String message) {
        return ApiResponse.<T>builder()
                .code(code)
                .message(message)
                .build();
    }
}

// 全域異常處理器
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException e) {
        StringBuilder errors = new StringBuilder();
        e.getBindingResult().getFieldErrors().forEach(error -> {
            errors.append(error.getField()).append(": ").append(error.getDefaultMessage()).append("; ");
        });
        
        return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, "資料驗證失敗: " + errors.toString()));
    }
    
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolation(ConstraintViolationException e) {
        return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, "參數驗證失敗: " + e.getMessage()));
    }
    
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, e.getMessage()));
    }
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFound(ResourceNotFoundException e) {
        return ResponseEntity.status(404)
                .body(ApiResponse.error(404, e.getMessage()));
    }
    
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDenied(AccessDeniedException e) {
        return ResponseEntity.status(403)
                .body(ApiResponse.error(403, "存取被拒絕"));
    }
    
    @ExceptionHandler(TooManyRequestsException.class)
    public ResponseEntity<ApiResponse<Void>> handleTooManyRequests(TooManyRequestsException e) {
        return ResponseEntity.status(429)
                .body(ApiResponse.error(429, "請求過於頻繁，請稍後再試"));
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneral(Exception e) {
        log.error("系統錯誤", e);
        return ResponseEntity.status(500)
                .body(ApiResponse.error(500, "系統錯誤，請聯繫管理員"));
    }
}
```

#### API 版本管理與基礎限流

```java
// API 版本管理
@RestController
@RequestMapping("/api/v1")
@Validated
public class AlertController {
    
    @PostMapping("/alerts")
    @RateLimited(requests = 100, timeWindow = "1m") // 自訂註解
    public ResponseEntity<ApiResponse<AlertResponse>> createAlert(
            @Valid @RequestBody CreateAlertRequest request,
            HttpServletRequest httpRequest) {
        
        // 業務邏輯處理
        AlertResponse response = alertService.createAlert(request);
        
        return ResponseEntity.ok(ApiResponse.success("告警建立成功", response));
    }
}

// 基礎限流實作
@Component
@Slf4j
public class BasicRateLimiter {
    
    private final Map<String, Map<String, AtomicInteger>> requestCounts = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    
    @PostConstruct
    public void init() {
        // 每分鐘清理一次計數器
        scheduler.scheduleAtFixedRate(this::cleanupCounters, 1, 1, TimeUnit.MINUTES);
    }
    
    public boolean isAllowed(String endpoint, String clientId, int maxRequests) {
        String key = endpoint + ":" + clientId;
        String timeWindow = getCurrentTimeWindow();
        
        Map<String, AtomicInteger> windowCounts = requestCounts.computeIfAbsent(key, 
                k -> new ConcurrentHashMap<>());
        
        AtomicInteger count = windowCounts.computeIfAbsent(timeWindow, 
                k -> new AtomicInteger(0));
        
        int currentCount = count.incrementAndGet();
        
        if (currentCount > maxRequests) {
            log.warn("限流觸發: endpoint={}, client={}, count={}", endpoint, clientId, currentCount);
            return false;
        }
        
        return true;
    }
    
    private String getCurrentTimeWindow() {
        return String.valueOf(System.currentTimeMillis() / 60000); // 以分鐘為單位
    }
    
    private void cleanupCounters() {
        String currentWindow = getCurrentTimeWindow();
        long currentWindowNum = Long.parseLong(currentWindow);
        
        requestCounts.forEach((key, windowCounts) -> {
            windowCounts.entrySet().removeIf(entry -> {
                long windowNum = Long.parseLong(entry.getKey());
                return windowNum < currentWindowNum - 2; // 保留最近 2 分鐘
            });
        });
    }
    
    @PreDestroy
    public void shutdown() {
        scheduler.shutdown();
    }
}

// 限流註解和攔截器
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimited {
    int requests() default 100;
    String timeWindow() default "1m";
}

@Component
public class RateLimitInterceptor implements HandlerInterceptor {
    
    @Autowired
    private BasicRateLimiter rateLimiter;
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, 
                            Object handler) throws Exception {
        
        if (handler instanceof HandlerMethod) {
            HandlerMethod method = (HandlerMethod) handler;
            RateLimited annotation = method.getMethodAnnotation(RateLimited.class);
            
            if (annotation != null) {
                String clientId = getClientIdentifier(request);
                String endpoint = request.getRequestURI();
                
                if (!rateLimiter.isAllowed(endpoint, clientId, annotation.requests())) {
                    throw new TooManyRequestsException("請求過於頻繁");
                }
            }
        }
        
        return true;
    }
    
    private String getClientIdentifier(HttpServletRequest request) {
        // 優先使用使用者 ID，其次使用 IP
        String userId = getCurrentUserId(request);
        if (userId != null) {
            return "user:" + userId;
        }
        
        String clientIp = getClientIpAddress(request);
        return "ip:" + clientIp;
    }
    
    private String getCurrentUserId(HttpServletRequest request) {
        // 從 JWT Token 中提取使用者 ID
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                // 解析 JWT Token 獲取使用者 ID
                return jwtTokenUtil.getUserIdFromToken(token);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }
    
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
```

#### 資料驗證增強

```java
// 告警建立請求驗證
@Data
@Valid
public class CreateAlertRequest {
    
    @NotBlank(message = "系統ID不能為空")
    @Size(max = 100, message = "系統ID長度不能超過100個字元")
    private String systemId;
    
    @NotBlank(message = "告警類型不能為空")
    @Size(max = 100, message = "告警類型長度不能超過100個字元")
    private String alertType;
    
    @NotNull(message = "優先級不能為空")
    @Pattern(regexp = "HIGH|MEDIUM|LOW", message = "優先級必須是 HIGH、MEDIUM 或 LOW")
    private String priority;
    
    @NotBlank(message = "告警內容不能為空")
    @Size(max = 5000, message = "告警內容長度不能超過5000個字元")
    private String content;
    
    @Valid
    private Map<String, Object> metadata;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime occurredAt;
    
    // 自訂驗證器
    @ValidAlertContent
    private String alertContent;
}

// 自訂驗證註解
@Constraint(validatedBy = AlertContentValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidAlertContent {
    String message() default "告警內容格式不正確";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

@Component
public class AlertContentValidator implements ConstraintValidator<ValidAlertContent, String> {
    
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        }
        
        // 檢查是否包含惡意腳本
        if (containsScript(value)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("告警內容不能包含腳本代碼")
                   .addConstraintViolation();
            return false;
        }
        
        // 檢查是否為有效的 JSON（如果聲稱是 JSON）
        if (value.trim().startsWith("{") && !isValidJson(value)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("JSON 格式不正確")
                   .addConstraintViolation();
            return false;
        }
        
        return true;
    }
    
    private boolean containsScript(String content) {
        String lowerContent = content.toLowerCase();
        return lowerContent.contains("<script") || 
               lowerContent.contains("javascript:") ||
               lowerContent.contains("eval(") ||
               lowerContent.contains("expression(");
    }
    
    private boolean isValidJson(String content) {
        try {
            new ObjectMapper().readTree(content);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

// 自訂異常類
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}

public class TooManyRequestsException extends RuntimeException {
    public TooManyRequestsException(String message) {
        super(message);
    }
}
```

```http
### 使用者登入
POST /api/auth/login
Content-Type: application/json

{
    "username": "admin",
    "password": "Admin123456"
}

Response:
{
    "code": 200,
    "message": "登入成功",
    "data": {
        "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
        "refreshToken": "refresh_token_here",
        "user": {
            "id": 1,
            "username": "admin",
            "email": "admin@company.com",
            "fullName": "系統管理員",
            "roles": ["ADMIN"],
            "permissions": ["USER_MANAGE", "SYSTEM_CONFIG", "ALERT_MANAGE"]
        },
        "expiresIn": 3600
    }
}

### 使用者登出
POST /api/auth/logout
Authorization: Bearer <token>

Response:
{
    "code": 200,
    "message": "登出成功"
}

### 刷新 Token
POST /api/auth/refresh
Content-Type: application/json

{
    "refreshToken": "refresh_token_here"
}

Response:
{
    "code": 200,
    "message": "Token 刷新成功",
    "data": {
        "token": "new_jwt_token",
        "expiresIn": 3600
    }
}

### 修改密碼
PUT /api/auth/password
Authorization: Bearer <token>
Content-Type: application/json

{
    "oldPassword": "Admin123456",
    "newPassword": "NewPassword123"
}

Response:
{
    "code": 200,
    "message": "密碼修改成功"
}

### 取得使用者資訊
GET /api/auth/profile
Authorization: Bearer <token>

Response:
{
    "code": 200,
    "data": {
        "id": 1,
        "username": "admin",
        "email": "admin@company.com",
        "fullName": "系統管理員",
        "avatarUrl": null,
        "lastLoginAt": "2025-08-21T10:30:00Z",
        "roles": ["ADMIN"],
        "permissions": ["USER_MANAGE", "SYSTEM_CONFIG", "ALERT_MANAGE"]
    }
}
```

#### 使用者管理 API (管理員功能)

```http
### 取得使用者列表
GET /api/users?page=1&size=20&keyword=admin
Authorization: Bearer <token>

Response:
{
    "code": 200,
    "data": {
        "content": [
            {
                "id": 1,
                "username": "admin",
                "email": "admin@company.com",
                "fullName": "系統管理員",
                "status": "ACTIVE",
                "roles": ["ADMIN"],
                "lastLoginAt": "2025-08-21T10:30:00Z",
                "createdAt": "2025-08-01T09:00:00Z"
            }
        ],
        "totalElements": 1,
        "totalPages": 1,
        "size": 20,
        "number": 0
    }
}

### 建立使用者
POST /api/users
Authorization: Bearer <token>
Content-Type: application/json

{
    "username": "operator1",
    "email": "operator1@company.com",
    "fullName": "操作員一號",
    "password": "TempPassword123",
    "roleIds": [2]
}

Response:
{
    "code": 200,
    "message": "使用者建立成功",
    "data": {
        "id": 2,
        "username": "operator1",
        "email": "operator1@company.com",
        "fullName": "操作員一號",
        "status": "ACTIVE",
        "roles": ["OPERATOR"]
    }
}

### 更新使用者
PUT /api/users/{id}
Authorization: Bearer <token>
Content-Type: application/json

{
    "email": "operator1@company.com",
    "fullName": "資深操作員",
    "status": "ACTIVE",
    "roleIds": [2]
}

### 刪除使用者
DELETE /api/users/{id}
Authorization: Bearer <token>

### 重置使用者密碼
POST /api/users/{id}/reset-password
Authorization: Bearer <token>

Response:
{
    "code": 200,
    "message": "密碼重置成功",
    "data": {
        "tempPassword": "TempPass789"
    }
}
```

#### 告警接收 API

```java
@RestController
@RequestMapping("/api/v1/alerts")
public class AlertController {
    
    @PostMapping("/webhook/{systemTopic}")
    public ResponseEntity<AlertResponse> receiveAlert(
            @PathVariable String systemTopic,
            @RequestBody AlertEvent alertEvent,
            @RequestHeader(value = "Authorization", required = false) String authToken
    ) {
        // 1. 驗證請求來源
        // 2. 告警事件預處理
        // 3. AI 分析 (如果啟用)
        // 4. 規則引擎處理
        // 5. 觸發 n8n workflow
        // 6. 返回處理結果
    }
    
    @GetMapping("/history")
    public ResponseEntity<PagedResponse<AlertHistory>> getAlertHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String systemName,
            @RequestParam(required = false) String alertType
    ) {
        // 查詢告警歷史
    }
}
```

#### 規則管理 API

```java
@RestController
@RequestMapping("/api/v1/rules")
public class RuleController {
    
    @PostMapping
    public ResponseEntity<AlertRule> createRule(@RequestBody CreateRuleRequest request) {
        // 創建新的告警規則
    }
    
    @PutMapping("/{ruleId}")
    public ResponseEntity<AlertRule> updateRule(
            @PathVariable Long ruleId,
            @RequestBody UpdateRuleRequest request
    ) {
        // 更新告警規則
    }
    
    @PostMapping("/{ruleId}/deploy")
    public ResponseEntity<DeploymentResult> deployRule(@PathVariable Long ruleId) {
        // 部署規則到 n8n (生成/更新 workflow)
    }
}
```

### 3.3 核心服務設計

#### 告警處理服務

```java
@Service
@Transactional
public class AlertProcessingService {
    
    private final AlertRuleService ruleService;
    private final AIAnalysisService aiService;
    private final N8nIntegrationService n8nService;
    private final NotificationService notificationService;
    
    public AlertResponse processAlert(String systemTopic, AlertEvent event) {
        try {
            // 1. 系統識別與驗證
            SystemInfo system = validateAndGetSystem(systemTopic);
            
            // 2. 規則匹配
            List<AlertRule> matchedRules = ruleService.findMatchingRules(system, event);
            
            // 3. AI 分析 (如果啟用)
            AIAnalysisResult aiResult = null;
            if (shouldPerformAIAnalysis(matchedRules)) {
                aiResult = aiService.analyzeAlert(event);
                event = enrichEventWithAI(event, aiResult);
            }
            
            // 4. 執行規則處理
            List<ProcessingResult> results = new ArrayList<>();
            for (AlertRule rule : matchedRules) {
                ProcessingResult result = executeRule(rule, event, aiResult);
                results.add(result);
            }
            
            // 5. 記錄處理歷史
            saveAlertHistory(system, event, aiResult, results);
            
            return AlertResponse.success(results);
            
        } catch (Exception e) {
            log.error("告警處理失敗: {}", e.getMessage(), e);
            return AlertResponse.error(e.getMessage());
        }
    }
    
    private ProcessingResult executeRule(AlertRule rule, AlertEvent event, AIAnalysisResult aiResult) {
        try {
            // 觸發對應的 n8n workflow
            N8nTriggerResult triggerResult = n8nService.triggerWorkflow(
                rule.getWorkflowId(),
                buildN8nPayload(rule, event, aiResult)
            );
            
            return ProcessingResult.success(rule, triggerResult);
            
        } catch (Exception e) {
            return ProcessingResult.error(rule, e.getMessage());
        }
    }
}
```

#### AI 分析服務

```java
@Service
public class AIAnalysisService {
    
    private final OpenAIClient openAIClient;
    private final AlertAnalysisPromptTemplate promptTemplate;
    
    public AIAnalysisResult analyzeAlert(AlertEvent event) {
        try {
            // 構建 AI 分析提示
            String prompt = promptTemplate.buildAnalysisPrompt(event);
            
            // 調用 OpenAI API
            ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model("gpt-4")
                .messages(List.of(
                    ChatMessage.systemMessage("你是一個專業的系統告警分析專家..."),
                    ChatMessage.userMessage(prompt)
                ))
                .temperature(0.1)
                .build();
                
            ChatCompletionResult result = openAIClient.createChatCompletion(request);
            
            // 解析 AI 回應
            return parseAIResponse(result.getChoices().get(0).getMessage().getContent());
            
        } catch (Exception e) {
            log.error("AI 分析失敗: {}", e.getMessage(), e);
            return AIAnalysisResult.error(e.getMessage());
        }
    }
    
    private AIAnalysisResult parseAIResponse(String aiResponse) {
        // 解析 AI 回應，提取分類、優先級、摘要等資訊
        return AIAnalysisResult.builder()
            .category(extractCategory(aiResponse))
            .priorityLevel(extractPriority(aiResponse))
            .summary(extractSummary(aiResponse))
            .recommendations(extractRecommendations(aiResponse))
            .build();
    }
}
```

#### MQTT 整合服務

基於您已部署的 EMQX 5.0 配置：

```java
@Service
public class MqttIntegrationService {
    
    @Value("${mqtt.broker-url}")
    private String brokerUrl;
    
    @Value("${mqtt.client-id}")
    private String clientId;
    
    @Value("${mqtt.username:}")
    private String username;
    
    @Value("${mqtt.password:}")
    private String password;
    
    private IMqttClient mqttClient;
    
    @PostConstruct
    public void init() {
        try {
            // 建立 MQTT 客戶端
            mqttClient = new MqttClient(brokerUrl, clientId, new MemoryPersistence());
            
            // 設定連接選項
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);
            options.setKeepAliveInterval(60);
            options.setConnectionTimeout(30);
            
            if (StringUtils.hasText(username)) {
                options.setUserName(username);
                options.setPassword(password.toCharArray());
            }
            
            // 設定回調處理
            mqttClient.setCallback(new MqttCallbackExtended() {
                @Override
                public void connectComplete(boolean reconnect, String serverURI) {
                    log.info("MQTT 連接成功: {}, 重連: {}", serverURI, reconnect);
                    subscribeToTopics();
                }
                
                @Override
                public void connectionLost(Throwable cause) {
                    log.error("MQTT 連接中斷", cause);
                }
                
                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    handleIncomingMessage(topic, message);
                }
                
                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    log.debug("訊息發送完成: {}", token.getMessageId());
                }
            });
            
            // 連接到 MQTT Broker
            mqttClient.connect(options);
            
        } catch (Exception e) {
            log.error("MQTT 客戶端初始化失敗", e);
        }
    }
    
    private void subscribeToTopics() {
        try {
            // 訂閱告警事件主題
            mqttClient.subscribe("alerts/events/+", 1);
            mqttClient.subscribe("system/status/+", 1);
            log.info("已訂閱 MQTT 主題");
        } catch (MqttException e) {
            log.error("訂閱 MQTT 主題失敗", e);
        }
    }
    
    public void publishAlert(String systemName, AlertEvent alertEvent) {
        try {
            String topic = String.format("alerts/events/%s", systemName);
            String payload = objectMapper.writeValueAsString(alertEvent);
            
            MqttMessage message = new MqttMessage(payload.getBytes());
            message.setQos(1);
            message.setRetained(false);
            
            mqttClient.publish(topic, message);
            log.info("發布告警事件到 MQTT: topic={}, message={}", topic, payload);
            
        } catch (Exception e) {
            log.error("發布 MQTT 訊息失敗", e);
        }
    }
    
    public void publishWorkflowTrigger(String workflowId, Object payload) {
        try {
            String topic = String.format("workflow/trigger/%s", workflowId);
            String message = objectMapper.writeValueAsString(payload);
            
            MqttMessage mqttMessage = new MqttMessage(message.getBytes());
            mqttMessage.setQos(1);
            
            mqttClient.publish(topic, mqttMessage);
            log.info("發布工作流程觸發到 MQTT: topic={}", topic);
            
        } catch (Exception e) {
            log.error("發布工作流程觸發失敗", e);
        }
    }
    
    private void handleIncomingMessage(String topic, MqttMessage message) {
        try {
            String payload = new String(message.getPayload());
            log.info("收到 MQTT 訊息: topic={}, payload={}", topic, payload);
            
            // 根據主題類型處理訊息
            if (topic.startsWith("alerts/events/")) {
                String systemName = extractSystemNameFromTopic(topic);
                AlertEvent alertEvent = objectMapper.readValue(payload, AlertEvent.class);
                alertProcessingService.processAlert(systemName, alertEvent);
                
            } else if (topic.startsWith("system/status/")) {
                String systemName = extractSystemNameFromTopic(topic);
                SystemStatus status = objectMapper.readValue(payload, SystemStatus.class);
                systemStatusService.updateStatus(systemName, status);
            }
            
        } catch (Exception e) {
            log.error("處理 MQTT 訊息失敗: topic={}", topic, e);
        }
    }
    
    private String extractSystemNameFromTopic(String topic) {
        String[] parts = topic.split("/");
        return parts.length > 2 ? parts[2] : "unknown";
    }
    
    // 檢查 MQTT 連接狀態
    public boolean isConnected() {
        return mqttClient != null && mqttClient.isConnected();
    }
    
    // 檢查 EMQX 服務狀態
    public MqttBrokerStatus getBrokerStatus() {
        try {
            // 呼叫 EMQX Management API
            String apiUrl = "http://localhost:8081/api/v5/stats";
            ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);
            
            if (response.getStatusCode().is2xxSuccessful()) {
                return MqttBrokerStatus.builder()
                    .healthy(true)
                    .connected(isConnected())
                    .stats(response.getBody())
                    .build();
            }
        } catch (Exception e) {
            log.warn("無法獲取 EMQX 狀態", e);
        }
        
        return MqttBrokerStatus.builder()
            .healthy(false)
            .connected(isConnected())
            .build();
    }
    
    @PreDestroy
    public void cleanup() {
        try {
            if (mqttClient != null && mqttClient.isConnected()) {
                mqttClient.disconnect();
                mqttClient.close();
            }
        } catch (MqttException e) {
            log.error("關閉 MQTT 客戶端失敗", e);
        }
    }
}
```

#### n8n 整合服務

基於您現有的 n8n 部署配置 (基本認證)：

```java
@Service
public class N8nIntegrationService {
    
    @Value("${n8n.api.base-url}")
    private String n8nApiBaseUrl;
    
    @Value("${n8n.api.username}")
    private String n8nUsername;
    
    @Value("${n8n.api.password}")
    private String n8nPassword;
    
    private final RestTemplate restTemplate;
    private final WorkflowTemplateService templateService;
    
    @PostConstruct
    public void init() {
        // 設定基本認證
        restTemplate.getInterceptors().add(new BasicAuthenticationInterceptor(n8nUsername, n8nPassword));
    }
    
    public String createWorkflow(AlertRule rule) {
        try {
            // 根據規則生成 workflow 定義
            N8nWorkflowDefinition workflow = templateService.generateWorkflow(rule);
            
            // 調用 n8n API 創建 workflow (使用基本認證)
            CreateWorkflowRequest request = CreateWorkflowRequest.builder()
                .name(generateWorkflowName(rule))
                .nodes(workflow.getNodes())
                .connections(workflow.getConnections())
                .active(true)
                .build();
                
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            // 基本認證會由 interceptor 自動處理
            
            HttpEntity<CreateWorkflowRequest> entity = new HttpEntity<>(request, headers);
            ResponseEntity<CreateWorkflowResponse> response = restTemplate.postForEntity(
                n8nApiBaseUrl + "/workflows",
                entity,
                CreateWorkflowResponse.class
            );
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody().getId();
            } else {
                throw new N8nIntegrationException("Workflow 創建失敗: " + response.getStatusCode());
            }
            
        } catch (Exception e) {
            log.error("創建 n8n workflow 失敗: {}", e.getMessage(), e);
            throw new N8nIntegrationException("Workflow 創建失敗", e);
        }
    }
    
    public N8nTriggerResult triggerWorkflow(String workflowId, Object payload) {
        try {
            // 觸發 workflow 執行 (使用 Webhook 方式)
            String webhookUrl = buildWebhookUrl(workflowId);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Object> entity = new HttpEntity<>(payload, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(
                webhookUrl,
                entity,
                String.class
            );
            
            return N8nTriggerResult.success(response.getBody());
            
        } catch (Exception e) {
            log.error("觸發 n8n workflow 失敗: workflowId={}, error={}", workflowId, e.getMessage(), e);
            throw new N8nIntegrationException("Workflow 觸發失敗", e);
        }
    }
    
    private String buildWebhookUrl(String workflowId) {
        return n8nApiBaseUrl.replace("/api/v1", "") + "/webhook/" + workflowId;
    }
    
    private String generateWorkflowName(AlertRule rule) {
        return String.format("Alert_%s_%s_%d", 
            rule.getSystemName(), 
            rule.getAlertType(), 
            System.currentTimeMillis());
    }
    
    // 檢查 n8n 服務狀態
    public boolean isN8nHealthy() {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(
                n8nApiBaseUrl + "/workflows",
                String.class
            );
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            log.warn("n8n 健康檢查失敗: {}", e.getMessage());
            return false;
        }
    }
}
```

### 3.4 認證授權服務設計

#### Spring Security 配置

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    
    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    
    @Autowired
    private JwtRequestFilter jwtRequestFilter;
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/api/auth/login", "/api/auth/refresh").permitAll()
                .requestMatchers("/api/alerts/webhook").permitAll() // 外部系統告警接收
                .requestMatchers(HttpMethod.GET, "/api/health").permitAll()
                .requestMatchers("/api/users/**").hasRole("ADMIN")
                .requestMatchers("/api/systems/**").hasAnyRole("ADMIN", "OPERATOR")
                .requestMatchers("/api/rules/**").hasAnyRole("ADMIN", "OPERATOR")
                .requestMatchers("/api/alerts/**").hasAnyRole("ADMIN", "OPERATOR", "VIEWER")
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
            );
        
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}
```

#### JWT 工具類

```java
@Component
public class JwtTokenUtil {
    
    private String secret = "your-secret-key-here-should-be-very-long-and-secure";
    private int jwtExpiration = 3600; // 1 小時
    private int refreshExpiration = 604800; // 7 天
    
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        if (userDetails instanceof CustomUserDetails) {
            CustomUserDetails customUser = (CustomUserDetails) userDetails;
            claims.put("userId", customUser.getId());
            claims.put("email", customUser.getEmail());
            claims.put("fullName", customUser.getFullName());
            claims.put("roles", customUser.getRoles());
        }
        return createToken(claims, userDetails.getUsername());
    }
    
    public String generateRefreshToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + refreshExpiration * 1000))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }
    
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration * 1000))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }
    
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }
    
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }
    
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }
    
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }
    
    public Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }
    
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
```

#### 使用者服務實作

```java
@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String username;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String passwordHash;
    
    private String fullName;
    private String avatarUrl;
    
    @Enumerated(EnumType.STRING)
    private UserStatus status = UserStatus.ACTIVE;
    
    private LocalDateTime lastLoginAt;
    private LocalDateTime passwordUpdatedAt;
    private Integer failedLoginAttempts = 0;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();
}

@Service
@Transactional
public class UserService implements UserDetailsService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private LoginLogService loginLogService;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("使用者不存在: " + username));
        
        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new DisabledException("使用者帳號已停用");
        }
        
        return CustomUserDetails.builder()
                .id(user.getId())
                .username(user.getUsername())
                .password(user.getPasswordHash())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .enabled(user.getStatus() == UserStatus.ACTIVE)
                .roles(user.getRoles().stream()
                        .map(Role::getRoleName)
                        .collect(Collectors.toSet()))
                .authorities(buildAuthorities(user.getRoles()))
                .build();
    }
    
    public User createUser(CreateUserRequest request) {
        // 檢查使用者名稱是否已存在
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("使用者名稱已存在");
        }
        
        // 檢查電子信箱是否已存在
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("電子信箱已存在");
        }
        
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .status(UserStatus.ACTIVE)
                .passwordUpdatedAt(LocalDateTime.now())
                .build();
        
        // 設定角色
        Set<Role> roles = roleRepository.findByIdIn(request.getRoleIds());
        user.setRoles(roles);
        
        return userRepository.save(user);
    }
    
    public void updateLastLogin(String username, String ipAddress, String userAgent) {
        userRepository.findByUsername(username).ifPresent(user -> {
            user.setLastLoginAt(LocalDateTime.now());
            user.setFailedLoginAttempts(0);
            userRepository.save(user);
            
            // 記錄登入日誌
            loginLogService.recordLogin(user.getId(), ipAddress, userAgent, LoginStatus.SUCCESS);
        });
    }
    
    public void recordFailedLogin(String username, String ipAddress, String userAgent, String reason) {
        userRepository.findByUsername(username).ifPresent(user -> {
            user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
            
            // 連續失敗 5 次鎖定帳號
            if (user.getFailedLoginAttempts() >= 5) {
                user.setStatus(UserStatus.LOCKED);
            }
            
            userRepository.save(user);
            loginLogService.recordLogin(user.getId(), ipAddress, userAgent, 
                    user.getStatus() == UserStatus.LOCKED ? LoginStatus.LOCKED : LoginStatus.FAILED, reason);
        });
    }
    
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("使用者不存在"));
        
        if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("原密碼錯誤");
        }
        
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setPasswordUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }
    
    public String resetPassword(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("使用者不存在"));
        
        // 生成隨機密碼
        String tempPassword = generateTempPassword();
        user.setPasswordHash(passwordEncoder.encode(tempPassword));
        user.setPasswordUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        
        return tempPassword;
    }
    
    private String generateTempPassword() {
        return RandomStringUtils.randomAlphanumeric(10);
    }
    
    private Collection<? extends GrantedAuthority> buildAuthorities(Set<Role> roles) {
        Set<String> permissions = new HashSet<>();
        for (Role role : roles) {
            permissions.add("ROLE_" + role.getRoleName());
            // 添加角色權限
            List<String> rolePermissions = objectMapper.readValue(
                    role.getPermissions(), new TypeReference<List<String>>() {});
            permissions.addAll(rolePermissions);
        }
        
        return permissions.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}
```

#### 認證控制器

```java
@RestController
@RequestMapping("/api/auth")
@Validated
public class AuthController {
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request, 
                                  HttpServletRequest httpRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(), request.getPassword())
            );
            
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            
            // 生成 Token
            String token = jwtTokenUtil.generateToken(userDetails);
            String refreshToken = jwtTokenUtil.generateRefreshToken(userDetails.getUsername());
            
            // 存儲 Refresh Token 到 Redis
            String redisKey = "refresh_token:" + userDetails.getUsername();
            redisTemplate.opsForValue().set(redisKey, refreshToken, Duration.ofDays(7));
            
            // 更新最後登入時間
            userService.updateLastLogin(userDetails.getUsername(), 
                    getClientIpAddress(httpRequest), httpRequest.getHeader("User-Agent"));
            
            LoginResponse response = LoginResponse.builder()
                    .token(token)
                    .refreshToken(refreshToken)
                    .user(buildUserInfo(userDetails))
                    .expiresIn(3600)
                    .build();
            
            return ResponseEntity.ok(ApiResponse.success("登入成功", response));
            
        } catch (BadCredentialsException e) {
            userService.recordFailedLogin(request.getUsername(),
                    getClientIpAddress(httpRequest), httpRequest.getHeader("User-Agent"), "密碼錯誤");
            return ResponseEntity.status(401).body(ApiResponse.error("帳號或密碼錯誤"));
        } catch (DisabledException e) {
            return ResponseEntity.status(403).body(ApiResponse.error("帳號已停用"));
        }
    }
    
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String token = extractTokenFromRequest(request);
        if (token != null) {
            String username = jwtTokenUtil.getUsernameFromToken(token);
            
            // 將 Token 加入黑名單
            String redisKey = "blacklist_token:" + token;
            redisTemplate.opsForValue().set(redisKey, "true", Duration.ofHours(1));
            
            // 刪除 Refresh Token
            redisTemplate.delete("refresh_token:" + username);
        }
        
        return ResponseEntity.ok(ApiResponse.success("登出成功"));
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        try {
            String username = jwtTokenUtil.getUsernameFromToken(request.getRefreshToken());
            String storedRefreshToken = redisTemplate.opsForValue()
                    .get("refresh_token:" + username);
            
            if (!request.getRefreshToken().equals(storedRefreshToken)) {
                return ResponseEntity.status(401).body(ApiResponse.error("Refresh Token 無效"));
            }
            
            UserDetails userDetails = userService.loadUserByUsername(username);
            String newToken = jwtTokenUtil.generateToken(userDetails);
            
            RefreshTokenResponse response = RefreshTokenResponse.builder()
                    .token(newToken)
                    .expiresIn(3600)
                    .build();
            
            return ResponseEntity.ok(ApiResponse.success("Token 刷新成功", response));
            
        } catch (Exception e) {
            return ResponseEntity.status(401).body(ApiResponse.error("Token 刷新失敗"));
        }
    }
    
    @PutMapping("/password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest request,
                                          Authentication authentication) {
        try {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            userService.changePassword(userDetails.getId(), 
                    request.getOldPassword(), request.getNewPassword());
            
            return ResponseEntity.ok(ApiResponse.success("密碼修改成功"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UserInfo userInfo = buildUserInfo(userDetails);
        return ResponseEntity.ok(ApiResponse.success(userInfo));
    }
    
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
    
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
    
    private UserInfo buildUserInfo(CustomUserDetails userDetails) {
        return UserInfo.builder()
                .id(userDetails.getId())
                .username(userDetails.getUsername())
                .email(userDetails.getEmail())
                .fullName(userDetails.getFullName())
                .roles(new ArrayList<>(userDetails.getRoles()))
                .permissions(userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .filter(auth -> !auth.startsWith("ROLE_"))
                        .collect(Collectors.toList()))
                .build();
    }
}
```

### 3.5 前端應用設計

#### 專案結構

```
frontend/
├── public/                 # 靜態資源
│   ├── index.html
│   └── favicon.ico
├── src/
│   ├── components/         # 通用組件
│   │   ├── AlertTable/    # 告警表格組件
│   │   ├── RuleForm/      # 規則表單組件
│   │   ├── ChartWidget/   # 圖表組件
│   │   └── common/        # 基礎組件
│   ├── views/             # 頁面視圖
│   │   ├── Login/         # 登入頁面
│   │   ├── Dashboard/     # 儀表板
│   │   ├── AlertMonitor/  # 告警監控
│   │   ├── RuleManage/    # 規則管理
│   │   ├── SystemConfig/  # 系統配置
│   │   ├── History/       # 歷史查詢
│   │   ├── Workflow/      # 工作流程
│   │   └── UserManage/    # 使用者管理
│   ├── stores/            # Pinia 狀態管理
│   │   ├── auth.ts       # 認證狀態
│   │   ├── alert.ts      # 告警狀態
│   │   ├── rule.ts       # 規則狀態
│   │   ├── system.ts     # 系統狀態
│   │   └── user.ts       # 使用者狀態
│   ├── services/          # API 服務
│   │   ├── alert.ts      # 告警 API
│   │   ├── rule.ts       # 規則 API
│   │   ├── system.ts     # 系統 API
│   │   └── workflow.ts   # 工作流程 API
│   ├── utils/             # 工具函數
│   │   ├── request.ts    # HTTP 請求工具
│   │   ├── auth.ts       # 認證工具
│   │   ├── websocket.ts  # WebSocket 工具
│   │   └── date.ts       # 日期工具
│   ├── router/            # 路由配置
│   │   └── index.ts
│   ├── styles/            # 樣式文件
│   │   ├── global.scss   # 全局樣式
│   │   └── variables.scss# 變數定義
│   ├── types/             # TypeScript 類型定義
│   │   ├── alert.ts      # 告警類型
│   │   ├── rule.ts       # 規則類型
│   │   └── api.ts        # API 類型
│   ├── App.vue
│   └── main.ts
├── package.json
├── vite.config.ts
├── tsconfig.json
└── README.md
```

#### 核心頁面設計

##### 1. 登入頁面 (Login)
```vue
<!-- views/Login/index.vue -->
<template>
  <div class="login-container">
    <el-card class="login-card">
      <template #header>
        <div class="login-header">
          <img src="/logo.png" alt="Logo" class="logo" />
          <h2>告警分發系統</h2>
          <p>智能化企業告警管理平台</p>
        </div>
      </template>
      
      <el-form 
        ref="loginFormRef"
        :model="loginForm" 
        :rules="loginRules"
        @submit.prevent="handleLogin"
        class="login-form"
      >
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="請輸入使用者名稱"
            prefix-icon="User"
            size="large"
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="請輸入密碼"
            prefix-icon="Lock"
            size="large"
            show-password
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        
        <el-form-item>
          <el-checkbox v-model="loginForm.rememberMe">
            記住我
          </el-checkbox>
        </el-form-item>
        
        <el-form-item>
          <el-button 
            type="primary" 
            size="large"
            :loading="loading"
            class="login-button"
            @click="handleLogin"
          >
            {{ loading ? '登入中...' : '登入' }}
          </el-button>
        </el-form-item>
      </el-form>
      
      <div class="login-footer">
        <p>預設帳號：admin / Admin123456</p>
        <p>系統版本：v1.0.0</p>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import type { FormInstance, FormRules } from 'element-plus'

const router = useRouter()
const authStore = useAuthStore()

const loginFormRef = ref<FormInstance>()
const loading = ref(false)

const loginForm = reactive({
  username: 'admin',
  password: '',
  rememberMe: false
})

const loginRules: FormRules = {
  username: [
    { required: true, message: '請輸入使用者名稱', trigger: 'blur' },
    { min: 3, max: 50, message: '使用者名稱長度為 3-50 個字元', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '請輸入密碼', trigger: 'blur' },
    { min: 6, message: '密碼長度至少 6 個字元', trigger: 'blur' }
  ]
}

onMounted(() => {
  // 檢查是否已經登入
  if (authStore.isAuthenticated) {
    router.push('/dashboard')
  }
  
  // 從 localStorage 載入記住的使用者名稱
  const rememberedUsername = localStorage.getItem('remembered_username')
  if (rememberedUsername) {
    loginForm.username = rememberedUsername
    loginForm.rememberMe = true
  }
})

const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  try {
    await loginFormRef.value.validate()
    
    loading.value = true
    
    await authStore.login({
      username: loginForm.username,
      password: loginForm.password
    })
    
    // 處理記住我功能
    if (loginForm.rememberMe) {
      localStorage.setItem('remembered_username', loginForm.username)
    } else {
      localStorage.removeItem('remembered_username')
    }
    
    ElMessage.success('登入成功')
    
    // 重定向到原本要訪問的頁面或儀表板
    const redirect = router.currentRoute.value.query.redirect as string
    router.push(redirect || '/dashboard')
    
  } catch (error) {
    ElMessage.error(error.message || '登入失敗')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-card {
  width: 400px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
  border-radius: 10px;
}

.login-header {
  text-align: center;
  margin-bottom: 20px;
}

.logo {
  width: 60px;
  height: 60px;
  margin-bottom: 15px;
}

.login-header h2 {
  margin: 0 0 10px 0;
  color: #333;
  font-weight: 500;
}

.login-header p {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.login-form {
  padding: 0 20px;
}

.login-button {
  width: 100%;
  height: 45px;
  font-size: 16px;
}

.login-footer {
  text-align: center;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.login-footer p {
  margin: 5px 0;
  color: #999;
  font-size: 12px;
}
</style>
```

##### 2. 儀表板 (Dashboard)
```vue
<!-- views/Dashboard/index.vue -->
<template>
  <div class="dashboard">
    <!-- 統計卡片 -->
    <el-row :gutter="20" class="stats-cards">
      <el-col :span="6">
        <StatCard 
          title="今日告警" 
          :value="stats.todayAlerts" 
          icon="warning"
          color="warning"
        />
      </el-col>
      <el-col :span="6">
        <StatCard 
          title="處理中" 
          :value="stats.processing" 
          icon="loading"
          color="primary"
        />
      </el-col>
      <el-col :span="6">
        <StatCard 
          title="已解決" 
          :value="stats.resolved" 
          icon="success"
          color="success"
        />
      </el-col>
      <el-col :span="6">
        <StatCard 
          title="系統數量" 
          :value="stats.systemCount" 
          icon="server"
          color="info"
        />
      </el-col>
    </el-row>

    <!-- 圖表區域 -->
    <el-row :gutter="20" class="charts-section">
      <el-col :span="12">
        <ChartWidget 
          title="告警趨勢" 
          :data="alertTrendData" 
          type="line"
        />
      </el-col>
      <el-col :span="12">
        <ChartWidget 
          title="系統分布" 
          :data="systemDistributionData" 
          type="pie"
        />
      </el-col>
    </el-row>

    <!-- 最新告警 -->
    <el-card title="最新告警" class="recent-alerts">
      <AlertTable 
        :data="recentAlerts" 
        :pagination="false"
        :limit="10"
      />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useAlertStore } from '@/stores/alert'
import StatCard from '@/components/StatCard.vue'
import ChartWidget from '@/components/ChartWidget.vue'
import AlertTable from '@/components/AlertTable.vue'

const alertStore = useAlertStore()

const stats = ref({
  todayAlerts: 0,
  processing: 0,
  resolved: 0,
  systemCount: 0
})

const alertTrendData = ref([])
const systemDistributionData = ref([])
const recentAlerts = ref([])

onMounted(async () => {
  await loadDashboardData()
})

const loadDashboardData = async () => {
  try {
    const [statsData, trendData, distributionData, alertsData] = await Promise.all([
      alertStore.getStats(),
      alertStore.getTrendData(),
      alertStore.getSystemDistribution(),
      alertStore.getRecentAlerts()
    ])
    
    stats.value = statsData
    alertTrendData.value = trendData
    systemDistributionData.value = distributionData
    recentAlerts.value = alertsData
  } catch (error) {
    console.error('載入儀表板資料失敗:', error)
  }
}
</script>
```

##### 2. 告警監控 (AlertMonitor)
```vue
<!-- views/AlertMonitor/index.vue -->
<template>
  <div class="alert-monitor">
    <!-- 搜尋和篩選 -->
    <el-card class="search-card">
      <el-form inline>
        <el-form-item label="系統">
          <el-select v-model="searchForm.system" clearable>
            <el-option 
              v-for="system in systems" 
              :key="system.id"
              :label="system.name" 
              :value="system.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="等級">
          <el-select v-model="searchForm.level" clearable>
            <el-option label="高" value="HIGH" />
            <el-option label="中" value="MEDIUM" />
            <el-option label="低" value="LOW" />
          </el-select>
        </el-form-item>
        <el-form-item label="狀態">
          <el-select v-model="searchForm.status" clearable>
            <el-option label="待處理" value="PENDING" />
            <el-option label="處理中" value="PROCESSING" />
            <el-option label="已解決" value="RESOLVED" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="searchAlerts">搜尋</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 告警表格 -->
    <el-card class="alert-table-card">
      <template #header>
        <div class="card-header">
          <span>告警列表</span>
          <div>
            <el-button 
              type="success" 
              @click="batchResolve"
              :disabled="!selectedAlerts.length"
            >
              批量解決
            </el-button>
            <el-button type="primary" @click="refreshAlerts">
              <el-icon><Refresh /></el-icon>
              刷新
            </el-button>
          </div>
        </div>
      </template>
      
      <AlertTable 
        :data="alerts"
        :loading="loading"
        :pagination="pagination"
        v-model:selected="selectedAlerts"
        @page-change="handlePageChange"
        @alert-detail="showAlertDetail"
        @alert-resolve="resolveAlert"
      />
    </el-card>

    <!-- 告警詳情彈窗 -->
    <AlertDetailDialog 
      v-model="detailVisible"
      :alert="currentAlert"
      @resolve="resolveAlert"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { useAlertStore } from '@/stores/alert'
import { useSystemStore } from '@/stores/system'
import { useWebSocket } from '@/utils/websocket'
import AlertTable from '@/components/AlertTable.vue'
import AlertDetailDialog from '@/components/AlertDetailDialog.vue'

const alertStore = useAlertStore()
const systemStore = useSystemStore()

const alerts = ref([])
const systems = ref([])
const selectedAlerts = ref([])
const loading = ref(false)
const detailVisible = ref(false)
const currentAlert = ref(null)

const searchForm = reactive({
  system: '',
  level: '',
  status: ''
})

const pagination = reactive({
  current: 1,
  size: 20,
  total: 0
})

// WebSocket 即時更新
const { connect, disconnect } = useWebSocket('/alerts/realtime', {
  onMessage: (data) => {
    if (data.type === 'NEW_ALERT') {
      alerts.value.unshift(data.alert)
    } else if (data.type === 'ALERT_UPDATED') {
      updateAlertInList(data.alert)
    }
  }
})

onMounted(async () => {
  await loadData()
  connect()
})

onUnmounted(() => {
  disconnect()
})

const loadData = async () => {
  await Promise.all([
    loadAlerts(),
    loadSystems()
  ])
}

const loadAlerts = async () => {
  loading.value = true
  try {
    const result = await alertStore.getAlerts({
      ...searchForm,
      page: pagination.current,
      size: pagination.size
    })
    alerts.value = result.data
    pagination.total = result.total
  } catch (error) {
    console.error('載入告警列表失敗:', error)
  } finally {
    loading.value = false
  }
}

const loadSystems = async () => {
  systems.value = await systemStore.getSystems()
}

const searchAlerts = () => {
  pagination.current = 1
  loadAlerts()
}

const resetSearch = () => {
  Object.assign(searchForm, {
    system: '',
    level: '',
    status: ''
  })
  searchAlerts()
}

const refreshAlerts = () => {
  loadAlerts()
}

const handlePageChange = (page: number) => {
  pagination.current = page
  loadAlerts()
}

const showAlertDetail = (alert: any) => {
  currentAlert.value = alert
  detailVisible.value = true
}

const resolveAlert = async (alertId: string) => {
  try {
    await alertStore.resolveAlert(alertId)
    await loadAlerts()
    ElMessage.success('告警已解決')
  } catch (error) {
    ElMessage.error('解決告警失敗')
  }
}

const batchResolve = async () => {
  try {
    const alertIds = selectedAlerts.value.map(alert => alert.id)
    await alertStore.batchResolveAlerts(alertIds)
    await loadAlerts()
    selectedAlerts.value = []
    ElMessage.success('批量解決成功')
  } catch (error) {
    ElMessage.error('批量解決失敗')
  }
}

const updateAlertInList = (updatedAlert: any) => {
  const index = alerts.value.findIndex(alert => alert.id === updatedAlert.id)
  if (index !== -1) {
    alerts.value[index] = updatedAlert
  }
}
</script>
```

#### 狀態管理 (Pinia Store)

```typescript
// stores/auth.ts
import { defineStore } from 'pinia'
import { authApi } from '@/services/auth'
import { router } from '@/router'
import type { LoginRequest, User } from '@/types/auth'

interface AuthState {
  token: string | null
  refreshToken: string | null
  user: User | null
  isAuthenticated: boolean
  permissions: string[]
}

export const useAuthStore = defineStore('auth', {
  state: (): AuthState => ({
    token: localStorage.getItem('access_token'),
    refreshToken: localStorage.getItem('refresh_token'),
    user: null,
    isAuthenticated: false,
    permissions: []
  }),

  getters: {
    hasPermission: (state) => (permission: string) => {
      return state.permissions.includes(permission)
    },
    
    hasRole: (state) => (role: string) => {
      return state.user?.roles?.includes(role) || false
    },
    
    hasAnyRole: (state) => (roles: string[]) => {
      return roles.some(role => state.user?.roles?.includes(role))
    }
  },

  actions: {
    async login(loginData: LoginRequest) {
      try {
        const response = await authApi.login(loginData)
        const { token, refreshToken, user } = response.data
        
        this.token = token
        this.refreshToken = refreshToken
        this.user = user
        this.isAuthenticated = true
        this.permissions = user.permissions || []
        
        // 存儲到 localStorage
        localStorage.setItem('access_token', token)
        localStorage.setItem('refresh_token', refreshToken)
        localStorage.setItem('user_info', JSON.stringify(user))
        
        // 設定 axios 預設 header
        this.setAuthHeader(token)
        
      } catch (error) {
        this.clearAuth()
        throw error
      }
    },

    async logout() {
      try {
        if (this.token) {
          await authApi.logout()
        }
      } catch (error) {
        console.error('登出 API 呼叫失敗:', error)
      } finally {
        this.clearAuth()
        router.push('/login')
      }
    },

    async refreshAccessToken() {
      try {
        if (!this.refreshToken) {
          throw new Error('No refresh token available')
        }
        
        const response = await authApi.refreshToken(this.refreshToken)
        const { token } = response.data
        
        this.token = token
        localStorage.setItem('access_token', token)
        this.setAuthHeader(token)
        
        return token
      } catch (error) {
        this.clearAuth()
        router.push('/login')
        throw error
      }
    },

    async fetchUserProfile() {
      try {
        const response = await authApi.getProfile()
        this.user = response.data
        this.permissions = response.data.permissions || []
        localStorage.setItem('user_info', JSON.stringify(response.data))
      } catch (error) {
        console.error('獲取使用者資訊失敗:', error)
        this.clearAuth()
      }
    },

    async changePassword(oldPassword: string, newPassword: string) {
      await authApi.changePassword({ oldPassword, newPassword })
    },

    initializeAuth() {
      const token = localStorage.getItem('access_token')
      const userInfo = localStorage.getItem('user_info')
      
      if (token && userInfo) {
        this.token = token
        this.user = JSON.parse(userInfo)
        this.isAuthenticated = true
        this.permissions = this.user?.permissions || []
        this.setAuthHeader(token)
      }
    },

    clearAuth() {
      this.token = null
      this.refreshToken = null
      this.user = null
      this.isAuthenticated = false
      this.permissions = []
      
      localStorage.removeItem('access_token')
      localStorage.removeItem('refresh_token')
      localStorage.removeItem('user_info')
      
      this.removeAuthHeader()
    },

    setAuthHeader(token: string) {
      // 在 request.ts 中設定
      window.$axios.defaults.headers.common['Authorization'] = `Bearer ${token}`
    },

    removeAuthHeader() {
      delete window.$axios.defaults.headers.common['Authorization']
    }
  }
})
```

```typescript
// services/auth.ts
import request from '@/utils/request'
import type { LoginRequest, LoginResponse, RefreshTokenRequest, ChangePasswordRequest } from '@/types/auth'

export const authApi = {
  login: (data: LoginRequest) => 
    request.post<LoginResponse>('/auth/login', data),
  
  logout: () => 
    request.post('/auth/logout'),
  
  refreshToken: (refreshToken: string) => 
    request.post('/auth/refresh', { refreshToken }),
  
  getProfile: () => 
    request.get('/auth/profile'),
  
  changePassword: (data: ChangePasswordRequest) => 
    request.put('/auth/password', data)
}
```

```typescript
// utils/request.ts
import axios, { AxiosResponse, AxiosError } from 'axios'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import { router } from '@/router'

// 建立 axios 實例
const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL + '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 儲存到全域供其他地方使用
window.$axios = request

// 請求攔截器
request.interceptors.request.use(
  (config) => {
    const authStore = useAuthStore()
    if (authStore.token) {
      config.headers.Authorization = `Bearer ${authStore.token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 響應攔截器
request.interceptors.response.use(
  (response: AxiosResponse) => {
    const res = response.data
    
    // API 成功響應
    if (res.code === 200) {
      return res
    } else {
      ElMessage.error(res.message || '請求失敗')
      return Promise.reject(new Error(res.message || '請求失敗'))
    }
  },
  async (error: AxiosError) => {
    const authStore = useAuthStore()
    
    if (error.response?.status === 401) {
      // Token 過期，嘗試刷新
      if (authStore.refreshToken && !error.config?.url?.includes('/auth/refresh')) {
        try {
          await authStore.refreshAccessToken()
          // 重新發送原請求
          if (error.config) {
            error.config.headers.Authorization = `Bearer ${authStore.token}`
            return request(error.config)
          }
        } catch (refreshError) {
          ElMessage.error('登入已過期，請重新登入')
          authStore.clearAuth()
          router.push('/login')
        }
      } else {
        ElMessage.error('登入已過期，請重新登入')
        authStore.clearAuth()
        router.push('/login')
      }
    } else if (error.response?.status === 403) {
      ElMessage.error('沒有權限執行此操作')
    } else if (error.response?.status >= 500) {
      ElMessage.error('伺服器錯誤，請稍後再試')
    } else {
      ElMessage.error(error.response?.data?.message || '請求失敗')
    }
    
    return Promise.reject(error)
  }
)

export default request
```

```typescript
// router/index.ts
import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { ElMessage } from 'element-plus'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login/index.vue'),
    meta: { 
      requiresAuth: false,
      title: '登入'
    }
  },
  {
    path: '/',
    redirect: '/dashboard'
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: () => import('@/views/Dashboard/index.vue'),
    meta: { 
      requiresAuth: true,
      title: '儀表板',
      permissions: ['ALERT_VIEW']
    }
  },
  {
    path: '/alerts',
    name: 'AlertMonitor',
    component: () => import('@/views/AlertMonitor/index.vue'),
    meta: { 
      requiresAuth: true,
      title: '告警監控',
      permissions: ['ALERT_VIEW']
    }
  },
  {
    path: '/rules',
    name: 'RuleManage',
    component: () => import('@/views/RuleManage/index.vue'),
    meta: { 
      requiresAuth: true,
      title: '規則管理',
      permissions: ['RULE_MANAGE']
    }
  },
  {
    path: '/systems',
    name: 'SystemConfig',
    component: () => import('@/views/SystemConfig/index.vue'),
    meta: { 
      requiresAuth: true,
      title: '系統配置',
      permissions: ['SYSTEM_CONFIG']
    }
  },
  {
    path: '/users',
    name: 'UserManage',
    component: () => import('@/views/UserManage/index.vue'),
    meta: { 
      requiresAuth: true,
      title: '使用者管理',
      permissions: ['USER_MANAGE'],
      roles: ['ADMIN']
    }
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/NotFound.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守衛
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  
  // 設定頁面標題
  if (to.meta.title) {
    document.title = `${to.meta.title} - 告警分發系統`
  }
  
  // 不需要認證的頁面
  if (!to.meta.requiresAuth) {
    next()
    return
  }
  
  // 檢查是否已登入
  if (!authStore.isAuthenticated) {
    ElMessage.warning('請先登入')
    next({ path: '/login', query: { redirect: to.fullPath } })
    return
  }
  
  // 檢查角色權限
  if (to.meta.roles && Array.isArray(to.meta.roles)) {
    if (!authStore.hasAnyRole(to.meta.roles)) {
      ElMessage.error('沒有權限訪問此頁面')
      next('/dashboard')
      return
    }
  }
  
  // 檢查功能權限
  if (to.meta.permissions && Array.isArray(to.meta.permissions)) {
    const hasPermission = to.meta.permissions.some(permission => 
      authStore.hasPermission(permission)
    )
    if (!hasPermission) {
      ElMessage.error('沒有權限訪問此頁面')
      next('/dashboard')
      return
    }
  }
  
  next()
})

export { router }
export default router
```

```typescript
// types/auth.ts
export interface LoginRequest {
  username: string
  password: string
}

export interface LoginResponse {
  token: string
  refreshToken: string
  user: User
  expiresIn: number
}

export interface User {
  id: number
  username: string
  email: string
  fullName: string
  avatarUrl?: string
  roles: string[]
  permissions: string[]
  lastLoginAt?: string
}

export interface RefreshTokenRequest {
  refreshToken: string
}

export interface ChangePasswordRequest {
  oldPassword: string
  newPassword: string
}

export interface CreateUserRequest {
  username: string
  email: string
  fullName: string
  password: string
  roleIds: number[]
}
```

```typescript
// main.ts
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import 'element-plus/dist/index.css'
import 'element-plus/theme-chalk/dark/css-vars.css'

import App from './App.vue'
import router from './router'
import { useAuthStore } from './stores/auth'

const app = createApp(App)
const pinia = createPinia()

app.use(pinia)
app.use(router)
app.use(ElementPlus)

// 註冊 Element Plus 圖示
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

// 初始化認證狀態
const authStore = useAuthStore()
authStore.initializeAuth()

app.mount('#app')
```

```vue
<!-- App.vue -->
<template>
  <el-config-provider :locale="zhCn">
    <router-view />
  </el-config-provider>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()

onMounted(async () => {
  // 如果已登入，獲取最新使用者資訊
  if (authStore.isAuthenticated && authStore.token) {
    try {
      await authStore.fetchUserProfile()
    } catch (error) {
      console.error('獲取使用者資訊失敗:', error)
    }
  }
})
</script>

<style>
#app {
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', '微軟雅黑', Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

/* 全域樣式 */
.page-container {
  padding: 20px;
}

.search-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

/* 響應式設計 */
@media (max-width: 768px) {
  .page-container {
    padding: 10px;
  }
}
</style>
```

```typescript
// stores/alert.ts
import { defineStore } from 'pinia'
import { alertApi } from '@/services/alert'
import type { Alert, AlertQuery, AlertStats } from '@/types/alert'

export const useAlertStore = defineStore('alert', {
  state: () => ({
    alerts: [] as Alert[],
    stats: null as AlertStats | null,
    loading: false
  }),

  actions: {
    async getAlerts(query: AlertQuery) {
      this.loading = true
      try {
        const response = await alertApi.getAlerts(query)
        this.alerts = response.data
        return response
      } finally {
        this.loading = false
      }
    },

    async getStats() {
      const response = await alertApi.getStats()
      this.stats = response
      return response
    },

    async getTrendData() {
      return await alertApi.getTrendData()
    },

    async getSystemDistribution() {
      return await alertApi.getSystemDistribution()
    },

    async getRecentAlerts() {
      return await alertApi.getRecentAlerts()
    },

    async resolveAlert(alertId: string) {
      await alertApi.resolveAlert(alertId)
      // 更新本地狀態
      const alert = this.alerts.find(a => a.id === alertId)
      if (alert) {
        alert.status = 'RESOLVED'
      }
    },

    async batchResolveAlerts(alertIds: string[]) {
      await alertApi.batchResolveAlerts(alertIds)
      // 更新本地狀態
      alertIds.forEach(id => {
        const alert = this.alerts.find(a => a.id === id)
        if (alert) {
          alert.status = 'RESOLVED'
        }
      })
    }
  }
})
```

#### WebSocket 整合

```typescript
// utils/websocket.ts
import { ref, onUnmounted } from 'vue'

export function useWebSocket(url: string, options: {
  onMessage?: (data: any) => void
  onError?: (error: Event) => void
  onClose?: () => void
}) {
  const socket = ref<WebSocket | null>(null)
  const isConnected = ref(false)

  const connect = () => {
    const wsUrl = `ws://localhost:8080${url}`
    socket.value = new WebSocket(wsUrl)

    socket.value.onopen = () => {
      isConnected.value = true
      console.log('WebSocket 連接成功')
    }

    socket.value.onmessage = (event) => {
      try {
        const data = JSON.parse(event.data)
        options.onMessage?.(data)
      } catch (error) {
        console.error('解析 WebSocket 訊息失敗:', error)
      }
    }

    socket.value.onerror = (error) => {
      console.error('WebSocket 錯誤:', error)
      options.onError?.(error)
    }

    socket.value.onclose = () => {
      isConnected.value = false
      console.log('WebSocket 連接關閉')
      options.onClose?.()
      
      // 自動重連
      setTimeout(() => {
        if (!isConnected.value) {
          connect()
        }
      }, 5000)
    }
  }

  const disconnect = () => {
    if (socket.value && socket.value.readyState === WebSocket.OPEN) {
      socket.value.close()
    }
  }

  const send = (data: any) => {
    if (socket.value && socket.value.readyState === WebSocket.OPEN) {
      socket.value.send(JSON.stringify(data))
    }
  }

  onUnmounted(() => {
    disconnect()
  })

  return {
    connect,
    disconnect,
    send,
    isConnected
  }
}
```

#### application.yml

```yaml
spring:
  application:
    name: intelligent-alert-system
  
  datasource:
    url: jdbc:postgresql://localhost:15432/alert_system
    username: ${DB_USERNAME:postgres}
    password: ${DB_PASSWORD:}
    driver-class-name: org.postgresql.Driver
    
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: true
        
  redis:
    database: ${REDIS_DATABASE:0}
    host: ${REDIS_HOST:127.0.0.1}
    port: ${REDIS_PORT:16379}
    # 如果Redis 服務未設定密碼，需要將password刪掉或註釋，而不是設定為空字串
    # password: ${REDIS_PASSWORD:123456}
    timeout: ${REDIS_TIMEOUT:10s}
    lettuce:
      pool:
        # 連線池最大連線數 預設8 ，負數表示沒有限制
        max-active: ${REDIS_POOL_MAX_ACTIVE:8}
        # 連線池最大阻塞等待時間（使用負值表示沒有限制） 預設-1
        max-wait: ${REDIS_POOL_MAX_WAIT:-1}
        # 連線池中的最大空閒連線 預設8
        max-idle: ${REDIS_POOL_MAX_IDLE:8}
        # 連線池中的最小空閒連線 預設0
        min-idle: ${REDIS_POOL_MIN_IDLE:0}
    
  security:
    jwt:
      secret: ${JWT_SECRET:your-secret-key}
      expiration: 86400 # 24 hours

# n8n 整合配置 (基於現有部署)
n8n:
  api:
    base-url: ${N8N_API_URL:http://localhost:5678/api/v1}
    auth-type: basic # 使用基本認證
    username: ${N8N_BASIC_AUTH_USER:admin}
    password: ${N8N_BASIC_AUTH_PASSWORD:Admin+12345678}
  webhook:
    base-url: ${N8N_WEBHOOK_URL:http://localhost:5678/webhook}
  database:
    type: postgresql # 使用 PostgreSQL 資料庫
    host: localhost
    port: 15432
    database: n8ndb
    username: postgres
    password: # 無密碼連接
  timezone: Asia/Taipei
    
# AI 服務配置
openai:
  api-key: ${OPENAI_API_KEY:your-openai-key}
  model: ${OPENAI_MODEL:gpt-4}
  max-tokens: ${OPENAI_MAX_TOKENS:1000}
  temperature: ${OPENAI_TEMPERATURE:0.1}

# MQTT 配置 (基於現有 EMQX 部署)
mqtt:
  enabled: ${MQTT_ENABLED:true}
  broker-url: ${MQTT_BROKER_URL:tcp://localhost:1883}
  broker-ws-url: ${MQTT_BROKER_WS_URL:ws://localhost:8083/mqtt}
  broker-ssl-url: ${MQTT_BROKER_SSL_URL:ssl://localhost:8883}
  broker-wss-url: ${MQTT_BROKER_WSS_URL:wss://localhost:8084/mqtt}
  username: ${MQTT_USERNAME:}
  password: ${MQTT_PASSWORD:}
  client-id: ${MQTT_CLIENT_ID:intelligent-alert-system}
  clean-session: ${MQTT_CLEAN_SESSION:true}
  keep-alive: ${MQTT_KEEP_ALIVE:60}
  connection-timeout: ${MQTT_CONNECTION_TIMEOUT:30}
  # EMQX Dashboard 配置
  dashboard:
    url: ${EMQX_DASHBOARD_URL:http://localhost:18083}
    username: ${EMQX_DASHBOARD_USERNAME:admin}
    password: ${EMQX_DASHBOARD_PASSWORD:Admin+12345678}
  # Topic 配置
  topics:
    alert-events: "alerts/events/{systemName}"
    alert-processed: "alerts/processed/{systemName}"
    system-status: "system/status/{systemName}"
    workflow-trigger: "workflow/trigger/{workflowId}"
  
# 監控與日誌
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    health:
      show-details: always
      
logging:
  level:
    com.mycredit.alertsystem: ${LOG_LEVEL:INFO}
    org.springframework.security: DEBUG
  pattern:
    file: "%d{ISO8601} [%thread] %-5level %logger{36} - %msg%n"
  file:
    name: logs/alert-system.log
```

---

## 4. 部署架構

### 4.1 Docker 容器化部署

#### docker-compose.yml

基於您現有的 n8n 配置，整合後的完整部署配置：

```yaml
version: '3.8'

services:
  # 主應用服務
  alert-system:
    image: mycredit/intelligent-alert-system:latest
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=production
      - DB_HOST=localhost
      - DB_PORT=15432
      - DB_USERNAME=postgres
      - DB_PASSWORD=
      - REDIS_HOST=redis
      - REDIS_PORT=16379
      - REDIS_DATABASE=0
      - MQTT_BROKER_URL=tcp://emqx:1883
      - MQTT_BROKER_WS_URL=ws://emqx:8083/mqtt
      - EMQX_DASHBOARD_URL=http://emqx:18083
      - N8N_API_URL=http://n8n:5678/api/v1
      - N8N_BASIC_AUTH_USER=admin
      - N8N_BASIC_AUTH_PASSWORD=Admin+12345678
    depends_on:
      - postgres
      - redis
      - n8n
      - emqx
    networks:
      - alert-network
      - cctv-net
      
  # PostgreSQL 資料庫
  postgres:
    image: postgres:15-alpine
    ports:
      - "15432:5432"  # 對應您現有的 PostgreSQL port
    environment:
      - POSTGRES_DB=alert_system
      - POSTGRES_USER=postgres
      - POSTGRES_PASSWORD=  # 無密碼設定
      - POSTGRES_HOST_AUTH_METHOD=trust  # 允許無密碼連接
    volumes:
      - postgres_data:/var/lib/postgresql/data
      - ./init-scripts:/docker-entrypoint-initdb.d
    networks:
      - alert-network
      
  # Redis 快取
  redis:
    image: redis:7-alpine
    ports:
      - "16379:6379"  # 對應您現有的 Redis port
    volumes:
      - redis_data:/data
    # Redis 無密碼配置，如需密碼可取消下列註解
    # command: redis-server --requirepass 123456
    networks:
      - alert-network
      
  # n8n 工作流程引擎 (基於現有配置)
  n8n:
    image: n8nio/n8n:latest
    container_name: n8n
    restart: unless-stopped
    ports:
      - "5678:5678"
    environment:
      # 基本設定 (沿用現有配置)
      - N8N_BASIC_AUTH_ACTIVE=true
      - N8N_BASIC_AUTH_USER=admin
      - N8N_BASIC_AUTH_PASSWORD=Admin+12345678
      
      # 網路設定
      - N8N_HOST=n8n
      - N8N_PORT=5678
      - N8N_PROTOCOL=http
      
      # 時區設定
      - TZ=Asia/Taipei
      
      # API 存取設定
      - N8N_API_ENABLED=true
      - N8N_DISABLE_UI=false
      
      # Webhook 設定
      - WEBHOOK_URL=http://n8n:5678/
      
    volumes:
      # 持久化資料 (沿用現有配置)
      - n8n_data:/home/node/.n8n
    networks:
      - alert-network
      
  # EMQX MQTT Broker (已部署)
  emqx:
    image: emqx/emqx:5.0.0
    container_name: emqx
    restart: unless-stopped
    ports:
      - "1883:1883"    # MQTT TCP
      - "8883:8883"    # MQTT SSL
      - "8083:8083"    # MQTT WebSocket
      - "8084:8084"    # MQTT WebSocket SSL
      - "8081:8081"    # HTTP Management API
      - "18083:18083"  # EMQX Dashboard
    environment:
      - EMQX_NAME=emqx
      - EMQX_HOST=127.0.0.1
      - EMQX_DASHBOARD__DEFAULT_USERNAME=admin
      - EMQX_DASHBOARD__DEFAULT_PASSWORD=Admin+12345678
      - EMQX_NODE__NAME=emqx@127.0.0.1
      - TZ=Asia/Taipei
    volumes:
      - emqx_data:/opt/emqx/data
      - emqx_log:/opt/emqx/log
      - emqx_etc:/opt/emqx/etc
    networks:
      - cctv-net

volumes:
  postgres_data:
  redis_data:
  n8n_data:
  emqx_data:
  emqx_log:
  emqx_etc:

networks:
  alert-network:
    driver: bridge
  cctv-net:
    external: true
```

> **注意事項**：
> - 您的 n8n 資料庫使用 PostgreSQL (localhost:15432/n8ndb) 搭配 postgres 使用者無密碼連接
> - Redis 快取服務運行在 port 16379，目前未設定密碼
> - EMQX MQTT Broker 已部署並運行在多個端口 (TCP: 1883, WebSocket: 8083, Dashboard: 18083)
> - EMQX Dashboard 認證: admin/Admin+12345678
> - n8n 基本認證帳密已設定為 `admin/Admin+12345678`
> - 時區已設定為 `Asia/Taipei`
> - 已啟用 API 存取功能供 Spring Boot 應用程式呼叫
> - 主應用資料庫建議使用相同的 PostgreSQL 實例但不同的資料庫名稱 (alert_system)
> - EMQX 使用現有的 cctv-net 網路進行容器間通信

### 4.2 Kubernetes 部署

#### deployment.yaml

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: alert-system
  labels:
    app: alert-system
spec:
  replicas: 3
  selector:
    matchLabels:
      app: alert-system
  template:
    metadata:
      labels:
        app: alert-system
    spec:
      containers:
      - name: alert-system
        image: mycredit/intelligent-alert-system:latest
        ports:
        - containerPort: 8080
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "production"
        - name: DB_HOST
          value: "postgres-service"
        - name: REDIS_HOST
          value: "redis-service"
        - name: N8N_API_URL
          value: "http://n8n-service:5678/api/v1"
        resources:
          requests:
            memory: "512Mi"
            cpu: "500m"
          limits:
            memory: "1Gi"
            cpu: "1000m"
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 5
          periodSeconds: 5
---
apiVersion: v1
kind: Service
metadata:
  name: alert-system-service
spec:
  selector:
    app: alert-system
  ports:
  - port: 80
    targetPort: 8080
  type: LoadBalancer
```

---

## 5. 測試策略

### 5.1 單元測試

```java
@ExtendWith(MockitoExtension.class)
class AlertProcessingServiceTest {
    
    @Mock
    private AlertRuleService ruleService;
    
    @Mock
    private AIAnalysisService aiService;
    
    @Mock
    private N8nIntegrationService n8nService;
    
    @InjectMocks
    private AlertProcessingService alertProcessingService;
    
    @Test
    void shouldProcessAlertSuccessfully() {
        // Given
        String systemTopic = "system-a";
        AlertEvent event = AlertEvent.builder()
            .alertType("CPU_HIGH")
            .content("CPU 使用率達到 95%")
            .timestamp(Instant.now())
            .build();
            
        AlertRule rule = AlertRule.builder()
            .alertType("CPU_HIGH")
            .priorityLevel("HIGH")
            .workflowId("workflow-123")
            .build();
            
        when(ruleService.findMatchingRules(any(), any()))
            .thenReturn(List.of(rule));
        when(n8nService.triggerWorkflow(any(), any()))
            .thenReturn(N8nTriggerResult.success("execution-123"));
            
        // When
        AlertResponse response = alertProcessingService.processAlert(systemTopic, event);
        
        // Then
        assertThat(response.isSuccess()).isTrue();
        verify(n8nService).triggerWorkflow("workflow-123", any());
    }
}
```

### 5.2 整合測試

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
class AlertControllerIntegrationTest {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @MockBean
    private N8nIntegrationService n8nService;
    
    @Test
    void shouldReceiveAndProcessAlert() {
        // Given
        AlertEvent alertEvent = AlertEvent.builder()
            .alertType("DATABASE_ERROR")
            .content("資料庫連線失敗")
            .severity("HIGH")
            .timestamp(Instant.now())
            .build();
            
        when(n8nService.triggerWorkflow(any(), any()))
            .thenReturn(N8nTriggerResult.success("execution-456"));
            
        // When
        ResponseEntity<AlertResponse> response = restTemplate.postForEntity(
            "/api/v1/alerts/webhook/database-system",
            alertEvent,
            AlertResponse.class
        );
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().isSuccess()).isTrue();
    }
}
```

---

## 6. 監控與運維

### 6.1 應用監控

#### Prometheus 指標配置

```java
@Component
public class AlertMetrics {
    
    private final Counter alertsReceived = Counter.builder("alerts_received_total")
        .description("告警接收總數")
        .tag("system", "unknown")
        .register(Metrics.globalRegistry);
        
    private final Timer alertProcessingTime = Timer.builder("alert_processing_duration_seconds")
        .description("告警處理時間")
        .register(Metrics.globalRegistry);
        
    private final Gauge activeWorkflows = Gauge.builder("n8n_active_workflows")
        .description("活躍的 n8n 工作流程數量")
        .register(Metrics.globalRegistry);
        
    public void recordAlertReceived(String systemName) {
        alertsReceived.increment(Tags.of("system", systemName));
    }
    
    public Timer.Sample startProcessingTimer() {
        return Timer.start(Metrics.globalRegistry);
    }
    
    public void recordProcessingTime(Timer.Sample sample) {
        sample.stop(alertProcessingTime);
    }
}
```

### 6.2 日誌管理

#### Logback 配置

```xml
<configuration>
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder class="net.logstash.logback.encoder.LoggingEventCompositeJsonEncoder">
            <providers>
                <timestamp/>
                <logLevel/>
                <loggerName/>
                <message/>
                <mdc/>
                <stackTrace/>
            </providers>
        </encoder>
    </appender>
    
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>logs/alert-system.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>logs/alert-system.%d{yyyy-MM-dd}.%i.gz</fileNamePattern>
            <maxFileSize>100MB</maxFileSize>
            <maxHistory>30</maxHistory>
        </rollingPolicy>
        <encoder class="net.logstash.logback.encoder.LoggingEventCompositeJsonEncoder">
            <providers>
                <timestamp/>
                <logLevel/>
                <loggerName/>
                <message/>
                <mdc/>
                <stackTrace/>
            </providers>
        </encoder>
    </appender>
    
    <logger name="com.mycredit.alertsystem" level="INFO"/>
    <logger name="org.springframework.security" level="DEBUG"/>
    
    <root level="INFO">
        <appender-ref ref="STDOUT"/>
        <appender-ref ref="FILE"/>
    </root>
</configuration>
```

---

## 7. 安全考量

### 7.1 API 安全

#### JWT 認證配置

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/api/v1/alerts/webhook/**").permitAll()
                .requestMatchers("/actuator/health").permitAll()
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer().jwt()
            .and()
            .build();
    }
    
    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withSecretKey(getSecretKey())
            .macAlgorithm(MacAlgorithm.HS256)
            .build();
    }
}
```

### 7.2 資料加密

```java
@Service
public class EncryptionService {
    
    private final AESUtil aesUtil;
    
    public String encryptSensitiveData(String data) {
        return aesUtil.encrypt(data);
    }
    
    public String decryptSensitiveData(String encryptedData) {
        return aesUtil.decrypt(encryptedData);
    }
}
```

---

## 8. 開發計劃

### 8.1 里程碑規劃

#### Phase 1: 基礎架構 (4 週)
- [ ] Spring Boot 3 專案建置
- [ ] 資料庫設計與建置
- [ ] 基本 API 開發
- [ ] n8n 基礎整合

#### Phase 2: 核心功能 (6 週)
- [ ] 告警接收與處理
- [ ] 規則引擎開發
- [ ] AI 分析整合
- [ ] 通知分流機制

#### Phase 3: 進階功能 (4 週)
- [ ] 動態 workflow 生成
- [ ] 歷史查詢與統計
- [ ] 監控與告警
- [ ] 性能優化

#### Phase 4: 上線準備 (2 週)
- [ ] 安全性加固
- [ ] 部署配置
- [ ] 文檔完善
- [ ] 使用者培訓

### 8.2 技術風險與緩解策略

| 風險項目 | 風險等級 | 緩解策略 |
|---------|---------|----------|
| n8n API 相容性 | 中 | 建立抽象層，支援多版本相容；基本認證方式穩定性高 |
| PostgreSQL 連接安全性 | 中 | 考慮為生產環境設定密碼；目前無密碼適合開發環境 |
| Redis 無密碼風險 | 中 | 生產環境建議設定密碼；目前配置適合開發環境 |
| AI 服務可用性 | 中 | 實作降級機制，支援離線處理 |
| 高併發性能 | 高 | 實作快取、非同步處理、負載均衡 |
| 資料一致性 | 中 | 實作分散式事務，確保資料一致性 |
| n8n 基本認證安全性 | 低 | 定期更新密碼，考慮升級至 API Token 認證 |

### 8.3 現有 n8n 配置整合建議

基於您目前的 n8n 配置，建議以下整合步驟：

1. **使用現有 PostgreSQL**: 利用現有的 PostgreSQL 實例 (localhost:15432/n8ndb)
2. **資料庫分離**: 為主應用建立獨立的資料庫 (alert_system) 在同一 PostgreSQL 實例中
3. **API 存取設定**: 確保 n8n 的 API 功能已啟用
4. **網路連接**: 確保 Spring Boot 應用程式能夠存取 n8n 的 5678 port
5. **認證整合**: 使用現有的 `admin/Admin+12345678` 進行 API 認證
6. **Webhook 配置**: 利用 n8n 的 Webhook 功能接收告警事件

### 8.4 資料庫設定建議

#### 建立主應用資料庫
```sql
-- 連接到現有 PostgreSQL 實例 (localhost:15432)
-- 使用者: postgres (無密碼)

-- 建立主應用資料庫
CREATE DATABASE alert_system;

-- 切換到主應用資料庫
\c alert_system;

-- 執行前面定義的資料表建立 SQL
-- (systems, alert_rules, alert_history, n8n_workflows)
```

#### 連接字串範例
```yaml
# 主應用連接設定
spring.datasource.url: jdbc:postgresql://localhost:15432/alert_system
spring.datasource.username: postgres
spring.datasource.password: 

# Redis 連接設定
spring.redis.host: 127.0.0.1
spring.redis.port: 16379
spring.redis.database: 0
# spring.redis.password: # 目前無密碼

# MQTT 連接設定 (EMQX)
mqtt.broker-url: tcp://localhost:1883
mqtt.broker-ws-url: ws://localhost:8083/mqtt
mqtt.client-id: intelligent-alert-system
# mqtt.username: # 目前允許匿名連接
# mqtt.password: # 目前允許匿名連接

# EMQX Dashboard
emqx.dashboard.url: http://localhost:18083
emqx.dashboard.username: admin
emqx.dashboard.password: Admin+12345678

# n8n 連接資訊 (供參考)
# n8n 使用: jdbc:postgresql://localhost:15432/n8ndb
```

### 8.5 升級路徑規劃

#### 短期 (1-2 個月)
- [ ] 整合現有 n8n 實例與 PostgreSQL
- [ ] 在同一 PostgreSQL 實例中建立主應用資料庫
- [ ] 實作基本的 API 認證
- [ ] 建立簡單的 workflow 範本

#### 中期 (3-6 個月)
- [ ] 實作更複雜的 workflow 自動生成
- [ ] 加強監控與日誌功能
- [ ] 考慮為生產環境加強資料庫安全性 (設定密碼)
- [ ] 為 Redis 設定密碼保護

#### 長期 (6+ 個月)
- [ ] 考慮升級至 API Token 認證
- [ ] 實作 Redis 叢集部署提升可用性
- [ ] 實作 n8n 叢集部署 (如有需要)
- [ ] 整合更多 AI 功能

---

## 9. 總結

本系統設計整合了 Spring Boot 3、n8n、AI 分析等技術，實現了智能化的企業告警分發系統。基於您現有的 n8n Docker 部署，主要特點包括：

1. **模組化設計**：各模組職責清晰，易於維護和擴展
2. **智能分析**：整合 AI 提升告警處理的智能化程度
3. **現有資源整合**：充分利用您已部署的 n8n 實例 (localhost:5678)
4. **基本認證整合**：基於現有的 admin/Admin+12345678 認證配置
5. **彈性架構**：支援從 SQLite 到 PostgreSQL 的升級路徑
6. **高可用性**：支援水平擴展和容錯機制
7. **安全可靠**：完善的安全措施和監控機制
8. **易於運維**：提供完整的監控、日誌和部署方案

### 現有基礎設施整合優勢

- **資料庫共享**: 利用現有的 PostgreSQL 實例 (localhost:15432)，節省資源
- **快取服務**: 整合現有的 Redis 實例 (127.0.0.1:16379) 提升效能  
- **MQTT 訊息代理**: 使用已部署的 EMQX 5.0 實現可靠的訊息傳遞和事件驅動架構
- **多協議支援**: EMQX 支援 TCP、WebSocket、SSL 等多種協議，適應不同場景需求
- **管理介面**: EMQX Dashboard (localhost:18083) 提供視覺化監控和管理功能
- **即開即用**: 基於您已驗證的 n8n 和 EMQX 部署，快速啟動專案
- **成本效益**: 無需重新部署基礎設施，充分利用現有服務
- **穩定可靠**: 基於您已驗證的配置參數和服務連接
- **無縫整合**: PostgreSQL、Redis 和 EMQX 提供完整的資料存儲、快取和訊息傳遞解決方案

該系統能夠有效提升企業告警處理效率，降低運維成本，提高系統穩定性，同時充分利用您現有的 n8n 基礎設施。

---

## 10. 前端專案配置

### 10.1 技術棧配置

#### 專案初始化

```bash
# 建立專案
npm create vue@latest alert-management-frontend
cd alert-management-frontend

# 安裝依賴
npm install
npm install element-plus @element-plus/icons-vue
npm install echarts vue-echarts
npm install @vueuse/core
npm install axios
```

#### Vite 配置

```typescript
// vite.config.ts
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src')
    }
  },
  server: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, '')
      },
      '/ws': {
        target: 'ws://localhost:8080',
        ws: true
      }
    }
  },
  build: {
    outDir: 'dist',
    assetsDir: 'assets',
    rollupOptions: {
      output: {
        manualChunks: {
          vendor: ['vue', 'vue-router', 'pinia'],
          element: ['element-plus'],
          echarts: ['echarts', 'vue-echarts']
        }
      }
    }
  }
})
```

#### TypeScript 配置

```json
// tsconfig.json
{
  "compilerOptions": {
    "target": "ES2020",
    "useDefineForClassFields": true,
    "lib": ["ES2020", "DOM", "DOM.Iterable"],
    "module": "ESNext",
    "skipLibCheck": true,
    "moduleResolution": "bundler",
    "allowImportingTsExtensions": true,
    "resolveJsonModule": true,
    "isolatedModules": true,
    "noEmit": true,
    "jsx": "preserve",
    "strict": true,
    "noUnusedLocals": true,
    "noUnusedParameters": true,
    "noFallthroughCasesInSwitch": true,
    "baseUrl": ".",
    "paths": {
      "@/*": ["src/*"]
    }
  },
  "include": ["src/**/*.ts", "src/**/*.d.ts", "src/**/*.tsx", "src/**/*.vue"],
  "references": [{ "path": "./tsconfig.node.json" }]
}
```

### 10.2 專案結構

```
frontend/
├── public/                 # 靜態資源
│   ├── index.html
│   └── favicon.ico
├── src/
│   ├── components/         # 通用組件
│   │   ├── AlertTable/    # 告警表格組件
│   │   ├── RuleForm/      # 規則表單組件
│   │   ├── ChartWidget/   # 圖表組件
│   │   ├── StatCard/      # 統計卡片組件
│   │   └── common/        # 基礎組件
│   ├── views/             # 頁面視圖
│   │   ├── Dashboard/     # 儀表板
│   │   ├── AlertMonitor/  # 告警監控
│   │   ├── RuleManage/    # 規則管理
│   │   ├── SystemConfig/  # 系統配置
│   │   ├── History/       # 歷史查詢
│   │   └── Workflow/      # 工作流程
│   ├── stores/            # Pinia 狀態管理
│   │   ├── alert.ts      # 告警狀態
│   │   ├── rule.ts       # 規則狀態
│   │   ├── system.ts     # 系統狀態
│   │   └── user.ts       # 使用者狀態
│   ├── services/          # API 服務
│   │   ├── alert.ts      # 告警 API
│   │   ├── rule.ts       # 規則 API
│   │   ├── system.ts     # 系統 API
│   │   └── workflow.ts   # 工作流程 API
│   ├── utils/             # 工具函數
│   │   ├── request.ts    # HTTP 請求工具
│   │   ├── auth.ts       # 認證工具
│   │   ├── websocket.ts  # WebSocket 工具
│   │   └── date.ts       # 日期工具
│   ├── router/            # 路由配置
│   │   └── index.ts
│   ├── styles/            # 樣式文件
│   │   ├── global.scss   # 全局樣式
│   │   └── variables.scss# 變數定義
│   ├── types/             # TypeScript 類型定義
│   │   ├── alert.ts      # 告警類型
│   │   ├── rule.ts       # 規則類型
│   │   └── api.ts        # API 類型
│   ├── App.vue
│   └── main.ts
├── package.json
├── vite.config.ts
├── tsconfig.json
└── README.md
```

### 10.3 環境配置

```env
# .env.development
VITE_APP_TITLE=告警分發系統
VITE_API_BASE_URL=http://localhost:8080
VITE_WS_BASE_URL=ws://localhost:8080
VITE_APP_ENV=development

# .env.production
VITE_APP_TITLE=告警分發系統
VITE_API_BASE_URL=https://api.yourdomain.com
VITE_WS_BASE_URL=wss://api.yourdomain.com
VITE_APP_ENV=production
```

### 10.4 部署配置

#### Docker 配置

```dockerfile
# frontend/Dockerfile
FROM node:18-alpine as build-stage

WORKDIR /app
COPY package*.json ./
RUN npm ci --only=production

COPY . .
RUN npm run build

# 生產階段
FROM nginx:alpine as production-stage

COPY --from=build-stage /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/nginx.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

#### Nginx 配置

```nginx
# nginx.conf
events {
    worker_connections 1024;
}

http {
    include       /etc/nginx/mime.types;
    default_type  application/octet-stream;
    
    server {
        listen 80;
        server_name localhost;
        root /usr/share/nginx/html;
        index index.html;
        
        # SPA 路由支援
        location / {
            try_files $uri $uri/ /index.html;
        }
        
        # API 代理
        location /api/ {
            proxy_pass http://backend:8080/;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
        }
        
        # WebSocket 代理
        location /ws/ {
            proxy_pass http://backend:8080/;
            proxy_http_version 1.1;
            proxy_set_header Upgrade $http_upgrade;
            proxy_set_header Connection "upgrade";
            proxy_set_header Host $host;
        }
    }
}
```

#### Docker Compose 整合

```yaml
# docker-compose.yml
version: '3.8'

services:
  frontend:
    build:
      context: ./frontend
      dockerfile: Dockerfile
    ports:
      - "3000:80"
    depends_on:
      - backend
    networks:
      - cctv-net

  backend:
    build:
      context: ./backend
      dockerfile: Dockerfile
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=production
    depends_on:
      - postgres
      - redis
      - emqx
    networks:
      - cctv-net

networks:
  cctv-net:
    external: true
```

### 10.5 開發流程

#### 本地開發

```bash
# 後端啟動
cd backend
./mvnw spring-boot:run

# 前端啟動
cd frontend
npm run dev
```

#### 生產部署

```bash
# 建構並啟動所有服務
docker-compose up -d

# 檢查服務狀態
docker-compose ps

# 查看日誌
docker-compose logs -f frontend
docker-compose logs -f backend
```

#### 開發工具推薦

1. **VS Code 擴展**：
   - Vue Language Features (Volar)
   - TypeScript Vue Plugin (Volar)
   - ESLint
   - Prettier

2. **瀏覽器工具**：
   - Vue.js devtools
   - Vue DevTools for Pinia

3. **測試工具**：
   - Vitest (單元測試)
   - Cypress (E2E 測試)

---

## 11. 專案管理建議

### 11.1 開發優先級

#### 第一階段 (MVP)
1. **使用者認證系統**：登入/登出、JWT Token、權限控制
2. **Spring Boot 後端 API**：認證 API、基礎 CRUD API
3. **Vue 3 前端基礎框架**：登入頁面、路由守衛、狀態管理
4. **基本告警監控功能**：告警接收、列表展示
5. **n8n 整合 API**：基礎 workflow 操作

#### 第二階段 (功能完善)
1. **規則管理介面**：視覺化規則配置、條件設定
2. **即時 WebSocket 通信**：即時告警推送、狀態更新
3. **儀表板圖表展示**：統計圖表、監控概覽
4. **系統配置管理**：系統註冊、Topic 管理
5. **使用者管理功能**：使用者 CRUD、角色分配

#### 第三階段 (優化增強)
1. **AI 輔助分析**：智能分類、優先級判斷
2. **歷史查詢統計**：數據分析、報表生成
3. **效能優化**：快取策略、查詢優化
4. **安全性增強**：審計日誌、安全掃描

### 11.2 技術選型總結

| 層級 | 技術選型 | 理由 |
|------|----------|------|
| 前端框架 | Vue 3 + TypeScript | 響應式設計、類型安全、生態豐富 |
| UI 組件庫 | Element Plus | 豐富組件、定制性強、中文友好 |
| 狀態管理 | Pinia | Vue 3 官方推薦、TypeScript 友好 |
| 圖表組件 | ECharts | 功能強大、可定制性強 |
| 建構工具 | Vite | 快速開發、優化打包 |
| 後端框架 | Spring Boot 3 | 企業級、生態成熟 |
| 認證授權 | Spring Security + JWT | 安全可靠、標準化 |
| 資料庫 | PostgreSQL | 關聯式資料庫、JSON 支援 |
| 快取 | Redis | 高效能、豐富資料結構 |
| 訊息代理 | EMQX | 高性能 MQTT、多協議支援 |
| 工作流引擎 | n8n | 視覺化配置、整合豐富 |

該前端設計提供了完整的使用者認證和告警管理介面，與後端 API 無縫整合，支援：

✅ **安全認證**：登入/登出、JWT Token、權限控制、路由守衛  
✅ **即時告警監控**：告警接收、列表展示、即時推送  
✅ **規則管理**：視覺化配置、條件設定、通知目標  
✅ **使用者管理**：使用者 CRUD、角色分配、權限控制  
✅ **系統配置**：系統註冊、Topic 管理、連接設定  
✅ **歷史查詢**：數據分析、統計報表、圖表展示  

為企業級告警分發系統提供了專業、安全、易用的管理控制台。

---

## 12. 系統設計缺失分析與改進建議

### 12.1 🚨 POC 階段重點設計缺失

#### A. 基礎監控與除錯支援不足

**問題**：
- 缺少基本的健康檢查端點
- 沒有結構化日誌輸出
- 缺少開發階段的除錯工具
- API 文件自動生成缺失

**影響**：開發和測試階段難以快速定位問題，影響開發效率

**改進建議**：
```yaml
# POC 階段基礎監控
basic_monitoring:
  health_checks:
    - "/actuator/health" 
    - "/actuator/info"
    - "/actuator/metrics"
  
  logging:
    level: DEBUG
    format: JSON
    output: "console + file"
  
  api_documentation:
    swagger_ui: true
    openapi_spec: true
    
  development_tools:
    hot_reload: true
    dev_profiles: true
```

#### B. 前端開發效率與使用者體驗

**問題**：
- 缺少載入狀態和錯誤處理的統一方案
- 沒有考慮響應式設計
- 缺少基本的表單驗證
- 沒有考慮 SEO 和無障礙設計

**改進建議**：
```vue
<!-- 統一載入和錯誤處理 -->
<template>
  <div class="page-container">
    <el-loading 
      v-loading="loading" 
      element-loading-text="載入中..."
      element-loading-spinner="el-icon-loading"
    >
      <el-alert 
        v-if="error" 
        :title="error" 
        type="error" 
        :closable="false"
        show-icon
      />
      
      <!-- 主要內容 -->
      <div v-else>
        <slot />
      </div>
    </el-loading>
  </div>
</template>

<!-- 響應式設計 -->
<style>
@media (max-width: 768px) {
  .search-form .el-form-item {
    width: 100%;
  }
  
  .action-buttons {
    flex-direction: column;
  }
}
</style>
```

#### C. POC 階段基礎安全缺失

**問題**：
- 開發環境預設密碼使用風險
- 缺少基本的輸入清理和驗證
- 沒有基礎的 CORS 配置
- 敏感資訊可能暴露在日誌中

**影響**：即使是 POC 也可能因基礎安全問題導致資料洩露

**改進建議**：
```yaml
# POC 階段基礎安全
basic_security:
  input_validation:
    - 資料長度限制
    - 特殊字元過濾
    - SQL 注入基礎防護
  
  environment_config:
    - 環境變數管理敏感資訊
    - 開發/測試環境分離
    - 預設密碼提醒機制
  
  cors_config:
    allowed_origins: ["http://localhost:3000"]
    allowed_methods: ["GET", "POST", "PUT", "DELETE"]
    
  log_security:
    - 密碼不出現在日誌
    - Token 部分遮罩
    - 敏感資料脫敏
```

### 12.2 📊 POC 階段核心技術問題

#### A. 資料存取與效能基礎優化

**問題**：
- 缺少基本的資料庫連接池配置
- 沒有考慮 N+1 查詢問題
- 基礎索引設計不完整
- 缺少簡單的快取策略

**改進建議**：
```yaml
# POC 階段基礎優化
database_basics:
  connection_pool:
    max_size: 10
    min_idle: 5
    connection_timeout: 30s
  
  jpa_config:
    show_sql: true
    hibernate_ddl: update
    batch_size: 20
  
  basic_indexes:
    - alert_history(system_id, created_at)
    - users(username)
    - alert_rules(system_id, status)

redis_basics:
  usage:
    session: 2h
    api_cache: 5min
    config_cache: 30min
```

#### B. 前端開發效率與使用者體驗

**問題**：
- 缺少載入狀態和錯誤處理的統一方案
- 沒有考慮響應式設計
- 缺少基本的表單驗證
- 沒有考慮 SEO 和無障礙設計

**改進建議**：
```vue
<!-- 統一載入和錯誤處理 -->
<template>
  <div class="page-container">
    <el-loading 
      v-loading="loading" 
      element-loading-text="載入中..."
      element-loading-spinner="el-icon-loading"
    >
      <el-alert 
        v-if="error" 
        :title="error" 
        type="error" 
        :closable="false"
        show-icon
      />
      
      <!-- 主要內容 -->
      <div v-else>
        <slot />
      </div>
    </el-loading>
  </div>
</template>

<!-- 響應式設計 -->
<style>
@media (max-width: 768px) {
  .search-form .el-form-item {
    width: 100%;
  }
  
  .action-buttons {
    flex-direction: column;
  }
}
</style>
```

### 12.3 🔄 POC 階段業務邏輯重點

#### A. 基礎告警處理邏輯

**問題**：
- 告警資料格式驗證不足
- 缺少基本的錯誤處理
- 沒有考慮告警狀態管理
- 通知失敗的基本處理缺失

**改進建議**：
```java
@Service
public class AlertProcessingService {
    
    // 基礎告警驗證
    public ValidationResult validateAlert(AlertRequest request) {
        if (StringUtils.isBlank(request.getSystemId())) {
            return ValidationResult.error("系統ID不能為空");
        }
        if (StringUtils.isBlank(request.getMessage())) {
            return ValidationResult.error("告警訊息不能為空");
        }
        return ValidationResult.success();
    }
    
    // 簡單的狀態管理
    public void updateAlertStatus(Long alertId, AlertStatus status) {
        Alert alert = alertRepository.findById(alertId)
            .orElseThrow(() -> new AlertNotFoundException("告警不存在"));
        
        alert.setStatus(status);
        alert.setUpdatedAt(LocalDateTime.now());
        alertRepository.save(alert);
        
        // 發送狀態變更事件
        eventPublisher.publishEvent(new AlertStatusChangedEvent(alert));
    }
}
```

#### B. n8n 整合的基礎錯誤處理

**問題**：
- n8n API 呼叫失敗處理不足
- Workflow 執行狀態追蹤缺失
- 缺少基本的重試機制
- 錯誤日誌記錄不完整

**改進建議**：
```java
@Service
public class N8nIntegrationService {
    
    private final RestTemplate restTemplate;
    private final RetryTemplate retryTemplate;
    
    public WorkflowResult executeWorkflow(String workflowId, Map<String, Object> data) {
        try {
            return retryTemplate.execute(context -> {
                String url = String.format("%s/webhook/%s", n8nBaseUrl, workflowId);
                
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.setBasicAuth(n8nUsername, n8nPassword);
                
                HttpEntity<Map<String, Object>> request = new HttpEntity<>(data, headers);
                
                ResponseEntity<String> response = restTemplate.exchange(
                    url, HttpMethod.POST, request, String.class);
                
                if (response.getStatusCode().is2xxSuccessful()) {
                    log.info("Workflow {} 執行成功", workflowId);
                    return WorkflowResult.success(response.getBody());
                } else {
                    log.warn("Workflow {} 執行失敗，狀態碼: {}", workflowId, response.getStatusCode());
                    return WorkflowResult.failed("HTTP " + response.getStatusCode());
                }
            });
        } catch (Exception e) {
            log.error("Workflow {} 執行異常", workflowId, e);
            return WorkflowResult.error("執行異常: " + e.getMessage());
        }
    }
}
```

### 12.4 📋 POC 階段開發效率問題

#### A. 基礎測試與除錯支援

**問題**：
- 缺少基本的單元測試範例
- 沒有 API 測試集合
- 除錯配置不完整
- 缺少開發環境快速啟動指南

**改進建議**：
```yaml
# POC 階段測試策略
basic_testing:
  unit_tests:
    coverage_target: 60%  # POC 階段不需要太高
    focus_areas:
      - 業務邏輯核心方法
      - API 控制器基本功能
      - 資料驗證邏輯
  
  integration_tests:
    database_tests: "基本 CRUD 操作"
    api_tests: "主要流程端到端"
    
  manual_testing:
    postman_collection: true
    test_data_scripts: true

# 除錯配置
debug_config:
  spring_profiles: development
  log_level: DEBUG
  hot_reload: true
  h2_console: true  # 開發階段可用記憶體資料庫
```

#### B. 開發環境配置與部署簡化

**問題**：
- Docker 開發環境配置複雜
- 缺少一鍵啟動腳本
- 環境變數管理混亂
- 沒有開發資料初始化腳本

**改進建議**：
```yaml
# 簡化的開發環境
dev_environment:
  quick_start:
    script: "./start-dev.sh"
    includes:
      - PostgreSQL (Docker)
      - Redis (Docker)  
      - EMQX (現有)
      - n8n (現有)
      - Backend (本地)
      - Frontend (本地)
  
  environment_config:
    .env.development: "統一環境變數"
    default_data: "初始化測試資料"
    
  dev_tools:
    api_docs: "http://localhost:8080/swagger-ui"
    database_ui: "pgAdmin4"
    redis_ui: "RedisInsight"
```

```bash
#!/bin/bash
# start-dev.sh - POC 一鍵啟動腳本
echo "🚀 啟動 POC 開發環境..."

# 啟動基礎服務
docker-compose -f docker/dev-compose.yml up -d

# 等待服務啟動
echo "⏳ 等待服務啟動..."
sleep 10

# 初始化資料庫
echo "📊 初始化資料庫..."
./scripts/init-db.sh

# 啟動後端
echo "🔧 啟動後端服務..."
cd backend && ./mvnw spring-boot:run &

# 啟動前端
echo "🎨 啟動前端服務..."
cd frontend && npm run dev &

echo "✅ 開發環境啟動完成!"
echo "📝 API 文件: http://localhost:8080/swagger-ui"
echo "🖥️  前端界面: http://localhost:3000"
```

### 12.5 📈 POC 階段實用性考量

#### A. 功能優先級與範圍控制

**問題**：
- 功能範圍過於龐大，不適合 POC
- 缺少 MVP (最小可行產品) 定義
- AI 功能複雜度過高
- 沒有明確的成功評估標準

**改進建議**：
```yaml
# POC MVP 功能範圍
mvp_scope:
  core_features:
    - 基礎告警接收 (MQTT/HTTP)
    - 簡單規則匹配
    - 單一通知通道 (Email)
    - 基礎前端展示
    - 使用者登入
  
  excluded_features:
    - AI 智能分析 (第二階段)
    - 複雜工作流程 (利用現有 n8n)
    - 多區域部署
    - 高級監控告警
  
  success_criteria:
    - 告警端到端流程打通
    - 基本的 Web 管理界面
    - 與現有 n8n 整合成功
    - 單機部署可運行
```

#### B. 現有基礎設施整合重點

**問題**：
- 與現有 n8n 整合方案不夠具體
- EMQX 使用場景定義模糊
- PostgreSQL 資料庫設計與現有 n8n 隔離不清楚
- Redis 用途重疊可能性

**改進建議**：
```yaml
# 基礎設施整合策略
infrastructure_integration:
  n8n_integration:
    approach: "API 呼叫，不修改現有配置"
    authentication: "基本認證 (admin/Admin+12345678)"
    usage_scenarios:
      - "通知發送 workflow"
      - "複雜業務邏輯處理"
      - "第三方系統整合"
  
  database_separation:
    alert_system_db: "alert_system"
    n8n_db: "n8ndb"  # 保持獨立
    connection_sharing: false
  
  emqx_usage:
    primary: "外部系統告警接收"
    topics: "alerts/{system_id}/{level}"
    backup: "系統間通信"
  
  redis_usage:
    session_cache: "使用者會話"
    api_cache: "API 響應快取"
    config_cache: "系統配置快取"
```

### 12.6 💡 POC 階段總結與建議

這份設計文件針對企業級系統進行了詳細設計，但對於 **POC (概念驗證)** 階段來說：

🎯 **POC 階段應該關注的核心問題**：

🔴 **高優先級 (必須解決)**：
1. ✅ **API 設計規範化** - 已整合統一響應格式、錯誤處理、資料驗證
2. ✅ **基礎限流保護** - 已實作簡單的 API 限流機制
3. **基礎安全配置** - 輸入驗證、CORS、敏感資訊保護
4. **開發環境快速啟動** - 一鍵啟動腳本
5. **MVP 功能範圍控制** - 避免過度設計

🟡 **中優先級 (可以簡化)**：
1. **基礎監控** - 健康檢查端點、結構化日誌
2. **簡單的測試** - 核心業務邏輯單元測試
3. **n8n 整合策略** - 明確整合方案和錯誤處理
4. **資料庫基礎優化** - 連接池、基本索引

🟢 **低優先級 (POC 階段可忽略)**：
1. ~~高可用性架構~~ - 單機部署即可
2. ~~複雜監控系統~~ - 基礎日誌足夠
3. ~~進階安全掃描~~ - 基礎防護即可
4. ~~效能壓測~~ - 功能驗證為主
5. ~~AI 智能分析~~ - 簡單規則匹配即可

**POC 成功標準**：
✅ 外部系統可以發送告警到系統  
✅ 系統可以根據規則進行基礎分流  
✅ 可以透過 Web 界面管理告警和規則  
✅ 與現有 n8n 整合可以發送通知  
✅ 基本的使用者認證和權限控制  

**建議後續迭代順序**：
1. **POC 驗證** (4-6 週)：核心功能打通
2. **功能完善** (6-8 週)：增加監控、測試、優化
3. **生產就緒** (8-12 週)：高可用、安全、效能優化

重點是 **先讓系統跑起來**，再逐步完善！
