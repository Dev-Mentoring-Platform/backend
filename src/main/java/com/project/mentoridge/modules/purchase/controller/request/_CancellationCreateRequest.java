package com.project.mentoridge.modules.purchase.controller.request;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class _CancellationCreateRequest {
/*
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
*/
}
