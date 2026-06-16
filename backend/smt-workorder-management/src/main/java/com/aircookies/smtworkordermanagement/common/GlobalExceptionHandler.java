package com.aircookies.smtworkordermanagement.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * 全局异常处理器
 * <p>
 * 使用 @RestControllerAdvice 拦截所有 Controller 层抛出的异常，
 * 统一处理并返回标准化的 {@link Result} 错误响应，避免异常信息直接暴露给客户端。
 * </p>
 *
 * <p>异常处理覆盖范围：</p>
 * <ul>
 *   <li>业务异常（BusinessException） - 返回自定义状态码和消息</li>
 *   <li>认证异常（BadCredentialsException / AuthenticationException） - 返回 401</li>
 *   <li>权限异常（AccessDeniedException） - 返回 403</li>
 *   <li>参数校验异常（MethodArgumentNotValidException / BindException） - 返回 400</li>
 *   <li>数据库异常（SQLException） - 返回 500</li>
 *   <li>其他未知异常（Exception） - 返回 500，记录完整堆栈</li>
 * </ul>
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常
     * <p>
     * 业务逻辑中主动抛出的异常，如数据校验失败、资源已存在等。
     * 直接使用异常中的状态码和消息作为响应。
     * </p>
     */
    @ExceptionHandler(BusinessException.class)
    public Result handleBusinessException(BusinessException e) {
        log.error("业务异常: {}", e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理资源未找到异常（404）
     * <p>
     * 当请求的 URL 路径不存在时触发，由 Spring MVC 抛出。
     * </p>
     */
    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Result handleNoResourceFoundException(NoResourceFoundException e) {
        log.error("请求的资源未找到: {}", e.getMessage());
        return Result.error(404, "请求的资源未找到");
    }

    /**
     * 处理请求参数校验异常（@Valid 校验失败）
     * <p>
     * 当使用 @Valid 注解校验请求体参数时，校验失败会触发此异常。
     * 提取第一个校验错误的字段名和错误信息返回给客户端。
     * </p>
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result handleValidationException(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        String errorMessage = errors.values().iterator().next();
        log.warn("参数验证异常: {}", errorMessage);
        return Result.error(400, "参数验证失败: " + errorMessage);
    }

    /**
     * 处理参数绑定异常
     * <p>
     * 当请求参数无法绑定到目标类型时触发（如类型不匹配）。
     * </p>
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result handleBindException(BindException e) {
        FieldError fieldError = e.getFieldError();
        String fieldName = fieldError != null ? fieldError.getField() : "未知字段";
        String errorMessage = fieldError != null ? fieldError.getDefaultMessage() : "参数绑定失败";
        log.error("参数绑定异常: {} - {}", fieldName, errorMessage);
        return Result.error(400, "参数错误: " + errorMessage);
    }

    /**
     * 处理认证失败异常（401）
     * <p>
     * 用户登录时用户名或密码错误时触发。
     * </p>
     */
    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Result handleBadCredentialsException(BadCredentialsException e) {
        log.error("认证失败: {}", e.getMessage());
        return Result.error(401, "用户名或密码错误");
    }

    /**
     * 处理认证异常（401）
     * <p>
     * 其他认证相关异常的通用处理，如令牌过期、令牌无效等。
     * </p>
     */
    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Result handleAuthenticationException(AuthenticationException e) {
        log.error("认证异常: {}", e.getMessage());
        return Result.error(401, "认证失败: " + e.getMessage());
    }

    /**
     * 处理权限不足异常（403）
     * <p>
     * 用户已认证但角色权限不足以访问目标资源时触发。
     * </p>
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Result handleAccessDeniedException(AccessDeniedException e) {
        log.error("权限不足: {}", e.getMessage());
        return Result.error(403, "权限不足，无法访问该资源");
    }

    /**
     * 处理非法参数异常
     * <p>
     * 业务逻辑中参数校验不通过时主动抛出。
     * </p>
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("非法参数异常: {}", e.getMessage());
        return Result.error(400, e.getMessage());
    }

    /**
     * 处理请求方法不支持异常（405）
     * <p>
     * 当请求的 HTTP 方法（GET/POST/PUT/DELETE）与接口定义不匹配时触发。
     * </p>
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public Result handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.error("请求方法不支持: {}", e.getMessage());
        return Result.error(405, "请求方法不支持，请使用正确的方法（GET/POST/PUT/DELETE）");
    }

    /**
     * 处理数据库异常（500）
     * <p>
     * 数据库操作异常的统一处理，如唯一约束冲突、外键约束冲突等。
     * </p>
     */
    @ExceptionHandler(SQLException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result handleSQLIntegrityConstraintViolationException(SQLException e) {
        log.error("数据库操作异常: {}", e.getMessage());
        return Result.error(500, "数据库操作异常，请检查数据是否重复");
    }

    /**
     * 兜底异常处理（500）
     * <p>
     * 捕获所有未被上述处理器处理的异常，记录完整堆栈信息，
     * 返回通用错误消息，避免敏感信息泄露。
     * </p>
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result handleException(Exception e) {
        log.error("系统异常: ", e);
        return Result.error(500, "系统内部错误，请联系管理员");
    }
}