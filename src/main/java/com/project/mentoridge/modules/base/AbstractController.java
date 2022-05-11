package com.project.mentoridge.modules.base;

import com.project.mentoridge.config.exception.UnauthorizedException;
import com.project.mentoridge.config.security.PrincipalDetails;
import com.project.mentoridge.modules.account.enums.RoleType;
import com.project.mentoridge.modules.account.vo.User;

public abstract class AbstractController {


    public static User checkAuthority(PrincipalDetails principalDetails, RoleType roleType) {
        String role = principalDetails.getAuthority();
        if (!role.equals(roleType.getType())) {
            throw new UnauthorizedException(roleType);
        }
        return principalDetails.getUser();
    }

    public static User checkMentorAuthority(PrincipalDetails principalDetails) {
        return checkAuthority(principalDetails, RoleType.MENTOR);
    }

}
