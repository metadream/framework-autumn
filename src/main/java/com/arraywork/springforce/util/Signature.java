package com.arraywork.springforce.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.util.StringUtils;

/**
 * 工具类：API参数签名
 * @author AiChen
 * @created 2022/12/12
 */
public class Signature {

    /**
     * 生成签名哈希值
     * @param params
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String makeSignature(Map<String, String> params) throws UnsupportedEncodingException {
        // 1. 将参数按ASCII编码排序
        Map<String, String> treeMap = new TreeMap<>(params);
        // 2. 将参数转换为查询字符串形式
        String queryString = makeQueryString(treeMap);
        // 3. 对查询字符串进行哈希
        return Digest.md5(queryString);
    }

    /**
     * 将Map对象参数转换为查询字符串形式（忽略空值）
     * @param params
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String makeQueryString(Map<String, String> params) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (!StringUtils.hasText(entry.getValue())) continue;
            if (sb.length() > 0) sb.append("&");
            sb.append(String.format("%s=%s",
                URLEncoder.encode(entry.getKey(), "UTF-8"),
                URLEncoder.encode(entry.getValue(), "UTF-8")));
        }
        return sb.toString();
    }

}