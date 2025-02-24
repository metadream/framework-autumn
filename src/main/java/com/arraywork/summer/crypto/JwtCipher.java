package com.arraywork.summer.crypto;

import java.time.Instant;
import java.util.Map;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.Payload;

/**
 * Json Web Token Utils
 *
 * @author Marco
 * @copyright ArrayWork Inc.
 * @since 2024/12/10
 */
public class JwtCipher {

    /** Create JWT with map */
    public static String create(Map<String, ?> payload, String secretKey) {
        return create(payload, secretKey, null);
    }

    /** Create JWT with map (with expiration time) */
    public static String create(Map<String, ?> payload, String secretKey, Instant expiresAt) {
        JWTCreator.Builder builder = JWT.create().withPayload(payload);
        if (expiresAt != null) {
            builder.withExpiresAt(expiresAt);
        }
        return builder.sign(Algorithm.HMAC256(secretKey));
    }

    /** Verify JWT */
    public static Payload verify(String jwt, String secretKey) {
        try {
            return JWT.require(Algorithm.HMAC256(secretKey)).build().verify(jwt);
        } catch (JWTDecodeException e) {
            throw new IllegalArgumentException("Token decoding failed");
        } catch (SignatureVerificationException e) {
            throw new IllegalArgumentException("Token signature verification failed");
        } catch (TokenExpiredException e) {
            throw new IllegalArgumentException("Token has expired");
        } catch (Exception e) {
            throw e;
        }
    }

}