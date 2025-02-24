package com.arraywork.summer.util;

import java.util.NoSuchElementException;

/**
 * Generate Letters in Alphabetical Order
 *
 * @author Marco
 * @copyright ArrayWork Inc.
 * @since 2024/04/28
 */
public class AlphabeticalOrder {

    private static final char[] alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    private int index = 0;

    public char next() {
        if (index >= alphabet.length) throw new NoSuchElementException("The ordinal index exceeds alphabet length");
        return alphabet[index++];
    }

    public void reset() {
        index = 0;
    }

}