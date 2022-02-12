package com.project.mentoridge.modules.base;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public abstract class AbstractService {

    protected static final Integer PAGE_SIZE = 10;

    protected static PageRequest getPageRequest(Integer page) {
        return PageRequest.of(page - 1, PAGE_SIZE, Sort.by("id").ascending());
    }
}
