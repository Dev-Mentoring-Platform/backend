package com.project.mentoridge.modules.log.component;

import com.project.mentoridge.configuration.annotation.ServiceTest;
import com.project.mentoridge.modules.account.enums.GenderType;
import com.project.mentoridge.modules.account.vo.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ServiceTest
class LoginLogServiceTest {

    @Autowired
    LoginLogService loginLogService;

    @Test
    void login_content() {

        // given
        User user = User.builder()
                .username("user@email.com")
                .name("name")
                .gender(GenderType.MALE)
                .birthYear("20220318")
                .phoneNumber("01012345678")
                .nickname("nickname")
                .image(null)
                .zone("서울특별시 강남구 청담동")
                .build();
        // when
        String log = loginLogService.login(user);
        // then
        assertEquals(String.format("[Login] user : %s", user.getUsername()), log);
    }

}