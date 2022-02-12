package com.project.mentoridge.modules.account.controller.request;

import com.project.mentoridge.modules.account.enums.EducationLevelType;
import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Data
public class EducationUpdateRequest {

    @ApiModelProperty(value = "최종학력", example = "", required = false)
    private EducationLevelType educationLevel;

    @ApiModelProperty(value = "학교명", example = "school", required = false)
    private String schoolName;

    @ApiModelProperty(value = "전공", example = "computer science", required = false)
    private String major;

    @ApiModelProperty(value = "그 외 학력", example = "", required = false)
    private String others;

    @Builder(access = AccessLevel.PRIVATE)
    private EducationUpdateRequest(EducationLevelType educationLevel, String schoolName, String major, String others) {
        this.educationLevel = educationLevel;
        this.schoolName = schoolName;
        this.major = major;
        this.others = others;
    }

    public static EducationUpdateRequest of(EducationLevelType educationLevel, String schoolName, String major, String others) {
        return EducationUpdateRequest.builder()
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
//    @ApiModelProperty(value = "졸업일자", example = "2021-09-01", required = false)
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
