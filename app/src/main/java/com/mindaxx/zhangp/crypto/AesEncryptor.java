package com.mindaxx.zhangp.crypto;

import android.text.TextUtils;
import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Administrator on 2019/10/24.
 */

public class AesEncryptor {

    private final static String CODING = "UTF-8";
    private static final String VIPARA = "1269571569321021";
    private final static String key = "1234567890987654"; // 加密钥匙（length==16）

    /*
    * 通过密码加密生成秘钥
    * */
    public static String make16(String key) {
        String k = Base64.encodeToString(key.getBytes(), 0);
        if (k.length() < 16) {
            k = make16(k);
        } else {
            byte[] bytes = k.getBytes();
            byte sum = 0;
            for (int j = 0; j < bytes.length; j++) {
                sum ^= bytes[j];
            }
            for (int j = 0; j < bytes.length; j++) {
                bytes[j] ^= sum;
            }
            k = Base64.encodeToString(bytes, 0).substring(0, 16);
        }
        return k;
    }

    /**
     * 加密
     *
     * @param content 要加密内容
     * @return 加密之后的内容
     */
    public static String encrypt(String content) {
        try {
            byte[] result = encrypt(key.getBytes(), content.getBytes(CODING));
            return Base64.encodeToString(result, 0);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 解密
     *
     * @param content 加密内容
     */
    public static String decrypt(String content) {
        try {
            byte[] enc = Base64.decode(content, 0);
            byte[] result = decrypt(key.getBytes(), enc);
            return new String(result, CODING);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 加密
     *
     * @param key     加密钥匙（length==16）
     * @param content 要加密内容
     * @param coding  编码
     * @return 加密之后的内容
     */
    public static String encrypt(String key, String content, String coding) throws Exception {
        byte[] result = encrypt(key.getBytes(), content.getBytes(TextUtils.isEmpty(coding) ? CODING : coding));
        return Base64.encodeToString(result, 0);
    }

    /**
     * 加密
     *
     * @param key     加密钥匙（length==16）
     * @param content 要加密内容
     * @return 加密之后的内容
     */
    public static String encrypt(String content, String key) throws Exception {
        byte[] result = encrypt(key.getBytes(), content.getBytes(CODING));
        return Base64.encodeToString(result, 0);
    }

    /**
     * 解密
     *
     * @param key       解密钥匙（length==16）
     * @param encrypted 加密内容
     * @param coding    编码
     */
    public static String decrypt(String key, String encrypted, String coding) throws Exception {
        byte[] enc = Base64.decode(encrypted, 0);
        byte[] result = decrypt(key.getBytes(), enc);
        return new String(result, TextUtils.isEmpty(coding) ? CODING : coding);
    }

    /**
     * 解密
     *
     * @param key       解密钥匙（length=16）
     * @param encrypted 加密内容
     */
    public static String decrypt(String encrypted, String key) throws Exception {
        byte[] enc = Base64.decode(encrypted, 0);
        byte[] result = decrypt(key.getBytes(), enc);
        return new String(result, CODING);
    }

    private static byte[] encrypt(byte[] raw, byte[] content) throws Exception {
        IvParameterSpec zeroIv = new IvParameterSpec(VIPARA.getBytes());
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, zeroIv);
        return cipher.doFinal(content);
    }

    private static byte[] decrypt(byte[] raw, byte[] encrypted) throws Exception {
        IvParameterSpec zeroIv = new IvParameterSpec(VIPARA.getBytes());
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, zeroIv);
        return cipher.doFinal(encrypted);
    }

}
