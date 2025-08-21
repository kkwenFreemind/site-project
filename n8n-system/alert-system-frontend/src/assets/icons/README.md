# CCMS 系統 Icons 資源

## 新增的 Icons 列表

### 基礎功能 Icons
- `dashboard.svg` - 儀表板圖標
- `home.svg` - 首頁圖標
- `users.svg` - 用戶群組圖標
- `settings.svg` - 設置圖標
- `gear.svg` - 齒輪設置圖標

### 操作功能 Icons
- `edit.svg` - 編輯圖標
- `delete.svg` - 刪除圖標
- `check.svg` - 確認圖標
- `x.svg` - 關閉/取消圖標
- `login.svg` - 登入圖標
- `logout.svg` - 登出圖標

### 導航 Icons
- `arrow-up.svg` - 上箭頭
- `arrow-down.svg` - 下箭頭
- `arrow-left.svg` - 左箭頭
- `arrow-right.svg` - 右箭頭
- `chevron-up.svg` - 上V型箭頭
- `chevron-down.svg` - 下V型箭頭
- `chevron-left.svg` - 左V型箭頭
- `chevron-right.svg` - 右V型箭頭

### 狀態 Icons
- `info.svg` - 信息圖標
- `alert-triangle.svg` - 警告三角形
- `alert-circle.svg` - 警告圓形
- `smile.svg` - 成功/快樂圖標
- `check-square.svg` - 選中方框
- `square.svg` - 空方框

### 系統功能 Icons
- `clock.svg` - 時間圖標
- `calendar.svg` - 日曆圖標
- `history.svg` - 歷史記錄圖標
- `search-2.svg` - 搜索圖標（替代版本）
- `more-horizontal.svg` - 更多選項（橫向）
- `more-vertical.svg` - 更多選項（縱向）

### CCMS 特定 Icons
- `camera.svg` - 攝像頭圖標
- `video.svg` - 視頻圖標
- `monitor-screen.svg` - 監控屏幕圖標
- `device.svg` - 設備圖標
- `location.svg` - 位置圖標
- `map-pin.svg` - 地圖標記圖標

### 文件操作 Icons
- `file-plus.svg` - 新增文件
- `file-minus.svg` - 刪除文件
- `report.svg` - 報告圖標
- `package.svg` - 包裝/模組圖標

### 安全 Icons
- `lock.svg` - 鎖定圖標
- `unlock.svg` - 解鎖圖標

### 其他實用 Icons
- `grid.svg` - 網格圖標
- `layers.svg` - 圖層圖標
- `cloud.svg` - 雲端圖標
- `activity.svg` - 活動圖標

## 使用方式

在 Vue 組件中使用：

```vue
<template>
  <!-- 使用 img 標籤 -->
  <img src="@/assets/icons/dashboard.svg" alt="Dashboard" class="w-6 h-6" />
  
  <!-- 或使用 SvgIcon 組件（如果有配置） -->
  <SvgIcon name="dashboard" class="w-6 h-6" />
</template>
```

## 圖標來源
這些圖標基於 Lucide Icons 設計風格，具有一致的線條粗細和設計語言，適合現代 Web 應用使用。

## 自定義顏色
所有 SVG 圖標都使用 `stroke="currentColor"`，因此會繼承父元素的文字顏色。您可以通過 CSS 來自定義顏色：

```css
.icon-primary {
  color: #3b82f6; /* 藍色 */
}

.icon-success {
  color: #10b981; /* 綠色 */
}

.icon-warning {
  color: #f59e0b; /* 黃色 */
}

.icon-danger {
  color: #ef4444; /* 紅色 */
}
```
