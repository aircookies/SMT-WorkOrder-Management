/**
 * 格式化日期为 YYYY-MM-DD
 */
export const formatDate = (date = new Date()) => {
    const d = new Date(date)
    const year = d.getFullYear()
    const month = String(d.getMonth() + 1).padStart(2, '0')
    const day = String(d.getDate()).padStart(2, '0')
    return `${year}-${month}-${day}`
}

/**
 * 格式化日期时间为 YYYY-MM-DDTHH:mm:ss
 */
export const formatDateTime = (date = new Date()) => {
    const d = new Date(date)
    const dateStr = formatDate(d)
    const hours = String(d.getHours()).padStart(2, '0')
    const minutes = String(d.getMinutes()).padStart(2, '0')
    const seconds = String(d.getSeconds()).padStart(2, '0')
    return `${dateStr}T${hours}:${minutes}:${seconds}`
}

/**
 * 获取默认日期范围
 * @param {number} days - 天数，默认30天
 */
export const getDefaultDateRange = (days = 30) => {
    const end = new Date()
    const start = new Date()
    start.setDate(start.getDate() - days)
    
    return [formatDate(start), formatDate(end)]
}

/**
 * 获取本月第一天和最后一天
 */
export const getCurrentMonthRange = () => {
    const now = new Date()
    const year = now.getFullYear()
    const month = now.getMonth()
    
    const start = new Date(year, month, 1)
    const end = new Date(year, month + 1, 0)
    
    return [formatDate(start), formatDate(end)]
}

/**
 * 获取本年起始和结束日期
 */
export const getCurrentYearRange = () => {
    const year = new Date().getFullYear()
    const start = new Date(year, 0, 1)
    const end = new Date(year, 11, 31)
    
    return [formatDate(start), formatDate(end)]
}