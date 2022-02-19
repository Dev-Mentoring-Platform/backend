package com.project.mentoridge.modules.purchase.repository;

import com.project.mentoridge.modules.account.vo.Mentor;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.project.mentoridge.modules.account.repository.MentorRepository;
import com.project.mentoridge.modules.purchase.controller.response.CancellationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;

@Disabled
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
class CancellationQueryRepositoryTest {

    @Autowired
    EntityManager em;
    @Autowired
    MentorRepository mentorRepository;

    private CancellationQueryRepository cancellationQueryRepository;

    @BeforeEach
    void setup() {
        JPAQueryFactory jpaQueryFactory = new JPAQueryFactory(em);
        cancellationQueryRepository = new CancellationQueryRepository(jpaQueryFactory);
    }

    @Test
    void findCancellationsOfMentor() {

        // given
        Mentor mentor = mentorRepository.findAll().stream().findFirst()
                .orElseThrow(RuntimeException::new);
        // when
        Page<CancellationResponse> cancellations = cancellationQueryRepository.findCancellationsOfMentor(mentor, Pageable.ofSize(20));
        // then
        for (CancellationResponse cancellation : cancellations) {
            System.out.println(cancellation);
        }
    }
}