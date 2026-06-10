package com.aircookies.smtworkordermanagement.util;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Component
public class RSAUtil {
    private static final String ALGORITHM = "RSA";
    private static final int KEY_SIZE = 2048;

    @Value("${rsa.key-file-path:./keys/}")
    private String keyFilePath;

    private PublicKey publicKey;
    private PrivateKey privateKey;

    @PostConstruct
    public void init() throws Exception {
        initializeKeys();
    }

    private void initializeKeys() throws Exception {
        Map<String, String> keys = loadKeysFromFile();

        if (keys.isEmpty() || !keys.containsKey("publicKey") || !keys.containsKey("privateKey")) {
            // 文件不存在或密钥不完整，生成新的密钥对
            generateNewKeys();
            saveKeysToFile();
            System.out.println("生成新的密钥对");
        } else {
            // 从文件加载密钥
            loadKeysFromMap(keys);
        }
    }

    private void generateNewKeys() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
        keyPairGenerator.initialize(KEY_SIZE);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        this.publicKey = keyPair.getPublic();
        this.privateKey = keyPair.getPrivate();
    }

    private void loadKeysFromMap(Map<String, String> keys) throws Exception {
        String publicKeyStr = keys.get("publicKey");
        String privateKeyStr = keys.get("privateKey");

        // 解码公钥
        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyStr);
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        this.publicKey = keyFactory.generatePublic(publicKeySpec);

        // 解码私钥
        byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyStr);
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        this.privateKey = keyFactory.generatePrivate(privateKeySpec);
    }

    /**
     * 保存密钥到文件
     */
    public void saveKeysToFile() throws IOException {
        // 确保目录存在
        Files.createDirectories(Paths.get(keyFilePath));

        // 保存公钥
        String publicKeyPath = keyFilePath + "public.key";
        try (FileWriter writer = new FileWriter(publicKeyPath)) {
            writer.write("-----BEGIN PUBLIC KEY-----\n");
            writer.write(getPublicKey());
            writer.write("\n-----END PUBLIC KEY-----");
        }

        // 保存私钥
        String privateKeyPath = keyFilePath + "private.key";
        try (FileWriter writer = new FileWriter(privateKeyPath)) {
            writer.write("-----BEGIN PRIVATE KEY-----\n");
            writer.write(getPrivateKey());
            writer.write("\n-----END PRIVATE KEY-----");
        }
    }

    /**
     * 从文件加载密钥
     */
    public Map<String, String> loadKeysFromFile() throws IOException {
        Map<String, String> keys = new HashMap<>();

        String publicKeyPath = keyFilePath + "public.key";
        String privateKeyPath = keyFilePath + "private.key";

        if (Files.exists(Paths.get(publicKeyPath)) && Files.exists(Paths.get(privateKeyPath))) {
            // 读取公钥
            String publicKeyContent = new String(Files.readAllBytes(Paths.get(publicKeyPath)));
            keys.put("publicKey", extractKeyContent(publicKeyContent));

            // 读取私钥
            String privateKeyContent = new String(Files.readAllBytes(Paths.get(privateKeyPath)));
            keys.put("privateKey", extractKeyContent(privateKeyContent));
        }

        return keys;
    }

    private String extractKeyContent(String keyContent) {
        return keyContent.replaceAll("-----BEGIN [A-Z ]+-----", "")
                .replaceAll("-----END [A-Z ]+-----", "")
                .replaceAll("\\s", "");
    }

    public String getPublicKey() {
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }

    private String getPrivateKey() {
        return Base64.getEncoder().encodeToString(privateKey.getEncoded());
    }

    public String decrypt(String encryptedData) throws Exception {
        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedData);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return new String(decryptedBytes);
    }

    public String encrypt(String data) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedBytes = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }
}
