package com.project.mentoridge.modules.purchase.controller.request;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CancellationCreateRequest {

    @NotBlank
    private String reason;

    private CancellationCreateRequest(String reason) {
        this.reason = reason;
    }

    public static CancellationCreateRequest of(String reason) {
        return new CancellationCreateRequest(reason);
    }
}
