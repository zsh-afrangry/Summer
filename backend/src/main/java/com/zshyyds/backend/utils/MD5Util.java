package com.zshyyds.backend.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5加密工具类
 * 
 * 学习目的：演示如何在Java中应用MD5哈希算法
 * 注意：MD5在生产环境中不推荐使用，这里仅用于学习
 */
public class MD5Util {
    
    /**
     * 对输入字符串进行MD5哈希加密
     * 
     * @param input 需要加密的原始字符串
     * @return MD5哈希后的32位十六进制字符串
     */
    public static String encrypt(String input) {
        try {
            // 1. 获取MD5算法的MessageDigest实例
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            
            // 2. 将输入字符串转换为字节数组，并进行哈希计算
            byte[] hashBytes = md5.digest(input.getBytes());
            
            // 3. 将字节数组转换为十六进制字符串
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                // 将每个字节转换为两位十六进制数
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0'); // 补零确保是两位
                }
                hexString.append(hex);
            }
            
            return hexString.toString();
            
        } catch (NoSuchAlgorithmException e) {
            // 理论上不会发生，因为MD5是标准算法
            throw new RuntimeException("MD5算法不可用", e);
        }
    }
    
    /**
     * 验证原始密码与哈希值是否匹配
     * 
     * @param rawPassword 原始密码
     * @param hashedPassword 存储的哈希值
     * @return 是否匹配
     */
    public static boolean verify(String rawPassword, String hashedPassword) {
        // 对原始密码进行MD5加密，然后与存储的哈希值比较
        String encrypted = encrypt(rawPassword);
        return encrypted.equals(hashedPassword);
    }
    
    /**
     * 测试方法：演示MD5加密效果
     */
    public static void main(String[] args) {
        String[] testPasswords = {"123456", "password", "admin", "MySecretPwd!"};
        
        System.out.println("=== MD5加密演示 ===");
        for (String password : testPasswords) {
            String hashed = encrypt(password);
            System.out.printf("原始密码: %-15s -> MD5: %s%n", password, hashed);
        }
        
        System.out.println("\n=== 验证演示 ===");
        String testPassword = "123456";
        String hashedPassword = encrypt(testPassword);
        boolean isValid = verify(testPassword, hashedPassword);
        System.out.printf("密码验证: %s -> %s%n", testPassword, isValid ? "通过" : "失败");
    }
}