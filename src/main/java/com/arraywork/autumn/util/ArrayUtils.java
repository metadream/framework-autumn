package com.arraywork.autumn.util;

import java.util.Arrays;
import java.util.List;

/**
 * Array Utilities
 *
 * @author Marco
 * @copyright ArrayWork Inc.
 * @since 2025/11/15
 */
public class ArrayUtils {

    /** Sub array */
    public static List<String> subArray(String[] arr, int n) {
        if (arr == null) return null;
        if (arr.length == 0 || n <= 0) return List.of();
        n = Math.min(n, arr.length);
        return Arrays.asList(arr).subList(0, n);
    }

}