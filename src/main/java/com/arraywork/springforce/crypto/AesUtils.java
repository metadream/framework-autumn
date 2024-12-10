package com.arraywork.springforce.crypto;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES Utils
 *
 * @author AiChen
 * @copyright ArrayWork Inc.
 * @since 2024/12/10
 */
public class AesUtils {

    public static String encrypt(String input, String key) throws GeneralSecurityException {
        byte[] keyBytes = generateKey(key, 16);
        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] cipherText = cipher.doFinal(input.getBytes());
        return Base64.getUrlEncoder().encodeToString(cipherText);
    }

    public static String decrypt(String cipherText, String key) throws GeneralSecurityException {
        byte[] keyBytes = generateKey(key, 16);
        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] plainText = cipher.doFinal(Base64.getUrlDecoder().decode(cipherText));
        return new String(plainText);
    }

    private static byte[] generateKey(String key, int length) throws NoSuchAlgorithmException {
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] keyBytes = sha.digest(key.getBytes());
        byte[] bytes = new byte[length];
        System.arraycopy(keyBytes, 0, bytes, 0, length);
        return bytes;
    }

}