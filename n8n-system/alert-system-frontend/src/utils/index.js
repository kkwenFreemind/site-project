/**
 * 日期格式化
 * @param {Date|string} date 日期
 * @param {string} format 格式化模式
 * @returns {string} 格式化後的日期字串
 */
export function formatDate(date, format = 'YYYY-MM-DD HH:mm:ss') {
  if (!date) return '';
  
  const d = new Date(date);
  if (isNaN(d.getTime())) return '';
  
  const year = d.getFullYear();
  const month = String(d.getMonth() + 1).padStart(2, '0');
  const day = String(d.getDate()).padStart(2, '0');
  const hours = String(d.getHours()).padStart(2, '0');
  const minutes = String(d.getMinutes()).padStart(2, '0');
  const seconds = String(d.getSeconds()).padStart(2, '0');
  
  return format
    .replace('YYYY', year)
    .replace('MM', month)
    .replace('DD', day)
    .replace('HH', hours)
    .replace('mm', minutes)
    .replace('ss', seconds);
}

/**
 * 相對時間格式化
 * @param {Date|string} date 日期
 * @returns {string} 相對時間描述
 */
export function formatRelativeTime(date) {
  if (!date) return '';
  
  const now = new Date();
  const past = new Date(date);
  const diff = now - past;
  
  const minutes = Math.floor(diff / 60000);
  const hours = Math.floor(diff / 3600000);
  const days = Math.floor(diff / 86400000);
  
  if (minutes < 1) return '剛剛';
  if (minutes < 60) return `${minutes}分鐘前`;
  if (hours < 24) return `${hours}小時前`;
  if (days < 30) return `${days}天前`;
  
  return formatDate(date, 'YYYY-MM-DD');
}

/**
 * 文件大小格式化
 * @param {number} bytes 位元組數
 * @returns {string} 格式化的文件大小
 */
export function formatFileSize(bytes) {
  if (bytes === 0) return '0 B';
  
  const k = 1024;
  const sizes = ['B', 'KB', 'MB', 'GB', 'TB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
}

/**
 * 數字格式化
 * @param {number} number 數字
 * @param {number} decimals 小數位數
 * @returns {string} 格式化的數字
 */
export function formatNumber(number, decimals = 0) {
  if (isNaN(number)) return '0';
  
  return Number(number).toLocaleString('zh-TW', {
    minimumFractionDigits: decimals,
    maximumFractionDigits: decimals
  });
}

/**
 * 防抖函數
 * @param {Function} func 要防抖的函數
 * @param {number} wait 等待時間（毫秒）
 * @returns {Function} 防抖後的函數
 */
export function debounce(func, wait) {
  let timeout;
  return function executedFunction(...args) {
    const later = () => {
      clearTimeout(timeout);
      func(...args);
    };
    clearTimeout(timeout);
    timeout = setTimeout(later, wait);
  };
}

/**
 * 節流函數
 * @param {Function} func 要節流的函數
 * @param {number} limit 時間限制（毫秒）
 * @returns {Function} 節流後的函數
 */
export function throttle(func, limit) {
  let inThrottle;
  return function(...args) {
    if (!inThrottle) {
      func.apply(this, args);
      inThrottle = true;
      setTimeout(() => inThrottle = false, limit);
    }
  };
}

/**
 * 深拷貝
 * @param {any} obj 要拷貝的對象
 * @returns {any} 拷貝後的對象
 */
export function deepClone(obj) {
  if (obj === null || typeof obj !== 'object') return obj;
  if (obj instanceof Date) return new Date(obj.getTime());
  if (obj instanceof Array) return obj.map(item => deepClone(item));
  if (typeof obj === 'object') {
    const copy = {};
    Object.keys(obj).forEach(key => {
      copy[key] = deepClone(obj[key]);
    });
    return copy;
  }
}

/**
 * 生成隨機字串
 * @param {number} length 字串長度
 * @returns {string} 隨機字串
 */
export function generateRandomString(length = 8) {
  const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
  let result = '';
  for (let i = 0; i < length; i++) {
    result += chars.charAt(Math.floor(Math.random() * chars.length));
  }
  return result;
}

/**
 * 驗證 Email 格式
 * @param {string} email Email 地址
 * @returns {boolean} 是否為有效的 Email
 */
export function validateEmail(email) {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return emailRegex.test(email);
}

/**
 * 驗證電話號碼格式
 * @param {string} phone 電話號碼
 * @returns {boolean} 是否為有效的電話號碼
 */
export function validatePhone(phone) {
  const phoneRegex = /^(\+?886-?|0)?9\d{8}$/;
  return phoneRegex.test(phone);
}

/**
 * 驗證密碼強度
 * @param {string} password 密碼
 * @returns {object} 密碼強度評估結果
 */
export function validatePasswordStrength(password) {
  const result = {
    score: 0,
    level: 'weak',
    suggestions: []
  };
  
  if (!password) {
    result.suggestions.push('密碼不能為空');
    return result;
  }
  
  if (password.length >= 8) result.score += 1;
  else result.suggestions.push('密碼長度至少 8 位');
  
  if (/[a-z]/.test(password)) result.score += 1;
  else result.suggestions.push('至少包含一個小寫字母');
  
  if (/[A-Z]/.test(password)) result.score += 1;
  else result.suggestions.push('至少包含一個大寫字母');
  
  if (/\d/.test(password)) result.score += 1;
  else result.suggestions.push('至少包含一個數字');
  
  if (/[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]/.test(password)) result.score += 1;
  else result.suggestions.push('至少包含一個特殊字符');
  
  if (result.score >= 4) result.level = 'strong';
  else if (result.score >= 2) result.level = 'medium';
  
  return result;
}

/**
 * 下載文件
 * @param {Blob|string} data 文件數據或 URL
 * @param {string} filename 文件名
 */
export function downloadFile(data, filename) {
  const link = document.createElement('a');
  
  if (typeof data === 'string') {
    link.href = data;
  } else {
    const url = window.URL.createObjectURL(data);
    link.href = url;
  }
  
  link.download = filename;
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
  
  if (typeof data !== 'string') {
    window.URL.revokeObjectURL(link.href);
  }
}

/**
 * 複製文字到剪貼板
 * @param {string} text 要複製的文字
 * @returns {Promise<boolean>} 是否成功複製
 */
export async function copyToClipboard(text) {
  try {
    await navigator.clipboard.writeText(text);
    return true;
  } catch (err) {
    // 備用方案
    const textArea = document.createElement('textarea');
    textArea.value = text;
    document.body.appendChild(textArea);
    textArea.select();
    try {
      document.execCommand('copy');
      return true;
    } catch (err2) {
      return false;
    } finally {
      document.body.removeChild(textArea);
    }
  }
}

/**
 * 獲取瀏覽器資訊
 * @returns {object} 瀏覽器資訊
 */
export function getBrowserInfo() {
  const userAgent = navigator.userAgent;
  const isChrome = /Chrome/.test(userAgent) && /Google Inc/.test(navigator.vendor);
  const isFirefox = /Firefox/.test(userAgent);
  const isSafari = /Safari/.test(userAgent) && /Apple Computer/.test(navigator.vendor);
  const isEdge = /Edg/.test(userAgent);
  const isIE = /Trident/.test(userAgent);
  
  return {
    userAgent,
    isChrome,
    isFirefox,
    isSafari,
    isEdge,
    isIE,
    isMobile: /Mobi|Android/i.test(userAgent)
  };
}
