package com.project.mentoridge.modules.log.component;

import com.project.mentoridge.modules.account.enums.GenderType;
import com.project.mentoridge.modules.account.vo.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest
class UserLogServiceTest {

    @Autowired
    UserLogService userLogService;

    @Test
    void insert_content() throws NoSuchFieldException, IllegalAccessException {
        // [User] 아이디 : -, 이름 : -, 성별 : -, 생년월일 : -, 연락처 : -, 닉네임 : -, 이미지 : -, 지역 : -
        // given
        User user = User.builder()
                .username("usernameA")
                .name("nameA")
                .gender(GenderType.MALE)
                .birthYear("20220318")
                .phoneNumber("01012345678")
                .nickname("nicknameA")
                .image(null)
                .zone("서울특별시 강남구 청담동")
                .build();

        // when
        String log = userLogService.insert(user, user);
        // then
        assertEquals(String.format("[User] 아이디 : %s, 이름 : %s, 성별 : %s, 생년월일 : %s, 연락처 : %s, 닉네임 : %s, 지역 : %s",
                user.getUsername(), user.getName(), user.getGender(), user.getBirthYear(), user.getPhoneNumber(), user.getNickname(), user.getZone()), log);
    }

    @Test
    void update_content() throws NoSuchFieldException, IllegalAccessException {
        // [User] 아이디 : -, 이름 : -, 성별 : -, 생년월일 : -, 연락처 : -, 닉네임 : -, 이미지 : -, 지역 : -
        // given
        User user = User.builder()
                .username("usernameA")
                .name("nameA")
                .gender(GenderType.MALE)
                .birthYear("20220318")
                .phoneNumber("01012345678")
                .nickname("nicknameA")
                .image(null)
                .zone("서울특별시 강남구 청담동")
                .build();
        User before = user;
        User after = User.builder()
                .username("usernameB")
                .name("nameB")
                .gender(GenderType.FEMALE)
                .birthYear("20220319")
                .phoneNumber("01012345679")
                .nickname("nicknameB")
                .image(null)
                .zone("서울특별시 강남구 압구정동")
                .build();
        // when
        String log = userLogService.update(user, before, after);
        // then
        assertEquals(String.format("[User] 아이디 : %s → %s, 이름 : %s → %s, 성별 : %s → %s, 생년월일 : %s → %s, 연락처 : %s → %s, 닉네임 : %s → %s, 지역 : %s → %s",
                before.getUsername(), after.getUsername(),
                before.getName(), after.getName(),
                before.getGender(), after.getGender(),
                before.getBirthYear(), after.getBirthYear(),
                before.getPhoneNumber(), after.getPhoneNumber(),
                before.getNickname(), after.getNickname(),
                before.getZone(), after.getZone()),
                log);
    }

    @Test
    void delete_content() throws NoSuchFieldException, IllegalAccessException {
        // [User] 아이디 : -, 이름 : -, 성별 : -, 생년월일 : -, 연락처 : -, 닉네임 : -, 이미지 : -, 지역 : -
        // given
        User user = User.builder()
                .username("usernameA")
                .name("nameA")
                .gender(GenderType.MALE)
                .birthYear("20220318")
                .phoneNumber("01012345678")
                .nickname("nicknameA")
                .image(null)
                .zone("서울특별시 강남구 청담동")
                .build();
        // when
        String log = userLogService.delete(user, user);
        // then
        assertEquals(String.format("[User] 아이디 : %s, 이름 : %s, 성별 : %s, 생년월일 : %s, 연락처 : %s, 닉네임 : %s, 지역 : %s",
                user.getUsername(), user.getName(), user.getGender(), user.getBirthYear(), user.getPhoneNumber(), user.getNickname(), user.getZone()), log);
    }
}