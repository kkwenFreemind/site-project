/**
 * 日期格式化工具函數
 */

// 格式化日期為 YYYY-MM-DD HH:mm:ss
export const formatDate = (date, format = 'YYYY-MM-DD HH:mm:ss') => {
  if (!date) return '-'
  
  const d = new Date(date)
  if (isNaN(d.getTime())) return '-'
  
  const year = d.getFullYear()
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  const hours = String(d.getHours()).padStart(2, '0')
  const minutes = String(d.getMinutes()).padStart(2, '0')
  const seconds = String(d.getSeconds()).padStart(2, '0')
  
  switch (format) {
    case 'YYYY-MM-DD':
      return `${year}-${month}-${day}`
    case 'YYYY-MM-DD HH:mm':
      return `${year}-${month}-${day} ${hours}:${minutes}`
    case 'YYYY-MM-DD HH:mm:ss':
      return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
    case 'MM-DD HH:mm':
      return `${month}-${day} ${hours}:${minutes}`
    case 'HH:mm:ss':
      return `${hours}:${minutes}:${seconds}`
    default:
      return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
  }
}

// 格式化相對時間
export const formatRelativeTime = (date) => {
  if (!date) return '-'
  
  const d = new Date(date)
  if (isNaN(d.getTime())) return '-'
  
  const now = new Date()
  const diff = now.getTime() - d.getTime()
  
  const minute = 60 * 1000
  const hour = 60 * minute
  const day = 24 * hour
  const week = 7 * day
  const month = 30 * day
  const year = 365 * day
  
  if (diff < minute) {
    return '剛剛'
  } else if (diff < hour) {
    return `${Math.floor(diff / minute)}分鐘前`
  } else if (diff < day) {
    return `${Math.floor(diff / hour)}小時前`
  } else if (diff < week) {
    return `${Math.floor(diff / day)}天前`
  } else if (diff < month) {
    return `${Math.floor(diff / week)}週前`
  } else if (diff < year) {
    return `${Math.floor(diff / month)}個月前`
  } else {
    return `${Math.floor(diff / year)}年前`
  }
}

// 解析日期字符串
export const parseDate = (dateString) => {
  if (!dateString) return null
  return new Date(dateString)
}

// 獲取日期範圍
export const getDateRange = (type = 'week') => {
  const now = new Date()
  const start = new Date()
  const end = new Date()
  
  switch (type) {
    case 'today':
      start.setHours(0, 0, 0, 0)
      end.setHours(23, 59, 59, 999)
      break
    case 'week':
      const dayOfWeek = now.getDay()
      const diff = now.getDate() - dayOfWeek + (dayOfWeek === 0 ? -6 : 1)
      start.setDate(diff)
      start.setHours(0, 0, 0, 0)
      end.setDate(diff + 6)
      end.setHours(23, 59, 59, 999)
      break
    case 'month':
      start.setDate(1)
      start.setHours(0, 0, 0, 0)
      end.setMonth(start.getMonth() + 1)
      end.setDate(0)
      end.setHours(23, 59, 59, 999)
      break
    case 'year':
      start.setMonth(0, 1)
      start.setHours(0, 0, 0, 0)
      end.setMonth(11, 31)
      end.setHours(23, 59, 59, 999)
      break
    default:
      return null
  }
  
  return [start, end]
}

// 判斷是否為同一天
export const isSameDay = (date1, date2) => {
  if (!date1 || !date2) return false
  const d1 = new Date(date1)
  const d2 = new Date(date2)
  return d1.getFullYear() === d2.getFullYear() &&
         d1.getMonth() === d2.getMonth() &&
         d1.getDate() === d2.getDate()
}

// 獲取時間戳
export const getTimestamp = (date = new Date()) => {
  return new Date(date).getTime()
}

// 格式化時長（毫秒轉換為可讀格式）
export const formatDuration = (milliseconds) => {
  if (!milliseconds || milliseconds < 0) return '0秒'
  
  const seconds = Math.floor(milliseconds / 1000)
  const minutes = Math.floor(seconds / 60)
  const hours = Math.floor(minutes / 60)
  const days = Math.floor(hours / 24)
  
  if (days > 0) {
    return `${days}天${hours % 24}小時${minutes % 60}分鐘`
  } else if (hours > 0) {
    return `${hours}小時${minutes % 60}分鐘`
  } else if (minutes > 0) {
    return `${minutes}分鐘${seconds % 60}秒`
  } else {
    return `${seconds}秒`
  }
}
