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
import com.project.mentoridge.modules.purchase.controller.response.EnrollmentWithSimpleLectureResponse;
import com.project.mentoridge.modules.purchase.repository.EnrollmentQueryRepository;
import com.project.mentoridge.modules.purchase.repository.EnrollmentRepository;
import com.project.mentoridge.modules.purchase.vo.Enrollment;
import com.project.mentoridge.modules.review.repository.MenteeReviewRepository;
import com.project.mentoridge.modules.review.repository.MentorReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
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
    private final MenteeRepository menteeRepository;

    private final MentorRepository mentorRepository;
    private final LectureRepository lectureRepository;
    private final LecturePriceRepository lecturePriceRepository;

    private final MenteeReviewRepository menteeReviewRepository;
    private final MentorReviewRepository mentorReviewRepository;

    private final AndroidPushNotificationsService androidPushNotificationsService;
    private final NotificationService notificationService;

    private final EnrollmentLogService enrollmentLogService;


    @Transactional(readOnly = true)
    @Override
    public Page<LecturePriceWithLectureResponse> getLecturePriceWithLectureResponsesOfMentee(User user, Integer page) {
        Mentee mentee = getMentee(menteeRepository, user);
        return enrollmentQueryRepository.findLecturePricesWithLecture(mentee, getPageRequest(page));
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

        // TODO - CHECK : lecture & mentor - fetch join
        // TODO - CHECK : lecture의 enrollment가 null vs mentee의 enrollment는 size = 0
        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new EntityNotFoundException(LECTURE));

        LecturePrice lecturePrice = lecturePriceRepository.findByLectureAndId(lecture, lecturePriceId)
                .orElseThrow(() -> new EntityNotFoundException(LECTURE_PRICE));

        if (!lecture.isApproved() || lecturePrice.isClosed()) {
            throw new RuntimeException("수강이 불가능한 강의입니다.");
        }

        // 강의 재구매 불가
        if (enrollmentRepository.findByMenteeAndLecture(mentee, lecture).isPresent()) {
            throw new AlreadyExistException(AlreadyExistException.ENROLLMENT);
        }

        // TODO - 구매 프로세스
        // TODO - 구매 중복 X 체크 (UNIQUE)

        // 성공 시
        // TODO - CHECK
        Enrollment enrollment = enrollmentRepository.save(buildEnrollment(mentee, lecture, lecturePrice));
        enrollmentLogService.insert(user, enrollment);

        User mentorUser = lecture.getMentor().getUser();
        // 강의 등록 시 멘토에게 알림 전송
        notificationService.createNotification(mentorUser, NotificationType.ENROLLMENT);
        // androidPushNotificationsService.send(mentorUser.getFcmToken(), "강의 등록", String.format("%s님이 %s 강의를 등록했습니다", user.getNickname(), lecture.getTitle()));
        return enrollment;
    }

    // @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void deleteEnrollment(Enrollment enrollment) {

        Optional.ofNullable(menteeReviewRepository.findByEnrollment(enrollment)).ifPresent(
                menteeReview -> {
                    // System.out.println(menteeReview.getId());
                    // mentorReview-Lecture PK 때문에 mentorReview 삭제 쿼리가 커밋될 때 에러 발생 -> menteeReview로 delete
                    // TODO - CHECK : mentorReview-Lecture PK 제거
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
        if (!enrollment.getLecture().getMentor().equals(mentor)) {
            throw new UnauthorizedException();
        }
        enrollment.check();
        enrollmentLogService.check(user, enrollment);
    }
}
