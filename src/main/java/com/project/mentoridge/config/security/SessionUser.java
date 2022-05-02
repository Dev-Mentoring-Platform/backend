package com.project.mentoridge.config.security;

import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.address.util.AddressUtils;
import lombok.Getter;

@Getter
public class SessionUser {

    private String username;
    private String name;
    private String nickname;
    private String zone;

    private String loginType;

    public SessionUser(PrincipalDetails principalDetails) {
        User user = principalDetails.getUser();
        this.username = user.getUsername();
        this.name = user.getName();
        this.nickname = user.getNickname();
        this.zone = AddressUtils.convertEmbeddableToStringAddress(user.getZone());
        this.loginType = principalDetails.getAuthorities().stream().findFirst().get().getAuthority();
    }
}
