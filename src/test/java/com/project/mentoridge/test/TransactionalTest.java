package com.project.mentoridge.test;

import com.project.mentoridge.configuration.auth.WithAccount;
import com.project.mentoridge.modules.account.controller.request.UserUpdateRequest;
import com.project.mentoridge.modules.account.repository.CareerRepository;
import com.project.mentoridge.modules.account.repository.EducationRepository;
import com.project.mentoridge.modules.account.repository.UserRepository;
import com.project.mentoridge.modules.account.service.UserService;
import com.project.mentoridge.modules.account.vo.User;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static com.project.mentoridge.config.init.TestDataBuilder.getUserUpdateRequestWithNickname;
import static com.project.mentoridge.config.init.TestDataBuilder.getUserWithName;

@Disabled
@Transactional
@SpringBootTest
public class TransactionalTest {

    @Autowired
    EntityManager em;

    @Autowired
    UserRepository userRepository;
    @Autowired
    UserService userService;

    @Autowired
    CareerRepository careerRepository;
    @Autowired
    EducationRepository educationRepository;

    // TODO
    // @Transactional
    @Test
    void whereTest() {
        List<User> users = userRepository.findAll();
        System.out.println(users);
    }

    @DisplayName("트랜잭션 쓰기지연 테스트")
    @Transactional
    @Test
    void transactionTest1() {
        educationRepository.deleteById(1L);
        /*
        Hibernate: select education0_.education_id as educatio1_4_0_, education0_.created_at as created_2_4_0_, education0_.updated_at as updated_3_4_0_, education0_.degree as degree4_4_0_, education0_.entrance_date as entrance5_4_0_, education0_.graduation_date as graduati6_4_0_, education0_.major as major7_4_0_, education0_.school_name as school_n8_4_0_, education0_.score as score9_4_0_, education0_.mentor_id as mentor_i10_4_0_ from education education0_ where education0_.education_id=?
         */
    }

    @DisplayName("트랜잭션 쓰기지연 테스트")
    @Transactional
    @Test
    void transactionTest2() {

        User user = getUserWithName("yk");
        userRepository.save(user);
        userRepository.delete(user);

        /*
        Hibernate: insert into user (created_at, bio, deleted, deleted_at, email, gender, name, nickname, password, phone_number, provider, provider_id, role, username, zone) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
         */
    }

    @DisplayName("트랜잭션 쓰기지연 테스트")
    @Transactional
    @Test
    void transactionTest3() {

        User user = getUserWithName("yk");
        userRepository.save(user);

        // user.setNickname("nickname");

        System.out.println(">>> 1 : " + userRepository.findById(6L).get());
        userRepository.flush();
        System.out.println(">>> 2 : " + userRepository.findById(6L).get());

        /*
        Hibernate: insert into user (created_at, bio, deleted, deleted_at, email, gender, name, nickname, password, phone_number, provider, provider_id, role, username, zone) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        >>> 1 : User(super=com.mentor.mentoridge.modules.account.vo.User@6a29f6aa, username=yk@email.com, password=password, name=yk, gender=FEMALE, phoneNumber=null,
                    email=yk@email.com, nickname=nickname, bio=null, zone=null, role=ROLE_MENTEE, provider=null, providerId=null, deleted=false, deletedAt=null)

        Hibernate: update user set updated_at=?, bio=?, deleted=?, deleted_at=?, email=?, gender=?, name=?, nickname=?, password=?, phone_number=?, provider=?, provider_id=?, role=?, zone=? where user_id=?
        >>> 2 : User(super=com.mentor.mentoridge.modules.account.vo.User@6a29f6aa, username=yk@email.com, password=password, name=yk, gender=FEMALE, phoneNumber=null,
                    email=yk@email.com, nickname=nickname, bio=null, zone=null, role=ROLE_MENTEE, provider=null, providerId=null, deleted=false, deletedAt=null)
         */
    }

    @DisplayName("EntityListener preUpdate 테스트")
    @Test
    @WithAccount("yk")
    void updateUser() {

        String email = "yk@email.com";
        User user = userRepository.findByUsername(email).orElseThrow(RuntimeException::new);

        UserUpdateRequest userUpdateRequest = getUserUpdateRequestWithNickname("yk");
        userService.updateUser(user, userUpdateRequest);
        em.flush();

        userRepository.findAll().stream()
                .forEach(u -> System.out.println(u.getUpdatedAt())); // 2021-08-26T23:26:14.841354400
    }

}