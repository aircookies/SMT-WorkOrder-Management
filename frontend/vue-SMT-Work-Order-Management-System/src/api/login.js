import request from "@/utils/request"
import {encryptPassword} from "@/utils/RSAUtil"


/**
 * 登录接口
 * 
 * @param {String} username 
 * @param {String} password 
 * @returns {Object} {
    "code": 200,
    "message": "success",
    "data": {
        "token": "eyJhbGciOiJIUzUxMiJ9.eyJyb2xlSWQiOjEsInVzZXJJZCI6NSwic3ViIjoiemhhbmdzYW4iLCJqdGkiOiIwMDQzZGYwZDVkNjQ0NDk3YWIzM2MxMWQwZmIzZDFlOSIsImlhdCI6MTc4MDc0NjcwMywiZXhwIjoxNzgwODMzMTAzfQ.OJOcbi0aua7AxLb87matbzg6aLWXQfjJVPsW7SkN1fnLGNIRuHqyQYh4T6d7KqsTWicUDbpddwSVKMZzHhdd-Q",
        "username": "zhangsan",
        "role": "1"
    }
}
 */
export const loginApi = async (username, password) => {
    // 加密密码
    const encryptedPassword = await encryptPassword(password)

    // 发送登录请求
    return request.post("/login", {
        username: username,
        password: encryptedPassword
    })
}

/**
 * 登出接口
 * @returns {null}
 */
export const logoutApi = () => request.post("/logout")
