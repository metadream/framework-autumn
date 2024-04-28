package com.arraywork.springforce.util;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.util.DigestUtils;

/**
 * Hash Digest Utilities
 * @author AiChen
 * @copyright ArrayWork Inc.
 * @since 2024/01/25
 */
public class Digest {

    // Signature for API request parameters
    public static String signature(Map<String, String> params) throws UnsupportedEncodingException {
        // Step 1. Sort the parameters by ASCII code
        Map<String, String> treeMap = new TreeMap<>(params);
        // Step 2. Convert the parameters to query string
        String queryString = Strings.buildQueryString(treeMap);
        // Step 3. Hash the query string
        return Digest.md5(queryString);
    }

    // MD5
    public static String md5(String str) {
        return DigestUtils.md5DigestAsHex(str.getBytes(StandardCharsets.UTF_8));
    }

    // SHA256
    public static String sha256(String str) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(str.getBytes(StandardCharsets.UTF_8));
        byte[] bs = md.digest();

        StringBuilder res = new StringBuilder();
        for (byte b : bs) res.append(String.format("%02x", b));
        return res.toString();
    }

}