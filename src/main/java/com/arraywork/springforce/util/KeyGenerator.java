package com.arraywork.springforce.util;

import java.io.Serializable;
import java.security.SecureRandom;
import java.util.UUID;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import com.arraywork.springforce.external.NanoIdUtils;

/**
 * Unique Primary Key Generator
 * @author AiChen
 * @copyright ArrayWork Inc.
 * @since 2024/01/25
 */
public class KeyGenerator {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    // NanoID: 24 characters base62 default
    public static String nanoId() {
        return nanoId(24);
    }

    // NanoID: specify characters length
    public static String nanoId(int length) {
        return nanoId(length, "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");
    }

    // NanoID: specify characters length and alphabet
    public static String nanoId(int length, String alphabet) {
        return NanoIdUtils.randomNanoId(SECURE_RANDOM, alphabet.toCharArray(), length);
    }

    // Positive Long ID: 64 bits, 8 bytes,
    public static long longId() {
        return Math.abs(SECURE_RANDOM.nextLong());
    }

    // UUID: 128 bits, 16 bytes, 32 characters
    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", "").toLowerCase();
    }

    /**
     * NanoID Generator Class for JPA
     * @Id
     * @Column(length = 24, insertable = false, updatable = false)
     * @GenericGenerator(name = "nano-id-generator", type = KeyGenerator.NanoId.class)
     * @GeneratedValue(generator = "nano-id-generator")
     */
    public static class NanoId implements IdentifierGenerator {

        private static final long serialVersionUID = longId();

        @Override
        public Serializable generate(SharedSessionContractImplementor session, Object obj) {
            return nanoId();
        }

    }

    /**
     * LongID Generator Class for JPA
     * WARNING: IF USING LONGID AS PRIMARY KEY, YOU MUST FIRST QUERY TO UPDATE DATA
     */
    public static class LongId implements IdentifierGenerator {

        private static final long serialVersionUID = longId();

        @Override
        public Serializable generate(SharedSessionContractImplementor session, Object obj) {
            return longId();
        }

    }

}