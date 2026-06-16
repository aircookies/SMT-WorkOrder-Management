import request from "@/utils/request";

/**
 * 分页查询报工记录列表
 * @param {number} pageNum - 当前页码
 * @param {number} pageSize - 每页条数
 */
export const getReportListApi = (pageNum, pageSize) => request.get("/workorder/process/findAll" + "?pageNum=" + pageNum + "&pageSize=" + pageSize);

/**
 * 新增报工记录
 * @param {Object} report - 报工数据对象
 */
export const addReportApi = (report) => request.post("/workorder/process/add", report);

/**
 * 根据 ID 删除报工记录
 * @param {number} id - 报工记录 ID
 */
export const deleteReportApi = (id) => request.delete("/workorder/process/delete/" + id);