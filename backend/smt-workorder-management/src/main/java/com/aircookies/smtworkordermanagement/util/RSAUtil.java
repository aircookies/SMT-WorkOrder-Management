package com.aircookies.smtworkordermanagement.util;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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

/**
 * RSA 加密工具类
 * <p>
 * 提供 RSA 密钥对的生成、加载、保存、加密和解密功能。
 * 主要用于前端登录时对密码进行加密传输，后端使用私钥解密后验证。
 * </p>
 *
 * <p>工作流程：</p>
 * <ol>
 *   <li>应用启动时（@PostConstruct），尝试从文件系统加载已有的密钥对</li>
 *   <li>如果密钥文件不存在，则自动生成新的 2048 位 RSA 密钥对并保存到文件</li>
 *   <li>前端通过 /publickey 接口获取公钥，用于加密登录密码</li>
 *   <li>后端使用私钥解密登录密码，再与数据库中的 BCrypt 哈希值比对</li>
 * </ol>
 *
 * <p>安全说明：</p>
 * <ul>
 *   <li>密钥文件路径通过 ${rsa.key-file-path} 配置，默认为 ./keys/</li>
 *   <li>私钥文件（private.key）不应提交到版本控制系统</li>
 *   <li>生产环境中应使用安全的密钥管理方案（如密钥管理服务）</li>
 * </ul>
 */
@Component
public class RSAUtil {
    /** RSA 算法名称 */
    private static final String ALGORITHM = "RSA";
    /** 密钥长度（2048 位，满足安全要求） */
    private static final int KEY_SIZE = 2048;

    /** 密钥文件存放路径，从配置文件注入 */
    @Value("${rsa.key-file-path:./keys/}")
    private String keyFilePath;

    /** RSA 公钥 */
    private PublicKey publicKey;
    /** RSA 私钥 */
    private PrivateKey privateKey;

    /**
     * 初始化方法
     * <p>
     * 在 Spring 容器初始化后自动调用，负责加载或生成 RSA 密钥对。
     * </p>
     *
     * @throws Exception 密钥加载或生成失败时抛出
     */
    @PostConstruct
    public void init() throws Exception {
        initializeKeys();
    }

    /**
     * 初始化密钥对
     * <p>
     * 优先从文件加载已有密钥，如果文件不存在或不完整则自动生成新密钥并保存。
     * </p>
     */
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

    /**
     * 生成新的 2048 位 RSA 密钥对
     */
    private void generateNewKeys() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
        keyPairGenerator.initialize(KEY_SIZE);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        this.publicKey = keyPair.getPublic();
        this.privateKey = keyPair.getPrivate();
    }

    /**
     * 从 Map 中加载密钥
     *
     * @param keys 包含 Base64 编码的公钥和私钥字符串的 Map
     */
    private void loadKeysFromMap(Map<String, String> keys) throws Exception {
        String publicKeyStr = keys.get("publicKey");
        String privateKeyStr = keys.get("privateKey");

        // 解码公钥（X.509 格式）
        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyStr);
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        this.publicKey = keyFactory.generatePublic(publicKeySpec);

        // 解码私钥（PKCS#8 格式）
        byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyStr);
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        this.privateKey = keyFactory.generatePrivate(privateKeySpec);
    }

    /**
     * 保存密钥对到文件系统
     * <p>
     * 公钥和私钥分别保存为 PEM 格式文件，包含 -----BEGIN/END----- 标记。
     * </p>
     *
     * @throws IOException 文件写入失败时抛出
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
     * 从文件系统加载密钥对
     *
     * @return 包含公钥和私钥（Base64编码）的 Map，如果文件不存在则返回空 Map
     * @throws IOException 文件读取失败时抛出
     */
    public Map<String, String> loadKeysFromFile() throws IOException {
        Map<String, String> keys = new HashMap<>();

        String publicKey = keyFilePath + "public.key";
        String privateKey = keyFilePath + "private.key";
        Path publicKeyPath = Paths.get(publicKey);
        Path privateKeyPath = Paths.get(privateKey);

        if (Files.exists(publicKeyPath) && Files.exists(privateKeyPath)) {
            // 读取公钥文件，去除 PEM 头尾标记和空白字符
            String publicKeyContent = new String(Files.readAllBytes(publicKeyPath));
            keys.put("publicKey", extractKeyContent(publicKeyContent));

            // 读取私钥文件，去除 PEM 头尾标记和空白字符
            String privateKeyContent = new String(Files.readAllBytes(privateKeyPath));
            keys.put("privateKey", extractKeyContent(privateKeyContent));
        }

        return keys;
    }

    /**
     * 从 PEM 格式内容中提取 Base64 编码的密钥内容
     * <p>
     * 去除 -----BEGIN ...----- 和 -----END ...----- 标记以及所有空白字符。
     * </p>
     *
     * @param keyContent PEM 格式的密钥文件内容
     * @return 纯净的 Base64 编码密钥字符串
     */
    private String extractKeyContent(String keyContent) {
        return keyContent.replaceAll("-----BEGIN [A-Z ]+-----", "")
                .replaceAll("-----END [A-Z ]+-----", "")
                .replaceAll("\\s", "");
    }

    /**
     * 获取 Base64 编码的公钥字符串
     * <p>
     * 供 /publickey 接口返回给前端，前端使用此公钥加密登录密码。
     * </p>
     *
     * @return Base64 编码的公钥
     */
    public String getPublicKey() {
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }

    /**
     * 获取 Base64 编码的私钥字符串
     *
     * @return Base64 编码的私钥
     */
    private String getPrivateKey() {
        return Base64.getEncoder().encodeToString(privateKey.getEncoded());
    }

    /**
     * 使用私钥解密数据
     * <p>
     * 用于解密前端使用公钥加密的登录密码。
     * </p>
     *
     * @param encryptedData Base64 编码的密文
     * @return 解密后的明文字符串
     * @throws Exception 解密失败时抛出
     */
    public String decrypt(String encryptedData) throws Exception {
        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedData);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return new String(decryptedBytes);
    }

    /**
     * 使用公钥加密数据
     *
     * @param data 明文数据
     * @return Base64 编码的密文
     * @throws Exception 加密失败时抛出
     */
    public String encrypt(String data) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedBytes = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }
}