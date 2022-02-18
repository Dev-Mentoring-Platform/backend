package com.project.mentoridge.modules.inquiry.controller.request;

import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.inquiry.enums.InquiryType;
import com.project.mentoridge.modules.inquiry.vo.Inquiry;
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
    private InquiryType type;
    @NotBlank(message = "제목을 입력해주세요.")
    private String title;
    @NotBlank(message = "상세 내용을 작성해주세요.")
    private String content;

    @Builder(access = AccessLevel.PUBLIC)
    private InquiryCreateRequest(InquiryType type, String title, String content) {
        this.type = type;
        this.title = title;
        this.content = content;
    }

    public Inquiry toEntity(User user) {
        return Inquiry.builder()
                .user(user)
                .type(type)
                .title(title)
                .content(content)
                .build();
    }
}
