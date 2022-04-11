package com.project.mentoridge.modules.review.service;

import com.project.mentoridge.config.exception.EntityNotFoundException;
import com.project.mentoridge.modules.lecture.repository.LectureRepository;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.review.controller.response.ReviewResponse;
import com.project.mentoridge.modules.review.repository.MenteeReviewQueryRepository;
import com.project.mentoridge.modules.review.vo.MenteeReview;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import static com.project.mentoridge.config.exception.EntityNotFoundException.EntityType.LECTURE;

@Disabled
@Transactional
@SpringBootTest
public class ReviewListTest {

    @Autowired
    MenteeReviewService menteeReviewService;
    @Autowired
    MenteeReviewQueryRepository menteeReviewQueryRepository;

    @Autowired
    LectureRepository lectureRepository;

    @Test
    void findWithUserByLecture() {

        Lecture lecture = lectureRepository.findById(10L)
                .orElseThrow(() -> new EntityNotFoundException(LECTURE));

        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>> START");
        Page<MenteeReview> review = menteeReviewQueryRepository.findReviewsWithUserByLecture(lecture, PageRequest.of(0, 20, Sort.by("id").ascending()));
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>> END");
    }

    @Test
    void getReviewResponsesOfLecture() {

        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>> START");
        Page<ReviewResponse> reviewResponses = menteeReviewService.getReviewResponsesOfLecture(10L, 1);
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>> END");
    }
}
