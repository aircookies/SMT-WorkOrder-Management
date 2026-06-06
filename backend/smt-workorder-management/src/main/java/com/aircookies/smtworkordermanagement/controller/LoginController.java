package com.aircookies.smtworkordermanagement.controller;

import com.aircookies.smtworkordermanagement.common.Result;
import com.aircookies.smtworkordermanagement.dto.LoginDTO;
import com.aircookies.smtworkordermanagement.service.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

// 登录控制器
@RestController
@Slf4j
public class LoginController {
    private final LoginService loginService;

    @Autowired
    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }


    // 实现登录功能
    @PostMapping("/login")
    public Result login(@RequestBody LoginDTO loginDTO) {
        return loginService.login(loginDTO);
    }
}
