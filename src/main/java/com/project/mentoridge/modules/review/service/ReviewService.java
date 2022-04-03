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
import com.project.mentoridge.modules.log.component.MentorReviewLogService;
import com.project.mentoridge.modules.purchase.repository.EnrollmentRepository;
import com.project.mentoridge.modules.purchase.vo.Enrollment;
import com.project.mentoridge.modules.review.controller.request.MenteeReviewCreateRequest;
import com.project.mentoridge.modules.review.controller.request.MenteeReviewUpdateRequest;
import com.project.mentoridge.modules.review.controller.request.MentorReviewCreateRequest;
import com.project.mentoridge.modules.review.controller.request.MentorReviewUpdateRequest;
import com.project.mentoridge.modules.review.controller.response.ReviewResponse;
import com.project.mentoridge.modules.review.controller.response.ReviewWithSimpleLectureResponse;
import com.project.mentoridge.modules.review.repository.ReviewQueryRepository;
import com.project.mentoridge.modules.review.repository.ReviewRepository;
import com.project.mentoridge.modules.review.vo.Review;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.project.mentoridge.config.exception.EntityNotFoundException.EntityType.*;

@Transactional
@RequiredArgsConstructor
@Service
public class ReviewService extends AbstractService {

    private final ReviewRepository reviewRepository;
    private final ReviewQueryRepository reviewQueryRepository;

    private final MenteeRepository menteeRepository;
    private final MentorRepository mentorRepository;
    private final LectureRepository lectureRepository;

    private final EnrollmentRepository enrollmentRepository;

    private final MenteeReviewLogService menteeReviewLogService;
    private final MentorReviewLogService mentorReviewLogService;

    public Review createMentorReview(User user, Long lectureId, Long parentId, MentorReviewCreateRequest mentorReviewCreateRequest) {

        Mentor mentor = Optional.ofNullable(mentorRepository.findByUser(user))
                .orElseThrow(() -> new UnauthorizedException(RoleType.MENTOR));

        // 1. 해당 멘토의 강의인가?
        Lecture lecture = lectureRepository.findByMentorAndId(mentor, lectureId)
                .orElseThrow(() -> new EntityNotFoundException(LECTURE));

        // 2. 해당 강의의 리뷰인가?
        Review parent = reviewRepository.findByLectureAndId(lecture, parentId)
                .orElseThrow(() -> new EntityNotFoundException(REVIEW));

        Review saved = reviewRepository.save(Review.buildMentorReview(user, lecture, parent, mentorReviewCreateRequest));
        mentorReviewLogService.insert(user, saved);
        return saved;
    }

    public void updateMentorReview(User user, Long lectureId, Long parentId, Long reviewId, MentorReviewUpdateRequest mentorReviewUpdateRequest) {

        Mentor mentor = Optional.ofNullable(mentorRepository.findByUser(user))
                .orElseThrow(() -> new UnauthorizedException(RoleType.MENTOR));

        // 1. 해당 멘토의 강의인가?
        Lecture lecture = lectureRepository.findByMentorAndId(mentor, lectureId)
                .orElseThrow(() -> new EntityNotFoundException(LECTURE));

        // 2. 해당 강의의 리뷰인가?
        Review parent = reviewRepository.findByLectureAndId(lecture, parentId)
                .orElseThrow(() -> new EntityNotFoundException(REVIEW));

        // 3. 해당 리뷰에 대한 댓글이 맞는가?
        Review review = reviewRepository.findByParentAndId(parent, reviewId)
                .orElseThrow(() -> new EntityNotFoundException(REVIEW));
        Review before = review.copy();
        review.updateMentorReview(mentorReviewUpdateRequest);
        mentorReviewLogService.update(user, before, review);
    }

    public void deleteMentorReview(User user, Long lectureId, Long parentId, Long reviewId) {

        Mentor mentor = Optional.ofNullable(mentorRepository.findByUser(user))
                .orElseThrow(() -> new UnauthorizedException(RoleType.MENTOR));

        Lecture lecture = lectureRepository.findByMentorAndId(mentor, lectureId)
                .orElseThrow(() -> new EntityNotFoundException(LECTURE));

        Review parent = reviewRepository.findByLectureAndId(lecture, parentId)
                .orElseThrow(() -> new EntityNotFoundException(REVIEW));

        // TODO - CHECK : vs findByParentId
        Review review = reviewRepository.findByParentAndId(parent, reviewId)
                .orElseThrow(() -> new EntityNotFoundException(REVIEW));

        review.delete();
        mentorReviewLogService.delete(user, review);
        // TODO - delete 시에 id로 먼저 조회
        reviewRepository.delete(review);
    }

    public Review createMenteeReview(User user, Long lectureId, MenteeReviewCreateRequest menteeReviewCreateRequest) {

        Mentee mentee = Optional.ofNullable(menteeRepository.findByUser(user))
                .orElseThrow(() -> new UnauthorizedException(RoleType.MENTEE));

        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new EntityNotFoundException(LECTURE));

        // checked된 enrollment만 리뷰 가능
        Enrollment enrollment = enrollmentRepository.findByMenteeAndLecture(mentee, lecture)
                .orElseThrow(() -> new EntityNotFoundException(ENROLLMENT));
        if (!enrollment.isChecked()) {
            throw new RuntimeException("멘토의 확인이 필요합니다.");
        }

        Review saved = reviewRepository.save(Review.buildMenteeReview(user, lecture, enrollment, menteeReviewCreateRequest));
        menteeReviewLogService.insert(user, saved);
        return saved;
    }

    public void updateMenteeReview(User user, Long lectureId, Long reviewId, MenteeReviewUpdateRequest menteeReviewUpdateRequest) {

        Mentee mentee = Optional.ofNullable(menteeRepository.findByUser(user))
                .orElseThrow(() -> new UnauthorizedException(RoleType.MENTEE));

        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new EntityNotFoundException(LECTURE));

        // checked된 enrollment만 리뷰 가능
        Enrollment enrollment = enrollmentRepository.findByMenteeAndLecture(mentee, lecture)
                .orElseThrow(() -> new EntityNotFoundException(ENROLLMENT));
        if (!enrollment.isChecked()) {
            throw new RuntimeException("멘토의 확인이 필요합니다.");
        }

        Review review = reviewRepository.findByEnrollmentAndId(enrollment, reviewId)
                .orElseThrow(() -> new EntityNotFoundException(REVIEW));
        Review before = review.copy();
        review.updateMenteeReview(menteeReviewUpdateRequest);
        menteeReviewLogService.update(user, before, review);
    }

    public void deleteMenteeReview(User user, Long lectureId, Long reviewId) {

        Mentee mentee = Optional.ofNullable(menteeRepository.findByUser(user))
                .orElseThrow(() -> new UnauthorizedException(RoleType.MENTEE));

        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new EntityNotFoundException(LECTURE));

        // checked된 enrollment만 리뷰 가능
        Enrollment enrollment = enrollmentRepository.findByMenteeAndLecture(mentee, lecture)
                .orElseThrow(() -> new EntityNotFoundException(ENROLLMENT));
        if (!enrollment.isChecked()) {
            throw new RuntimeException("멘토의 확인이 필요합니다.");
        }

        Review review = reviewRepository.findByEnrollmentAndId(enrollment, reviewId)
                .orElseThrow(() -> new EntityNotFoundException(REVIEW));

        review.delete();
        menteeReviewLogService.delete(user, review);
        reviewRepository.delete(review);
    }

    @Transactional(readOnly = true)
    public Page<ReviewResponse> getReviewResponsesOfLecture(Long lectureId, Integer page) {

        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new EntityNotFoundException(LECTURE));
        return reviewQueryRepository.findReviewsWithChildByLecture(lecture, getPageRequest(page));
    }

    @Transactional(readOnly = true)
    public ReviewResponse getReviewResponseOfLecture(Long lectureId, Long reviewId) {

        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new EntityNotFoundException(LECTURE));

        Review parent = reviewRepository.findByLectureAndId(lecture, reviewId)
                .orElseThrow(() -> new EntityNotFoundException(REVIEW));

        // TODO - Optional 체크
        Optional<Review> child = reviewRepository.findByParent(parent);
        return new ReviewResponse(parent, child.orElse(null));
    }

    @Transactional(readOnly = true)
    public Page<ReviewWithSimpleLectureResponse> getReviewWithSimpleLectureResponses(User user, Integer page) {
        return reviewQueryRepository.findReviewsWithChildAndSimpleLectureByUser(user, getPageRequest(page));
    }

    @Transactional(readOnly = true)
    public ReviewResponse getReviewResponse(Long reviewId) {

        Review parent = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException(REVIEW));

        // TODO - Optional 체크
        Optional<Review> child = reviewRepository.findByParent(parent);
        return new ReviewResponse(parent, child.orElse(null));
    }

    @Transactional(readOnly = true)
    public ReviewWithSimpleLectureResponse getReviewWithSimpleLectureResponse(Long reviewId) {

        Review parent = reviewRepository.findWithLectureByReviewId(reviewId)
                .orElseThrow(() -> new EntityNotFoundException(REVIEW));

        // TODO - Optional 체크
        Optional<Review> child = reviewRepository.findByParent(parent);
        return new ReviewWithSimpleLectureResponse(parent, child.orElse(null));
    }

    @Transactional(readOnly = true)
    public Page<ReviewWithSimpleLectureResponse> getReviewWithSimpleLectureResponsesOfMentorByMentees(User user, Integer page) {

        Mentor mentor = Optional.ofNullable(mentorRepository.findByUser(user))
                .orElseThrow(() -> new UnauthorizedException(RoleType.MENTOR));
        return reviewQueryRepository.findReviewsWithSimpleLectureOfMentorByMentees(mentor, getPageRequest(page));
    }
}
