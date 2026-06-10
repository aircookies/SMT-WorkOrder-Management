package com.aircookies.smtworkordermanagement.controller;

import com.aircookies.smtworkordermanagement.common.Result;
import com.aircookies.smtworkordermanagement.util.RSAUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/publickey")
public class PublicKeyController {
    private final RSAUtil rsaUtil;

    @Autowired
    public PublicKeyController(RSAUtil rsaUtil) {
        this.rsaUtil = rsaUtil;
    }

    @GetMapping
    public Result getPublicKey() {
        return Result.success((Object) rsaUtil.getPublicKey());
    }
}
