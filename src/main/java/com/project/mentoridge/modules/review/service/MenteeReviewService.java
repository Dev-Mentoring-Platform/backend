package com.project.mentoridge.modules.review.service;

import com.project.mentoridge.config.exception.EntityNotFoundException;
import com.project.mentoridge.modules.account.repository.MenteeRepository;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.base.AbstractService;
import com.project.mentoridge.modules.lecture.repository.LectureRepository;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.log.component.MenteeReviewLogService;
import com.project.mentoridge.modules.purchase.repository.EnrollmentRepository;
import com.project.mentoridge.modules.purchase.vo.Enrollment;
import com.project.mentoridge.modules.review.controller.request.MenteeReviewCreateRequest;
import com.project.mentoridge.modules.review.controller.request.MenteeReviewUpdateRequest;
import com.project.mentoridge.modules.review.controller.response.ReviewResponse;
import com.project.mentoridge.modules.review.controller.response.ReviewWithSimpleEachLectureResponse;
import com.project.mentoridge.modules.review.repository.MenteeReviewQueryRepository;
import com.project.mentoridge.modules.review.repository.MenteeReviewRepository;
import com.project.mentoridge.modules.review.repository.MentorReviewRepository;
import com.project.mentoridge.modules.review.vo.MenteeReview;
import com.project.mentoridge.modules.review.vo.MentorReview;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.project.mentoridge.config.exception.EntityNotFoundException.EntityType.*;

@Transactional
@RequiredArgsConstructor
@Service
public class MenteeReviewService extends AbstractService {

    private final MenteeReviewRepository menteeReviewRepository;
    private final MentorReviewRepository mentorReviewRepository;
    private final MenteeReviewQueryRepository menteeReviewQueryRepository;
    private final MenteeReviewLogService menteeReviewLogService;

    private final MenteeRepository menteeRepository;
    private final LectureRepository lectureRepository;
    private final EnrollmentRepository enrollmentRepository;

        private Lecture getLecture(Long lectureId) {
            return lectureRepository.findById(lectureId)
                    .orElseThrow(() -> new EntityNotFoundException(LECTURE));
        }

    @Transactional(readOnly = true)
    public Page<ReviewResponse> getReviewResponsesOfLecture(Long lectureId, Integer page) {
        Lecture lecture = getLecture(lectureId);
        return menteeReviewQueryRepository.findReviewsWithChildByLecture(lecture, getPageRequest(page));
    }

    @Transactional(readOnly = true)
    public ReviewResponse getReviewResponseOfLecture(Long lectureId, Long menteeReviewId) {
        Lecture lecture = getLecture(lectureId);
        MenteeReview parent = menteeReviewRepository.findMenteeReviewByLectureAndId(lecture, menteeReviewId)
                .orElseThrow(() -> new EntityNotFoundException(REVIEW));

        // TODO - Optional 체크
        Optional<MentorReview> child = mentorReviewRepository.findByParent(parent);
        return new ReviewResponse(parent, child.orElse(null));
    }

    // TODO - check
    @Transactional(readOnly = true)
    public Page<ReviewResponse> getReviewResponsesOfEachLecture(Long lectureId, Long lecturePriceId, Integer page) {
        List<Enrollment> enrollments = enrollmentRepository.findAllByLectureIdAndLecturePriceId(lectureId, lecturePriceId);
        return menteeReviewQueryRepository.findReviewsWithChildByLecturePrice(enrollments, getPageRequest(page));
    }

    @Transactional(readOnly = true)
    public ReviewResponse getReviewResponseOfEachLecture(Long lectureId, Long lecturePriceId, Long menteeReviewId) {
        // List<Enrollment> enrollments = enrollmentRepository.findAllByLectureIdAndLecturePriceId(lectureId, lecturePriceId);
        MenteeReview parent = menteeReviewRepository.findMenteeReviewById(menteeReviewId)
                .orElseThrow(() -> new EntityNotFoundException(REVIEW));

        // TODO - Optional 체크
        Optional<MentorReview> child = mentorReviewRepository.findByParent(parent);
        return new ReviewResponse(parent, child.orElse(null));
    }

    @Transactional(readOnly = true)
    public ReviewResponse getReviewResponseOfEnrollment(Long menteeId, Long enrollmentId, Long menteeReviewId) {

        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new EntityNotFoundException(ENROLLMENT));
        MenteeReview parent = menteeReviewRepository.findByEnrollmentAndId(enrollment, menteeReviewId)
                .orElseThrow(() -> new EntityNotFoundException(REVIEW));

        // TODO - Optional 체크
        Optional<MentorReview> child = mentorReviewRepository.findByParent(parent);
        return new ReviewResponse(parent, child.orElse(null));
    }

    @Transactional(readOnly = true)
    public Page<ReviewWithSimpleEachLectureResponse> getReviewWithSimpleEachLectureResponses(User user, Integer page) {
        return menteeReviewQueryRepository.findReviewsWithChildAndSimpleEachLectureByUser(user, getPageRequest(page));
    }

    @Transactional(readOnly = true)
    public ReviewResponse getReviewResponse(Long menteeReviewId) {

        MenteeReview parent = menteeReviewRepository.findById(menteeReviewId)
                .orElseThrow(() -> new EntityNotFoundException(REVIEW));

        // TODO - Optional 체크
        Optional<MentorReview> child = mentorReviewRepository.findByParent(parent);
        return new ReviewResponse(parent, child.orElse(null));
    }

    @Transactional(readOnly = true)
    public ReviewWithSimpleEachLectureResponse getReviewWithSimpleEachLectureResponse(Long menteeReviewId) {
        MenteeReview parent = Optional.of(menteeReviewRepository.findByMenteeReviewId(menteeReviewId))
                .orElseThrow(() -> new EntityNotFoundException(REVIEW));

        // TODO - Optional 체크
        Optional<MentorReview> child = mentorReviewRepository.findByParent(parent);
        return new ReviewWithSimpleEachLectureResponse(parent, child.orElse(null));
    }

        private Enrollment getEnrollment(Long enrollmentId) {

            Enrollment enrollment = enrollmentRepository.findEnrollmentWithLectureByEnrollmentId(enrollmentId)
                    .orElseThrow(() -> new EntityNotFoundException(ENROLLMENT));
            if (!enrollment.isChecked()) {
                throw new RuntimeException("멘토의 확인이 필요합니다.");
            }
            return enrollment;
        }

    public MenteeReview createMenteeReview(User menteeUser, Long enrollmentId, MenteeReviewCreateRequest menteeReviewCreateRequest) {

        Mentee mentee = getMentee(menteeRepository, menteeUser);
        Enrollment enrollment = getEnrollment(enrollmentId);

        MenteeReview saved = menteeReviewRepository.save(menteeReviewCreateRequest.toEntity(mentee, enrollment.getLecture(), enrollment));
        menteeReviewLogService.insert(menteeUser, saved);
        return saved;
    }

    public void updateMenteeReview(User menteeUser, Long menteeReviewId, MenteeReviewUpdateRequest menteeReviewUpdateRequest) {

        MenteeReview menteeReview = menteeReviewRepository.findById(menteeReviewId)
                .orElseThrow(() -> new EntityNotFoundException(REVIEW));
        menteeReview.update(menteeReviewUpdateRequest, menteeUser, menteeReviewLogService);
    }

    /*
    but with bi-directionnal removing the parent won't remove automatically the foreign-key in the childs,
    you must ensure yourself than childs don't have the reference before removing the parent.
     */
    public void deleteMenteeReview(User user, Long menteeReviewId) {

        MenteeReview menteeReview = menteeReviewRepository.findById(menteeReviewId)
                .orElseThrow(() -> new EntityNotFoundException(REVIEW));

        menteeReview.delete(user, menteeReviewLogService);
        // mentorReviewRepository.deleteByParent(menteeReview);
        menteeReviewRepository.delete(menteeReview);
    }
}
