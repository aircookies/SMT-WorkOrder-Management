package com.aircookies.smtworkordermanagement.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("RSA工具类测试")
public class RSAUtilTest {

    @Autowired
    private RSAUtil rsaUtil;

    private static final String PLAIN_TEXT = "Hello, RSA Encryption!";
    private static final String CHINESE_TEXT = "你好，RSA加密测试！";
    private static final String EMPTY_TEXT = "";
    private static final String LONG_TEXT = "This is a longer text to test RSA encryption with more data. " +
            "RSA encryption has limitations on the key size, but 2048-bit keys should handle this text.";

    @BeforeEach
    @DisplayName("初始化测试环境")
    public void setUp() {
        assertNotNull(rsaUtil, "RSAUtil实例不应为空");
    }

    @Test
    @DisplayName("测试获取公钥")
    public void testGetPublicKey() {
        String publicKey = rsaUtil.getPublicKey();

        assertNotNull(publicKey, "公钥不应为空");
        assertFalse(publicKey.isEmpty(), "公钥长度应大于0");

        byte[] decodedKey = java.util.Base64.getDecoder().decode(publicKey);
        assertTrue(decodedKey.length > 0, "公钥解码后应有内容");
    }

    @Test
    @DisplayName("测试多次获取公钥一致性")
    public void testGetPublicKeyConsistency() {
        String publicKey1 = rsaUtil.getPublicKey();
        String publicKey2 = rsaUtil.getPublicKey();

        assertEquals(publicKey1, publicKey2, "多次获取公钥应返回相同值");
    }

    @Test
    @DisplayName("测试加密和解密英文文本")
    public void testEncryptAndDecryptEnglishText() throws Exception {
        String encrypted = rsaUtil.encrypt(PLAIN_TEXT);
        assertNotNull(encrypted, "加密结果不应为空");
        assertFalse(encrypted.isEmpty(), "加密结果长度应大于0");

        String decrypted = rsaUtil.decrypt(encrypted);
        assertEquals(PLAIN_TEXT, decrypted, "解密后的文本应与原文本一致");
    }

    @Test
    @DisplayName("测试加密和解密中文文本")
    public void testEncryptAndDecryptChineseText() throws Exception {
        String encrypted = rsaUtil.encrypt(CHINESE_TEXT);
        assertNotNull(encrypted, "加密结果不应为空");

        String decrypted = rsaUtil.decrypt(encrypted);
        assertEquals(CHINESE_TEXT, decrypted, "解密后的中文文本应与原文本一致");
    }

    @Test
    @DisplayName("测试加密和解密空字符串")
    public void testEncryptAndDecryptEmptyText() throws Exception {
        String encrypted = rsaUtil.encrypt(EMPTY_TEXT);
        assertNotNull(encrypted, "加密结果不应为空");

        String decrypted = rsaUtil.decrypt(encrypted);
        assertEquals(EMPTY_TEXT, decrypted, "解密后的空字符串应与原文本一致");
    }

    @Test
    @DisplayName("测试加密和解密长文本")
    public void testEncryptAndDecryptLongText() throws Exception {
        String encrypted = rsaUtil.encrypt(LONG_TEXT);
        assertNotNull(encrypted, "加密结果不应为空");

        String decrypted = rsaUtil.decrypt(encrypted);
        assertEquals(LONG_TEXT, decrypted, "解密后的长文本应与原文本一致");
    }

    @Test
    @DisplayName("测试加密结果的Base64编码")
    public void testEncryptedDataIsBase64Encoded() throws Exception {
        String encrypted = rsaUtil.encrypt(PLAIN_TEXT);

        assertDoesNotThrow(() -> {
            java.util.Base64.getDecoder().decode(encrypted);
        }, "加密结果应为有效的Base64编码");
    }

    @Test
    @DisplayName("测试不同明文生成不同密文")
    public void testDifferentPlainTextGeneratesDifferentCipherText() throws Exception {
        String encrypted1 = rsaUtil.encrypt("text1");
        String encrypted2 = rsaUtil.encrypt("text2");

        assertNotEquals(encrypted1, encrypted2, "不同明文应生成不同的密文");
    }

    @Test
    @DisplayName("测试相同明文生成不同密文")
    public void testSamePlainTextGeneratesDifferentCipherText() throws Exception {
        String encrypted1 = rsaUtil.encrypt(PLAIN_TEXT);
        String encrypted2 = rsaUtil.encrypt(PLAIN_TEXT);

        assertNotEquals(encrypted1, encrypted2, "相同明文应生成不同的密文（RSA使用随机填充）");
    }


    @Test
    @DisplayName("测试无效Base64解密抛出异常")
    public void testDecryptInvalidBase64ThrowsException() {
        String invalidBase64 = "invalid_base64_string!!!";

        assertThrows(Exception.class, () -> {
            rsaUtil.decrypt(invalidBase64);
        }, "无效的Base64字符串解密应抛出异常");
    }

    @Test
    @DisplayName("测试无效密文解密抛出异常")
    public void testDecryptInvalidCipherTextThrowsException() {
        String validBase64ButInvalidCipher = java.util.Base64.getEncoder()
                .encodeToString("invalid_encrypted_data".getBytes());

        assertThrows(Exception.class, () -> {
            rsaUtil.decrypt(validBase64ButInvalidCipher);
        }, "无效密文解密应抛出异常");
    }

    @Test
    @DisplayName("测试加密解密特殊字符")
    public void testEncryptAndDecryptSpecialCharacters() throws Exception {
        String specialChars = "!@#$%^&*()_+-=[]{}|;:',.<>?/~`";

        String encrypted = rsaUtil.encrypt(specialChars);
        String decrypted = rsaUtil.decrypt(encrypted);

        assertEquals(specialChars, decrypted, "特殊字符加密解密后应与原文本一致");
    }

    @Test
    @DisplayName("测试加密解密包含数字的文本")
    public void testEncryptAndDecryptTextWithNumbers() throws Exception {
        String textWithNumbers = "Test123 with numbers 456 and 789";

        String encrypted = rsaUtil.encrypt(textWithNumbers);
        String decrypted = rsaUtil.decrypt(encrypted);

        assertEquals(textWithNumbers, decrypted, "包含数字的文本加密解密后应与原文本一致");
    }

    @Test
    @DisplayName("测试加密解密换行符和空白字符")
    public void testEncryptAndDecryptWhitespaceCharacters() throws Exception {
        String whitespaceText = "Line1\nLine2\r\nLine3\tTab   Spaces";

        String encrypted = rsaUtil.encrypt(whitespaceText);
        String decrypted = rsaUtil.decrypt(encrypted);

        assertEquals(whitespaceText, decrypted, "包含空白字符的文本加密解密后应与原文本一致");
    }

    @Test
    @DisplayName("测试公钥格式正确性")
    public void testPublicKeyFormat() throws Exception {
        String publicKey = rsaUtil.getPublicKey();

        byte[] decodedBytes = java.util.Base64.getDecoder().decode(publicKey);

        assertTrue(decodedBytes.length >= 294,
                "RSA 2048位公钥解码后长度应至少为294字节（X.509格式）");
    }

    @Test
    @DisplayName("测试加密结果不为空且不等于原文")
    public void testEncryptedResultIsValid() throws Exception {
        String encrypted = rsaUtil.encrypt(PLAIN_TEXT);

        assertNotNull(encrypted, "加密结果不应为空");
        assertNotEquals(PLAIN_TEXT, encrypted, "加密结果不应等于原文");
        assertTrue(encrypted.length() > PLAIN_TEXT.length(),
                "加密结果长度通常应大于原文长度");
    }

    @Test
    @DisplayName("测试完整加密解密流程")
    public void testCompleteEncryptionDecryptionFlow() throws Exception {
        String originalText = "Complete flow test";

        String publicKey = rsaUtil.getPublicKey();
        assertNotNull(publicKey, "公钥应可获取");

        String encrypted = rsaUtil.encrypt(originalText);
        assertNotNull(encrypted, "加密应成功");

        String decrypted = rsaUtil.decrypt(encrypted);
        assertEquals(originalText, decrypted, "解密结果应与原文一致");
    }

    @Test
    @DisplayName("测试多次加密解密循环")
    public void testMultipleEncryptionDecryptionCycles() throws Exception {
        for (int i = 0; i < 5; i++) {
            String testText = "Cycle test " + i;
            String encrypted = rsaUtil.encrypt(testText);
            String decrypted = rsaUtil.decrypt(encrypted);
            assertEquals(testText, decrypted,
                    "第" + (i + 1) + "次加密解密循环应成功");
        }
    }
}
