package com.arraywork.springforce.util;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Array and List Utilities
 *
 * @author AiChen
 * @copyright ArrayWork Inc.
 * @since 2024/04/28
 */
public class Arrays {

    // Is empty or not?
    public static boolean isEmpty(String[] array) {
        return array == null || array.length == 0;
    }

    // Truncate a portion of the array
    public static String[] slice(String[] source, int from, int to) {
        return java.util.Arrays.copyOfRange(source, from, to);
    }

    // Find any matched elements in array
    public static <T> T findAny(T[] array, Predicate<T> predicate) {
        List<T> list = java.util.Arrays.asList(array);
        return findAny(list, predicate);
    }

    // Find any matched elements in list
    public static <T> T findAny(Collection<T> list, Predicate<T> predicate) {
        return list.stream().filter(predicate).findAny().orElse(null);
    }

    // Find all matched elements in array
    public static <T> List<T> filter(T[] array, Predicate<T> predicate) {
        List<T> list = java.util.Arrays.asList(array);
        return list.stream().filter(predicate).collect(Collectors.toList());
    }

    // Find all matched elements in list
    public static <T> List<T> filter(Collection<T> list, Predicate<T> predicate) {
        return list.stream().filter(predicate).collect(Collectors.toList());
    }

    // Combine specific elements in a list into a new list
    public static <T, R> List<R> map(Collection<T> list, Function<T, R> mapper) {
        return list.stream().map(mapper).collect(Collectors.toList());
    }

}