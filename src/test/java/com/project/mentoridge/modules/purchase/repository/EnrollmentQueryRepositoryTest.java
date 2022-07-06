package com.project.mentoridge.modules.purchase.repository;

import com.project.mentoridge.modules.purchase.controller.response.EnrollmentWithSimpleEachLectureResponse;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.project.mentoridge.modules.account.repository.MenteeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
class EnrollmentQueryRepositoryTest {

    @Autowired
    EntityManager em;
    @Autowired
    MenteeRepository menteeRepository;

    private JPAQueryFactory jpaQueryFactory;
    private EnrollmentQueryRepository enrollmentQueryRepository;

    @BeforeEach
    void setup() {
        jpaQueryFactory = new JPAQueryFactory(em);
        enrollmentQueryRepository = new EnrollmentQueryRepository(jpaQueryFactory);
    }

    @Test
    void findUnreviewedEnrollments() {

        // given
        assertNotNull(em);
        assertNotNull(jpaQueryFactory);
        assertNotNull(enrollmentQueryRepository);

        // when
        // then
        menteeRepository.findAll().forEach(mentee -> {
            Page<EnrollmentWithSimpleEachLectureResponse> responses = enrollmentQueryRepository.findEnrollments(mentee, false, Pageable.ofSize(20));
            responses.forEach(System.out::println);
        });
    }
}