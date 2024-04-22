package com.arraywork.springhood;

import java.io.Serializable;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import com.arraywork.springhood.util.Digest;

/**
 * Long Id Generator for JPA
 * Warning: When using longid as the primary key, you must first query to update data
 * 
 * @Id
 * @Column(length = 20, insertable = false, updatable = false)
 * @GenericGenerator(name = "long-id-generator", type = LongIdGenerator.class)
 * @GeneratedValue(generator = "long-id-generator")
 * 
 * @author AiChen
 * @copyright ArrayWork Inc.
 * @created 2024/02/26
 */
public class LongIdGenerator implements IdentifierGenerator {

    private static final long serialVersionUID = -6862333156340587235L;

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object obj) {
        return Digest.longId();
    }

}