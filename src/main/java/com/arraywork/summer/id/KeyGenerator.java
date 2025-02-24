package com.arraywork.summer.id;

import java.io.Serializable;
import java.security.SecureRandom;
import java.util.UUID;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

/**
 * Unique Primary Key Generator
 *
 * @author Marco
 * @copyright ArrayWork Inc.
 * @since 2024/01/25
 */
public class KeyGenerator {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    /**
     * NanoID: 24 characters base62 default
     * The first character is a letter to apply to DOM id
     */
    public static String nanoId() {
        return nanoId(1, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ") + nanoId(23);
    }

    /** NanoID: specify characters length */
    public static String nanoId(int length) {
        return nanoId(length, "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");
    }

    /** NanoID: specify characters length and alphabet */
    public static String nanoId(int length, String alphabet) {
        return com.arraywork.summer.external.NanoId.randomNanoId(SECURE_RANDOM, alphabet.toCharArray(), length);
    }

    /** Positive Long ID: 64 bits, 8 bytes, */
    public static long longId() {
        return Math.abs(SECURE_RANDOM.nextLong());
    }

    /** UUID: 128 bits, 16 bytes, 32 characters */
    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", "").toLowerCase();
    }

    /**
     * NanoID Generator Class for JPA
     * Examples:
     * <pre>
     *   @Id
     *   @Column(length = 24, insertable = false, updatable = false)
     *   @GenericGenerator(name = "nano-id-generator", type = KeyGenerator.NanoId.class)
     *   @GeneratedValue(generator = "nano-id-generator")
     * </pre>
     */
    public static class NanoId implements IdentifierGenerator {
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
        @Override
        public Serializable generate(SharedSessionContractImplementor session, Object obj) {
            return longId();
        }
    }

}