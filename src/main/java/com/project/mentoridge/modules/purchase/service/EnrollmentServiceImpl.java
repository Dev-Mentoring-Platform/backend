package com.project.mentoridge.modules.purchase.service;

import com.project.mentoridge.config.exception.AlreadyExistException;
import com.project.mentoridge.config.exception.EntityNotFoundException;
import com.project.mentoridge.config.exception.UnauthorizedException;
import com.project.mentoridge.modules.account.repository.MenteeRepository;
import com.project.mentoridge.modules.account.repository.MentorRepository;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.base.AbstractService;
import com.project.mentoridge.modules.base.BaseEntity;
import com.project.mentoridge.modules.lecture.controller.response.EachLectureResponse;
import com.project.mentoridge.modules.lecture.repository.LecturePriceRepository;
import com.project.mentoridge.modules.lecture.repository.LectureRepository;
import com.project.mentoridge.modules.lecture.repository.dto.LectureMentorQueryDto;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.lecture.vo.LecturePrice;
import com.project.mentoridge.modules.log.component.EnrollmentLogService;
import com.project.mentoridge.modules.notification.enums.NotificationType;
import com.project.mentoridge.modules.notification.service.NotificationService;
import com.project.mentoridge.modules.purchase.controller.response.EnrollmentWithEachLectureResponse;
import com.project.mentoridge.modules.purchase.controller.response.EnrollmentWithSimpleEachLectureResponse;
import com.project.mentoridge.modules.purchase.repository.EnrollmentQueryRepository;
import com.project.mentoridge.modules.purchase.repository.EnrollmentRepository;
import com.project.mentoridge.modules.purchase.vo.Enrollment;
import com.project.mentoridge.modules.review.repository.MenteeReviewRepository;
import com.project.mentoridge.modules.review.repository.MentorReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.project.mentoridge.config.exception.EntityNotFoundException.EntityType.LECTURE;
import static com.project.mentoridge.config.exception.EntityNotFoundException.EntityType.LECTURE_PRICE;
import static com.project.mentoridge.modules.purchase.vo.Enrollment.buildEnrollment;

@Service
@Transactional
@RequiredArgsConstructor
public class EnrollmentServiceImpl extends AbstractService implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final EnrollmentQueryRepository enrollmentQueryRepository;
    private final EnrollmentLogService enrollmentLogService;

    private final MenteeRepository menteeRepository;
    private final MentorRepository mentorRepository;
    private final LectureRepository lectureRepository;
    private final LecturePriceRepository lecturePriceRepository;
    private final MenteeReviewRepository menteeReviewRepository;
    private final MentorReviewRepository mentorReviewRepository;

    // private final AndroidPushNotificationsService androidPushNotificationsService;
    private final NotificationService notificationService;

    @Transactional(readOnly = true)
    @Override
    public Page<EnrollmentWithEachLectureResponse> getEnrollmentWithEachLectureResponsesOfMentee(User menteeUser, boolean checked, Integer page) {
        Mentee mentee = getMentee(menteeRepository, menteeUser);

        Page<EnrollmentWithEachLectureResponse> enrollments = enrollmentQueryRepository.findEnrollmentsWithEachLecture(mentee, checked, getPageRequest(page));

        // 후기 작성 여부 추가
        List<Long> enrollmentIds = enrollments.stream().map(EnrollmentWithEachLectureResponse::getEnrollmentId).collect(Collectors.toList());
        Map<Long, Long> map = menteeReviewRepository.findByEnrollmentIds(enrollmentIds).stream()
                .collect(Collectors.toMap(menteeReview -> menteeReview.getEnrollment().getId(), BaseEntity::getId));
        for (EnrollmentWithEachLectureResponse enrollment : enrollments) {
            if (map.containsKey(enrollment.getEnrollmentId())) {
                enrollment.setReviewed(true);
            }
        }
        return enrollments;
    }

    @Transactional(readOnly = true)
    @Override
    public EachLectureResponse getEachLectureResponseOfEnrollment(User menteeUser, Long enrollmentId, boolean checked) {
        Mentee mentee = getMentee(menteeRepository, menteeUser);
        return enrollmentQueryRepository.findEachLectureOfEnrollment(mentee, enrollmentId, checked);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<EnrollmentWithSimpleEachLectureResponse> getEnrollmentWithSimpleEachLectureResponses(User menteeUser, boolean reviewed, Integer page) {
        Mentee mentee = getMentee(menteeRepository, menteeUser);
        return enrollmentQueryRepository.findEnrollments(mentee, reviewed, getPageRequest(page));
    }

    @Transactional(readOnly = true)
    @Override
    public EnrollmentWithSimpleEachLectureResponse getEnrollmentWithSimpleEachLectureResponse(User menteeUser, Long enrollmentId) {
        Mentee mentee = getMentee(menteeRepository, menteeUser);
        return enrollmentQueryRepository.findEnrollment(mentee, enrollmentId);
    }

    @Override
    public Enrollment createEnrollment(User menteeUser, Long lectureId, Long lecturePriceId) {

        Mentee mentee = getMentee(menteeRepository, menteeUser);
        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new EntityNotFoundException(LECTURE));
        LecturePrice lecturePrice = lecturePriceRepository.findByLectureAndId(lecture, lecturePriceId)
                .orElseThrow(() -> new EntityNotFoundException(LECTURE_PRICE));
        if (!lecture.isApproved() || lecturePrice.isClosed()) {
            throw new RuntimeException("수강이 불가능한 강의입니다.");
        }

        // 강의 재구매 불가
        if (enrollmentRepository.findByMenteeAndLectureAndLecturePrice(mentee, lecture, lecturePrice).isPresent()) {
            throw new AlreadyExistException(AlreadyExistException.ENROLLMENT);
        }
        Enrollment saved = enrollmentRepository.save(buildEnrollment(mentee, lecture, lecturePrice));
        enrollmentLogService.insert(menteeUser, saved);

        // TODO - CHECK : fetch join
        User mentorUser = lecture.getMentor().getUser();
        // 강의 등록 시 멘토에게 알림 전송
        notificationService.createNotification(mentorUser, NotificationType.ENROLLMENT);
        // androidPushNotificationsService.send(mentorUser.getFcmToken(), "강의 등록", String.format("%s님이 %s 강의를 등록했습니다", user.getNickname(), lecture.getTitle()));
        return saved;
    }

    // @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void deleteEnrollment(Enrollment enrollment) {

        Optional.ofNullable(menteeReviewRepository.findByEnrollment(enrollment)).ifPresent(
                menteeReview -> {
                    mentorReviewRepository.deleteByParent(menteeReview);
                    menteeReviewRepository.delete(menteeReview);
                }
        );
        enrollment.delete();
        enrollmentRepository.delete(enrollment);
    }

    @Override
    public void check(User mentorUser, Long enrollmentId) {

        Mentor mentor = getMentor(mentorRepository, mentorUser);
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new EntityNotFoundException(EntityNotFoundException.EntityType.ENROLLMENT));
        // TODO - CHECK
//        if (!enrollment.getLecture().getMentor().equals(mentor)) {
//            throw new UnauthorizedException();
//        }
        enrollment.check(mentorUser, enrollmentLogService);
    }

    @Override
    public void finish(User menteeUser, Long enrollmentId) {

        Mentee mentee = getMentee(menteeRepository, menteeUser);
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new EntityNotFoundException(EntityNotFoundException.EntityType.ENROLLMENT));
        if (!enrollment.getMentee().equals(mentee)) {
            throw new UnauthorizedException();
        }
        enrollment.finish(menteeUser, enrollmentLogService);
    }
}
