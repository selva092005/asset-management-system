package com.learn.demo.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;

@Data
@MappedSuperclass
public abstract class BaseEntity {

    @Column(nullable = false)
    private boolean deleted = false;

    // 🔥 NEW FIELDS
    private String deletedBy;
    private LocalDateTime deletedAt;
}