package com.project.mentoridge.modules.account.controller.request;

import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.User;
import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MenteeUpdateRequest {

    @ApiModelProperty(value = "강의주제", example = "python,java", required = false)
    private String subjects;

    @Builder(access = AccessLevel.PUBLIC)
    private MenteeUpdateRequest(String subjects) {
        this.subjects = subjects;
    }

    public Mentee toEntity(User user) {
        return Mentee.builder()
                .user(user)
                .subjects(subjects)
                .build();
    }
}
