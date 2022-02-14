package com.project.mentoridge.modules.account.vo;

import com.project.mentoridge.config.security.oauth.provider.OAuthType;
import com.project.mentoridge.modules.account.controller.request.SignUpOAuthDetailRequest;
import com.project.mentoridge.modules.account.controller.request.UserUpdateRequest;
import com.project.mentoridge.modules.account.enums.GenderType;
import com.project.mentoridge.modules.account.enums.RoleType;
import com.project.mentoridge.modules.address.embeddable.Address;
import com.project.mentoridge.modules.address.util.AddressUtils;
import com.project.mentoridge.modules.base.BaseEntity;
import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Where(clause = "deleted = false and email_verified = true")
@ToString(callSuper = true)
//@EqualsAndHashCode(callSuper = true)
@Getter
//@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AttributeOverride(name = "id", column = @Column(name = "user_id"))
//@Table(indexes = {@Index(name = "IDX_USERNAME", columnList = "username", unique = true),
//        @Index(name = "IDX_NICKNAME", columnList = "nickname", unique = true)})
@Entity
public class User extends BaseEntity {

    @Column(nullable = false, unique = true, updatable = false)
    private String username;    // 이메일
    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    private GenderType gender;

    private String birthYear;

    private String phoneNumber;
    private String email;

    @Column(nullable = false, unique = true)
    private String nickname;

    private String image;       // 프로필 이미지

    @Lob
    private String bio;         // 소개글

    // TODO - CHECK : 정적 테이블 관리
    @Embedded
    private Address zone;        // 지역
    // private String zone;

    @Enumerated(EnumType.STRING)
    private RoleType role;

    @Enumerated(EnumType.STRING)
    private OAuthType provider;
    private String providerId;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean emailVerified = false;
    private String emailVerifyToken;
    private LocalDateTime emailVerifiedAt;

    // UNIQUE
    @Lob
    private String fcmToken;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean deleted = false;
    private LocalDateTime deletedAt;
    @Lob
    private String quitReason;

    private LocalDateTime lastLoginAt;

    private int accusedCount = 0;

    // TODO - Notification과 양방향

    @Builder(access = AccessLevel.PUBLIC)
    private User(String username, String password, String name, String gender, String birthYear, String phoneNumber, String email, String nickname, String bio, String zone, String image,
                RoleType role, OAuthType provider, String providerId) {
        this.username = username;
        this.password = password;
        this.name = name;
        if (!StringUtils.isBlank(gender)) {
            this.gender = gender.equals("MALE") ? GenderType.MALE : GenderType.FEMALE;
        } else {
            this.gender = null;
        }
        this.birthYear = birthYear;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.nickname = nickname;
        this.bio = bio;
        this.zone = AddressUtils.convertStringToEmbeddableAddress(zone);
        this.image = image;
        this.role = role;
        this.provider = provider;
        this.providerId = providerId;
    }

//    public static User of(String username, String password, String name, String gender, String birthYear, String phoneNumber, String email, String nickname, String bio, String zone, String image,
//                                  RoleType role, OAuthType provider, String providerId) {
//        return User.builder()
//                .username(username)
//                .password(password)
//                .name(name)
//                .gender(gender)
//                .birthYear(birthYear)
//                .phoneNumber(phoneNumber)
//                .email(email)
//                .nickname(nickname)
//                .bio(bio)
//                .zone(zone)
//                .image(image)
//                .role(role)
//                .provider(provider)
//                .providerId(providerId)
//                .build();
//    }

    public void login() {
        this.lastLoginAt = LocalDateTime.now();
    }

    public void quit(String quitReason) {
        this.quitReason = quitReason;

        this.deleted = true;
        this.deletedAt = LocalDateTime.now();
    }

    // TODO - CHECK : pre or post
    @PrePersist
    public void generateEmailVerifyToken() {
        this.emailVerifyToken = UUID.randomUUID().toString();
    }

    public void verifyEmail() {
        if (isEmailVerified()) {
            throw new RuntimeException("이미 인증된 사용자입니다.");
        }
        this.emailVerified = true;
        this.emailVerifiedAt = LocalDateTime.now();
    }

    public void accused() {
        this.accusedCount++;
        if (this.accusedCount == 5) {
            // TODO - 즉시 로그아웃
            // TODO - quitReason;
            quit(null);
        }
    }

    public void updateImage(String image) {
        this.image = image;
    }

    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }

    // TODO - update와 동일
    public void updateOAuthDetail(SignUpOAuthDetailRequest signUpOAuthDetailRequest) {
        // TODO - converter
        this.gender = signUpOAuthDetailRequest.getGender().equals("MALE") ? GenderType.MALE : GenderType.FEMALE;
        this.birthYear = signUpOAuthDetailRequest.getBirthYear();
        this.phoneNumber = signUpOAuthDetailRequest.getPhoneNumber();
        this.email = signUpOAuthDetailRequest.getEmail();
        this.nickname = signUpOAuthDetailRequest.getNickname();
        this.bio = signUpOAuthDetailRequest.getBio();
        this.zone = AddressUtils.convertStringToEmbeddableAddress(signUpOAuthDetailRequest.getZone());
        this.image = signUpOAuthDetailRequest.getImage();
    }

    public void update(UserUpdateRequest userUpdateRequest) {
        // TODO - converter
        this.gender = userUpdateRequest.getGender().equals("MALE") ? GenderType.MALE : GenderType.FEMALE;
        this.birthYear = userUpdateRequest.getBirthYear();
        this.phoneNumber = userUpdateRequest.getPhoneNumber();
        this.email = userUpdateRequest.getEmail();
        this.nickname = userUpdateRequest.getNickname();
        this.bio = userUpdateRequest.getBio();
        this.zone = AddressUtils.convertStringToEmbeddableAddress(userUpdateRequest.getZone());
        this.image = userUpdateRequest.getImage();
    }

    public void setRole(RoleType role) {
        this.role = role;
    }

    public void updateFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

}
