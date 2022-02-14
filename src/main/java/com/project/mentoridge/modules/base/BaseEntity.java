package com.project.mentoridge.modules.base;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

//@EntityListeners(AuditingEntityListener.class)
@Getter
//@Setter
@EqualsAndHashCode(of = "id")
@MappedSuperclass
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    //@CreatedDate
    @Column(updatable = false)
    protected LocalDateTime createdAt = LocalDateTime.now();
    //@LastModifiedDate
    @Column(insertable = false)
    protected LocalDateTime updatedAt;

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @PreUpdate
    public void preUpdate() {
        setUpdatedAt(LocalDateTime.now());
    }

}
