package com.arraywork.springforce;

import java.io.Serializable;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import com.arraywork.springforce.util.Digest;

/**
 * Nano Id Generator for JPA
 * 
 * @Id
 * @Column(length = 24, insertable = false, updatable = false)
 * @GenericGenerator(name = "nano-id-generator", type = NanoIdGenerator.class)
 * @GeneratedValue(generator = "nano-id-generator")
 * 
 * @author AiChen
 * @copyright ArrayWork Inc.
 * @created 2024/02/26
 */
public class NanoIdGenerator implements IdentifierGenerator {

    private static final long serialVersionUID = -4516258547979177329L;

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object obj) {
        return Digest.nanoId();
    }

}