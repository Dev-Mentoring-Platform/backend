package com.project.mentoridge.modules.account.controller.response;

import com.project.mentoridge.modules.account.enums.RoleType;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.address.util.AddressUtils;
import lombok.Data;

@Data
public class UserResponse {

    private Long userId;
    private String username;
    // private String password;
    private RoleType role;
    private String name;
    private String gender;
    private String birthYear;
    private String phoneNumber;
    private String email;
    private String nickname;
    private String bio;
    private String image;
    private String zone;

    public UserResponse(User user) {
        this.userId = user.getId();
        this.username = user.getUsername();
        this.role = user.getRole();
        this.name = user.getName();
        this.gender = user.getGender() != null ? user.getGender().toString() : null;
        this.birthYear = user.getBirthYear();
        this.phoneNumber = user.getPhoneNumber();
        this.email = user.getEmail();
        this.nickname = user.getNickname();
        this.bio = user.getBio();
        this.image = user.getImage();
        this.zone = AddressUtils.convertEmbeddableToStringAddress(user.getZone());
    }
}
