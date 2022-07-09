package com.project.mentoridge.modules.review.repository;

import com.project.mentoridge.modules.account.enums.RoleType;
import com.project.mentoridge.modules.account.repository.UserRepository;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.lecture.repository.LectureRepository;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.review.controller.response.ReviewResponse;
import com.project.mentoridge.modules.review.controller.response.ReviewWithSimpleEachLectureResponse;
import com.project.mentoridge.modules.review.vo.MenteeReview;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Disabled
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
class MenteeReviewQueryRepositoryTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    LectureRepository lectureRepository;
    @Autowired
    EntityManager em;

    private JPAQueryFactory jpaQueryFactory;
    private MenteeReviewQueryRepository menteeReviewQueryRepository;

    @BeforeEach
    void init() {
        jpaQueryFactory = new JPAQueryFactory(em);
        menteeReviewQueryRepository = new MenteeReviewQueryRepository(jpaQueryFactory, em);
    }

    @Test
    void findReviewsWithUserByLecture() {

        // given
        Lecture lecture = lectureRepository.findAll().stream().findFirst()
                .orElseThrow(RuntimeException::new);
        // when
        Page<MenteeReview> reviews = menteeReviewQueryRepository.findReviewsWithUserByLecture(lecture, Pageable.ofSize(20));
        // then
        for (MenteeReview review : reviews.getContent()) {
            System.out.println(review);
        }
    }

    @Test
    void findReviewsWithChildByLecture() {

        // given
        Lecture lecture = lectureRepository.findAll().stream().findFirst()
                .orElseThrow(RuntimeException::new);
        // when
        Page<ReviewResponse> reviews = menteeReviewQueryRepository.findReviewsWithChildByLecture(lecture, Pageable.ofSize(20));
        // then
        for (ReviewResponse review : reviews.getContent()) {
            System.out.println(review);
        }
    }

    @Test
    void findReviewsWithChildAndSimpleEachLectureByUser() {

        // given
        assertNotNull(menteeReviewQueryRepository);
        assertNotNull(userRepository);

        User user = userRepository.findAll().stream()
                .filter(u -> u.getRole().equals(RoleType.MENTEE)).findFirst()
                .orElseThrow(RuntimeException::new);

        // when
        Page<ReviewWithSimpleEachLectureResponse> responses = menteeReviewQueryRepository.findReviewsWithChildAndSimpleEachLectureByUser(user, PageRequest.of(0, 20));

        // then
        responses.forEach(System.out::println);
    }
}