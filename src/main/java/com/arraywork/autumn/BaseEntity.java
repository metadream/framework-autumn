package com.arraywork.autumn;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.arraywork.autumn.id.NanoIdGeneration;

import lombok.Data;

/**
 * Base Entity
 *
 * @author Marco
 * @created 2025/04/01
 */
@Data
@MappedSuperclass
public class BaseEntity {

    @Id
    @NanoIdGeneration
    @Column(length = 24, insertable = false, updatable = false)
    private String id;

    @CreationTimestamp
    private LocalDateTime createdTime;
    @UpdateTimestamp
    private LocalDateTime updatedTime;

}