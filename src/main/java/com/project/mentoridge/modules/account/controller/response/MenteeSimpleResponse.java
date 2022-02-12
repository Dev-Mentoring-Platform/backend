package com.project.mentoridge.modules.account.controller.response;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class MenteeSimpleResponse {

    private Long menteeId;
    private Long userId;
    private String name;
    // private List<Long> lectureIds;

    @Builder
    private MenteeSimpleResponse(Long menteeId, Long userId, String name) {
        this.menteeId = menteeId;
        this.userId = userId;
        this.name = name;
    }
}
