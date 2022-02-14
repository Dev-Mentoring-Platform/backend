package com.project.mentoridge.modules.account.controller.request;

import com.project.mentoridge.modules.account.vo.Career;
import com.project.mentoridge.modules.account.vo.Mentor;
import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CareerCreateRequest {

    @ApiModelProperty(value = "직업", example = "engineer", required = false)
    private String job;

    @ApiModelProperty(value = "직장명", example = "mentoridge", required = false)
    private String companyName;

    @ApiModelProperty(value = "그 외 경력", example = "", required = false)
    private String others;

    @ApiModelProperty(value = "자격증", example = "", required = false)
    private String license;

    @Builder(access = AccessLevel.PUBLIC)
    private CareerCreateRequest(String job, String companyName, String others, String license) {
        this.job = job;
        this.companyName = companyName;
        this.others = others;
        this.license = license;
    }

//    private Career toEntity() {
//        return Career.builder()
//                .job(job)
//                .companyName(companyName)
//                .others(others)
//                .license(license)
//                .build();
//    }

    public Career toEntity(Mentor mentor) {
        return Career.builder()
                .mentor(mentor)
                .job(job)
                .companyName(companyName)
                .others(others)
                .license(license)
                .build();
    }

//    @ApiModelProperty(value = "입사일자", example = "2007-12-03", required = false)
//    @NotBlank
//    private String startDate;
//
//    @ApiModelProperty(value = "퇴사일자", example = "2007-12-10", allowEmptyValue = true, required = false)
//    private String endDate;
//
//    @ApiModelProperty(value = "재직 여부", example = "false", required = true)
//    @NotNull
//    private boolean present;

//    @AssertTrue
//    private boolean isEndDate() {
//        boolean valid = true;
//
//        // - if present is true, endDate must be blank
//        // - if present is false, endDate must not be blank
//        if ((isPresent() && StringUtils.isNotEmpty(getEndDate())) ||
//                (!isPresent() && StringUtils.isEmpty(getEndDate()))) {
//            valid = false;
//            return valid;
//        }
//
//        try {
//
//            LocalDate startDate = LocalDate.parse(getStartDate());
//            LocalDate endDate = null;
//
//            if (StringUtils.isNotEmpty(getEndDate())) {
//                endDate = LocalDate.parse(getEndDate());
//                // - startDate < endDate
//                valid = startDate.isBefore(endDate);
//            }
//
//        } catch (DateTimeParseException e) {
//            valid = false;
//        }
//
//        return valid;
//    }
}
