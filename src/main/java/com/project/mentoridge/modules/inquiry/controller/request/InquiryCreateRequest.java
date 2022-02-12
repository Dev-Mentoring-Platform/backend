package com.project.mentoridge.modules.inquiry.controller.request;

import com.project.mentoridge.modules.inquiry.enums.InquiryType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InquiryCreateRequest {

    @NotNull(message = "문의 유형을 선택해주세요.")
    private InquiryType inquiryType;
    @NotBlank(message = "제목을 입력해주세요.")
    private String title;
    @NotBlank(message = "상세 내용을 작성해주세요.")
    private String content;

    @Builder(access = AccessLevel.PRIVATE)
    private InquiryCreateRequest(InquiryType inquiryType, String title, String content) {
        this.inquiryType = inquiryType;
        this.title = title;
        this.content = content;
    }

    public static InquiryCreateRequest of(InquiryType inquiryType, String title, String content) {
        return InquiryCreateRequest.builder()
                .inquiryType(inquiryType)
                .title(title)
                .content(content)
                .build();
    }
}
