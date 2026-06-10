package com.aircookies.smtworkordermanagement.controller;

import com.aircookies.smtworkordermanagement.common.Result;
import com.aircookies.smtworkordermanagement.dto.LoginDTO;
import com.aircookies.smtworkordermanagement.dto.LoginResponseDTO;
import com.aircookies.smtworkordermanagement.service.LoginService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

// 登录控制器
@RestController
@Slf4j
public class LoginController {
    private final LoginService loginService;

    @Value("${jwt.expiration:86400000}")
    private long jwtExpiration;

    @Autowired
    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }


    // 实现登录功能
    @PostMapping("/login")
    public Result login(@RequestBody LoginDTO loginDTO, HttpServletResponse response) {
        Result result = loginService.login(loginDTO);

        if (result.getCode() == 200 && result.getData() != null) {
            LoginResponseDTO loginResponseDTO = (LoginResponseDTO) result.getData();
            String token = loginResponseDTO.getToken();

            ResponseCookie cookie = ResponseCookie.from("JWT_TOKEN", token)
                    .httpOnly(true)
                    .secure(false)
                    .path("/")
                    .maxAge(Duration.ofMillis(jwtExpiration))
                    .sameSite("Strict")
                    .build();

            response.addHeader("Set-Cookie", cookie.toString());

            loginResponseDTO.setToken(null);
            result.setData(loginResponseDTO);
        }
        return result;
    }
}
