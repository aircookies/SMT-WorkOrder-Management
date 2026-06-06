import request from "@/utils/request";

/**
 * 获取系统角色列表
 * @param {void} void
 * @returns {Object} {
    "code": 200,
    "message": "success",
    "data": [
        {
            "id": 1,
            "name": "管理员",
            "description": "系统的管理员",
            "createTime": "2026-03-25T16:46:56",
            "updateTime": "2026-03-25T16:46:56"
        }
    ]
}
 */
export const getSystemRoleListApi = () => request.get("/sysRole/list");