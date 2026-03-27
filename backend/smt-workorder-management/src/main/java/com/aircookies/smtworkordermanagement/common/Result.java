package com.aircookies.smtworkordermanagement.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 统一返回结果
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result {
    private int code; // 状态码
    private String message; // 错误信息
    private Object data; // 数据

    // 成功响应
    public static Result success(Object data) {
        return new Result(200, "success", data);
    }

    public static Result success(String msg) {
        return new Result(200, msg, null);
    }

    public static Result success() {
        return new Result(200, "success", null);
    }

    // 失败响应
    public static Result fail(String message) {
        return new Result(500, message, null);
    }

    public static Result fail(int code, String message) {
        return new Result(code, message, null);
    }
}
