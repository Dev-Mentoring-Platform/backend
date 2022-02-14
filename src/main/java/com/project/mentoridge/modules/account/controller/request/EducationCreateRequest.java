package com.project.mentoridge.modules.account.controller.request;

import com.project.mentoridge.modules.account.enums.EducationLevelType;
import com.project.mentoridge.modules.account.vo.Career;
import com.project.mentoridge.modules.account.vo.Education;
import com.project.mentoridge.modules.account.vo.Mentor;
import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EducationCreateRequest {

    @ApiModelProperty(value = "최종학력", example = "", required = false)
    private EducationLevelType educationLevel;

    @ApiModelProperty(value = "학교명", example = "school", required = false)
    private String schoolName;

    @ApiModelProperty(value = "전공", example = "computer science", required = false)
    private String major;

    @ApiModelProperty(value = "그 외 학력", example = "", required = false)
    private String others;

    @Builder(access = AccessLevel.PUBLIC)
    private EducationCreateRequest(EducationLevelType educationLevel, String schoolName, String major, String others) {
        this.educationLevel = educationLevel;
        this.schoolName = schoolName;
        this.major = major;
        this.others = others;
    }

    public Education toEntity(Mentor mentor) {
        return Education.builder()
                .mentor(mentor)
                .educationLevel(educationLevel)
                .schoolName(schoolName)
                .major(major)
                .others(others)
                .build();
    }

//    @ApiModelProperty(value = "입학일자", example = "2021-01-01", required = false)
//    @NotBlank
//    private String entranceDate;
//
//    @ApiModelProperty(value = "졸업일자", allowEmptyValue = true, required = false)
//    private String graduationDate;

//    @AssertTrue
//    private boolean isGraduationDate() {
//        boolean valid = true;
//
//        try {
//
//            LocalDate entranceDate = LocalDate.parse(getEntranceDate());
//            LocalDate graduationDate = null;
//
//            if (StringUtils.isNotEmpty(getGraduationDate())) {
//                graduationDate = LocalDate.parse(getGraduationDate());
//                valid = entranceDate.isBefore(graduationDate);
//            }
//
//        } catch (DateTimeParseException e) {
//            valid = false;
//        }
//
//        return valid;
//    }
}
