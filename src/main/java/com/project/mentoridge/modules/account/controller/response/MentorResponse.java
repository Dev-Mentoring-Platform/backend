package com.project.mentoridge.modules.account.controller.response;

import com.project.mentoridge.modules.account.vo.Mentor;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class MentorResponse {

    private Long mentorId;
    private UserResponse user;
    private List<CareerResponse> careers;
    private List<EducationResponse> educations;

    public MentorResponse(Mentor mentor) {
        this.mentorId = mentor.getId();
        this.user = new UserResponse(mentor.getUser());
        this.careers = mentor.getCareers().stream().map(CareerResponse::new).collect(Collectors.toList());
        this.educations = mentor.getEducations().stream().map(EducationResponse::new).collect(Collectors.toList());
    }
}
