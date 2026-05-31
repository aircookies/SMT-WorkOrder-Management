import request from "@/utils/request";

/**
 * 查询部门，不指定查询参数则返回全部部门
 * @param {Object} dataModel 数据模型([id], [name])
 * @example //查询开发部门
 * queryDepartmentApi({name:"开发部"})
 * @returns {Object} {"code":200,"message":"success","data":[{"id":6,"name":"开发部","createTime":"2026-03-25T15:47:40","updateTime":"2026-03-25T15:47:40"},{"id":5,"name":"售后部","createTime":"2026-03-25T15:47:28","updateTime":"2026-03-25T15:47:28"},{"id":4,"name":"人事部","createTime":"2026-03-25T15:47:20","updateTime":"2026-03-25T15:47:20"},{"id":3,"name":"研发部","createTime":"2026-03-25T15:47:07","updateTime":"2026-03-25T15:47:07"}]}
 */
export const queryDepartmentApi = (dataModel) => request.post("/department/find", dataModel);

/**
 * 
 * @param {Object} dataModel 数据模型(name)
 * @example // 添加部门
 * addDepartmentApi({name:"部门名称"})
 * @returns {Object} {"code":200,"message":"success","data":"添加部门成功"}
 */
export const addDepartmentApi = (dataModel) => request.post("/department/add", dataModel);

/**
 * 
 * @param {Object} dataModel 数据模型(id, name)
 * @example // 修改部门
 * updateDepartmentApi({id:部门ID, name:"新的部门名称"})
 * @returns {Object} {
    "code": 0,
    "message": "string",
    "data": null
}
 */
export const updateDepartmentApi = (dataModel) => request.put("/department/update", dataModel);

/**
 * 
 * @param {Integer} id 部门ID
 * @example // 删除部门
 * deleteDepartmentApi(部门ID)
 * @returns {Object} {
    "code": 0,
    "message": "string",
    "data": "string"
}
 */
export const deleteDepartmentApi = (id) => request.delete("/department/delete/" + id);