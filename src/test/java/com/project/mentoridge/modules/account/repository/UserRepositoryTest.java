package com.project.mentoridge.modules.account.repository;

import com.project.mentoridge.configuration.annotation.RepositoryTest;
import com.project.mentoridge.modules.account.vo.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RepositoryTest
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    private User user;

    @BeforeEach
    void init() {
        user = userRepository.findAll().stream().findFirst()
                .orElseThrow(RuntimeException::new);
    }

    @Test
    void findByUsername() {
        // given
        String username = user.getUsername();
        // when
        Optional<User> result = userRepository.findByUsername(username);
        // then
        assertThat(result).isNotEmpty();
    }

    @DisplayName("존재하지 않는 User")
    @Test
    void findByUsername_notExist_user() {

        // given
        // when
        Optional<User> result = userRepository.findByUsername("username");
        // then
        assertThat(result).isEmpty();
    }

    @DisplayName("parameter가 empty-string인 경우")
    @Test
    void findByUsername_with_empty_param() {

        // given
        // when
        // then
//        Assertions.assertThrows(RuntimeException.class,
//                () -> userRepository.findByUsername(""));
    }

    @DisplayName("parameter가 null인 경우")
    @Test
    void findByUsername_with_null_param() {

        // given
        // when
        // then
//        Assertions.assertThrows(RuntimeException.class,
//                () -> userRepository.findByUsername(null));
        Optional<User> result = userRepository.findByUsername(null);
        System.out.println(result);
    }

    @Test
    void findAllByUsername() {

        // given
        String username = user.getUsername();
        // when
        User result = userRepository.findAllByUsername(username);
        // then
        assertThat(result).isNotNull();
    }

    @DisplayName("존재하지 않는 User")
    @Test
    void findAllByUsername_notExist_user() {

        // given
        // when
        User result = userRepository.findAllByUsername("username");
        // then
        assertThat(result).isNull();
    }

    @DisplayName("parameter가 empty-string인 경우")
    @Test
    void findAllByUsername_with_empty_param() {

        // given
        // when
        // then
//        Assertions.assertThrows(RuntimeException.class,
//                () -> userRepository.findAllByUsername(""));
    }

    @DisplayName("parameter가 null인 경우")
    @Test
    void findAllByUsername_with_null_param() {

        // given
        // when
        // then
//        Assertions.assertThrows(RuntimeException.class,
//                () -> userRepository.findAllByUsername(null));
    }
}