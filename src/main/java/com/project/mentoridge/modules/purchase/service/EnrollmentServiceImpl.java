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
import com.project.mentoridge.modules.firebase.service.AndroidPushNotificationsService;
import com.project.mentoridge.modules.lecture.controller.response.LecturePriceWithLectureResponse;
import com.project.mentoridge.modules.lecture.repository.LecturePriceRepository;
import com.project.mentoridge.modules.lecture.repository.LectureRepository;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.lecture.vo.LecturePrice;
import com.project.mentoridge.modules.log.component.EnrollmentLogService;
import com.project.mentoridge.modules.notification.enums.NotificationType;
import com.project.mentoridge.modules.notification.service.NotificationService;
import com.project.mentoridge.modules.purchase.controller.response.EnrollmentWithLecturePriceResponse;
import com.project.mentoridge.modules.purchase.controller.response.EnrollmentWithSimpleLectureResponse;
import com.project.mentoridge.modules.purchase.repository.EnrollmentQueryRepository;
import com.project.mentoridge.modules.purchase.repository.EnrollmentRepository;
import com.project.mentoridge.modules.purchase.vo.Enrollment;
import com.project.mentoridge.modules.review.repository.MenteeReviewRepository;
import com.project.mentoridge.modules.review.repository.MentorReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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

    private final AndroidPushNotificationsService androidPushNotificationsService;
    private final NotificationService notificationService;

    @Transactional(readOnly = true)
    @Override
    public Page<EnrollmentWithLecturePriceResponse> getEnrollmentWithLecturePriceResponsesOfMentee(User user, boolean checked, Integer page) {
        Mentee mentee = getMentee(menteeRepository, user);
        return enrollmentQueryRepository.findEnrollmentsWithLecturePrice(mentee, checked, getPageRequest(page));
    }

    @Transactional(readOnly = true)
    @Override
    public LecturePriceWithLectureResponse getLecturePriceWithLectureResponseOfMentee(User user, Long enrollmentId) {
        Mentee mentee = getMentee(menteeRepository, user);
        return enrollmentQueryRepository.findLecturePriceWithLecture(mentee, enrollmentId);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<EnrollmentWithSimpleLectureResponse> getEnrollmentWithSimpleLectureResponses(User user, boolean reviewed, Integer page) {
        Mentee mentee = getMentee(menteeRepository, user);
        return enrollmentQueryRepository.findEnrollments(mentee, reviewed, getPageRequest(page));
    }

    @Transactional(readOnly = true)
    @Override
    public EnrollmentWithSimpleLectureResponse getEnrollmentWithSimpleLectureResponse(User user, Long enrollmentId) {
        Mentee mentee = getMentee(menteeRepository, user);
        return enrollmentQueryRepository.findEnrollment(mentee, enrollmentId);
    }

    @Override
    public Enrollment createEnrollment(User user, Long lectureId, Long lecturePriceId) {

        Mentee mentee = getMentee(menteeRepository, user);
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
        enrollmentLogService.insert(user, saved);

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
                    mentorReviewRepository.findByParent(menteeReview).ifPresent(mentorReview -> {
                        mentorReview.delete();
                        // mentorReviewRepository.deleteById(mentorReview.getId());
                    });
                    menteeReview.delete();
                    menteeReviewRepository.delete(menteeReview);
                }
        );
        enrollment.delete();
        enrollmentRepository.delete(enrollment);
    }

    @Override
    public void check(User user, Long enrollmentId) {

        Mentor mentor = getMentor(mentorRepository, user);
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new EntityNotFoundException(EntityNotFoundException.EntityType.ENROLLMENT));
        // TODO - CHECK
//        if (!enrollment.getLecture().getMentor().equals(mentor)) {
//            throw new UnauthorizedException();
//        }
        enrollment.check(user, enrollmentLogService);
    }

    @Override
    public void finish(User user, Long enrollmentId) {

        Mentee mentee = getMentee(menteeRepository, user);
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new EntityNotFoundException(EntityNotFoundException.EntityType.ENROLLMENT));
        if (!enrollment.getMentee().equals(mentee)) {
            throw new UnauthorizedException();
        }
        enrollment.finish(user, enrollmentLogService);
    }
}
