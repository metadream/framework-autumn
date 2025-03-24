package com.arraywork.autumn.crypto;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES Cipher
 *
 * @author Marco
 * @copyright ArrayWork Inc.
 * @since 2024/12/10
 */
public class AesCipher {

    public static String encrypt(String input, String key) throws GeneralSecurityException {
        byte[] keyBytes = generateKey(key, 16);
        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] cipherText = cipher.doFinal(reverse(input.getBytes()));
        return Base64.getUrlEncoder().encodeToString(cipherText);
    }

    public static String decrypt(String cipherText, String key) throws GeneralSecurityException {
        byte[] keyBytes = generateKey(key, 16);
        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] plainText = cipher.doFinal(Base64.getUrlDecoder().decode(cipherText));
        return new String(reverse(plainText));
    }

    /** 生成指定长度密钥 (AES-128必须是16个字节的密钥) */
    private static byte[] generateKey(String key, int length) throws NoSuchAlgorithmException {
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] keyBytes = sha.digest(xor(key.getBytes()));
        byte[] bytes = new byte[length];
        System.arraycopy(keyBytes, 0, bytes, 0, length);
        return bytes;
    }

    /** 反转混淆 */
    private static byte[] reverse(byte[] bytes) {
        int length = bytes.length;
        for (int i = 0; i < length / 2; i++) {
            int j = length - 1 - i;
            byte temp = bytes[i];
            bytes[i] = bytes[j];
            bytes[j] = temp;
        }
        return bytes;
    }

    /** 异或运算 */
    private static byte[] xor(byte[] bytes) {
        int length = bytes.length;
        for (int i = 0; i < length / 2; i++) {  // 首尾相互异或
            int j = length - 1 - i;
            byte temp = bytes[i];
            bytes[i] = (byte) (bytes[i] ^ bytes[j]);
            bytes[j] = (byte) (bytes[j] ^ temp);
        }
        return bytes;
    }

}