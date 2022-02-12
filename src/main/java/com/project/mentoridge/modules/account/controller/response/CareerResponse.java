package com.project.mentoridge.modules.account.controller.response;

import com.project.mentoridge.modules.account.vo.Career;
import lombok.Data;

@Data
public class CareerResponse {

    private String job;
    private String companyName;
    private String others;
    private String license;

    public CareerResponse(Career career) {
        this.job = career.getJob();
        this.companyName = career.getCompanyName();
        this.others = career.getOthers();
        this.license = career.getLicense();
    }
}
