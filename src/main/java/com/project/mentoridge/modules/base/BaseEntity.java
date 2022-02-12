package com.project.mentoridge.modules.base;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
//@Setter
@EqualsAndHashCode(of = "id")
@MappedSuperclass
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(updatable = false)
    protected LocalDateTime createdAt = LocalDateTime.now();
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
