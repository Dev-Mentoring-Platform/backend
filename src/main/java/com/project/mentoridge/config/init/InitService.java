package com.project.mentoridge.config.init;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Profile("dev")
@Component
@RequiredArgsConstructor
public class InitService {

    // @PostConstruct
    @Transactional
    void init() {

    }

}
