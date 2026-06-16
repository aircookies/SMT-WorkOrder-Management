import request from "@/utils/request";

/**
 * 获取全部产线列表
 */
export const getLineListApi = () => request.get("/line/findAll");

/**
 * 根据 ID 查询单个产线
 * @param {number} id - 产线 ID
 */
export const getLineByIdApi = (id) => request.get("/line/find/" + id);

/**
 * 根据 ID 删除产线
 * @param {number} id - 产线 ID
 */
export const deleteLineApi = (id) => request.delete("/line/delete/" + id);

/**
 * 新增产线
 * @param {Object} dataModel - 产线数据对象
 */
export const addLineApi = (dataModel) => request.post("/line/add", dataModel);

/**
 * 编辑产线
 * @param {Object} dataModel - 产线数据对象
 */
export const editLineApi = (dataModel) => request.put("/line/update", dataModel);