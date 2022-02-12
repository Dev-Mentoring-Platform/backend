package com.project.mentoridge.test;

import com.project.mentoridge.modules.account.enums.RoleType;
import com.project.mentoridge.modules.account.repository.MentorRepository;
import com.project.mentoridge.modules.account.repository.UserRepository;
import com.project.mentoridge.modules.account.vo.User;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Disabled
@SpringBootTest
public class MainTestServiceTest {

    @Autowired
    MainTestService mainTestService;
    @Autowired
    IsolationTestService isolationTestService;

    @Autowired
    UserRepository userRepository;
    @Autowired
    MentorRepository mentorRepository;

//    @BeforeEach
//    void init() {
//        userRepository.deleteAll();
//        mentorRepository.deleteAll();
//    }

    @Test
    void propagationTest() {
        try {
            mainTestService.saveMentor();
        } catch (RuntimeException e) {
            System.out.println(">>> " + e.getMessage());
        }

        System.out.println("user : " + userRepository.findAll());
        System.out.println("mentor : " + mentorRepository.findAll());

    }

    @Test
    void transactionTest() {


    }

    @Test
    void isolationTest() {

        String name = "yk";
        User user = User.of(
                name + "@email.com",
                "password",
                name,
                "MALE",
                null,
                null,
                null,
                name,
                null,
                "서울특별시 강남구 삼성동",
                null,
                RoleType.MENTEE,
                null,
                null
        );
        Long userId = userRepository.save(user).getId();

        isolationTestService.get(userId);
        System.out.println(userRepository.findAll());

    }

}