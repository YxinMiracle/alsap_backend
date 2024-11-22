package com.yxinmiracle.alsap.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;

public class CryptoUtils {
    private static final String DES_KEY = "YxinMiracle-ALSAP"; // DES密钥原文
    private static final String INNER_REQ_VALUE = "askliyij%kkefoo)))Yxi0==@@cdfMiracle"; // DES密钥原文
    private static final String CHARSET = "UTF-8"; // 字符集
    private static final String DES_TRANSFORMATION = "DES/ECB/PKCS5Padding"; // DES算法/工作模式/填充方式

    private static final long TIME_THRESHOLD = 60000; // 1 minute in milliseconds


    /**
     * 解密使用DES加密的Base64编码字符串
     *
     * @param encryptedData 加密后的文本，Base64编码的字符串
     * @return 解密后的字符串
     * @throws Exception 可能抛出的异常，如密钥错误、算法错误等
     */
    public static String decryptAndDecodeBase64(String encryptedData) throws Exception {
        // 根据DES算法要求调整密钥长度为8字节
        byte[] keyBytes = Arrays.copyOf(DES_KEY.getBytes(CHARSET), 8);
        SecretKeySpec key = new SecretKeySpec(keyBytes, "DES");

        // 实例化Cipher对象，它用于完成实际的解密操作
        Cipher cipher = Cipher.getInstance(DES_TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, key);

        // Base64解码加密字符串
        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedData);

        // 解密
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

        // 将解密后的结果再次进行Base64解码
        byte[] decodedBytes = Base64.getDecoder().decode(new String(decryptedBytes, CHARSET));

        // 返回最终解密的原始数据
        return new String(decodedBytes, CHARSET);
    }

    // 判断header加密信息是否合法
    public static boolean validateRequest(String receivedEncryptedHeader, String timestamp, String randomValue, String key) {
        try {
            String reconstructedString = randomValue + key + timestamp;
            String computedHash = computeSHA256(reconstructedString);
            return receivedEncryptedHeader.equals(computedHash);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static String computeSHA256(String input) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
        return DatatypeConverter.printHexBinary(hash).toLowerCase();
    }

    /**
     * 解析内部请求是否合法
     *
     * @param signature
     * @return
     */
    public static boolean validateInnerRequest(String signature) {
        try {
            return signature.equals(INNER_REQ_VALUE);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 判断请求时间是否合法
    public static boolean isTimestampValid(long requestTimestamp) {
        long currentTimestamp = System.currentTimeMillis();
        return Math.abs(currentTimestamp - requestTimestamp) <= TIME_THRESHOLD;
    }


    public static void main(String[] args) {
        try {
            // 假设这是你从前端接收到的加密字符串
            String encryptedData = "U826XW6OceZeG9+FjrtXGpYFpsgczHWVwC2BjF0JBuyHTLkewah75w==";

            // 使用工具类解密
            String decryptedData = CryptoUtils.decryptAndDecodeBase64(encryptedData);
            System.out.println("Decrypted Data: " + decryptedData);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to decrypt data.");
        }
    }

}
