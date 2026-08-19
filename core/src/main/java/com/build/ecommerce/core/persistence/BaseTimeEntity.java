package com.build.ecommerce.core.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(value = {AuditingEntityListener.class})
@Getter
public abstract class BaseTimeEntity {

    @CreatedDate
    @Column(name = "regAt", updatable = false)
    private LocalDateTime regAt;

    @LastModifiedDate
    @Column(name = "modAt")
    private LocalDateTime modAt;
}
