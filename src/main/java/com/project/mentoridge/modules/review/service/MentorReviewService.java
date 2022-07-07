package com.project.mentoridge.modules.review.service;

import com.project.mentoridge.config.exception.EntityNotFoundException;
import com.project.mentoridge.modules.account.repository.MentorRepository;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.base.AbstractService;
import com.project.mentoridge.modules.lecture.repository.LectureRepository;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.log.component.MentorReviewLogService;
import com.project.mentoridge.modules.review.controller.request.MentorReviewCreateRequest;
import com.project.mentoridge.modules.review.controller.request.MentorReviewUpdateRequest;
import com.project.mentoridge.modules.review.controller.response.ReviewListResponse;
import com.project.mentoridge.modules.review.controller.response.ReviewWithSimpleEachLectureResponse;
import com.project.mentoridge.modules.review.repository.MenteeReviewRepository;
import com.project.mentoridge.modules.review.repository.MentorReviewQueryRepository;
import com.project.mentoridge.modules.review.repository.MentorReviewRepository;
import com.project.mentoridge.modules.review.vo.MenteeReview;
import com.project.mentoridge.modules.review.vo.MentorReview;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.project.mentoridge.config.exception.EntityNotFoundException.EntityType.LECTURE;
import static com.project.mentoridge.config.exception.EntityNotFoundException.EntityType.REVIEW;

@Transactional
@RequiredArgsConstructor
@Service
public class MentorReviewService extends AbstractService {

    private final MenteeReviewRepository menteeReviewRepository;
    private final MentorReviewRepository mentorReviewRepository;
    private final MentorReviewQueryRepository mentorReviewQueryRepository;
    private final MentorReviewLogService mentorReviewLogService;

    private final MentorRepository mentorRepository;
    private final LectureRepository lectureRepository;

        private Lecture getLecture(Mentor mentor, Long lectureId) {
            return lectureRepository.findByMentorAndId(mentor, lectureId)
                    .orElseThrow(() -> new EntityNotFoundException(LECTURE));
        }

    @Transactional(readOnly = true)
    public Page<ReviewWithSimpleEachLectureResponse> getReviewWithSimpleEachLectureResponsesOfMentorByMentees(User mentorUser, Integer page) {
        Mentor mentor = getMentor(mentorRepository, mentorUser);
        return mentorReviewQueryRepository.findReviewsWithSimpleEachLectureOfMentorByMentees(mentor, getPageRequest(page));
    }

    @Transactional(readOnly = true)
    public ReviewListResponse getReviewWithSimpleEachLectureResponsesOfMentorByMentees(Long mentorId, Integer page) {
        Mentor mentor = getMentor(mentorRepository, mentorId);
        return mentorReviewQueryRepository.findReviewsOfMentorByMentees(mentor, getPageRequest(page));
    }

    public MentorReview createMentorReview(User mentorUser, Long lectureId, Long menteeReviewId, MentorReviewCreateRequest mentorReviewCreateRequest) {

        Mentor mentor = getMentor(mentorRepository, mentorUser);
        // 1. 해당 멘토의 강의인가?
        Lecture lecture = getLecture(mentor, lectureId);
        // 2. 해당 강의의 리뷰인가?
        MenteeReview parent = menteeReviewRepository.findMenteeReviewByLectureAndId(lecture, menteeReviewId)
                .orElseThrow(() -> new EntityNotFoundException(REVIEW));

        MentorReview saved = mentorReviewRepository.save(mentorReviewCreateRequest.toEntity(mentor, parent));
        mentorReviewLogService.insert(mentorUser, saved);
        return saved;
    }

    public void updateMentorReview(User mentorUser, Long lectureId, Long menteeReviewId, Long mentorReviewId, MentorReviewUpdateRequest mentorReviewUpdateRequest) {

        Mentor mentor = getMentor(mentorRepository, mentorUser);
        // 1. 해당 멘토의 강의인가?
        Lecture lecture = getLecture(mentor, lectureId);
        // 2. 해당 강의의 리뷰인가?
        MenteeReview parent = menteeReviewRepository.findMenteeReviewByLectureAndId(lecture, menteeReviewId)
                .orElseThrow(() -> new EntityNotFoundException(REVIEW));

        // 3. 해당 리뷰에 대한 댓글이 맞는가?
        MentorReview mentorReview = mentorReviewRepository.findByParentAndId(parent, mentorReviewId)
                .orElseThrow(() -> new EntityNotFoundException(REVIEW));
        mentorReview.update(mentorReviewUpdateRequest, mentorUser, mentorReviewLogService);
    }

    public void deleteMentorReview(User mentorUser, Long lectureId, Long menteeReviewId, Long mentorReviewId) {

        Mentor mentor = getMentor(mentorRepository, mentorUser);
        Lecture lecture = getLecture(mentor, lectureId);
        MenteeReview parent = menteeReviewRepository.findMenteeReviewByLectureAndId(lecture, menteeReviewId)
                .orElseThrow(() -> new EntityNotFoundException(REVIEW));

        // TODO - CHECK : vs findByParentId
        MentorReview mentorReview = mentorReviewRepository.findByParentAndId(parent, mentorReviewId)
                .orElseThrow(() -> new EntityNotFoundException(REVIEW));
        mentorReview.delete(user, mentorReviewLogService);
        // TODO - delete 시에 id로 먼저 조회
        mentorReviewRepository.delete(mentorReview);
    }
}
