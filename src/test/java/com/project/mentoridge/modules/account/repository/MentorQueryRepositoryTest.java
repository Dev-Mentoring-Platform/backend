package com.project.mentoridge.modules.account.repository;

import com.project.mentoridge.modules.account.controller.response.SimpleMenteeResponse;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.project.mentoridge.modules.account.controller.response.MenteeEnrollmentInfoResponse;
import com.project.mentoridge.modules.account.vo.Mentor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Disabled
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
class MentorQueryRepositoryTest {

    @Autowired
    EntityManager em;
    @Autowired
    MentorRepository mentorRepository;

    @Autowired
    MenteeRepository menteeRepository;

    private MentorQueryRepository mentorQueryRepository;

    @BeforeEach
    void init() {
        JPAQueryFactory jpaQueryFactory = new JPAQueryFactory(em);
        mentorQueryRepository = new MentorQueryRepository(jpaQueryFactory);

        assertNotNull(em);
        assertNotNull(jpaQueryFactory);
        assertNotNull(mentorQueryRepository);
    }

    @Test
    void findMenteesOfMentor() {

        // given
        Mentor mentor = mentorRepository.findAll().stream().findFirst()
                .orElseThrow(RuntimeException::new);
        // when
        // then
        Page<SimpleMenteeResponse> result = mentorQueryRepository.findMenteesOfMentor(mentor, false, true, Pageable.ofSize(20));
        result.forEach(System.out::println);
    }

    @Test
    void findMenteeLecturesOfMentor() {

        // given
        Mentor mentor = mentorRepository.findAll().stream().findFirst()
                .orElseThrow(RuntimeException::new);
        Long menteeId = menteeRepository.findAll().stream().findFirst()
                .orElseThrow(RuntimeException::new).getId();
        // when
        // then
        Page<MenteeEnrollmentInfoResponse> result = mentorQueryRepository.findMenteeLecturesOfMentor(mentor, menteeId, Pageable.ofSize(20));
        result.forEach(System.out::println);
    }
}