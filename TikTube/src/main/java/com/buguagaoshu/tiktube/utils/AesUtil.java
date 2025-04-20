package com.buguagaoshu.tiktube.utils;

import com.buguagaoshu.tiktube.config.WebConstant;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-09-11 16:34
 * update          2025-04-20
 * */
@Slf4j
public class AesUtil {
    private static final String ALGORITHM = "AES/ECB/PKCS5Padding";
    private static volatile AesUtil instance;
    private final SecretKeySpec secretKey;

    // 私有构造函数
    private AesUtil(String secret) {
        this.secretKey = generateKey(secret);
    }

    /**
     * 获取单例实例
     * @return AesUtil实例
     */
    public static AesUtil getInstance() {
        if (instance == null) {
            synchronized (AesUtil.class) {
                if (instance == null) {
                    instance = new AesUtil(WebConstant.AES_KEY);
                }
            }
        }
        return instance;
    }

    /**
     * 生成密钥
     */
    private SecretKeySpec generateKey(String myKey) {
        try {
            byte[] key = myKey.getBytes(StandardCharsets.UTF_8);
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16); // AES-128
            return new SecretKeySpec(key, "AES");
        } catch (NoSuchAlgorithmException e) {
            log.error("密钥生成失败", e);
            throw new RuntimeException("密钥生成失败", e);
        }
    }

    /**
     * 加密
     * @param strToEncrypt 原文
     * @return 加密后数据
     */
    public String encrypt(String strToEncrypt) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encrypted = cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            log.error("加密时出错", e);
            throw new RuntimeException("加密失败", e);
        }
    }

    /**
     * 解密
     * @param strToDecrypt 密文
     * @return 解密后原文
     */
    public String decrypt(String strToDecrypt) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decoded = Base64.getDecoder().decode(strToDecrypt);
            byte[] decrypted = cipher.doFinal(decoded);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            log.error("解密失败 - 可能是密钥不正确或数据损坏", e);
            throw new RuntimeException("解密失败", e);
        } catch (Exception e) {
            log.error("解密时出错", e);
            throw new RuntimeException("解密失败", e);
        }
    }
}