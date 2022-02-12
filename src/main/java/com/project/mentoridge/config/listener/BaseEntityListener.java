package com.project.mentoridge.config.listener;

import com.project.mentoridge.modules.base.BaseEntity;

import javax.persistence.PreUpdate;
import java.time.LocalDateTime;

public class BaseEntityListener {

    @PreUpdate
    public void preUpdate(Object o) {
        if (o instanceof BaseEntity) {
            ((BaseEntity) o).setUpdatedAt(LocalDateTime.now());
        }
    }
}
