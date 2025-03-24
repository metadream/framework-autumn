package com.arraywork.autumn.type;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.GeneralSecurityException;
import jakarta.persistence.AttributeConverter;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.arraywork.autumn.crypto.AesCipher;

/**
 * Encrypted Decimal Converter
 *
 * @author Marco
 * @copyright ArrayWork Inc.
 * @since 2025/03/20
 */
public class EncryptedDecimalConverter implements AttributeConverter<BigDecimal, String> {

    @Override
    public String convertToDatabaseColumn(BigDecimal attribute) {
        try {
            if (attribute != null) {
                attribute = attribute.setScale(2, RoundingMode.HALF_UP);
                return AesCipher.encrypt(attribute.toString(), getDsk());
            }
            return null;
        } catch (GeneralSecurityException e) {
            throw new RuntimeException("Decimal encryption failed", e);
        }
    }

    @Override
    public BigDecimal convertToEntityAttribute(String dbData) {
        try {
            return dbData == null ? null : new BigDecimal(AesCipher.decrypt(dbData, getDsk()));
        } catch (GeneralSecurityException e) {
            throw new RuntimeException("Decimal decryption failed", e);
        }
    }

    private String getDsk() {
        String dsk = System.getProperty("D.S.K");
        Assert.isTrue(StringUtils.hasText(dsk), "The DSK not found in system properties");
        return dsk;
    }

}