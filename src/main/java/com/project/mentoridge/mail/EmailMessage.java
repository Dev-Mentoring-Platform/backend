package com.project.mentoridge.mail;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EmailMessage {

    private String to;
    // private String from;
    private String subject;   // title
    private String content;

    @Builder
    public EmailMessage(String to, String subject, String content) {
        this.to = to;
        this.subject = subject;
        this.content = content;
    }
}
