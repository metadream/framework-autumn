package com.arraywork.springforce.util;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.util.Assert;

/**
 * Generate random numbers within a given interval
 *
 * @author AiChen
 * @copyright ArrayWork Inc.
 * @since 2024/11/05
 */
public class IntervalRandom {

    private static Random random = ThreadLocalRandom.current();

    /**
     * Generate a random number in a given interval
     *
     * @param origin The least value that can be returned
     * @param bound  The upper bound (exclusive) for the returned value
     * @return
     */
    public static int nextInt(int origin, int bound) {
        return random.nextInt(origin, bound);
    }

    /**
     * Generate a set of unique random numbers in a given interval
     *
     * @param origin The least value that can be returned
     * @param bound  The upper bound (exclusive) for the returned value
     * @param size   Number of random result (positive integer and cannot exceed the given interval)
     */
    public static Set<Integer> nextInt(int origin, int bound, int size) {
        Assert.isTrue(size > 0 && size <= bound - origin,
            "The size of random numbers must be a positive integer and cannot exceed the given interval");
        Set<Integer> numbers = new HashSet<>();
        while (numbers.size() < size) {
            Integer next = random.nextInt(origin, bound);
            numbers.add(next);
        }
        return numbers;
    }

}