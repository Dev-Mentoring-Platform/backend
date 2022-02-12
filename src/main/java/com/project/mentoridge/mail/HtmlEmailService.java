package com.project.mentoridge.mail;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Slf4j
@Component
@RequiredArgsConstructor
public class HtmlEmailService implements EmailService {

    private final JavaMailSender javaMailSender;

    @Override
    public void send(EmailMessage emailMessage) {

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        try {
            // (MimeMessage mimeMessage, boolean multipart, @Nullable String encoding)
            // MessagingException if multipart creation failed
            // TODO - CHECK : 왜 helper를 사용하는가?
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            mimeMessageHelper.setTo(emailMessage.getTo());
            mimeMessageHelper.setSubject(emailMessage.getSubject());
            mimeMessageHelper.setText(emailMessage.getContent(), true);

            javaMailSender.send(mimeMessage);
            // TODO - 로그 포맷
            log.info("[EMAIL] send to : {}", emailMessage.getTo());
            // log.info("sent email : {}", emailMessage.getContent());
        } catch (MessagingException e) {
            log.error("failed to send email : {}", e.getMessage());
            e.printStackTrace();
        }
    }

}
