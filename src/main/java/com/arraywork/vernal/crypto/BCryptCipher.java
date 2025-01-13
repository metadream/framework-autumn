package com.arraywork.vernal.crypto;

import com.arraywork.vernal.external.BCryptEncoder;

/**
 * BCrypt Cipher
 *
 * @author Marco
 * @copyright ArrayWork Inc.
 * @since 2025/01/13
 */
public class BCryptCipher {

    private static final BCryptEncoder BCRYPT_ENCODER = new BCryptEncoder();

    /** Encode raw password */
    public static String encode(CharSequence rawPassword) {
        return BCRYPT_ENCODER.encode(rawPassword);
    }

    /** Compare raw password and encoded password */
    public static boolean matches(CharSequence rawPassword, String encodedPassword) {
        return BCRYPT_ENCODER.matches(rawPassword, encodedPassword);
    }

}