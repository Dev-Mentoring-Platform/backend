package com.project.mentoridge.modules.account.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import static com.project.mentoridge.config.response.Response.ok;

@Slf4j
@Api(tags = {"MainController"})
@RestController
@RequiredArgsConstructor
public class MainController {

    @ApiIgnore
    @GetMapping("/")
    public ResponseEntity<?> main() {

        log.info("MainController : main");
        // Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        // System.out.println(principal);
        return ok();
    }
}