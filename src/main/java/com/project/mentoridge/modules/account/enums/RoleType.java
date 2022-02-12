package com.project.mentoridge.modules.account.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RoleType {

    ADMIN("ROLE_ADMIN", "관리자"),
    MENTOR("ROLE_MENTOR", "튜터"),
    MENTEE("ROLE_MENTEE", "튜티");

    private String type;
    private String name;
}
