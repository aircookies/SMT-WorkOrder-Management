package com.aircookies.smtworkordermanagement;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.beans.Encoder;

@SpringBootTest
public class SecurityTest {
    @Autowired
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // 测试加密和解密
//    @Test
    public void testPasswordEncoder() {
        String password = "password";
        String encodedPassword = passwordEncoder.encode(password);

        System.out.println("原始密码: " + password);
        System.out.println("加密后的密码：" + encodedPassword);
        System.out.println("密码是否匹配：" + passwordEncoder.matches(password, encodedPassword));
    }
}
