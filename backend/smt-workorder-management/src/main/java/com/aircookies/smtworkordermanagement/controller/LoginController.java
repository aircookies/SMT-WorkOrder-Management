package com.aircookies.smtworkordermanagement.controller;

import com.aircookies.smtworkordermanagement.common.Result;
import com.aircookies.smtworkordermanagement.dto.LoginDTO;
import com.aircookies.smtworkordermanagement.dto.LoginResponseDTO;
import com.aircookies.smtworkordermanagement.service.LoginService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

/**
 * 登录控制器
 * <p>
 * 处理用户登录请求，验证用户名和密码，生成 JWT 令牌并通过 HttpOnly Cookie 返回。
 * 登录流程：
 * </p>
 * <ol>
 *   <li>前端使用 RSA 公钥加密密码后发送登录请求</li>
 *   <li>后端解密密码并验证用户名密码</li>
 *   <li>验证通过后生成 JWT 令牌</li>
 *   <li>将 JWT 令牌设置为 HttpOnly、Secure、SameSite=Strict 的 Cookie</li>
 *   <li>响应体中不返回令牌（仅返回用户基本信息），防止 XSS 攻击窃取令牌</li>
 * </ol>
 */
@RestController
@Slf4j
public class LoginController {
    private final LoginService loginService;

    /** JWT 令牌过期时间（毫秒），从配置文件注入，默认 86400000（24小时） */
    @Value("${jwt.expiration:86400000}")
    private long jwtExpiration;

    @Autowired
    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    /**
     * 用户登录接口
     * <p>
     * 接收加密的登录凭证，验证通过后生成 JWT 令牌并写入 Cookie。
     * 出于安全考虑，令牌仅在 HttpOnly Cookie 中传输，不在响应体中返回。
     * </p>
     *
     * @param loginDTO 登录请求体（包含加密的用户名和密码）
     * @param request  HTTP 请求对象，用于判断请求协议（HTTP/HTTPS）
     * @param response HTTP 响应对象，用于写入 Cookie
     * @return 登录结果，包含用户基本信息（不含令牌）
     */
    @PostMapping("/login")
    public Result login(@RequestBody LoginDTO loginDTO, HttpServletRequest request, HttpServletResponse response) {
        Result result = loginService.login(loginDTO);

        if (result.getCode() == 200 && result.getData() != null) {
            LoginResponseDTO loginResponseDTO = (LoginResponseDTO) result.getData();
            String token = loginResponseDTO.getToken();

            // 判断是否为 HTTPS 请求，决定 Cookie 的 Secure 属性
            boolean isSecure = "https".equalsIgnoreCase(request.getScheme());

            // 构建安全的 HttpOnly Cookie
            ResponseCookie cookie = ResponseCookie.from("JWT_TOKEN", token)
                    .httpOnly(true)        // 禁止 JavaScript 访问，防止 XSS 攻击
                    .secure(isSecure)       // HTTPS 环境下启用，防止中间人攻击
                    .path("/")              // 全路径可用
                    .maxAge(Duration.ofMillis(jwtExpiration)) // 与 JWT 令牌过期时间一致
                    .sameSite("Strict")     // 严格同站策略，防止 CSRF 攻击
                    .build();

            response.addHeader("Set-Cookie", cookie.toString());

            // 从响应体中移除令牌，仅保留用户信息
            loginResponseDTO.setToken(null);
            result.setData(loginResponseDTO);
        }
        return result;
    }
}