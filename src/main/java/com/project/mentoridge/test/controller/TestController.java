package com.project.mentoridge.test.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tests")
public class TestController {

    @GetMapping("/exception")
    public ResponseEntity<?> getErrorTest() throws RuntimeException {
        boolean errorFlag = true;
        if (Boolean.TRUE.equals(errorFlag)) {
            throw new RuntimeException();
        }
        return ResponseEntity.internalServerError().build();
    }

}
