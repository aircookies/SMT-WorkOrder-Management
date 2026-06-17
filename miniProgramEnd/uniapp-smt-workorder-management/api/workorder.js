/**
 * 工单相关 API
 */

import { get } from '../utils/request'

/**
 * 获取工单分页列表
 * @param {number} pageNum - 页码（默认 1）
 * @param {number} pageSize - 每页数量（默认 10）
 */
export const getWorkOrderList = (pageNum = 1, pageSize = 10) => {
  return get('/workorder/findAll', { pageNum, pageSize })
}

/**
 * 条件查询工单
 * 后端使用 GET + @RequestParam，条件对象会被序列化为 URL 查询参数
 * @param {Object} conditions - 查询条件（参考 WorkOrderDetailedDTO 字段）
 *   支持: pageNum, pageSize, status, productId, lineId, priority 等
 */
export const queryWorkOrders = (conditions) => {
  return get('/workorder/query', conditions)
}

/**
 * 根据 ID 获取工单详情
 * @param {number|string} id - 工单 ID
 */
export const getWorkOrderById = (id) => {
  return get(`/workorder/find/${id}`)
}

/**
 * 获取工单统计信息（详细统计）
 * @param {string} startTime - 开始日期 (yyyy-MM-dd)
 * @param {string} endTime - 结束日期 (yyyy-MM-dd)
 */
export const getWorkOrderStatistics = (startTime, endTime) => {
  return get('/workorder/detailed', { startTime, endTime })
}
