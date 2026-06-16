package com.aircookies.smtworkordermanagement.common;

import lombok.Getter;

/**
 * 业务异常类
 * <p>
 * 用于封装业务逻辑中的异常情况，如数据校验失败、资源不存在、操作冲突等。
 * 配合 {@link GlobalExceptionHandler} 统一处理，返回标准化的错误响应。
 * </p>
 *
 * <ul>
 *   <li>code - 异常状态码，默认为 500</li>
 *   <li>message - 异常描述信息，由 {@link GlobalExceptionHandler} 返回给客户端</li>
 * </ul>
 */
@Getter
public class BusinessException extends RuntimeException {
    private final Integer code;

    /**
     * 创建业务异常（默认状态码500）
     *
     * @param message 异常描述信息
     */
    public BusinessException(String message) {
        super(message);
        this.code = 500;
    }

    /**
     * 创建业务异常（自定义状态码）
     *
     * @param code    自定义状态码
     * @param message 异常描述信息
     */
    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * 创建业务异常（携带原始异常）
     *
     * @param message 异常描述信息
     * @param cause   原始异常，用于链路追踪
     */
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.code = 500;
    }
}