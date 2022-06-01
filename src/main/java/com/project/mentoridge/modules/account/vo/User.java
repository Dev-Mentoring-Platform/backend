package com.project.mentoridge.modules.account.vo;

import com.project.mentoridge.config.security.oauth.provider.OAuthType;
import com.project.mentoridge.modules.account.controller.request.SignUpOAuthDetailRequest;
import com.project.mentoridge.modules.account.controller.request.UserUpdateRequest;
import com.project.mentoridge.modules.account.enums.GenderType;
import com.project.mentoridge.modules.account.enums.RoleType;
import com.project.mentoridge.modules.address.embeddable.Address;
import com.project.mentoridge.modules.address.util.AddressUtils;
import com.project.mentoridge.modules.base.BaseEntity;
import com.project.mentoridge.modules.log.component.LoginLogService;
import com.project.mentoridge.modules.log.component.UserLogService;
import lombok.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.hibernate.annotations.Where;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

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

    @Column(nullable = false, unique = true)
    private String nickname;

    private String image;       // 프로필 이미지

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
    @Lob
    private String refreshToken;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean deleted = false;
    private LocalDateTime deletedAt;
    @Lob
    private String quitReason;

    private LocalDateTime lastLoginAt;

    private int accusedCount = 0;

    // TODO - Notification과 양방향

    @Builder(access = AccessLevel.PUBLIC)
    private User(String username, String password, String name, GenderType gender, String birthYear, String phoneNumber, String nickname, String zone, String image,
                RoleType role, OAuthType provider, String providerId) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.gender = gender;
        this.birthYear = birthYear;
        this.phoneNumber = phoneNumber;
        this.nickname = nickname;
        this.zone = AddressUtils.convertStringToEmbeddableAddress(zone);
        this.image = image;
        this.role = role;
        this.provider = provider;
        this.providerId = providerId;
    }

    public void login(LoginLogService loginLogService) {
        this.lastLoginAt = LocalDateTime.now();
        loginLogService.login(this);
    }

    private void quit(String quitReason) {

        this.quitReason = quitReason;

        this.deleted = true;
        this.deletedAt = LocalDateTime.now();
    }

    public void quit(String quitReason, UserLogService userLogService) {
        quit(quitReason);
        userLogService.delete(this, this);
    }

    // TODO - CHECK : pre or post
    @PrePersist
    public void generateEmailVerifyToken() {
        this.emailVerifyToken = UUID.randomUUID().toString();
    }

    private void verifyEmail() {
        if (isEmailVerified()) {
            throw new RuntimeException("이미 인증된 사용자입니다.");
        }
        this.emailVerified = true;
        this.emailVerifiedAt = LocalDateTime.now();
    }

    public void verifyEmail(UserLogService userLogService) {
        verifyEmail();
        userLogService.verifyEmail(this);
    }
/*
    public void accused() {
        this.accusedCount++;
        if (this.accusedCount == 5) {
            // TODO - 즉시 로그아웃
            // TODO - quitReason;
            quit(null);
        }
    }*/

    public void updateImage(String image, UserLogService userLogService) {
        User before = this.copy();
        this.image = image;
        userLogService.updateImage(this, before, this);
    }

    public void updatePassword(String newPassword, UserLogService userLogService) {
        User before = this.copy();
        this.password = newPassword;
        userLogService.updatePassword(this, before, this);
    }

        private String generateRandomPassword(int count) {
            return RandomStringUtils.randomAlphanumeric(count);
        }

    public String findPassword(BCryptPasswordEncoder bCryptPasswordEncoder, UserLogService userLogService) {
        String randomPassword = generateRandomPassword(10);
        this.password = bCryptPasswordEncoder.encode(randomPassword);
        userLogService.findPassword(this);
        return randomPassword;
    }

    public void updateFcmToken(String fcmToken, UserLogService userLogService) {
        User before = this.copy();
        this.fcmToken = fcmToken;
        userLogService.updateFcmToken(this, before, this);
    }

    private void updateOAuthDetail(SignUpOAuthDetailRequest signUpOAuthDetailRequest) {
        this.gender = signUpOAuthDetailRequest.getGender();
        this.birthYear = signUpOAuthDetailRequest.getBirthYear();
        this.phoneNumber = signUpOAuthDetailRequest.getPhoneNumber();
        this.nickname = signUpOAuthDetailRequest.getNickname();
        this.zone = AddressUtils.convertStringToEmbeddableAddress(signUpOAuthDetailRequest.getZone());
        this.image = signUpOAuthDetailRequest.getImage();
    }

    public void updateOAuthDetail(SignUpOAuthDetailRequest signUpOAuthDetailRequest, UserLogService userLogService) {
        User before = this.copy();
        updateOAuthDetail(signUpOAuthDetailRequest);
        userLogService.update(this, before, this);
    }

    private void update(UserUpdateRequest userUpdateRequest) {
        this.gender = userUpdateRequest.getGender();
        this.birthYear = userUpdateRequest.getBirthYear();
        this.phoneNumber = userUpdateRequest.getPhoneNumber();
        this.nickname = userUpdateRequest.getNickname();
        this.zone = AddressUtils.convertStringToEmbeddableAddress(userUpdateRequest.getZone());
        this.image = userUpdateRequest.getImage();
    }

    public void update(UserUpdateRequest userUpdateRequest, UserLogService userLogService) {
        User before = this.copy();
        update(userUpdateRequest);
        userLogService.update(this, before, this);
    }

    public User update(String name, String picture) {
        this.name = name;
        this.nickname = name;
        this.image = picture;

        return this;
    }

    public void joinMentor(UserLogService userLogService) {
        User before = this.copy();
        this.role = RoleType.MENTOR;
        userLogService.update(this, before, this);
    }

    public void quitMentor(UserLogService userLogService) {
        User before = this.copy();
        this.role = RoleType.MENTEE;
        userLogService.update(this, before, this);
    }

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    private User copy() {
        return User.builder()
                .username(username)
                .name(name)
                .gender(gender)
                .birthYear(birthYear)
                .phoneNumber(phoneNumber)
                .nickname(nickname)
                .zone(AddressUtils.convertEmbeddableToStringAddress(zone))
                .image(image)
                .role(role)
                .provider(provider)
                .build();
    }
}
