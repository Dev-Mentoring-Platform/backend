package com.project.mentoridge.mail;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("test")
@Slf4j
@Component
@RequiredArgsConstructor
public class ConsoleEmailService implements EmailService {

    @Override
    public void send(EmailMessage emailMessage) {
        // TODO - 로그 포맷
        log.info("[EMAIL] send to : {}", emailMessage.getTo());
        log.info("sent email : {}", emailMessage.getContent());
    }

}
