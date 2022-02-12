package com.project.mentoridge.modules.account.controller.response;

import com.project.mentoridge.modules.account.enums.EducationLevelType;
import com.project.mentoridge.modules.account.vo.Education;
import lombok.Data;

@Data
public class EducationResponse {

    private EducationLevelType educationLevel;
    private String schoolName;
    private String major;
    private String others;

    public EducationResponse(Education education) {
        this.educationLevel = education.getEducationLevel();
        this.schoolName = education.getSchoolName();
        this.major = education.getMajor();
        this.others = education.getOthers();
    }
}
