import request from "@/utils/request";

/**
 * 分页查询工单列表
 * @param {number} pageNum - 当前页码
 * @param {number} pageSize - 每页条数
 */
export const getWorkOrderListApi = (pageNum, pageSize) => request.get("/workorder/findAll" + "?pageNum=" + pageNum + "&pageSize=" + pageSize);

/**
 * 根据 ID 查询单个工单
 * @param {number} id - 工单 ID
 */
export const getWorkOrderByIdApi = (id) => request.get("/workorder/find/" + id);

/**
 * 条件查询工单列表
 * @param {number} pageNum - 当前页码
 * @param {number} pageSize - 每页条数
 * @param {number|string} id - 工单编号（可选）
 * @param {number|string} status - 工单状态（可选）：0:待生产, 1:生产中, 2:生产完成, 3:已关闭
 * @param {number|string} priority - 优先级（可选）：0:低, 1:中, 2:高, 3:紧急
 */
export const queryWorkOrderApi = (dataModel) => request.post("/workorder/query", dataModel);

/**
 * 新增工单
 * @param {Object} workOrder - 工单数据对象
 */
export const addWorkOrderApi = (workOrder) => request.post("/workorder/add", workOrder);

/**
 * 编辑工单
 * @param {Object} workOrder - 工单数据对象
 */
export const editWorkOrderApi = (workOrder) => request.put("/workorder/update", workOrder);

/**
 * 批量删除工单
 * @param {number[]} ids - 工单 ID 数组
 */
export const deleteWorkOrderApi = (ids) => request.delete("/workorder/delete", { data: ids });