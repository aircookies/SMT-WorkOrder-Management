import request from "@/utils/request";

/**
 * 查询用户列表(不指定参数返回所有用户，指定参数查询符合条件的用户)
 * @param {Object} dataModel 查询参数([name]，[gender]，[roleId]，[departmentId]，pageNum, pageSize)
 * @example // 查询所有用户
 * queryUserApi()
 * 
 * // 带条件查询
 * queryUserApi({
 *   name: '张三',
 *   pageNum: 1,
 *   pageSize: 10
 * 
 * @returns {object} {
    "code": 200,
    "message": "success",
    "data": {
        "pageNum": 1,
        "pageSize": 10,
        "total": 1,
        "list": [
            {
                "id": 1,
                "username": "lisi",
                "password": "123456",
                "name": "李四",
                "gender": 1,
                "roleId": 1,
                "departmentId": 1,
                "status": 1,
                "createTime": "2026-03-22T08:12:42",
                "updateTime": "2026-04-22T06:49:49"
            }
        ]
    }
}
 */
export const queryUserApi = (dataModel) => request.post("/user/list", dataModel);

/**
 * 根据ID查询用户
 * @param {} id 用户ID
 * @example // 查询用户ID为1的用户
 * queryUserByIdApi(1)
 * @returns {Object} {
    "code": 200,
    "message": "success",
    "data": {
        "id": 1,
        "username": "lisi",
        "password": "123456",
        "name": "李四",
        "gender": 1,
        "roleId": 1,
        "departmentId": 1,
        "status": 1,
        "createTime": "2026-03-22T08:12:42",
        "updateTime": "2026-04-22T06:49:49"
    }
}
 */
export const getUserByIdApi = (id) => request.get("/user/find/" + id);

/**
 * 
 * @param {Integer} pageNum 当前页码
 * @param {Integer} pageSize 分页大小
 * @example // 获取用户列表
 * getUserListApi(1, 10)
 * @returns {Object} {
    "code": 200,
    "message": "success",
    "data": {
        "pageNum": 1,
        "pageSize": 15,
        "total": 1,
        "list": [
            {
                "id": 1,
                "username": "lisi",
                "password": "123456",
                "name": "李四",
                "gender": 1,
                "roleId": 1,
                "departmentId": 1,
                "status": 1,
                "createTime": "2026-03-22T08:12:42",
                "updateTime": "2026-04-22T06:49:49"
            }
        ]
    }
}
 */
export const getUserListApi = (pageNum, pageSize) => request.get("/user/findAll?pageNum=" + pageNum + "&pageSize=" + pageSize);

/**
 * 
 * @param {Object} dataModel 
 * @example // 添加用户
 * addUserApi({
    "username": "zhangsan",
    "password": "123456",
    "name": "张三",
    "gender": 1,
    "roleId": 1,
    "departmentId": 1,
    "status": 1
})
 * @returns {Object} {
    "code": 200,
    "message": "success",
    "data": null
}
 */
export const addUserApi = (dataModel) => request.post("/user/add", dataModel);

/**
 * 
 * @param {Object} dataModel 
 * @example // 修改用户
 * updateUserApi({
    "id": "4",
    "username": "lisi",
    "password": "123456",
    "name": "李四",
    "gender": "1",
    "roleId": "1",
    "departmentId": "1",
    "status": "1"
})
 * @returns {Object} {
    "code": 0,
    "message": "string",
    "data": null
}
 */
export const updateUserApi = (dataModel) => request.put("/user/update", dataModel);

/**
 * 
 * @param {array[Integer]} ids 用户id数组
 * @example // 删除用户ID为1,2,3的用户
 * deleteUserApi([1, 2, 3])
 * @returns {Object} {"code":200,"message":"success","data":null}
 */
export const deleteUserApi = (ids) => request.delete("/user/delete", { data: ids });
