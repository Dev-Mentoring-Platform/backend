package com.project.mentoridge.modules.purchase.controller.request;

import com.project.mentoridge.modules.purchase.vo.Cancellation;
import com.project.mentoridge.modules.purchase.vo.Enrollment;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CancellationCreateRequest {

    @NotBlank
    private String reason;

    @Builder(access = AccessLevel.PUBLIC)
    private CancellationCreateRequest(String reason) {
        this.reason = reason;
    }

    public Cancellation toEntity(Enrollment enrollment) {
        return Cancellation.builder()
                .enrollment(enrollment)
                .reason(reason)
                .build();
    }
}
