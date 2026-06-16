package com.aircookies.smtworkordermanagement.controller;

import com.aircookies.smtworkordermanagement.common.Result;
import com.aircookies.smtworkordermanagement.util.RSAUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * RSA 公钥控制器
 * <p>
 * 提供 RSA 公钥查询接口，用于前端在登录时加密密码。
 * 前端使用公钥加密密码后发送登录请求，后端使用私钥解密并验证。
 * 该接口无需认证，因为登录前用户尚未获取令牌。
 * </p>
 */
@RestController
@RequestMapping("/publickey")
public class PublicKeyController {
    private final RSAUtil rsaUtil;

    @Autowired
    public PublicKeyController(RSAUtil rsaUtil) {
        this.rsaUtil = rsaUtil;
    }

    /**
     * 获取 RSA 公钥
     * <p>
     * 返回 Base64 编码的公钥字符串，前端用于加密登录密码。
     * 每次请求返回相同的公钥（由 RSAUtil 在启动时加载）。
     * </p>
     *
     * @return 包含公钥字符串的成功响应
     */
    @GetMapping
    public Result getPublicKey() {
        return Result.success((Object) rsaUtil.getPublicKey());
    }
}