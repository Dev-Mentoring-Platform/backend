package com.project.mentoridge.modules.account.controller.request;

import com.project.mentoridge.modules.account.vo.Career;
import com.project.mentoridge.modules.account.vo.Mentor;
import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Data
public class CareerUpdateRequest {

    @ApiModelProperty(value = "직업", example = "engineer")
    private String job;

    @ApiModelProperty(value = "직장명", example = "mentoridge")
    private String companyName;

    @ApiModelProperty(value = "그 외 경력", example = "")
    private String others;

    @ApiModelProperty(value = "자격증", example = "")
    private String license;

    @Builder(access = AccessLevel.PUBLIC)
    public CareerUpdateRequest(String job, String companyName, String others, String license) {
        this.job = job;
        this.companyName = companyName;
        this.others = others;
        this.license = license;
    }

    public Career toEntity(Mentor mentor) {
        return Career.builder()
                .mentor(mentor)
                .job(job)
                .companyName(companyName)
                .others(others)
                .license(license)
                .build();
    }

//    @ApiModelProperty(value = "입사일자", example = "2007-12-01", required = false)
//    @NotBlank
//    private String startDate;
//
//    @ApiModelProperty(value = "퇴사일자", allowEmptyValue = true, required = false)
//    private String endDate;
//
//    @ApiModelProperty(value = "재직 여부", example = "true", required = true)
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
