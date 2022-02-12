package com.project.mentoridge.modules.account.controller.response;

import com.project.mentoridge.modules.account.vo.Mentee;
import lombok.Data;

@Data
public class MenteeResponse {

    private UserResponse user;
    private String subjects;

    public MenteeResponse(Mentee mentee) {
        this.user = new UserResponse(mentee.getUser());
        this.subjects = mentee.getSubjects();
    }
}
