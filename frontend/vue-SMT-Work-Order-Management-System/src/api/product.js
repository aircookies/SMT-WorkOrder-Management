import request from "@/utils/request";

/**
 * 分页查询产品列表
 * @param {number} pageNum - 当前页码
 * @param {number} pageSize - 每页条数
 */
export const getProductListApi = (pageNum, pageSize) => request.get("/product/findAll?pageNum=" + pageNum + "&pageSize=" + pageSize);

/**
 * 根据 ID 查询单个产品
 * @param {number} id - 产品 ID
 */
export const getProductByIdApi = (id) => request.get("/product/find/" + id);

/**
 * 条件查询产品列表
 * @param {Object} dataModel - 查询条件对象
 */
export const queryProductApi = (dataModel) => request.post("/product/query", dataModel);

/**
 * 新增产品
 * @param {Object} dataModel - 产品数据对象
 */
export const addProductApi = (dataModel) => request.post("/product/add", dataModel);

/**
 * 编辑产品
 * @param {Object} dataModel - 产品数据对象
 */
export const editProductApi = (dataModel) => request.put("/product/update", dataModel);

/**
 * 批量删除产品
 * @param {number[]} ids - 产品 ID 数组
 */
export const deleteProductApi = (ids) => request.delete("/product/deleteBatch", { data: ids });

/**
 * 根据 ID 删除单个产品
 * @param {number} id - 产品 ID
 */
export const deleteProductByIdApi = (id) => request.delete("/product/delete/" + id);