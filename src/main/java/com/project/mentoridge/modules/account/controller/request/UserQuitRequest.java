package com.project.mentoridge.modules.account.controller.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.*;
import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Data
public class UserQuitRequest {

    // TODO - CHECK
    public static final Map<Integer, String> reasons = new HashMap<>();
    static {
        reasons.put(1, "마음에 드는 강의가 없어서");
        reasons.put(2, "이용이 불편하고 오류가 많아서");
        reasons.put(3, "강의 이용료가 부담돼서");
        reasons.put(4, "활용도가 낮아서");
        reasons.put(5, "다른 어플이 더 좋아서");
        reasons.put(6, "기타");
    }

    @NotNull
    @Min(1) @Max(6)
    private Integer reasonId;
    private String reason;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String password;

    @Builder(access = AccessLevel.PRIVATE)
    private UserQuitRequest(Integer reasonId, String reason, String password) {
        this.reasonId = reasonId;
        this.reason = reason;
        this.password = password;
    }

    public static UserQuitRequest of(Integer reasonId, String reason, String password) {
        return UserQuitRequest.builder()
                .reasonId(reasonId)
                .reason(reason)
                .password(password)
                .build();
    }

    @AssertTrue(message = "이유를 입력해주세요.")
    private boolean hasReason() {
        boolean valid = true;

        Integer reasonId = getReasonId();
        if (reasonId == null || (reasonId < 1 || reasonId > 6)) {
            valid = false;
        } else {

            if (reasonId == 6) {
                if (StringUtils.isBlank(getReason())) {
                    valid = false;
                }
            }
        }
        return valid;
    }

    public String getReason() {
        if (reasonId == reasons.size()) {
            return reason;
        }
        return reasons.get(reasonId);
    }

}
