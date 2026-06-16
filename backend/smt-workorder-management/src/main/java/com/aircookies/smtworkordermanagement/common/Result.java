package com.aircookies.smtworkordermanagement.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一返回结果封装类
 * <p>
 * 所有控制器层（Controller）的返回值统一使用此类进行包装，
 * 确保前端接收到一致的数据格式。
 * </p>
 *
 * <ul>
 *   <li>code - 状态码，200 表示成功，4xx 表示客户端错误，5xx 表示服务端错误</li>
 *   <li>message - 提示信息，用于向客户端描述操作结果</li>
 *   <li>data - 返回的数据，可以是任意类型</li>
 * </ul>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result {
    private int code; // 状态码
    private String message; // 错误信息
    private Object data; // 数据

    /**
     * 成功响应（带数据）
     *
     * @param data 返回的数据对象
     * @return 包含数据的成功响应
     */
    public static Result success(Object data) {
        return new Result(200, "success", data);
    }

    /**
     * 成功响应（带自定义消息）
     *
     * @param msg 自定义成功消息
     * @return 包含自定义消息的成功响应
     */
    public static Result success(String msg) {
        return new Result(200, msg, null);
    }

    /**
     * 成功响应（无数据）
     *
     * @return 默认的成功响应
     */
    public static Result success() {
        return new Result(200, "success", null);
    }

    /**
     * 失败响应（默认500状态码）
     *
     * @param message 错误消息
     * @return 包含错误消息的失败响应
     */
    public static Result error(String message) {
        return new Result(500, message, null);
    }

    /**
     * 失败响应（自定义状态码）
     *
     * @param code    自定义状态码
     * @param message 错误消息
     * @return 包含自定义状态码和错误消息的失败响应
     */
    public static Result error(int code, String message) {
        return new Result(code, message, null);
    }
}