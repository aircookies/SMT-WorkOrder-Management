/**
 * 工序报告相关 API
 */

import { get, post, put, del } from '../utils/request'

/**
 * 根据工单 ID 获取工序报告列表
 * @param {number|string} orderId - 工单 ID
 */
export const getProcessByOrderId = (orderId) => {
  return get(`/workorder/process/find/${orderId}`)
}

/**
 * 新增工序报告
 * @param {Object} report - 工序报告数据
 * @param {number} report.orderId - 工单 ID
 * @param {number} report.processSeq - 工序序号（1=印刷, 2=贴片, 3=回流焊）
 * @param {number} report.qualifiedQuantity - 合格数量
 * @param {number} report.badQuantity - 不良数量
 * @param {string} [report.startTime] - 开始时间 (yyyy-MM-dd HH:mm:ss)
 * @param {string} [report.finishTime] - 结束时间 (yyyy-MM-dd HH:mm:ss)
 * @param {string} [report.remarks] - 备注
 */
export const addProcess = (report) => {
  return post('/workorder/process/add', report)
}

/**
 * 更新工序报告
 * @param {Object} report - 工序报告数据（需包含 id 字段）
 */
export const updateProcess = (report) => {
  return put('/workorder/process/update', report)
}

/**
 * 删除工序报告
 * @param {number|string} id - 报告 ID
 */
export const deleteProcess = (id) => {
  return del(`/workorder/process/delete/${id}`)
}

/**
 * 获取工序质量统计
 * @param {string} startTime - 开始日期 (yyyy-MM-dd)
 * @param {string} endTime - 结束日期 (yyyy-MM-dd)
 */
export const getProcessStatistics = (startTime, endTime) => {
  return get('/workorder/process/statistics', { startTime, endTime })
}
