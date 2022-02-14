package com.project.mentoridge.modules.account.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RoleType {

    ADMIN("ROLE_ADMIN", "관리자"),
    MENTOR("ROLE_MENTOR", "멘토"),
    MENTEE("ROLE_MENTEE", "멘티");

    private final String type;
    private final String name;
}
