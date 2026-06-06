import request from "@/utils/request"

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
export const loginApi = (username, password) => request.post("/login", { username, password })