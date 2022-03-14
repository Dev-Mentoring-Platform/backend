package com.project.mentoridge.modules.log.vo;

import com.project.mentoridge.config.exception.EntityNotFoundException;
import com.project.mentoridge.modules.log.enums.ManipulationType;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import static com.project.mentoridge.modules.log.enums.ManipulationType.*;

@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter //@Setter
@Document(collection = "logs")
public class Log {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;

    private ManipulationType type;
    private String username;
    private String content;

    @Builder(access = AccessLevel.PRIVATE)
    private Log(ManipulationType type, String username, String content) {
        this.type = type;
        this.username = username;
        this.content = content;
    }

    public static Log buildSelectLog(String username, String content) {
        return Log.builder()
                .type(SELECT)
                .username(username)
                .content(content)
                .build();
    }

    public static Log buildInsertLog(String username, String content) {
        return Log.builder()
                .type(INSERT)
                .username(username)
                .content(content)
                .build();
    }

    public static Log buildUpdateLog(String username, String content) {
        return Log.builder()
                .type(UPDATE)
                .username(username)
                .content(content)
                .build();
    }

    public static Log buildDeleteLog(String username, String content) {
        return Log.builder()
                .type(DELETE)
                .username(username)
                .content(content)
                .build();
    }

}
