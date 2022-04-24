package com.project.mentoridge.modules.review.service;

import com.project.mentoridge.config.exception.EntityNotFoundException;
import com.project.mentoridge.config.exception.UnauthorizedException;
import com.project.mentoridge.modules.account.enums.RoleType;
import com.project.mentoridge.modules.account.repository.MenteeRepository;
import com.project.mentoridge.modules.account.repository.MentorRepository;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.Mentor;
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
import com.project.mentoridge.modules.review.controller.response.ReviewWithSimpleLectureResponse;
import com.project.mentoridge.modules.review.repository.MenteeReviewQueryRepository;
import com.project.mentoridge.modules.review.repository.MenteeReviewRepository;
import com.project.mentoridge.modules.review.repository.MentorReviewRepository;
import com.project.mentoridge.modules.review.vo.MenteeReview;
import com.project.mentoridge.modules.review.vo.MentorReview;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.project.mentoridge.config.exception.EntityNotFoundException.EntityType.*;
import static com.project.mentoridge.modules.account.enums.RoleType.MENTEE;

@Transactional
@RequiredArgsConstructor
@Service
public class MenteeReviewService extends AbstractService {

    private final MenteeReviewRepository menteeReviewRepository;
    private final MentorReviewRepository mentorReviewRepository;
    private final MenteeReviewQueryRepository menteeReviewQueryRepository;
    private final MenteeReviewLogService menteeReviewLogService;

    private final MenteeRepository menteeRepository;
    private final MentorRepository mentorRepository;
    private final LectureRepository lectureRepository;
    private final EnrollmentRepository enrollmentRepository;

        private Mentee getMentee(User user) {
            return Optional.ofNullable(menteeRepository.findByUser(user))
                    .orElseThrow(() -> new UnauthorizedException(MENTEE));
        }

        private Mentor getMentor(User user) {
            return Optional.ofNullable(mentorRepository.findByUser(user))
                    .orElseThrow(() -> new UnauthorizedException(RoleType.MENTOR));
        }

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
        MenteeReview parent = menteeReviewRepository.findByLectureAndId(lecture, menteeReviewId)
                .orElseThrow(() -> new EntityNotFoundException(REVIEW));

        // TODO - Optional 체크
        Optional<MentorReview> child = mentorReviewRepository.findByParent(parent);
        return new ReviewResponse(parent, child.orElse(null));
    }

    @Transactional(readOnly = true)
    public Page<ReviewWithSimpleLectureResponse> getReviewWithSimpleLectureResponses(User user, Integer page) {
        return menteeReviewQueryRepository.findReviewsWithChildAndSimpleLectureByUser(user, getPageRequest(page));
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
    public ReviewWithSimpleLectureResponse getReviewWithSimpleLectureResponse(Long menteeReviewId) {

//        MenteeReview parent = menteeReviewRepository.findById(menteeReviewId)
//                .orElseThrow(() -> new EntityNotFoundException(REVIEW));
        MenteeReview parent = Optional.of(menteeReviewRepository.findByMenteeReviewId(menteeReviewId))
                .orElseThrow(() -> new EntityNotFoundException(REVIEW));

        // TODO - Optional 체크
        Optional<MentorReview> child = mentorReviewRepository.findByParent(parent);
        return new ReviewWithSimpleLectureResponse(parent, child.orElse(null));
    }

        private Enrollment getEnrollment(Mentee mentee, Lecture lecture) {

            Enrollment enrollment = enrollmentRepository.findByMenteeAndLecture(mentee, lecture)
                    .orElseThrow(() -> new EntityNotFoundException(ENROLLMENT));
            // checked된 enrollment만 리뷰 가능
            if (!enrollment.isChecked()) {
                throw new RuntimeException("멘토의 확인이 필요합니다.");
            }
            return enrollment;
        }

        private Enrollment getEnrollment(Long enrollmentId) {

            Enrollment enrollment = enrollmentRepository.findEnrollmentWithLectureByEnrollmentId(enrollmentId)
                    .orElseThrow(() -> new EntityNotFoundException(ENROLLMENT));
            if (!enrollment.isChecked()) {
                throw new RuntimeException("멘토의 확인이 필요합니다.");
            }
            return enrollment;
        }

        private Enrollment getEnrollment(User user, Long lectureId) {
            Mentee mentee = getMentee(user);
            Lecture lecture = getLecture(lectureId);
            return getEnrollment(mentee, lecture);
        }

    public MenteeReview createMenteeReview(User user, Long enrollmentId, MenteeReviewCreateRequest menteeReviewCreateRequest) {

        Mentee mentee = getMentee(user);
        Enrollment enrollment = getEnrollment(enrollmentId);

        MenteeReview saved = menteeReviewRepository.save(menteeReviewCreateRequest.toEntity(mentee, enrollment.getLecture(), enrollment));
        menteeReviewLogService.insert(user, saved);
        return saved;
    }

    public void updateMenteeReview(User user, Long menteeReviewId, MenteeReviewUpdateRequest menteeReviewUpdateRequest) {

        MenteeReview review = menteeReviewRepository.findById(menteeReviewId)
                .orElseThrow(() -> new EntityNotFoundException(REVIEW));

        MenteeReview before = review.copy();
        review.updateMenteeReview(menteeReviewUpdateRequest);
        menteeReviewLogService.update(user, before, review);
    }

    public void deleteMenteeReview(User user, Long menteeReviewId) {

        MenteeReview review = menteeReviewRepository.findById(menteeReviewId)
                .orElseThrow(() -> new EntityNotFoundException(REVIEW));

        review.delete();
        menteeReviewLogService.delete(user, review);
        mentorReviewRepository.deleteByParent(review);
        menteeReviewRepository.delete(review);
    }
}
