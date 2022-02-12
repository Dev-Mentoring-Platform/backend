package com.project.mentoridge.config.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class Response {

    private static final ResponseEntity<?> OK = ResponseEntity.ok().build();
    private static final ResponseEntity<?> CREATED = ResponseEntity.status(HttpStatus.CREATED).build();

    public static ResponseEntity<?> ok() {
        return OK;
    }

    public static ResponseEntity<?> created() {
        return CREATED;
    }
}
