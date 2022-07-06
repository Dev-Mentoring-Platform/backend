package com.project.mentoridge.modules.account.controller.response;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class SimpleMenteeResponse {

    private Long menteeId;
    private Long userId;
    private String name;
    private String nickname;

    private Long enrollmentId;

    @Builder
    private SimpleMenteeResponse(Long menteeId, Long userId, String name, String nickname, Long enrollmentId) {
        this.menteeId = menteeId;
        this.userId = userId;
        this.name = name;
        this.nickname = nickname;

        this.enrollmentId = enrollmentId;
    }
}
