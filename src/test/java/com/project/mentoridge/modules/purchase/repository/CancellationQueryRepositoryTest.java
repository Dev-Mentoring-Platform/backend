package com.project.mentoridge.modules.purchase.repository;

import org.junit.jupiter.api.Disabled;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@Disabled
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
class CancellationQueryRepositoryTest {
/*
    @Autowired
    EntityManager em;
    @Autowired
    MentorRepository mentorRepository;

    private CancellationQueryRepository cancellationQueryRepository;

    @BeforeEach
    void init() {
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
    }*/
}